package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.BlahBlahUtil;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.data.VMGQueue;
import com.tv.xeeng.base.protocol.messages.LoginRequest;
import com.tv.xeeng.base.protocol.messages.LoginResponse;
import com.tv.xeeng.base.protocol.messages.SendAdvResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.databaseDriven.LogDB;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Zone;
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

public class LoginBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LoginBusiness.class);

    private void addAno(ISession aSession, String ano, MessageFactory msgFactory) {
        CommonQueue queue = new CommonQueue();

        //it 's new device send to client
        SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);

        advRes.session = aSession;

        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(1)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(ano);

        advRes.setSuccess(sb.toString());
        QueueEntity entity = new QueueEntity(aSession, advRes);
        queue.insertQueue(entity);
    }

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
//        mLog.debug("[LOGIN]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        LoginResponse resLogin = (LoginResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        LoginRequest rqLogin = (LoginRequest) aReqMsg;
        String device = rqLogin.mobileVersion;

//        InfoDB infoDb = new InfoDB();
        UserDB userDb = new UserDB();
        try {
            UserEntity user;
            aSession.setByteProtocol(rqLogin.protocol);
            resLogin.session = aSession;
            resLogin.numberOnline = Server.numberOnline;

            if (rqLogin.mobileVersion.length() > 0) {
                mLog.debug("[LOGIN]: Mobile Ver :  - " + rqLogin.mobileVersion);
                aSession.setMobile(rqLogin.mobileVersion);
                aSession.setMobileDevice(true);
                aSession.setMXHDevice(rqLogin.isMxh);
                resLogin.isMxh = rqLogin.isMxh;
                aSession.setScreenSize(rqLogin.screen);
                VersionEntity latestVersion = InfoDB.getLatestVersion(rqLogin.partnerId);

                if (rqLogin.device != null) {
                    if (rqLogin.device.toUpperCase().contains("ANDROID")) {
                        mLog.debug("android device");
                        aSession.setDeviceType(AIOConstants.ANDROID_DEVICE);
                    } else if (rqLogin.device.toUpperCase().contains("IPHONE") || rqLogin.device.toUpperCase().contains("IPOD") || rqLogin.device.toUpperCase().contains("IPAD")) {
                        mLog.debug("iphone device");
                        aSession.setDeviceType(AIOConstants.IPHONE_DEVICE);
                    } else {
                        aSession.setDeviceType(AIOConstants.MOBILE_DEVICE);
                    }
                    device += rqLogin.device;
                }

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
                }
                resLogin.isMobile = true;
                user = userDb.login(rqLogin.loginName, rqLogin.mPassword, rqLogin.device, device, rqLogin.screen, aSession.getIP(), rqLogin.partnerId, resLogin.isMxh);

            } else {
                resLogin.isMobile = false;
                user = userDb.flashLogin(rqLogin.loginName, rqLogin.mPassword);//DatabaseDriver.getUserInfo(username);
            }

            if (user == null) // non-existed user
            {
                if (rqLogin.partnerId == AIOConstants.VMG_PARTNER_ID) {
                    VMGQueue vmgQueue = new VMGQueue();
                    QueueUserEntity entity = new QueueUserEntity(rqLogin, aSession, resLogin);
                    vmgQueue.insertUser(entity);
                    return PROCESS_FAILURE; //continue process in Queue
                } else {
                    String upperLoginName = rqLogin.loginName.toUpperCase();
                    if (aSession.getRetryLogin(upperLoginName) > AIOConstants.MAX_RETRY_LOGIN) {
                        throw new LoginException("Brute password");
                    }
                    aSession.setRetryLogin(upperLoginName);
                    mLog.warn("***Không thể đăng nhập với tài khoản " + rqLogin.loginName + " password: " + rqLogin.mPassword);
                    throw new Exception("Bạn sai mật khẩu, tên hoặc chưa đăng ký!!");
                }
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
//                if (temp != null && (temp.getLoginName() != null && !temp.getLoginName().equals("")) && !temp.realDead() && !temp.isExpired()) {
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
                                if (curZoneID > 0 && curMatchID > 0) {
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

                VersionEntity lVer = InfoDB.getLatestVersion(rqLogin.partnerId);
                long moneyUpdateLevel = 0;

                resLogin.setSuccess(ResponseCode.SUCCESS, user.mUid,
                        user.money, user.avatarID, user.level, user.lastLogin, "", user.playsNumber, moneyUpdateLevel);
                resLogin.avatarVerion = "2.0.0";// user.avatarVersion;
                resLogin.cellPhone = user.cellPhone;

                if (lVer != null && !rqLogin.mobileVersion.equals(lVer.desc)) { // && aSession.getDeviceType() != AIOConstants.ANDROID_DEVICE
//                    if (rqLogin.mobileVersion.contains("3.4") || rqLogin.mobileVersion.contains("3.0") || rqLogin.mobileVersion.contains("3.2")) {
//                        resLogin.isNeedUpdate = true;
//                    } else {
//                        resLogin.isNeedUpdate = lVer.isNeedUpdate;
//                    }
                    resLogin.isNeedUpdate = lVer.isNeedUpdate;
                    resLogin.version = lVer;
                }
                //Login Event like GoldenHour
                try {
                    String resLEvent = userDb.loginEvent(user.mUid, rqLogin.mobileVersion);
                    mLog.debug("LoginEvent: " + resLEvent);
                    if (resLEvent.compareTo("-1") == 0) {
                        throw new Exception("Tài khoản không tồn tại!");
                    } else {
                        String[] ev = resLEvent.split("#");// Tien
                        if (ev.length == 2) {
                            user.money = Long.parseLong(ev[0]);
                            addAno(aSession, ev[1], msgFactory);
                        } else if (ev.length == 1) {
                            user.money = Long.parseLong(ev[0]);
                        } else {
                            throw new Exception("Tài khoản không tồn tại!");
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new Exception("Tài khoản không tồn tại!");
                }

                int partnerId;/*= rqLogin.partnerId;
                 if (user.partnerId == AIOConstants.MEDIA_MXH_ID || user.partnerId == AIOConstants.ME_MXH_ID) {
                 partnerId = user.partnerId;
                 }*/

                partnerId = user.partnerId;
                resLogin.chargingInfo = InfoDB.getLstChargings(partnerId); //TODO:

//                    if(rqLogin.protocol >2  && (resLogin.newVer == null || resLogin.newVer.length()==0))
                if (rqLogin.protocol > 2 || isSmartPhone(aSession.getDeviceType())) {
                    try {
                        if (aSession.isMXHDevice()) {
                            resLogin.usrEntity = user;

                        }

                        if (aSession.isMobileDevice() && !user.isActive) {
                            //int chargingSize = resLogin.chargingInfo.size();
//                                 System.out.println(aSession.getIP());
//                                 if (chargingSize > 3) {
                            StringBuilder sb = new StringBuilder();
                            ChargingInfo activeInfo;
                            InfoDB infoDb = new InfoDB();

                            activeInfo = infoDb.getActive(resLogin.chargingInfo, user.cellPhone);

                            if (activeInfo != null && activeInfo.isNeedActive && user.cellPhone == null) {
                                if ((user.partnerId == AIOConstants.LOTUS_PARTNER_ID && rqLogin.protocol > AIOConstants.PROTOCOL_MXH)
                                        || user.partnerId != AIOConstants.LOTUS_PARTNER_ID) {
                                    StringBuilder sbActive = new StringBuilder();
                                    String activeValue = (user.refCode != 0) ? activeInfo.value + " " + user.refCode : activeInfo.value;
                                    sbActive.append(epActiveInfo).append(AIOConstants.SEPERATOR_BYTE_1);
                                    sbActive.append(activeValue).append(" ").append(Long.toString(user.mUid)).append(" ").append(activeInfo.number);

                                    //sbActive.append(info.number).append(AIOConstants.SEPERATOR_BYTE_1);
                                    //sbActive.append(info.value);
                                    throw new BusinessException(sbActive.toString());
                                }

                            }

                            // Login lan dau khong hien thi thong bao active 
                            if (activeInfo != null && user.lastLogin != null) {
                                String activeValue = (user.refCode != 0) ? activeInfo.value + " " + user.refCode : activeInfo.value;
                                sb.append(activeValue).append(AIOConstants.SEPERATOR_BYTE_1);
                                sb.append(activeInfo.number).append(AIOConstants.SEPERATOR_BYTE_1);
                                sb.append(activeInfo.desc);
                                //mLog.debug("Desc:" + activeInfo.desc);
                            }

                            if (rqLogin.protocol > AIOConstants.PROTOCOL_MODIFY_MID || isSmartPhone(aSession.getDeviceType())) {
                                resLogin.active = sb.toString();
                            }
                        }

//                             }
                    } catch (BusinessException be) {
                        mLog.debug("BusinessException", be);
                        throw be;
                    } catch (Exception ex) {
                        mLog.error(ex.getMessage(), ex);
                    }
                }

                if (!resLogin.isMobile) {
                    //getAll Room for flash
                    if (rqLogin.zoneId > 0) {
                        aSession.setDeviceType(AIOConstants.FLASH_DEVICE);
                        RoomDB db = new RoomDB();
                        resLogin.lstRooms = db.getRooms(rqLogin.zoneId);

                        aSession.setCurrentZone(rqLogin.zoneId);
//                            mLog.debug("size " + resLogin.lstRooms.size());

                        if (resLogin.lstRooms != null && resLogin.lstRooms.size() > 0) {

                            Zone zone = aSession.findZone(rqLogin.zoneId);
                            int roomId = resLogin.lstRooms.get(0).getId();
                            Phong enterPhong = zone.getPhong(roomId);
                            enterPhong.enterPhong(aSession);
                            resLogin.lstTables = zone.dumpNewWaitingTables(roomId);
                        }
                    }
                }

                aSession.setUserName(user.mUsername);
                aSession.setLoggedIn(true);
                aSession.setUserEntity(user);

                aSession.setLoginName(user.loginName);
                aSession.setCMND(user.cmnd);
                aSession.setXEPhoneNumber(user.xePhoneNumber);

                // thống kê người chơi online
                Server.userOnlineList.put(user.mUid, true);

                if (rqLogin.protocol > 2 || isSmartPhone(aSession.getDeviceType())) {
                    CommonQueue queue = new CommonQueue();

                    QueueEntity entity = new QueueEntity(aSession, resLogin);
                    resLogin.deviceId = rqLogin.deviceId;
                    resLogin.mobileVersion = rqLogin.device + rqLogin.mobileVersion;
                    queue.insertQueue(entity);
                }
            }
            // 	return
            rtn = PROCESS_OK;
        } catch (LoginException ex) {
            resLogin = null;
//            resLogin.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aSession.setLoggedIn(false);
            aSession.setCommit(false);
            try {
                userDb.logout(aSession.getUID(), "");
            } catch (SQLException ex1) {
                mLog.error(ex.getMessage(), ex1);
            }

            rtn = PROCESS_OK;
            mLog.error(ex.getMessage(), ex);
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
            mLog.error(ex.getMessage(), ex);
//            mLog.warn("wrong version");
        } catch (Exception ex) {
            resLogin.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aSession.setLoggedIn(false);
            aSession.setCommit(false);
            rtn = PROCESS_OK;
            mLog.error(ex.getMessage() + " - " + rqLogin.loginName);
        } /* 
         catch (Throwable t) {
         resLogin.setFailure(ResponseCode.FAILURE, "Hiện tại không đăng nhập được. Bạn hãy thử lại xem!");
         aSession.setLoggedIn(false);
         aSession.setCommit(false);
         rtn = PROCESS_OK;
         mLog.error("Process message " + aReqMsg.getID() + " error.", t);
         mLog.warn("Hiện tại không đăng nhập được", t);

         } */ finally {
            if ((resLogin != null)) {
                aResPkg.addMessage(resLogin);
            }
        }

        return rtn;
    }

    private boolean isSmartPhone(int deviceType) {
        return (deviceType == AIOConstants.IPHONE_DEVICE || deviceType == AIOConstants.ANDROID_DEVICE);
    }

    String epActiveInfo = "Kích hoạt để hoàn thành đăng ký và nhận ngay 10K Gold.";
}
