package com.tv.xeeng.workflow;

import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.ISessionFactory;
import com.tv.xeeng.base.session.SessionManager;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.chat.data.ChatRoomZone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.game.tournement.TourManager;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.*;
import com.tv.xeeng.protocol.messages.ExpiredSessionResponse;
import com.tv.xeeng.server.IServer;
import com.tv.xeeng.server.Server;
import org.slf4j.Logger;

import java.util.Calendar;

public class SimpleWorkflow
        implements IWorkflow {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(SimpleWorkflow.class);
    private ISessionFactory mSessionFactory;
    private IBusinessPropertiesFactory mBusinessPropertiesFactory;
    private ZoneManager mZoneMgr;
    private SessionManager mSessionMgr;
    private MessageFactory mMsgFactory;
    private WorkflowConfig mWorkflowConfig;
    private TourManager tourMgr;
    private ChatRoomZone chatZone;

    public TourManager getTourMgr() {
        return tourMgr;
    }

    public SimpleWorkflow() throws ServerException {
        try {
            this.mWorkflowConfig = new WorkflowConfig();
            this.mWorkflowConfig.getClass();
            this.mLog.info("[WF] 1. Load workflow's config from source " + "conf/workflow-config.xml");

            this.mMsgFactory = new MessageFactory();

            this.mLog.info("[WF] 2. Initial game's messages");
            this.mMsgFactory.initModeling();

            String businessPropertiesFactoryName = this.mWorkflowConfig.getBusinessPropertiesFactory();

            this.mBusinessPropertiesFactory = ((IBusinessPropertiesFactory) Class.forName(businessPropertiesFactoryName).newInstance());
            this.mLog.info("[WF] 3. Generate Business's Properties from " + this.mBusinessPropertiesFactory.getClass().getName());

            this.mLog.info("[WF] 4. Start database connection");

            this.startDB();

            this.mLog.info("[WF] 5. Load zones...");
            this.mZoneMgr = new ZoneManager();

        } catch (Throwable t) {
            throw new ServerException(t);
        }
    }

    public void startDB() throws Exception {
        Server.REAL_GOT_MONEY = mWorkflowConfig.getRealGotMoney();

        Server.MONEY_TRANSFER_DAY_LIMIT = mWorkflowConfig.getMoneyTransferDayLimit();

        Server.MONEY_TRANSFER_TAX = mWorkflowConfig.getMoneyTransferTax();

        this.mLog.info("[WF] REAL_GOT_MONEY " + Server.REAL_GOT_MONEY);
        this.mLog.info("[WF] MONEY_TRANSFER_DAY_LIMIT " + Server.MONEY_TRANSFER_DAY_LIMIT);
        this.mLog.info("[WF] MONEY_TRANSFER_TAX " + Server.MONEY_TRANSFER_TAX);

        // thanhnvt {
        Class.forName("com.tv.xeeng.game.room.XEGameConstants");
        // } thanhnvt

        try {
            DBCache.isLoadQuestion = this.mWorkflowConfig.isLoadQuestion();
            DBCache.isLoadMonthlyEvent = this.mWorkflowConfig.isLoadMonthlyEvent();
            CacheUserInfo.isUseCache = this.mWorkflowConfig.isUseCache();
            if (CacheUserInfo.isUseCache) {
                CacheUserInfo.initCache();
            }

        } catch (Throwable e) {
            this.mLog.info("Exception Config " + e.getMessage());
        }

        DBCache.reload();
        UserDB userdb = new UserDB();
        userdb.clearLogin();
    }

    @Override
    public void start() throws ServerException {
        String serverName;
        try {
            serverName = this.mWorkflowConfig.getServerName();
            IServer server = (IServer) Class.forName(serverName).newInstance();

            server.setWorkflow(this);
            this.mSessionFactory = server.getSessionFactory();

            int serverPort = this.mWorkflowConfig.getServerPort();
            server.setServerPort(serverPort);

            int connectTimeout = this.mWorkflowConfig.getServerConnectTimeout();
            server.setConnectTimeout(connectTimeout);

            int receiveBufferSize = this.mWorkflowConfig.getServerReceiveBufferSize();
            server.setReceiveBufferSize(receiveBufferSize);

            boolean reuseAddress = this.mWorkflowConfig.getReuseAddress();
            server.setReuseAddress(reuseAddress);

            boolean tcpNoDelay = this.mWorkflowConfig.getTcpNoDelay();
            server.setTcpNoDelay(tcpNoDelay);

            server.start();

            int sessionTimeout = this.mWorkflowConfig.getSessionTimeout();
            this.mLog.info("[WF] 6. Create session manager with sessiontimeout = " + sessionTimeout);
            this.mSessionMgr = new SessionManager(sessionTimeout);
            this.mSessionMgr.addSessionListener(this.mZoneMgr);

            this.mLog.info("[WF] 7. end. Server started with name = " + serverName);
            this.mLog.info("Port = " + serverPort);
            this.mLog.info("ConnectTimeout (ms) = " + connectTimeout);
            this.mLog.info("ReceiveBufferSize (bytes) = " + receiveBufferSize);
            this.mLog.info("ReuseAddress = " + reuseAddress);
            this.mLog.info("TcpNoDelay = " + tcpNoDelay);

        } catch (Throwable t) {
            System.out.println("Fail to start Server!");
            t.printStackTrace();
            throw new ServerException(t);
        }
    }

    @Override
    public WorkflowConfig getWorkflowConfig() {
        return this.mWorkflowConfig;
    }

    protected IRequestPackage decode(ISession aSession, IByteBuffer aRequest) throws ServerException {
        String pkgFormat = aSession.getPackageFormat();

        IPackageProtocol pkgProtocol = this.mMsgFactory.getPackageProtocol(pkgFormat);
        IRequestPackage requestPkg = pkgProtocol.decode(aSession, aRequest);

        PackageHeader pkgHeader = requestPkg.getRequestHeader();

        String sessionId = pkgHeader.getSessionID();
        if ((sessionId != null) && (!(sessionId.trim().equals("")))) {
            this.mSessionMgr.addSession(pkgHeader.getSessionID(), aSession);
        }

        return requestPkg;
    }

    @Override
    public IByteBuffer process(ISession aSession, IByteBuffer aRequest) throws ServerException {
        boolean canDecode = false;
        IRequestPackage requestPkg = null;

        if (SimpleRequestPackage.canDecode(aSession, aRequest)) {
            aSession.setIsHandling(Boolean.TRUE);

            aSession.setLastAccessTime(Calendar.getInstance().getTime());
            // Added by TungHX
            aSession.setLastTimestamp(Calendar.getInstance().getTimeInMillis());

            // End Added by TungHX
            String pkgFormat = aRequest.getString().toLowerCase();

            aSession.setPackageFormat(pkgFormat);

            requestPkg = decodeNew(aSession, aRequest);

            canDecode = true;

        }

        if (canDecode) {
            filterIn(aSession, requestPkg);

            IResponsePackage responsePkg = new SimpleResponsePackage();

            handleRequest(aSession, requestPkg, responsePkg);

            filterOut(aSession, responsePkg);

            responsePkg.prepareEncode(aSession);

            IByteBuffer result = encode(aSession, responsePkg);

            return result;
        }

        return null;

    }

    protected IRequestPackage decodeNew(ISession aSession, IByteBuffer aRequest) throws ServerException {
        String pkgFormat = aSession.getPackageFormat();

        IPackageProtocol pkgProtocol = this.mMsgFactory.getPackageProtocol(pkgFormat);
        IRequestPackage requestPkg = pkgProtocol.decodeNew(aSession, aRequest);

        PackageHeader pkgHeader = requestPkg.getRequestHeader();

        String sessionId = pkgHeader.getSessionID();
        if ((sessionId != null) && (!(sessionId.trim().equals("")))) {
            this.mSessionMgr.addSession(pkgHeader.getSessionID(), aSession);
        }

        return requestPkg;
    }

    protected void filterIn(ISession aSession, IRequestPackage aRequestPkg) {
        if (aSession.isClosed()) {
            //return;
        }
    }

    @SuppressWarnings("unused")
    protected void handleRequest(ISession aSession, IRequestPackage aRequestPkg, IResponsePackage aResponsePkg) {
        if (aSession.isClosed()) {
            return;
        }
        try {
            while (true) {
                if (!(aRequestPkg.hasNext())) {
                    break;
                }

                IRequestMessage reqMsg = aRequestPkg.next();

                if ((reqMsg != null) && (((!(reqMsg.isNeedLoggedIn())) || (aSession.isLoggedIn())))) {
                    long timeStart;
                    long timeEnd;
                    timeStart = System.currentTimeMillis();

                    int msgId = reqMsg.getID();

                    IBusiness business = this.mMsgFactory.getBusiness(msgId);
                    try {
                        int result = business.handleMessage(aSession, reqMsg, aResponsePkg);
                        timeEnd = System.currentTimeMillis();
                        if (timeEnd - timeStart > 200L) {
                            this.mLog.warn("LONG TIME REQUEST " + msgId + ": " + (timeEnd - timeStart));
                        }
                    } catch (ServerException se) {
                        this.mLog.error("[WF] process message " + msgId + " error.", se);
                    } finally {
                    }
                } else if (reqMsg != null) {
                    String sessionId = aSession.getID();
//                    this.mSessionMgr.removeSession(sessionId);
                    this.mLog.debug("Fake message " + reqMsg.getID() + ", sessionid = " + sessionId);
                    ExpiredSessionResponse expiredSession = (ExpiredSessionResponse) this.mMsgFactory.getResponseMessage(9999);
                    expiredSession.mErrorMsg = "Phiên làm việc đã kết thúc. Vui lòng đăng nhập lại để tiếp tục chơi.";
                    aResponsePkg.addMessage(expiredSession);

                    // thống kê người chơi online
                    Server.userOnlineList.remove(aSession.getUID());

                    break;
                }
            }
        } catch (Throwable t) {
            label448:
            this.mLog.error("Unexpected error on handlePackage() method!", t);
        } finally {
            //aSession.setCurrentDBConnection(null);
        }
    }

    protected void filterOut(ISession aSession, IResponsePackage aResponsePkg) {
        if (aSession.isClosed()) {
            //return;
        }
    }

    protected IByteBuffer encode(ISession aSession, IResponsePackage aResponsePkg)
            throws ServerException {
        if (aSession.isClosed()) {
            return null;
        }

        String pkgFormat = aSession.getPackageFormat();

        IPackageProtocol pkgProtocol = this.mMsgFactory.getPackageProtocol(pkgFormat);

        IByteBuffer result = pkgProtocol.encode(aSession, aResponsePkg);

        return result;
    }

    @Override
    public ISession sessionCreated(Object aAttachmentObj)
            throws ServerException {
        ISession session = this.mSessionFactory.createSession();

        session.setCreatedTime(Calendar.getInstance().getTime());

        this.mSessionMgr.sessionCreated(session);

        session.sessionCreated(aAttachmentObj);

        BusinessProperties businessProps = createBusinessProperties();
        session.setBusinessProperties(businessProps);

        session.setZoneManager(this.mZoneMgr);
        session.setTourMgr(tourMgr);

        session.setMessageFactory(this.mMsgFactory);
        return session;
    }

    @Override
    public BusinessProperties createBusinessProperties() {
        return this.mBusinessPropertiesFactory.createBusinessProperties();
    }

    @Override
    public void serverStarted() {
        this.mLog.debug("[WF] Server Started");
    }

    @Override
    public void serverStoppted() {
        this.mLog.debug("[WF] Server Stoppted");
    }

    public ZoneManager getZoneManager() {
        return this.mZoneMgr;
    }

    public SessionManager getmSessionMgr() {
        return mSessionMgr;
    }

    /**
     * @param mSessionMgr the mSessionMgr to set
     */
    public void setmSessionMgr(SessionManager mSessionMgr) {
        this.mSessionMgr = mSessionMgr;
    }

    /**
     * @return the chatZone
     */
    public ChatRoomZone getChatZone() {
        return chatZone;
    }
}
