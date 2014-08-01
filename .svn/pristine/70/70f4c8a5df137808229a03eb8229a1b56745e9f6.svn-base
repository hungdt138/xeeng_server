package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.FastPlayRequest;
import com.tv.xeeng.base.protocol.messages.FastPlayResponse;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.protocol.messages.NewRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class FastPlayBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(FastPlayBusiness.class);
    private static final String ALL_TABLES_BUSY = "Hiện tại không có bàn rỗi nào phù hợp với số tiền của bạn! Xin bạn thử lại sau ít phút";
    private static final String MAX_TWO_REQUEST = "Bạn thực hiện quá nhanh 2 yêu cầu tìm bàn. 2 yêu cầu tìm bàn cách nhau 10s!";
    private static final int MAX_BETWEEN_TWO_REQUEST = 10000;

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        //int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        FastPlayResponse resfP = (FastPlayResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        mLog.debug("[FAST PLAY]: Catch");
        try {
            // request message and its values
            FastPlayRequest rqfP = (FastPlayRequest) aReqMsg;
            // broadcast

            resfP.session = aSession;

            long currentTime = System.currentTimeMillis();
            if (currentTime - aSession.getLastFP() < MAX_BETWEEN_TWO_REQUEST && aSession.getLastFP() > 0) {
                throw new BusinessException(MAX_TWO_REQUEST);
            }

            int zoneID = aSession.getCurrentZone();
            if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ACTIVE_LOGIN) {
                zoneID = rqfP.zoneId;
                aSession.setCurrentZone(zoneID);
            }

            Zone zone = aSession.findZone(zoneID);
            Couple<Integer, Long> res = new Couple<>(0, 0l);
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity usrEntity = cacheUser.getUserInfo(aSession.getUID());
            if (usrEntity.money / 4 < 101) {
                throw new BusinessException(Messages.NOT_ENOUGH_MONEY);
            }

            try {
                res = zone.fastPlay(aSession.getLastFastMatch(), usrEntity.money / 4, rqfP.getLevelId());
            } catch (Exception e) {
                throw new BusinessException(ALL_TABLES_BUSY);

            }

            if (res == null) {
                //create new Table and send create new table to this user
                Phong phongAvailable = zone.phongAvailable(rqfP.getLevelId());

                IResponsePackage responsePkg = aSession.getDirectMessages();
                IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_NEW);

                NewRequest newRequest = (NewRequest) msgFactory.getRequestMessage(MessagesID.MATCH_NEW);
                newRequest.moneyBet = 100;
                newRequest.phongID = phongAvailable.id;
                newRequest.tableIndex = (int) phongAvailable.avaiableTable();

                if (newRequest.tableIndex == 0) {
                    throw new BusinessException(ALL_TABLES_BUSY);
                }

                aSession.setPhongID(newRequest.phongID);

                aSession.setChatRoom(0);

                business.handleMessage(aSession, newRequest, responsePkg);

                return 1;
                //throw new BusinessException(ALL_TABLES_BUSY);
            }
            // try {

            aSession.setLastFastMatch(res.e2);
            IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_JOIN);
            JoinRequest rqJoin = (JoinRequest) msgFactory.getRequestMessage(MessagesID.MATCH_JOIN);
            rqJoin.mMatchId = res.e2;
            rqJoin.zone_id = zoneID;
            //rqJoin.uid = aSession.getUID();
            //rqJoin.roomID = rqfP.roomID;
            business.handleMessage(aSession, rqJoin, aResPkg);
            /*
             * } catch (ServerException se) {
             * 
             * }
             */

            //rtn = PROCESS_OK;
        } catch (BusinessException be) {
            resfP.setFailure(ResponseCode.FAILURE,
                    be.getMessage());
            try {
                //System.out.println("Hello:" + resfP.mCode);
                aSession.write(resfP);
            } catch (ServerException ex) {
                mLog.error("fast playing ", ex);
            }

            return PROCESS_OK;
        } catch (Throwable t) {
            // response failure
            resfP.setFailure(ResponseCode.FAILURE, "Lỗi !");
            // aSession.setLoggedIn(false);
            //rtn = PROCESS_OK;
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resfP);
        }

        return 1;
    }
}
