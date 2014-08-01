package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.BlahBlahUtil;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.GuestLoginRequest;
import com.tv.xeeng.base.protocol.messages.GuestLoginResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.databaseDriven.LogDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class GuestLoginBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GuestLoginBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;

//        mLog.debug("[Guest LOGIN]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GuestLoginResponse resLogin = (GuestLoginResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        GuestLoginRequest rqLogin = (GuestLoginRequest) aReqMsg;

        UserDB userDb = new UserDB();

        try {
            UserEntity user;
            aSession.setByteProtocol(AIOConstants.PROTOCOL_MXH);
            resLogin.session = aSession;
            resLogin.numberOnline = Server.numberOnline;

            aSession.setMobileDevice(true);
            if (rqLogin.mobileVersion != null) {
                if (rqLogin.mobileVersion.toUpperCase().contains("ANDROID")) {
                    mLog.debug("android device");
                    aSession.setDeviceType(AIOConstants.ANDROID_DEVICE);
                } else if (rqLogin.mobileVersion.toUpperCase().contains("IPHONE") || rqLogin.mobileVersion.toUpperCase().contains("IPOD") || rqLogin.mobileVersion.toUpperCase().contains("IPAD")) {
                    mLog.debug("iphone device");
                    aSession.setDeviceType(AIOConstants.IPHONE_DEVICE);
                } else {
                    aSession.setDeviceType(AIOConstants.MOBILE_DEVICE);
                }
            }

            resLogin.isMobile = true;
//            user = userDb.guestLogin(rqLogin.deviceUId, rqLogin.partnerId, rqLogin.refCode, rqLogin.mobileVersion);
            user = userDb.newGuestLogin(rqLogin.deviceUId, rqLogin.partnerId, rqLogin.refCode, rqLogin.mobileVersion, rqLogin.regTime);

            if (user == null) // non-existed user
            {
                if (aSession.getRetryLogin(rqLogin.deviceUId) > AIOConstants.MAX_RETRY_LOGIN) {
                    throw new LoginException("Không thể đăng nhập");
                }
                aSession.setRetryLogin(rqLogin.deviceUId);
                mLog.warn("***Khong the dang nhap voi deviceUid " + rqLogin.deviceUId);
                throw new Exception("Không thể đăng nhập");

            } else {
                // ghi log thiết bị [thanhnvt] {
                if (!BlahBlahUtil.hasEmptyString(rqLogin.getOsName(), rqLogin.getOsVersion(), rqLogin.getOsMAC())) {
                    LogDB.updateUserDevice(user.mUid, rqLogin.getOsName(), rqLogin.getOsVersion(), rqLogin.getOsMAC());
                }
                // } ghi log thiết bị [thanhnvt]

                if (user.getLockExpired() != null && user.getLockExpired().compareTo(Calendar.getInstance().getTime()) > 0) {
                    throw new Exception("Tài khoản của bạn hiện tại đang bị khóa. Vui lòng liên hệ đến các kênh hỗ trợ để được giải đáp!");
                }

                mLog.warn("Check user " + user.mUsername + " is logined: " + user.isOnline);
                ISession temp = aSession.getManager().findSession(user.mUid);

                if (temp != null) {
                    mLog.warn("----------");
                    mLog.warn("Session value login name: " + temp.getUserName());
                    mLog.warn("Session value real dead: " + temp.realDead());
                    mLog.warn("Session value current time: " + System.currentTimeMillis());
                    mLog.warn("Session value last access time: " + temp.getLastAccessTime().getTime());
                    mLog.warn("----------");
                } else {
                    mLog.warn("----------");
                    mLog.warn("Session value null: " + user.mUsername);
                    mLog.warn("----------");
                }

                if (temp != null && (temp.getLoginName() != null && !temp.getLoginName().equals("")) && !temp.realDead() && !temp.isExpiredNew() && temp.isLoggedIn()) {
//                if (temp != null && (temp.getLoginName() != null && !temp.getLoginName().equals("")) && !temp.realDead() && !temp.isExpired() && temp.isLoggedIn()) {
                    mLog.warn("[UserName] is logged " + user.mUsername);
                    temp.setLastAccessTime(new Date());
                    throw new Exception("Tài khoản này đã đăng nhập!");
                } else {
                    //TODO: kick out current session
//                    if (temp != null) {
//                        try {
//                            if (temp.getRoom() != null) {
//                                temp.cancelTable();
//                            }
//                        } catch (Exception ex) {
//                            mLog.error("close idle connection");
//                        }
//                        temp.sessionClosed();
//                    }

                    //TODO: force player to rejoin current room - ThangTD
                    if (temp != null) {
                        try {
                            if (temp.getRoom() != null) {
                                int curZoneID = temp.getRoom().getZoneID();
                                long curMatchID = temp.getRoom().getAttactmentData().getMatchID();
                                if (curZoneID > 0 &&curMatchID > 0) {
                                    resLogin.setLastMatch(curZoneID, curMatchID);
//                                    resLogin.setLastMatch(curMatchID);
                                    mLog.warn(user.mUsername + " need to finish the current game " + curMatchID);
                                }
                            }
                        } catch (Exception ex) {
                            mLog.error("close idle connection");
                        }
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

                VersionEntity latestVersion = InfoDB.getLatestVersion(rqLogin.partnerId);
                if (latestVersion != null && !rqLogin.mobileVersion.equals(latestVersion.desc)) {
//                if (latestVersion != null && !rqLogin.mobileVersion.equals(latestVersion.desc) && aSession.getDeviceType() == AIOConstants.IPHONE_DEVICE) { 
                    boolean flagNewVersion = false;
                    try {
                        String[] serverArr = latestVersion.desc.split("\\.");
                        String[] clientArr = rqLogin.mobileVersion.split("\\.");

                        int serverSize = serverArr.length;

                        for (int i = 0; i < serverSize; i++) {
                            int serverPart = Integer.parseInt(serverArr[i]);
                            int clientPart = Integer.parseInt(clientArr[i]);
                            if (clientPart < serverPart) {
                                flagNewVersion = true;
                                break;
                            } else if (clientPart > serverPart) {
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        flagNewVersion = true;
                    }

                    if (flagNewVersion) {
                        resLogin.isNeedUpdate = latestVersion.isNeedUpdate;

                        resLogin.newVer = latestVersion.desc;
                        resLogin.linkDown = latestVersion.link;
                    }

                    resLogin.version = latestVersion;
                }

                aSession.setUserName(user.mUsername);
                aSession.setLoggedIn(true);
                aSession.setUserEntity(user);

                CommonQueue queue = new CommonQueue();

                QueueEntity entity = new QueueEntity(aSession, resLogin);
                resLogin.deviceUId = rqLogin.deviceUId;
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
