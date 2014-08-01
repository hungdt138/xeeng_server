/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LoginResponse;
import com.tv.xeeng.base.protocol.messages.SendAdvResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AdvertisingEntity;
import com.tv.xeeng.game.data.AlertUserEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.Utils;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.MessageFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class CommonQueue {

    // private static Queue queue;
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CommonQueue.class);

    private static ConcurrentHashMap<UUID, QueueEntity> queue = new ConcurrentHashMap<>();
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(CommonQueue.class);
    private static boolean isInProgress = false;
    private static final int ADV_POPUP = 1;
    private static final int DEVICE_ID_ADV_POPUP = 5;

    private static int count = 0;
    private static final long TRI_AN_MONEY = 3000;
    private static final long TRI_AN__GOOD_CUSTOMER_MONEY = 5000;

    private static final int TRI_AN_LOG_TYPE = 50;
    private static final long ACTIVATE_ADV_AFTER_LOGIN = 2000;

    private static Date today;
    private static Date dt3;

    static {
        init();
    }

    private static Date setDate(int hour) {
        Date result = new Date();
        result.setHours(hour);
        result.setMinutes(0);
        result.setSeconds(0);
        return result;
    }

    private static void init() {
        try {
            Calendar c = Calendar.getInstance();
            Date dtNow = new Date();
            c.setTime(dtNow);
            c.add(Calendar.DATE, -3);
            today = setDate(0);
            dt3 = c.getTime();
            c.add(Calendar.DATE, -1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CommonQueue() {

    }

    public String getAdv(UserEntity usrEntity) {
        int partnerId = usrEntity.partnerId;
        InfoDB db = new InfoDB();
        List<AdvertisingEntity> lstAdversts = db.getAdvertising();
        boolean onlyPartner = false;
        int size = lstAdversts.size();
        List<AdvertisingEntity> eventPartners = new ArrayList<AdvertisingEntity>();
        List<AdvertisingEntity> allPartners = new ArrayList<AdvertisingEntity>();

        for (int i = 0; i < size; i++) {
            AdvertisingEntity entity = lstAdversts.get(i);
            if (entity.getPartnerId() == 0) {
                allPartners.add(entity);
            }

            if (entity.getPartnerId() == partnerId) {
                eventPartners.add(entity);
                onlyPartner = true;
            }
        }
        List<AdvertisingEntity> retAdvs = null;
        if (onlyPartner) {
            retAdvs = eventPartners;
        } else {
            retAdvs = allPartners;
        }

        int advSize = retAdvs.size();
        if (advSize == 0) {
            return "";
        }

        if (count >= advSize) {
            count = 0;
        }

        return retAdvs.get(count).getContent();
    }

    public void insertQueue(QueueEntity entity) {
        try {
            UUID uuid = UUID.randomUUID();
            queue.put(uuid, entity);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void sendQueueMessage() {
        if (!isInProgress) {
            isInProgress = true;
            try {
                Enumeration<UUID> e = queue.keys();
                List<QueueEntity> lstNotActivated = new ArrayList<QueueEntity>();

                while (e.hasMoreElements()) {
                    try {
                        long currentTime = System.currentTimeMillis();
                        UUID key = e.nextElement();

                        QueueEntity queryEntity = queue.get(key);
                        queue.remove(key);
                        if (queryEntity == null) {
                            continue;
                        }

                        ISession aSession = queryEntity.getSession();

                        if (aSession == null || aSession.isExpired() || aSession.isClosed()) {
                            continue;
                        }

                        if (queryEntity.getResponse() instanceof SendAdvResponse) {
                            SendAdvResponse advRes = (SendAdvResponse) queryEntity.getResponse();
                            advRes.session = aSession;
                            if (currentTime < advRes.activateTime && advRes.activateTime > 0) {
                                // it's not enough time to activate
                                lstNotActivated.add(queryEntity);
                                continue;
                            }

                            CommonQueue commonQueue = new CommonQueue();
                            String active = ((SendAdvResponse) queryEntity.getResponse()).active;
                            if (queryEntity.isJustLogin()) {
                                if (active == null && commonQueue.getAdv(aSession.getUserEntity()).equals("")) {
                                    continue;
                                }

                                StringBuilder sb = new StringBuilder();
                                sb.append(AIOConstants.FIRST_LOGIN_ADVERTISING).append(AIOConstants.SEPERATOR_BYTE_1);
                                sb.append(commonQueue.getAdv(aSession.getUserEntity()));
                                sb.append(AIOConstants.SEPERATOR_BYTE_3);

                                if (active != null) {
                                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                                    sb.append(active);

                                }

                                ((SendAdvResponse) queryEntity.getResponse()).setSuccess(sb.toString());
                            }

                            aSession.write(queryEntity.getResponse());

                        } else if (queryEntity.getResponse() instanceof LoginResponse) {
                            // Audit this user

                            // send advertising first login
                            LoginResponse resLogin = (LoginResponse) queryEntity.getResponse();
                            MessageFactory msgFactory = aSession.getMessageFactory();

                            UserDB db = new UserDB();
                            long deviceId = db.auditUser(aSession.getUID(), resLogin.mobileVersion, "", aSession.getIP(), resLogin.deviceId);

                            if (resLogin.deviceId > -1 && resLogin.deviceId == 0) {
                                // it 's new device send to client
                                SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);

                                advRes.session = aSession;

                                StringBuilder sb = new StringBuilder();
                                // sb.append(Integer.toString(ADV_POPUP)).append(AIOConstants.SEPERATOR_BYTE_1);
                                sb.append(Integer.toString(DEVICE_ID_ADV_POPUP)).append(AIOConstants.SEPERATOR_BYTE_3);
                                sb.append(deviceId);

                                advRes.setSuccess(sb.toString());
                                aSession.write(advRes);

                            }

                            insertGuideTour(aSession);

                            SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);
                            QueueEntity entity = new QueueEntity(aSession, advRes);

                            if (aSession.isMobileDevice() && !aSession.isMXHDevice()) {
                                entity.setSendMessage(true);
                            }
                            entity.setJustLogin(true);

                            Date dtNow = new Date();

                            advRes.activateTime = dtNow.getTime() + ACTIVATE_ADV_AFTER_LOGIN;
                            insertQueue(entity);

                            alertUser(aSession);

                        }
                    } catch (ServerException ex) {
                        log.error(ex.getMessage(), ex);
                    } catch (Exception ex) {
                        try {
                            log.error(ex.getMessage(), ex);
                        } catch (Exception ex1) {

                        }
                    }

                }

                int notActivateSize = lstNotActivated.size();
                for (int i = 0; i < notActivateSize; i++) {
                    insertQueue(lstNotActivated.get(i));
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        isInProgress = false;

    }

    private void alertUser(ISession aSession) {
        try {
            UserDB db = new UserDB();
            List<AlertUserEntity> lstAlerts = db.getAlertUser(aSession.getUID());

            int size = lstAlerts.size();
            if (size > 0) {

                Date dtNow = new Date();
                long lastActivate = dtNow.getTime();

                MessageFactory msgFactory = aSession.getMessageFactory();
                CommonQueue queue = new CommonQueue();
                for (int i = 0; i < size; i++) {
                    AlertUserEntity alertEntity = lstAlerts.get(i);
                    SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);
                    QueueEntity entity = new QueueEntity(aSession, advRes);

                    advRes.session = aSession;

                    entity.setSendMessage(false);
                    StringBuilder sb = new StringBuilder();
                    sb.append(Integer.toString(ADV_POPUP)).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(alertEntity.getContent());

                    lastActivate = lastActivate + ACTIVATE_ADV_AFTER_LOGIN;
                    advRes.activateTime = lastActivate;
                    advRes.setSuccess(sb.toString());

                    queue.insertQueue(entity);
                }
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    private boolean checkPeriod(UserEntity userEntity, Date toDay, Date fromTime, Date toTime) {
        if (toDay.compareTo(fromTime) >= 0 && toDay.compareTo(toTime) <= 0) {
            if (userEntity.lastLogin == null || userEntity.lastLogin.compareTo(fromTime) < 0) {
                return true;
            }
        }

        return false;
    }

    private void insertGuideTour(ISession aSession) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);
        QueueEntity entity = new QueueEntity(aSession, advRes);

        advRes.session = aSession;

        entity.setSendMessage(false);
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(ADV_POPUP)).append(AIOConstants.SEPERATOR_BYTE_1);
        Date dtNow = new Date();

        advRes.activateTime = dtNow.getTime() + ACTIVATE_ADV_AFTER_LOGIN;

        boolean flagVip3 = false;
        boolean flagVip1 = false;
        UserEntity userEntity = aSession.getUserEntity();

        if (userEntity == null) {
            CacheUserInfo cache = new CacheUserInfo();
            userEntity = cache.getUserInfo(aSession.getUID());
        }
        boolean isSendMessage = false;

        today = setDate(0);

        if ((userEntity.lastLogin == null || today.compareTo(userEntity.lastLogin) > 0)) {

//            if (userEntity.vipId > 2 && (userEntity.lastLogin == null || today.compareTo(userEntity.lastLogin) > 0)) {
//                userEntity.lastLogin = dtNow;
//                try {
//                    UserDB db = new UserDB();
//                    long cash = db.bonusMoney(userEntity.mUid, TRI_AN__GOOD_CUSTOMER_MONEY, TRI_AN_LOG_TYPE);
//                    CacheUserInfo cache = new CacheUserInfo();
//                    userEntity.money = cash;
//                    flagVip3 = true;
//                    cache.updateCacheUserInfo(userEntity);
//                } catch (Exception ex) {
//                    mLog.error(ex.getMessage(), ex);
//                }
//            } else if (userEntity.vipId > 0 && (userEntity.lastLogin == null || dt3.compareTo(userEntity.lastLogin) > 0)) {
//                userEntity.lastLogin = dtNow;
//                try {
//                    UserDB db = new UserDB();
//                    long cash = db.bonusMoney(userEntity.mUid, TRI_AN_MONEY, TRI_AN_LOG_TYPE);
//                    CacheUserInfo cache = new CacheUserInfo();
//                    userEntity.money = cash;
//                    flagVip1 = false;
//                    cache.updateCacheUserInfo(userEntity);
//                } catch (Exception ex) {
//                    mLog.error(ex.getMessage(), ex);
//                }
//            }
//
//             //send message to client
//            if (flagVip3) {
//                sb.append("Để Tri ân khách hàng có nhiều đóng góp, hàng ngày chúng tôi tặng bạn 5.000 Gold miễn phí. Chúc bạn có những giây phút vui vẻ");
//                isSendMessage = true;
//            } else if (flagVip1) {
//                sb.append("Lâu rồi không thấy bạn ghé thăm. Để Tri ân khách hàng trung thành chúng tôi tặng bạn 3.000 Gold miễn phí. Chúc bạn có những giây phút vui vẻ");
//                isSendMessage = true;
//            }
            if (isSendMessage) {
                advRes.setSuccess(sb.toString());
                CommonQueue queue = new CommonQueue();
                queue.insertQueue(entity);
            }

        }
    }

    /*
     * Cap nhat quang cao moi
     */
    public void changeAdv() {
        try {
            mLog.debug("Reload ADV:");
            InfoDB.reloadAdv();
            mLog.debug("Reload superUser:");
            Utils.reloadSuperUser();
        } catch (Throwable e) {

        }
        count++;
    }

}
