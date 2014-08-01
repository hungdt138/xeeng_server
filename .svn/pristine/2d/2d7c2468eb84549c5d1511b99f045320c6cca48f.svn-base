package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.RegisterRequest;
import com.tv.xeeng.base.protocol.messages.RegisterResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AuditRegisterEntity;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.room.XEGameConstants;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RegisterBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(RegisterBusiness.class);
    private static ConcurrentHashMap<String, AuditRegisterEntity> auditRegister = new ConcurrentHashMap<String, AuditRegisterEntity>();

    private static final int MAX_TIME = 60000;
    private static final int MAX_USER = 20;

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) throws ServerException {

        int rtn = PROCESS_FAILURE;
        mLog.debug("[REGISTER] : Catch");

        MessageFactory msgFactory = aSession.getMessageFactory();

        RegisterResponse resRegister
                = (RegisterResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            RegisterRequest rqRegister = (RegisterRequest) aReqMsg;
            String username = rqRegister.mUsername;
            String password = rqRegister.mPassword;
            String phone = rqRegister.phone;
            String mail = rqRegister.mail;
            String key = rqRegister.key;
            int age = rqRegister.mAge;
            int sex = 0;
            int partnerId = rqRegister.partnerId;
            aSession.setByteProtocol(rqRegister.protocol);

            resRegister.session = aSession;

            if (rqRegister.mPassword == null || rqRegister.loginName == null || rqRegister.mUsername == null || rqRegister.mPassword.equals("")) {
                throw new SimpleException("Vui lòng điền đầy đủ thông tin");

            }
            if (rqRegister.loginName.length() < 6) {
                throw new SimpleException("Tài khoản phải lớn hơn 6 ký tự");
            }

            String badWord = checkInvalidName(rqRegister.loginName);
            if (badWord != null) {
                throw new SimpleException(String.format("Tên tài khoản chứa ký tự không hợp lệ: %s", badWord));
            }

            badWord = checkInvalidName(rqRegister.mUsername);
            if (badWord != null) {
                throw new SimpleException(String.format("Tên nhân vật chứa ký tự không hợp lệ: %s", badWord));
            }
            if (rqRegister.mPassword.length() < 6) {
                throw new SimpleException("Mật khẩu phải lớn hơn 6 ký tự");
            }
            long currentTime = System.currentTimeMillis();
            if (aSession.getLastRegister() > 0 && currentTime - aSession.getLastRegister() < MAX_TIME) {
                throw new SimpleException("Xin bạn thử đăng ký lại sau");
            }

            try {
                //check spam register
                if (rqRegister.clientSessionId != null && !rqRegister.clientSessionId.equals("")
                        && !rqRegister.clientSessionId.equals("0")) {
                    String clientSessionKey = aSession.getOnlyIP() + rqRegister.clientSessionId;
                    if (auditRegister.contains(clientSessionKey)) {
                        AuditRegisterEntity lastEntity = auditRegister.get(clientSessionKey);
                        if (currentTime - lastEntity.getLastDateTime() < MAX_TIME) {
                            throw new SimpleException("Xin bạn thử đăng ký lại sau");
                        }

                        if (lastEntity.getCount() > MAX_USER) {
                            if (currentTime - lastEntity.getLastDateTime() < MAX_TIME * 5) {
                                throw new SimpleException("Xin bạn thử đăng ký lại sau");
                            }
                        }

                        lastEntity.setCount(lastEntity.getCount() + 1);
                        lastEntity.setLastDateTime(currentTime);

                    } else {
                        AuditRegisterEntity lastEntity = new AuditRegisterEntity(1, currentTime);
                        auditRegister.put(clientSessionKey, lastEntity);
                    }

                }
            } catch (Exception ex) {

            }

            Pattern p = Pattern.compile("^[1-9]\\d{0,}+$");
            if (p.matcher(rqRegister.loginName).matches()) {
                throw new SimpleException("Tài khoản không được toàn chữ số(trừ bắt đầu bằng 0)");
            }

            String pattern = "^[a-zA-Z_0-9.@_,-]{3,100}$";

            if (!rqRegister.loginName.matches(pattern)) {
                throw new SimpleException("Tài khoản không được chứa ký tự đặc biệt");
            }

            if (rqRegister.isMale) {
                sex = 1;
            }

            UserDB userDb = new UserDB();
            long uid = -1;

            uid = userDb.registerUser(rqRegister.loginName, password, sex, phone, partnerId, rqRegister.gioiThieuUid, rqRegister.isMxh, rqRegister.refCode, rqRegister.registerTime, rqRegister.deviceUid, username, rqRegister.cmnd, rqRegister.xePhoneNumber);

            if (uid == -1) {
                resRegister.setFailure(ResponseCode.FAILURE, "Tài khoản đã có người đăng ký, bạn vui lòng đăng ký tài khoản khác!");
            } else {
                aSession.setLastRegister(System.currentTimeMillis());

                int rnd = (int) (Math.random() * 3);

                int money = rnd == 0 ? 30000
                        : (rnd == 1 ? 20000
                        : 10000);
                resRegister.setSuccess(ResponseCode.SUCCESS, uid, money, 1, 1);

                //for active
                try {
                    InfoDB db = new InfoDB();
                    List<ChargingInfo> partnerCharging = db.getPartnerChargings(rqRegister.partnerId, rqRegister.refCode);
                    int chargingSize = partnerCharging.size();
                    ChargingInfo activeInfo = null;
                    for (int i = 0; i < chargingSize; i++) {
                        ChargingInfo info = partnerCharging.get(i);
                        if (info.isActive) {
                            activeInfo = info;
                            break;
                        }
                    }

                    if (activeInfo != null && activeInfo.isNeedActive) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(activeInfo.value).append(" ").append(Long.toString(uid)).append(" ").append(activeInfo.number);
                        resRegister.values = sb.toString();
                    }

                } catch (Exception ex) {

                }

                mLog.debug("[REGISTER] : " + username + " Success");
            }

            rtn = PROCESS_OK;
        } catch (SimpleException se) {

            resRegister.setFailure(ResponseCode.FAILURE, se.msg);
            rtn = PROCESS_OK;
            mLog.debug("[REGISTER] : " + se.msg);
        } catch (Throwable t) {
            resRegister.setFailure(ResponseCode.FAILURE, "Dữ liệu bạn nhập không chính xác!");
            rtn = PROCESS_OK;
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resRegister != null) && (rtn == PROCESS_OK)) {
                aResPkg.addMessage(resRegister);
            }
        }
        return rtn;
    }

    private String checkInvalidName(String name) {
        if (name == null) {
            return null;
        }

        for (String badWord : XEGameConstants.BLACKLIST_WORDS) {
            if (name.contains(badWord)) {
                return badWord;
            }
        }

        return null;
    }
}
