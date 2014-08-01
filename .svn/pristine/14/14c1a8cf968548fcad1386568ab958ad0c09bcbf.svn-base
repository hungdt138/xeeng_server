package com.tv.xeeng.base.session;

import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.protocol.messages.KeepConnectionResponse;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.game.tournement.TourManager;
import com.tv.xeeng.protocol.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class AbstractSession implements ISession {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AbstractSession.class);
    @SuppressWarnings("unused")
    private final String SESSION_ID = "session.id";
    @SuppressWarnings("unused")
    private final String SESSION_USER_ID = "session.user.id";
    @SuppressWarnings("unused")
    private final String SESSION_USER_NAME = "session.user.name";
    @SuppressWarnings("unused")
    private final String SESSION_BUSINESS_PROPS = "session.business.props";
    @SuppressWarnings("unused")
    private final String SESSION_IS_HANDLING = "session.is.handling";
    @SuppressWarnings("unused")
    private final String SESSION_ATTACHTMENT_OBJECT = "session.attachment.object";
    @SuppressWarnings("unused")
    private final String SESSION_COOKIES = "session.cookies";
    @SuppressWarnings("unused")
    private final String SESSION_MESSAGE_FACTORY = "session.message.factory";
    @SuppressWarnings("unused")
    private final String SESSION_PKG_FORMAT = "session.package.format";
    @SuppressWarnings("unused")
    private final String SESSION_CREATED_TIME = "session.created.time";
    @SuppressWarnings("unused")
    private final String SESSION_LASTACCESS_TIME = "session.lastaccess.time";
    @SuppressWarnings("unused")
    private final String SESSION_TIMEOUT = "session.timeout";
    @SuppressWarnings("unused")
    private final String SESSION_LOGGED_IN = "session.logged.in";
    @SuppressWarnings("unused")
    private final String SESSION_CURRENT_DB_CONNECTION = "session.current.db.connection";
    @SuppressWarnings("unused")
    private final String SESSION_IS_COMMIT = "session.is.commit";

    @SuppressWarnings("unused")
    private final String SESSION_USER_LOGIN_NAME = "session.user.loginName";
    @SuppressWarnings("unused")
    private final String SESSION_USER_CMND = "session.user.cmnd";
    @SuppressWarnings("unused")
    private final String SESSION_USER_XEPHONENUMBER = "session.user.xephonenumber";

    private ConcurrentHashMap<String, Object> mAttrs;
    private final IResponsePackage mResPkg;
    private boolean mIsClosed = true;
    private SessionManager mSessionMgr;
    private ZoneManager mZoneMgr;
    private final ConcurrentHashMap<Long, Room> mJoinedRooms;
    private boolean isMobile = false;
    private static final int DEAD_TIMEOUT = 300000;
    private String mobileVersion = "";
    private String screenSize = "";
    private StringBuilder collectInfo;
    private Room room;
    private long lastFastMatchId;
    private int protocolVersion;
    private boolean mobileDevice;
    private int chatRoomId;
    private boolean uploadAvatar;
    private long lastRegister = 0;
    private UserEntity userEntity;
    private String ip;

    // Added by TungHX
    private long lastTimestamp = -1;

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public boolean isExpiredNew() {
        long lastTS = getLastTimestamp();
        if (lastTS < 0) {
            // TODO
            return false;
        }

        Integer timeout = (Integer) getAttribute("session.timeout");
        if (timeout == null) {
            timeout = Integer.valueOf(0);
        }

        long lastTimeout = lastTS + timeout.intValue();

        long now = Calendar.getInstance().getTimeInMillis();

        return (lastTimeout < now);
    }

    // End Added by TungHX

    // Added by ThangTD
    @Override
    public void setUIDNull() {
        setAttribute("session.user.id", null);
    }
    // End Added by ThangTD

    @Override
    public String getOnlyIP() {
        return ip;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractSession() {
        this.mAttrs = new ConcurrentHashMap();
        this.mResPkg = new SimpleResponsePackage();
        this.mJoinedRooms = new ConcurrentHashMap();
        this.mIsClosed = true;
        collectInfo = new StringBuilder();
    }

    private SimpleTable table;

    @Override
    public SimpleTable getTable() {
        return table;
    }

    @Override
    public void setTable(SimpleTable table) {
        this.table = table;
    }

    //    @Override
//    public void broadcast(Object obj, int type) throws ServerException, BusinessException {
//    }
    private boolean version35 = false;

    @Override
    public boolean getVersion35() {
        return version35;
    }

    @Override
    public void setVersion35(boolean is) {
        version35 = is;
    }

    private TourManager tourMgr;

    @Override
    public void setTourMgr(TourManager tourMgr) {
        this.tourMgr = tourMgr;
    }

    @Override
    public TourManager getTourMgr() {
        return tourMgr;
    }
    // tuanda - Cancel Message while disconnected.

    @Override
    public void cancelTable() {
        IResponsePackage responsePkg = this.getDirectMessages();// new
        // SimpleResponsePackage();

        MessageFactory msgFactory = this.getMessageFactory();
        IBusiness business;
        long uid = this.getUID();
        Zone zone = this.findZone(this.getCurrentZone());

        // Set Phong
        if (zone != null) {
            Phong currPhong = zone.getPhong(this.getPhongID());
            if (currPhong != null) {
                currPhong.outPhong(this);
            }
        }

        Vector<Room> joinedRoom = this.getJoinedRooms();
        long matchID; // Find match
        if (joinedRoom.size() > 0) {
            Room temp = joinedRoom.lastElement();
            matchID = temp.getRoomId();
            business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);
            CancelRequest rqMatchCancel = (CancelRequest) msgFactory.getRequestMessage(MessagesID.MATCH_CANCEL);
            rqMatchCancel.uid = uid;
            rqMatchCancel.mMatchId = matchID;
            rqMatchCancel.isLogout = true;

            try {
                business.handleMessage(this, rqMatchCancel, responsePkg);
            } catch (ServerException se) {
                this.mLog.error("[Netty Socket] Exception Catch Error!", se.getCause());
            }
        }
    }

    private int currRoomID = 0;
    private int roomLevel = 0;
    private int phongID = 0;

    @Override
    public void setAvatarNull() {
        avatarString = "";
    }

    @Override
    public void addAvatarString(String temp) {
        this.lastPing = System.currentTimeMillis();
        avatarString = avatarString + temp;
        mLog.debug(" avatar length" + avatarString.length());
    }

    @Override
    public String getAvatarString() {
        return avatarString;
    }

    private String avatarString = "";

    @Override
    public int getPhongID() {
        return phongID;
    }

    @Override
    public void setPhongID(int phongID) {
        this.phongID = phongID;
    }

    @Override
    public int getRoomLevel() {
        return roomLevel;
    }

    @Override
    public void setRoomLevel(int roomLevel) {
        this.roomLevel = roomLevel;
    }

    @Override
    public int getRoomID() {
        return this.currRoomID;
    }

    @Override
    public void setRoomID(int id) {
        this.currRoomID = id;

    }

    private int currentZone = -1;

    @Override
    public int getCurrentZone() {
        return currentZone;
    }

    @Override
    public void setScreenSize(String screen) {
        this.screenSize = screen;
    }

    @Override
    public void setMobile(String ver) {
        mobileVersion = ver;
        isMobile = true;
    }

    @Override
    public int getByteProtocol() {
        return protocolVersion;
    }

    @Override
    public void setByteProtocol(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    long lastPing = 0;

    @Override
    public long getLastMessage() {
        return lastMessage;
    }

    @Override
    public boolean realExpired() {
        if (lastMessage > 0
                && (System.currentTimeMillis() - lastMessage) > (Integer) getAttribute("session.timeout")) {
            return true;
        }
        return false;
    }

    public String remoteIP = "";

    @Override
    public void setIP(String ip1) {

        remoteIP = ip1;
        try {
            String[] ipElements = remoteIP.split(":");
            ip = ipElements[0];
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String getIP() {
        return remoteIP;
    }

    @Override
    public boolean realDead() {
        try {
            if (lastPing != 0 && (lastPing - lastMessage) > DEAD_TIMEOUT) {
                //find  dead session in table and put it into database
                if (isLoggedIn()) {
                    cancelTable();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private long lastMessage = 0;

    @Override
    public void receiveMessage() {
        lastMessage = System.currentTimeMillis();
    }

    @Override
    public void ping(ISession owner) {
        // MessageFactory msgFactory = owner.getMessageFactory();
        MessageFactory msgFactory = getMessageFactory();
        // KeepConnectionResponse k = new KeepConnectionResponse();
        KeepConnectionResponse k = (KeepConnectionResponse) msgFactory.getResponseMessage(MessagesID.KEEP_CONNECTION);
        // GetPokerResponse k = (GetPokerResponse)
        // msgFactory.getResponseMessage(MessagesID.GET_POKER);
        try {
            if (k == null) {
                System.out.println("Errror :(");
            }

            this.write(k);
            if (!realDead()) {
                lastPing = System.currentTimeMillis();
            }

        } catch (ServerException ex) {
            java.util.logging.Logger.getLogger(AbstractSession.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public boolean getMobile() {
        return isMobile;
    }

    @Override
    public String getMobileVer() {
        return mobileVersion;
    }

    @Override
    public void setCurrentZone(int currentZone) {
        this.currentZone = currentZone;
    }

    // End
    protected void setAttribute(String aKey, Object aValue) {
        if (aValue == null) {
            this.mAttrs.remove(aKey);
        } else {
            this.mAttrs.put(aKey, aValue);
        }
    }

    protected Object getAttribute(String aKey) {
        Object value = this.mAttrs.get(aKey);
        return value;
    }

    void setID(String aId) {
        String sId = (String) getAttribute("session.id");
        if (sId != null) {
            getManager().removeSession(sId);

            AbstractSession existedSession = (AbstractSession) getManager()
                    .removeSession(aId);
            if ((existedSession != null) && (!(existedSession.isClosed()))) {
                this.mAttrs.clear();
                this.mAttrs.putAll(existedSession.mAttrs);

                existedSession.doClose();
            }
        }

        setAttribute("session.id", aId);

        getManager().addSession(aId, this);
    }

    @Override
    public String getID() {
        String sessionId = (String) getAttribute("session.id");
        return sessionId;
    }

    @Override
    public void setUID(Long aId) {
        getManager().addUIDSession(aId, this);

        setAttribute("session.user.id", aId);
    }

    @Override
    public Long getUID() {
        Long uid = (Long) getAttribute("session.user.id");
        if (uid == null) {
            uid = Long.valueOf(0L);
        }
        return uid;
    }

    @Override
    public void setUserName(String aUserName) {
        setAttribute("session.user.name", aUserName);
    }

    @Override
    public String getUserName() {
        String userName = (String) getAttribute("session.user.name");
        return userName;
    }

    @Override
    public void setLoginName(String fullName) {
        setAttribute("session.user.loginName", fullName);
    }

    @Override
    public String getLoginName() {
        return (String) getAttribute("session.user.loginName");
    }

    @Override
    public void setCMND(String cmnd) {
        setAttribute("session.user.cmnd", cmnd);
    }

    @Override
    public String getCMND() {
        return (String) getAttribute("session.user.cmnd");
    }

    @Override
    public void setXEPhoneNumber(String xePhoneNumber) {
        setAttribute("session.user.xephonenumber", xePhoneNumber);
    }

    @Override
    public String getXEPhoneNumber() {
        return (String) getAttribute("session.user.xephonenumber");
    }

    void setManager(SessionManager aSessionMgr) {
        this.mSessionMgr = aSessionMgr;
    }

    @Override
    public SessionManager getManager() {
        return this.mSessionMgr;
    }

    @Override
    public void setBusinessProperties(BusinessProperties aBusinessProps) {
        setAttribute("session.business.props", aBusinessProps);
    }

    @Override
    public BusinessProperties getBusinessProperties() {
        BusinessProperties businessProps = (BusinessProperties) getAttribute("session.business.props");
        return businessProps;
    }

    boolean isSpam = false;

    @Override
    public void setSpam(boolean is) {
        isSpam = is;
    }

    @Override
    public boolean isSpam() {
        return isSpam;
    }

    @Override
    public synchronized void close() {
        if (!(isClosed())) {
            //long uid = this.getUID();
//			DatabaseDriver.updateUserOnline(uid, false);

            String id = getID();
            getManager().sessionClosed(id);

            doClose();

            this.mLog.debug("[SESSION] Session Closed: " + id);
        }
    }

    private void doClose() {
        this.mIsClosed = true;

        BusinessProperties businessProps = getBusinessProperties();
        if (businessProps != null) {
            businessProps.freeResources();
        }

        this.mAttrs.clear();
    }

    @Override
    public String userInfo() {
        //String s = "";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (this.isMobile) {
            sb.append("Mobile:Ver-").append(mobileVersion).append(":").append(screenSize).append(":");
        } //			s = s + "Mobile:Ver-" + mobileVersion + ":" + screenSize + ":";
        else {
            sb.append("Flash:");
        }
//			s = s + "Flash:";
        sb.append(getUserName()).append("][").append(getIP()).append("][").
                append(getUID()).append("-").append(getCurrentZone()).append("]");
//		s = s + getUserName() + "][" + getIP() + "][" + getUID() + "-"
//				+ getCurrentZone();
//		return "[" + s + "]";
        return sb.toString();
    }

    @Override
    public synchronized void sessionClosed() {
        if (!(isClosed())) {
            if ((isDirect()) || (isExpired())) {
                close();
            } else {
                setAttribute("session.attachment.object", null);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this.mIsClosed;
    }

    @Override
    public void sessionCreated(Object aAttachmentObj) {
        setAttribute("session.attachment.object", aAttachmentObj);

        this.mIsClosed = false;
    }

    @Override
    public Object getProtocolOutput() {
        return getAttribute("session.attachment.object");
    }

    @Override
    public boolean write(Object aObj) throws ServerException {
        if (realDead()) {
//			mLog.info(this.getUserName() + " : write to RealDead");
            return false;
        }

        synchronized (this.mResPkg) {
            if (aObj instanceof IResponseMessage) {
                this.mResPkg.addMessage((IResponseMessage) aObj);
            }/* else if (aObj instanceof IResponsePackage) {
             this.mResPkg.addPackage((IResponsePackage) aObj);
             }*/ else if (aObj instanceof String) {
                String res = (String) aObj;
//                                System.out.println(" length package response" + res.getBytes());
                return writeResponse(res.getBytes());
            }

            return write();
        }
    }

    private boolean write() throws ServerException {

        synchronized (this.mResPkg) {
            if (isDirect()) {
                if (!(isHandling())) {
                    String pkgFormat = getPackageFormat();

                    MessageFactory msgFactory = getMessageFactory();

                    if (msgFactory != null) {
                        // thanhnvt: chỗ này thường xuyên bị báo Null Pointer,
                        IPackageProtocol pkgProtocol = msgFactory
                                .getPackageProtocol(pkgFormat);

                        IByteBuffer encodedRes = pkgProtocol.encode(this,
                                this.mResPkg);
                        if (encodedRes != null) {
                            setLastAccessTime(Calendar.getInstance().getTime());

                            byte[] resData = encodedRes.array();

                            return writeResponse(resData);
                        }
                        //return true;
                    } else {
                        mLog.warn("msgFactory = null, no way...", pkgFormat);

                        return false;
                    }
                }

                return true;
            }

            Vector directMsgs = this.mResPkg.optAllMessages();
            directMsgs.clear();
            return true;
        }
    }

    protected abstract boolean writeResponse(byte[] paramArrayOfByte)
            throws ServerException;

    @Override
    public IResponsePackage getDirectMessages() {
        return this.mResPkg;
    }

    @Override
    public void setIsHandling(Boolean aIsHandling) {
        if (realDead()) {
            mLog.info(this.getUserName() + " : setIsHandling to RealDead");
        }

        synchronized (this.mResPkg) {
            setAttribute("session.is.handling", aIsHandling);
        }
    }

    @Override
    public boolean isHandling() {
        synchronized (this.mResPkg) {
            Boolean result = (Boolean) getAttribute("session.is.handling");
            if (result == null) {
                result = Boolean.valueOf(false);
            }
            return result.booleanValue();
        }
    }

    @Override
    public String getCookies() {
        String result = (String) getAttribute("session.cookies");
        return result;
    }

    @Override
    public void setCookies(String aCookies) {
        setAttribute("session.cookies", aCookies);
    }

    @Override
    public MessageFactory getMessageFactory() {
        MessageFactory msgFactory = (MessageFactory) getAttribute("session.message.factory");
        return msgFactory;
    }

    @Override
    public void setMessageFactory(MessageFactory aMsgFactory) {
        setAttribute("session.message.factory", aMsgFactory);
    }

    @Override
    public String getPackageFormat() {
        String pkgFormat = (String) getAttribute("session.package.format");
        return pkgFormat;
    }

    @Override
    public void setPackageFormat(String aPkgFormat) {
        setAttribute("session.package.format", aPkgFormat);
    }

    @Override
    public Date getCreatedTime() {
        Date createdTime = (Date) getAttribute("session.created.time");
        return createdTime;
    }

    @Override
    public void setCreatedTime(Date aCreatedTime) {
        setAttribute("session.created.time", aCreatedTime);
    }

    @Override
    public Date getLastAccessTime() {
        Date lastAccessTime = (Date) getAttribute("session.lastaccess.time");
        return lastAccessTime;
    }

    @Override
    public void setLastAccessTime(Date aLastAccessTime) {
        setAttribute("session.lastaccess.time", aLastAccessTime);
    }

    @Override
    public void setTimeout(Integer aMiliSeconds) {
        setAttribute("session.timeout", aMiliSeconds);
    }

    @Override
    public boolean isExpired() {
        Date lastAccessTime = getLastAccessTime();
        if (lastAccessTime == null) {
            lastAccessTime = getCreatedTime();
            if (lastAccessTime == null) {

                return false;
            }
        }

        Integer timeout = (Integer) getAttribute("session.timeout");
        if (timeout == null) {
            timeout = Integer.valueOf(0);
        }

        long lastTimeout = lastAccessTime.getTime() + timeout.intValue();

        long now = Calendar.getInstance().getTimeInMillis();

        return (lastTimeout < now);
    }

    @Override
    public void setLoggedIn(Boolean aIsLoggedIn) {
        setAttribute("session.logged.in", aIsLoggedIn);
    }

    @Override
    public boolean isLoggedIn() {
        Boolean isLoggedIn = (Boolean) getAttribute("session.logged.in");
        if (isLoggedIn == null) {
            isLoggedIn = Boolean.FALSE;
        }
        return isLoggedIn;
    }

    /*
     * public void setCurrentDBConnection(IConnection aConn) {
     * setAttribute("session.current.db.connection", aConn); }
     * 
     * public IConnection getCurrentDBConnection() { IConnection result =
     * (IConnection)getAttribute("session.current.db.connection"); return
     * result; }
     */
    @Override
    public void setCommit(boolean aIsCommit) {
        setAttribute("session.is.commit", Boolean.valueOf(aIsCommit));
    }

    @Override
    public boolean isCommit() {
        Boolean result = (Boolean) getAttribute("session.is.commit");
        if (result == null) {
            result = Boolean.FALSE;
        }
        return result.booleanValue();
    }

    @Override
    public void joinedRoom(Room aRoom) {
        if ((aRoom != null) && (aRoom.getRoomId() > 0L)) {
            synchronized (this.mJoinedRooms) {
                this.mJoinedRooms.put(Long.valueOf(aRoom.getRoomId()), aRoom);
            }
        }
    }

    @Override
    public Room findJoinedRoom(long aRoomId) {
        synchronized (this.mJoinedRooms) {
            return ((Room) this.mJoinedRooms.get(Long.valueOf(aRoomId)));
        }
    }

    @Override
    public Room leftRoom(long aRoomId) {
        synchronized (this.mJoinedRooms) {
            return ((Room) this.mJoinedRooms.remove(Long.valueOf(aRoomId)));
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Vector<Room> getJoinedRooms() {
        Enumeration eRooms;
        synchronized (this.mJoinedRooms) {
            eRooms = this.mJoinedRooms.elements();
        }
        Vector joinedRooms = new Vector();
        while (eRooms.hasMoreElements()) {
            Room aRoom = (Room) eRooms.nextElement();
            joinedRooms.add(aRoom);
        }
        return joinedRooms;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isJoinedFull(int aZoneId) {
        int joinedZone = 0;

        Enumeration eRooms;
        synchronized (this.mJoinedRooms) {
            eRooms = this.mJoinedRooms.elements();
        }
        while (eRooms.hasMoreElements()) {
            Room aRoom = (Room) eRooms.nextElement();
            if (aRoom.getZone().getZoneId() == aZoneId) {
                ++joinedZone;
            }
        }

        Zone zone = findZone(aZoneId);
        return ((zone.getJoinLimited() != -1) && (zone.getJoinLimited() <= joinedZone));
    }

    @Override
    public void setZoneManager(ZoneManager aZoneMgr) {
        this.mZoneMgr = aZoneMgr;
    }

    @Override
    public Zone findZone(int aZoneId) {
        return this.mZoneMgr.findZone(aZoneId);
    }

    @Override
    public Room getRoom() {
        return room;
    }

    @Override
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * @return the collectInfo
     */
    @Override
    public StringBuilder getCollectInfo() {
        return collectInfo;
    }

    @Override
    public long getLastFastMatch() {
        return lastFastMatchId;
    }

    @Override
    public void setLastFastMatch(long lastFastMatchId) {
        this.lastFastMatchId = lastFastMatchId;
    }

    /**
     * @return the mobileDevice
     */
    @Override
    public boolean isMobileDevice() {
        return mobileDevice;
    }

    /**
     * @param mobileDevice the mobileDevice to set
     */
    @Override
    public void setMobileDevice(boolean mobileDevice) {
        this.mobileDevice = mobileDevice;
    }

    @Override
    public int getChatRoom() {
        return chatRoomId;
    }

    @Override
    public void setChatRoom(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @Override
    public boolean isUploadAvatar() {
        return uploadAvatar;
    }

    @Override
    public void setUploadAvatar(boolean isUpload) {
        this.uploadAvatar = isUpload;
    }

    private boolean isHidden = false;

    @Override
    public void setHide(boolean b) {
        isHidden = b;
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    private boolean isMxh = false;

    @Override
    public void setMXHDevice(boolean b) {
        isMxh = b;
    }

    @Override
    public boolean isMXHDevice() {
        return isMxh;
    }

    @Override
    public UserEntity getUserEntity() {
        return this.userEntity;
    }

    @Override
    public void setUserEntity(UserEntity entity) {
        this.userEntity = entity;

    }

    private boolean acceptInvite = true;

    @Override
    public void setAcceptInvite(boolean b) {
        this.acceptInvite = b;
    }

    @Override
    public boolean isAcceptInvite() {
        return acceptInvite;
    }

    private InviteEntity inviteEnity;   //avoid conflict invitation

    @Override
    public void setInviteEntity(InviteEntity entity) {
        this.inviteEnity = entity;
    }

    @Override
    public InviteEntity getInviteEntity() {
        return this.inviteEnity;
    }

    private boolean rejectInvite;

    @Override
    public void setRejectInvite(boolean rejectInvite) {
        this.rejectInvite = rejectInvite;
    }

    @Override
    public boolean isRejectInvite() {
        return rejectInvite;
    }

    @Override
    public void setLastRegister(long lastRegister) {
        this.lastRegister = lastRegister;
    }

    @Override
    public long getLastRegister() {
        return this.lastRegister;
    }

    private long lastFP;

    @Override
    public void setLastFP(long lastFP) {
        this.lastFP = lastFP;
    }

    @Override
    public long getLastFP() {
        return lastFP;
    }

    private boolean replyInvite = true;

    @Override
    public void setReplyInvite(boolean b) {
        this.replyInvite = b;
    }

    @Override
    public boolean isReplyInvite() {
        return this.replyInvite;
    }

    private int numInvite = 0;

    @Override
    public int getNumInvite() {
        return numInvite;
    }

    @Override
    public void setNumInvite(int numInvite) {
        this.numInvite = numInvite;
    }

    private FindFriendEntity findFriendCache;

    @Override
    public FindFriendEntity getFindFriendCache() {
        return findFriendCache;
    }

    @Override
    public void setFindFriendCache(FindFriendEntity entity) {
        this.findFriendCache = entity;
    }

    private UploadAvatarEntity uploadEntity;

    @Override
    public UploadAvatarEntity getUploadAvatarEntity() {
        return uploadEntity;
    }

    @Override
    public void setUploadAvatarEntity(UploadAvatarEntity uploadEntity) {
        this.uploadEntity = uploadEntity;
    }

    private byte[] uploadPart;

    @Override
    public byte[] getUploadBytePart() {
        return uploadPart;
    }

    @Override
    public void setUploadBytePart(byte[] part) {
        this.uploadPart = part;
    }

    private PositionEntity currPosition = new PositionEntity();

    @Override
    public PositionEntity getCurrPosition() {
        return currPosition;
    }

    @Override
    public void setCurrPosition(PositionEntity currPosition) {
        this.currPosition = currPosition;
    }

    private List<RetryLoginEntity> lstRetries = new ArrayList<>();

    @Override
    public int getRetryLogin(String userName) {
        int retrySize = lstRetries.size();
        for (int i = 0; i < retrySize; i++) {
            RetryLoginEntity entity = lstRetries.get(i);
            if (entity.getName().equals(userName)) {

                return entity.getRetryTimes();
            }
        }

        return 0;
    }

    @Override
    public void setRetryLogin(String loginName) {
        int retrySize = lstRetries.size();
        for (int i = 0; i < retrySize; i++) {
            RetryLoginEntity entity = lstRetries.get(i);
            if (entity.getName().equals(loginName)) {
                entity.setRetryTimes(entity.getRetryTimes() + 1);
                return;
            }
        }

        RetryLoginEntity entity = new RetryLoginEntity(loginName, 1);
        lstRetries.add(entity);
    }

    private int botType = 0;

    @Override
    public int getBotType() {
        return botType;

    }

    @Override
    public void setBotType(int botType) {
        this.botType = botType;
    }

    private long lastSendImage = 0;

    @Override
    public long getLastSendImage() {
        return lastSendImage;
    }

    @Override
    public void setLastSendImage(long lastSendImage) {
        this.lastSendImage = lastSendImage;
    }

    private int deviceType = 0;

    @Override
    public int getDeviceType() {
        return deviceType;

    }

    @Override
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    private UUID imageRequest = null;

    @Override
    public UUID getImageRequest() {
        return imageRequest;
    }

    @Override
    public void setImageRequest(UUID imageRequest) {
        this.imageRequest = imageRequest;
    }
}
