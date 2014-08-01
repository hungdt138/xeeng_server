package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.FaceLoginRequest;
import com.tv.xeeng.base.protocol.messages.FaceLoginResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;
import java.sql.SQLException;
import java.util.Date;
import org.slf4j.Logger;

public class FaceLoginBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(FaceLoginBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
//        mLog.debug("[Guest LOGIN]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        FaceLoginResponse resLogin = (FaceLoginResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        FaceLoginRequest rqLogin = (FaceLoginRequest) aReqMsg;

        UserDB userDb = new UserDB();

        try {

            UserEntity user;
            resLogin.session = aSession;
            aSession.setByteProtocol(AIOConstants.PROTOCOL_MXH);
            resLogin.numberOnline = Server.numberOnline;

            aSession.setMobileDevice(true);

            if (rqLogin.mobileVersion != null) {
                if (rqLogin.mobileVersion.toUpperCase().contains("ANDROID")) {
                    mLog.debug("android device");
                    aSession.setDeviceType(AIOConstants.ANDROID_DEVICE);

                } else if (rqLogin.mobileVersion.toUpperCase().contains("IPHONE")) {
                    mLog.debug("iphone device");
                    aSession.setDeviceType(AIOConstants.IPHONE_DEVICE);

                } else {
                    aSession.setDeviceType(AIOConstants.MOBILE_DEVICE);
                }

            }

            resLogin.isMobile = true;

            user = userDb.faceLogin(rqLogin.faceId, rqLogin.partnerId, rqLogin.refCode, rqLogin.mobileVersion);

            if (user == null) // non-existed user
            {
                String upperUserName = "facebook" + rqLogin.faceId.toUpperCase();
                if (aSession.getRetryLogin(upperUserName) > AIOConstants.MAX_RETRY_LOGIN) {
                    throw new LoginException("Không thể đăng nhập");
                }
                aSession.setRetryLogin(upperUserName);
                mLog.warn("***Khong the dang nhap voi faceId " + rqLogin.faceId);
                throw new Exception("Không thể đăng nhập");

            } else {
                mLog.warn("Check is logined" + user.isOnline);
                ISession temp = aSession.getManager().findSession(user.mUid);

                if (temp != null) {
                    mLog.warn("Session value " + temp.getUserName());
                    mLog.warn("Session value " + temp.realDead());
                    mLog.warn("Session value " + System.currentTimeMillis());
                    mLog.warn("Session value " + temp.getLastAccessTime().getTime());
                } else {
                    mLog.warn("Session value null");
                }

                if (temp != null && (temp.getLoginName()!= null && !temp.getLoginName().equals("")) && !temp.realDead() && !temp.isExpired() && temp.isLoggedIn()) {
                    mLog.warn("[UserName] is logged " + user.loginName);
                    temp.setLastAccessTime(new Date());
                    throw new Exception("Tài khoản này đã đăng nhập!");
                } else {
                    //TODO: kick out current session
                    if (temp != null) {
                        try {
                            if (temp.getRoom() != null) {
                                temp.cancelTable();
                            }
                        } catch (Exception ex) {
                            mLog.error("close idle connection");
                        }
                        temp.sessionClosed();
                    }

                    user.isLogin = true;

                    CacheUserInfo cache = new CacheUserInfo();
                    cache.updateCacheUserInfo(user);
                }

                aSession.setUID(user.mUid);

                resLogin.setSuccess(ResponseCode.SUCCESS, user.mUid, user.money, user.lastLogin);

                int partnerId = user.partnerId;
                resLogin.chargingInfo = InfoDB.getLstChargings(partnerId);
                resLogin.usrEntity = user;

                aSession.setUserName(user.mUsername);
                aSession.setLoggedIn(true);
                aSession.setUserEntity(user);

                aSession.setLoginName(user.loginName);
                aSession.setCMND(user.cmnd);
                aSession.setXEPhoneNumber(user.xePhoneNumber);

                CommonQueue queue = new CommonQueue();

                QueueEntity entity = new QueueEntity(aSession, resLogin);
                queue.insertQueue(entity);
            }

            rtn = PROCESS_OK;
        } catch (LoginException ex) {
            resLogin = null;
            aSession.setLoggedIn(false);
            aSession.setCommit(false);

            try {
                userDb.logout(aSession.getUID(), "");
            } catch (SQLException ex1) {
                mLog.error(ex.getMessage(), ex1);
            }

            rtn = PROCESS_OK;
            mLog.warn(ex.getMessage());

        } catch (BusinessException ex) {
            resLogin.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aSession.setLoggedIn(false);
            aSession.setCommit(false);

            try {
                userDb.logout(aSession.getUID(), "");
            } catch (SQLException ex1) {
                mLog.error(ex.getMessage(), ex1);
            }
            rtn = PROCESS_OK;

        } catch (Exception ex) {
            resLogin.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aSession.setLoggedIn(false);
            aSession.setCommit(false);
            rtn = PROCESS_OK;
        } finally {
            if ((resLogin != null)) {
                aResPkg.addMessage(resLogin);
            }
        }

        return rtn;
    }

}
