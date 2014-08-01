package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.InviteRequest;
import com.tv.xeeng.base.protocol.messages.InviteResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.Vector;
import org.slf4j.Logger;

public class InviteBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(InviteBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        InviteResponse resInvite = (InviteResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resInvite.session = aSession;
        mLog.debug("[INVITE]: Catch");
        try {
            InviteRequest rqInvite = (InviteRequest) aReqMsg;
            long sourceID = aSession.getUID();
            long destID = rqInvite.destUid;
            long roomID = rqInvite.roomID;
            
            Room room = null;
            MatchEntity matchEntity =  CacheMatch.getMatch(roomID);;
            if(matchEntity != null)
            {
                room = matchEntity.getRoom();
                
            }
            
            if(room== null)
            {
                room = aSession.getRoom();
            }
            
            if(room == null || room.getAttactmentData().matchID != roomID)
            {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(roomID);
            }
            
            if (room == null)
            {
                mLog.error("OMG room is null : " + roomID);
                resInvite.setFailure(ResponseCode.FAILURE, "Bàn chơi của bạn đã bị hủy. Không mời được.");
                aSession.write(resInvite);
                return 1;
            }
            
            SimpleTable table = (SimpleTable) room.getAttactmentData();
            /*
             * switch (aSession.getCurrentZone()) { case ZoneID.BACAY: {
             */
            ISession buddySession = aSession.getManager().findSession(destID);
            
            if (buddySession != null) {
                
                int buddyNumInvite = buddySession.getNumInvite();
                if(buddySession.isRejectInvite())
                {
                    resInvite.setFailure(ResponseCode.FAILURE, buddySession.getUserName()+ " đã từ chối lời mời của bạn");
                    
                    aSession.write(resInvite);
                    return 1;
                }
                
                if(buddyNumInvite>2)
                {
                    resInvite.setFailure(ResponseCode.FAILURE, buddySession.getUserName() + " đã có nhiều lời mời rồi");
                    
                    aSession.write(resInvite);
                    return 1;
                }
                
                Vector<Room> joinedRoom = buddySession.getJoinedRooms();

                //if ( buddySession.getCurrentZone() > 0 && buddySession.getCurrentZone() != aSession.getCurrentZone()) {
                if ((buddySession.getCurrentZone() != aSession.getCurrentZone()) && (buddySession.getCurrentZone()>0)) {
                    resInvite.setFailure(ResponseCode.FAILURE, "Không mời được. Bạn chơi đang ở Game khác rồi.");
                    aSession.write(resInvite);
                    
                } else if (joinedRoom.size() > 0) {
                    resInvite.setFailure(ResponseCode.FAILURE, "Bạn ý đang chơi ở room khác mất rồi");
                    aSession.write(resInvite);
                } else {
                    // Get user information
//                    UserDB userDb = new UserDB();
                    CacheUserInfo cacheUser = new CacheUserInfo();
                    UserEntity newUser = cacheUser.getUserInfo(destID);
                    buddySession.setNumInvite(buddyNumInvite + 1);
                    
                    if (newUser != null) {
                        switch (aSession.getCurrentZone()) {
                            case ZoneID.PHOM:
                            case ZoneID.TIENLEN:
                            case ZoneID.NEW_BA_CAY:
                            case ZoneID.PIKACHU:
                            case ZoneID.BAU_CUA_TOM_CA:
                            case ZoneID.AILATRIEUPHU:
                                default:
                                
                                if (newUser.money < table.getMinBet()) {
                                    resInvite.setFailure(ResponseCode.FAILURE, "Người bạn mời không đủ tiền chơi bàn này");
                                    aSession.write(resInvite);
                                    return 1;
                                }
                                break;
                        }
                        //room.addWaitingSessionByID(buddySession);
                        resInvite.setSuccess(ResponseCode.SUCCESS, sourceID, roomID, room.getName(), aSession.getUserName(), table.getMinBet(),
                                table.getLevel());
                        resInvite.timeout = ZoneID.getTimeout(aSession.getCurrentZone());
                        resInvite.phongId = aSession.getPhongID();
                                                                      
                        resInvite.currentZone = aSession.getCurrentZone();
                        //System.out.println("Hello"+buddySession.realDead());
                        if (!buddySession.realDead())
                        {
                            resInvite.session = buddySession;
                            buddySession.write(resInvite);
                            buddySession.setReplyInvite(false);
                        }
                    } else {
                        resInvite.setFailure(ResponseCode.FAILURE, "Không thực hiện mời được!");
                        aSession.write(resInvite);
                    }
                }
            } else {
                resInvite.setFailure(ResponseCode.FAILURE, "Không thực hiện mời được!");
                aSession.write(resInvite);
            }
        } catch (Throwable t) {
            resInvite.setFailure(ResponseCode.FAILURE, "Không thực hiện mời được!");
            //aSession.setLoggedIn(false);
            aResPkg.addMessage(resInvite);
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        }
        return rtn;
    }
}
