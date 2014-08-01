package com.tv.xeeng.base.business;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.VMGQueue;
import com.tv.xeeng.base.protocol.messages.UseCardRequest;
import com.tv.xeeng.base.protocol.messages.UseCardResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.QueueUserEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class UseCardBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog = LoggerContext.getLoggerFactory().getLogger(UseCardBusiness.class);
    private static final String TXN_IN_PROGRESS = "Giao dịch đang được xử lý. Gold sẽ vào tài khoản nếu giao dịch thành công";
    private static final String WRONG_SERVICE = "Sai dịch vụ";
    private static final String WRONG_INPUT_SERVICE = "Dữ liệu nhập vào có ký tự đặc biệt";
    private static final String WRONG_INPUT_CARD_ID = "Bạn cần phải nhập serial";
    private static final String WRONG_INPUT_CARD_CODE = "Bạn cần phải nhập mã thẻ";

    private boolean containSpecialWord(String strChecker) {
        String WORD_DIGIT_PATTERN = "\\w{1,20}$";
        Pattern pattern = Pattern.compile(WORD_DIGIT_PATTERN);
        Matcher matcher = pattern.matcher(strChecker);

        return matcher.matches();
    }

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Use Card] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        UseCardResponse useCardRes = (UseCardResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        UseCardRequest useCardRq = (UseCardRequest) aReqMsg;
        try {
            try {
                //resize image
                useCardRes.mCode = ResponseCode.FAILURE;
                useCardRes.session = aSession;
                if (!containSpecialWord(useCardRq.cardCode) || !containSpecialWord(useCardRq.cardId)) {
                    throw new BusinessException(WRONG_INPUT_SERVICE);
                }

                if (useCardRq.cardCode == null || useCardRq.cardCode.equals("")) {
                    throw new BusinessException(WRONG_INPUT_CARD_CODE);
                }

                if ((useCardRq.cardId == null || useCardRq.cardId.equals("")) && !useCardRq.serviceId.equals("mobifone")) {
                    throw new BusinessException(WRONG_INPUT_CARD_ID);
                }

                if (useCardRq.serviceId.equals("viettel") || useCardRq.serviceId.equals("mobifone")
                        || useCardRq.serviceId.equals("vinaphone") || useCardRq.serviceId.equals("vcoin") 
                        || useCardRq.serviceId.equals("gate") || useCardRq.serviceId.equals("mobay")) {
                    useCardRes.message = TXN_IN_PROGRESS;
                    useCardRes.mCode = ResponseCode.SUCCESS;
                    CacheUserInfo cache = new CacheUserInfo();
                    UserEntity entity = cache.getUserInfo(aSession.getUID());
                    useCardRq.refCode = Integer.toString(entity.refCode);

                    VMGQueue vmgQueue = new VMGQueue();
                    QueueUserEntity qentity = new QueueUserEntity(useCardRq, aSession, useCardRes);
                    vmgQueue.insertUser(qentity);
                    useCardRes = null;
                } else {
                    useCardRes.message = WRONG_SERVICE;
                }
                return 1;
            } catch (BusinessException be) {
                useCardRes.message = be.getMessage();
            }
        } finally {
            if ((useCardRes != null)) {
                aResPkg.addMessage(useCardRes);
            }
        }
        return 1;
    }
}
