/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.protocol.messages.NewRequest;
import com.tv.xeeng.base.protocol.messages.NewResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.binh.data.BinhPlayer;
import com.tv.xeeng.game.binh.data.BinhTable;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newbacay.data.NewBaCayPlayer;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.newpika.data.NewPikaPlayer;
import com.tv.xeeng.game.newpika.data.NewPikaTable;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.poker.data.PokerPlayer;
import com.tv.xeeng.game.poker.data.PokerTable;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.XEGameConstants;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

/**
 * @author tuanda
 * @modified thangtd - 25/02/2014
 */
public class NewBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(NewBusiness.class);
    private static final int TIMES = 4;
    private static final String MSG_NOT_ENOUGH_MONEY = "Bạn không đủ tiền để vào bàn. Số tiền bạn có nhỏ hơn 4 lần tiền bàn";
    private static final String MSG_NOT_ENOUGH_MONEY_TO_CREATE = "Bạn không đủ tiền để tạo bàn. Số tiền bạn có nhỏ hơn 4 lần tiền bàn";

    private String getRandomRoomName() {
        String[] roomNames = {
            "Đại gia thì vào",
            "Không thử sao biết",
            "Cao thủ vào đây chiến hết",
            "A đù lại thắng",
            "Làm giàu không khó",
            "Không dành cho gà",
            "Chơi tới bến",
            "Đấu với Thần Bài",
            "Buồn phiền vì nhiều tiền",
            "Tiền hết tình tan",
            "Tiền không thiếu nhưng nhiều thì không có"};

        return roomNames[(int) Math.floor(Math.random() * roomNames.length)];
    }

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {

        mLog.debug("[CREATE NEW ROOM]: Catch");

        MessageFactory msgFactory = aSession.getMessageFactory();
        NewResponse resMatchNew = (NewResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        resMatchNew.session = aSession;

        try {
            NewRequest rqMatchNew = (NewRequest) aReqMsg;

            if (checkInvalidName(rqMatchNew.roomName)) {
                throw new BusinessException("Tên bàn không hợp lệ.");
            }

            aSession.setChatRoom(0);

            if (rqMatchNew.uid == 0) {
                rqMatchNew.uid = aSession.getUID();
            }
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity user = cacheUser.getUserInfo(rqMatchNew.uid);

            resMatchNew.setCash(user.money);
            long moneyBet = rqMatchNew.moneyBet;
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            int phongId = rqMatchNew.phongID;

            int index = rqMatchNew.tableIndex;
            RoomDB roomDB = new RoomDB();
            NRoomEntity roomEntity = roomDB.getRoomEntity(zoneID, phongId);
            if (roomEntity == null) {
                phongId = aSession.getPhongID();
                roomEntity = roomDB.getRoomEntity(zoneID, phongId);
                if (roomEntity == null) {
                    throw new BusinessException("Bị lỗi bạn thử cập nhật lại xem sao");
                }
            }

            if (moneyBet < roomEntity.getMinCash()) {
                moneyBet = roomEntity.getMinCash();
            }
            // moneyBet = resMatchNew.minBet >roomEntity.getMinCash()?
            // resMatchNew.minBet:roomEntity.getMinCash();
            rqMatchNew.moneyBet = moneyBet;
            Phong phong = zone.getPhong(phongId);
            // long matchID = zone.isCreatable(index, phongId);
            long matchID = phong.isCreableTable(index);
            if (matchID != 0) {

                IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_JOIN);
                JoinRequest rqMatchJoin = (JoinRequest) msgFactory.getRequestMessage(MessagesID.MATCH_JOIN);
                resMatchNew = null;// don't want to send to client if

                try {
                    rqMatchJoin.mMatchId = matchID;
                    rqMatchJoin.roomID = phongId;
                    rqMatchJoin.uid = rqMatchNew.uid;
                    rqMatchJoin.zone_id = zoneID;
                    business.handleMessage(aSession, rqMatchJoin, aResPkg);
                    return 1;
                } catch (ServerException se) {
                    se.printStackTrace();
                }

            } else if (user != null) {

                aSession.setRoomID(phongId);

                if (rqMatchNew.size == 0) {
                    rqMatchNew.size = 4;
                }

                /* nếu không đặt tên bàn thì tạo ngẫu nhiên*/
                if (rqMatchNew.roomName == null || rqMatchNew.roomName.length() == 0) {
                    rqMatchNew.roomName = getRandomRoomName();
                }

                switch (zoneID) {

                    case ZoneID.PHOM: {
                        if ((user.money < TIMES * moneyBet)) {
                            resMatchNew.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                        } else {
                            rqMatchNew.isTai = true;
                            rqMatchNew.isKhan = true;
//                            rqMatchNew.isTai = true;
                            //rqMatchNew.roomName = "[Phỏm] " + XEGameConstants.getRandomName();

                            Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                            newRoom.setPhongID(rqMatchNew.phongID);
                            newRoom.setIndex(rqMatchNew.tableIndex);
                            newRoom.join(aSession);
                            newRoom.setZoneID(zoneID);
                            newRoom.setPassword(rqMatchNew.password);
                            newRoom.setName(rqMatchNew.roomName);
                            newRoom.setPlayerSize(rqMatchNew.size);
                            newRoom.setOwnerName(user.mUsername);
                            // mLog.debug("RoomName:" + rqMatchNew.roomName);
                            // mLog.debug("Size:" + rqMatchNew.zise);
                            PhomPlayer player = new PhomPlayer(user.mUid);
                            player.setCash(user.money);
                            player.avatarID = user.avatarID;
                            player.level = user.level;
                            player.username = user.mUsername;
                            player.currentSession = aSession;

                            player.setReady(true);
                            if (rqMatchNew.moneyBet > 0) {
                                moneyBet = rqMatchNew.moneyBet;
                            }

                            PhomTable table = new PhomTable(player, rqMatchNew.roomName, moneyBet, newRoom);
                            table.setTableIndex(rqMatchNew.tableIndex);
                            // index = rqMatchNew.tableIndex;
                            table.setPhongID(rqMatchNew.phongID);
                            table.setAnCayMatTien(rqMatchNew.isAn);
                            table.setTai(rqMatchNew.isTai);
                            table.setUKhan(rqMatchNew.isKhan);
                            table.setOwnerSession(aSession);
//						table.testCode = rqMatchNew.testCode;
                            // newRoom.setRoomId(matchIDAuto);
                            table.setMatchID(newRoom.getRoomId());
                            player.currentMatchID = newRoom.getRoomId();
                            table.name = rqMatchNew.roomName;
                            table.setRoom(newRoom);
                            aSession.setRoom(newRoom);
//                            DatabaseDriver.logUserMatch(player.id, newRoom.getRoomId(), "ban la chu room", table.firstCashBet, false, newRoom.getRoomId());
                            table.setMaximumPlayer(4);
                            newRoom.setAttachmentData(table);
                            resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), player.id, moneyBet, rqMatchNew.size);
                            resMatchNew.session = aSession;
                            MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                            CacheMatch.add(entity);
                        }
                        break;
                    }
                    case ZoneID.TIENLEN: {
                        if ((user.money < TIMES * moneyBet)) {
                            resMatchNew.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                        } else {
//                            rqMatchNew.roomName = "[Tiến lên] " + XEGameConstants.getRandomName();
                            Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                            /*
                             * zone.createOrDeleteTable(rqMatchNew.phongID,
                             * newRoom.getRoomId(), rqMatchNew.tableIndex, true);
                             */
                            newRoom.setPhongID(rqMatchNew.phongID);
                            newRoom.setIndex(rqMatchNew.tableIndex);
                            newRoom.join(aSession);
                            newRoom.setZoneID(zoneID);
                            newRoom.setPassword(rqMatchNew.password);
                            newRoom.setName(rqMatchNew.roomName);
                            newRoom.setPlayerSize(rqMatchNew.size);
                            newRoom.setOwnerName(user.mUsername);
                            // mLog.debug("RoomName:" + rqMatchNew.roomName);
                            // mLog.debug("Size:" + rqMatchNew.size);
                            TienLenPlayer owner = new TienLenPlayer(user.mUid);
                            owner.setCash(user.money);
                            owner.avatarID = user.avatarID;
                            owner.level = user.level;
                            owner.username = user.mUsername;
                            owner.currentSession = aSession;
                            owner.moneyForBet = rqMatchNew.moneyBet;
                            owner.currentMatchID = newRoom.getRoomId();
                            owner.setReady(true);

                            TienLenTable table = new TienLenTable(owner, moneyBet, newRoom.getRoomId(), rqMatchNew.size);
                            table.name = rqMatchNew.roomName;
                            table.setOwnerSession(aSession);
                            table.setTableIndex(rqMatchNew.tableIndex);
                            table.setRoom(newRoom);
                            aSession.setRoom(newRoom);

                            // index = rqMatchNew.tableIndex;
                            table.setMaximumPlayer(4);
                            table.setPhongID(rqMatchNew.phongID);

                            newRoom.setAttachmentData(table);
                            resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);
                            resMatchNew.isHidePoker = table.isHidePoker();
                            resMatchNew.session = aSession;

                            MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                            CacheMatch.add(entity);
                        }
                        break;
                    }

                    case ZoneID.SAM: {
                        if ((user.money < TIMES * moneyBet)) {
                            resMatchNew.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                        } else {

                        }
                        
                        Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                        newRoom.setIndex(rqMatchNew.tableIndex);
                        newRoom.join(aSession);
                        newRoom.setPassword(rqMatchNew.password);
                        newRoom.setName(rqMatchNew.roomName);
                        newRoom.setPlayerSize(5);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(zoneID);
                        newRoom.setPhongID(phongId);

                        SamPlayer owner = new SamPlayer(user.mUid);
                        owner.setCash(user.money);
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);

                        SamTable table = new SamTable(owner, rqMatchNew.moneyBet, newRoom.getRoomId());
                        table.name = rqMatchNew.roomName;
                        table.setOwnerSession(aSession);
                        table.setTableIndex(rqMatchNew.tableIndex);
                        table.setRoom(newRoom);
                        // index = req.tableIndex;
                        table.setMaximumPlayer(5);
                        table.setPhongID(phongId);

                        owner.setTable(table);
                        newRoom.setAttachmentData(table);
                        aSession.setRoom(newRoom);

                        resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);
                        resMatchNew.isHidePoker = false;
                        resMatchNew.session = aSession;

                        //resp.isHidePoker = table.isHidePoker();
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                        break;
                    }

                    case ZoneID.NEW_BA_CAY: {
                        if ((user.money < TIMES * moneyBet)) {
                            throw new BusinessException(
                                    MSG_NOT_ENOUGH_MONEY_TO_CREATE);
                        }

//                        rqMatchNew.roomName = "[3Cây]" + XEGameConstants.getRandomName();
                        Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);

                        rqMatchNew.size = 6;
                        
                        newRoom.setIndex(rqMatchNew.tableIndex);
                        newRoom.join(aSession);
                        newRoom.setPassword(rqMatchNew.password);
                        newRoom.setName(rqMatchNew.roomName);
                        newRoom.setPlayerSize(rqMatchNew.size);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(zoneID);

                        NewBaCayPlayer owner = new NewBaCayPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.isReady = true;

                        NewBaCayTable table = new NewBaCayTable(owner, moneyBet, newRoom.getRoomId());
                        table.name = rqMatchNew.roomName;
                        table.setOwnerSession(aSession);
                        table.setTableIndex(rqMatchNew.tableIndex);
                        table.setRoom(newRoom);
                        aSession.setRoom(newRoom);
                        table.setMaximumPlayer(6);
                        // index = rqMatchNew.tableIndex;
                        table.setPhongID(rqMatchNew.phongID);
                        newRoom.setAttachmentData(table);
                        resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);
                        resMatchNew.session = aSession;
                        
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                    }
                    break;

                    case ZoneID.POKER: {
                        // Kiểm tra điều kiện số tiền
                        if ((user.money < TIMES * moneyBet)) {
                            throw new BusinessException(
                                    MSG_NOT_ENOUGH_MONEY_TO_CREATE);
                        }

                        // Khởi tạo phòng chơi
//                    	rqMatchNew.roomName = "[Poker]" + XEGameConstants.getRandomName();
                        Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                        newRoom.setIndex(rqMatchNew.tableIndex);
                        newRoom.join(aSession);
                        newRoom.setPassword(rqMatchNew.password);
                        newRoom.setName(rqMatchNew.roomName);
                        newRoom.setPlayerSize(rqMatchNew.size);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(zoneID);
                        newRoom.setZone(zone);
                        aSession.setRoom(newRoom);

                        // Khởi tạo chủ phòng
                        PokerPlayer owner = new PokerPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.isReady = true;

                        // Khởi tạo bàn chơi
                        PokerTable table = new PokerTable(owner, moneyBet, newRoom.getRoomId());
                        table.name = rqMatchNew.roomName;
                        table.setOwnerSession(aSession);
                        table.setTableIndex(rqMatchNew.tableIndex);
                        table.setRoom(newRoom);
                        aSession.setRoom(newRoom);
                        table.setMaximumPlayer(9);
                        table.setPhongID(rqMatchNew.phongID);

                        newRoom.setAttachmentData(table);

                        resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);
                        resMatchNew.session = aSession;

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                        CacheMatch.add(entity);
                    }
                    break;

                    case ZoneID.BAU_CUA_TOM_CA: {
                        // kiem tra dieu kien so tien
                        if ((user.money < TIMES * moneyBet)) {
                            throw new BusinessException(
                                    MSG_NOT_ENOUGH_MONEY_TO_CREATE);
                        }

                        // Khoi tao phong
//                        rqMatchNew.roomName = "[Bầu cua]" + XEGameConstants.getRandomName();
                        Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                        newRoom.setIndex(rqMatchNew.tableIndex);
                        newRoom.join(aSession);

                        newRoom.setPassword(rqMatchNew.password);
                        newRoom.setName(rqMatchNew.roomName);
                        newRoom.setPlayerSize(rqMatchNew.size);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(zoneID);
                        aSession.setRoom(newRoom);

                        // Khoi tao player	
                        BauCuaTomCaPlayer owner = new BauCuaTomCaPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.isReady = true;

                        // tao ban choi bau cua gan voi phong
                        BauCuaTomCaTable table = new BauCuaTomCaTable(owner, moneyBet, newRoom.getRoomId());
                        table.name = rqMatchNew.roomName;
                        table.setOwnerSession(aSession);
                        table.setTableIndex(rqMatchNew.tableIndex);
                        table.setRoom(newRoom);
                        rqMatchNew.size = 6;
                        table.setMaximumPlayer(6);
                        // index = rqMatchNew.tableIndex;
                        table.setPhongID(rqMatchNew.phongID);
                        newRoom.setAttachmentData(table);

                        resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);
                        resMatchNew.session = aSession;
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                    }
                    break;

                    case ZoneID.PIKACHU: {
                        if ((user.money < 2 * moneyBet)) {
                            throw new BusinessException(
                                    MSG_NOT_ENOUGH_MONEY_TO_CREATE);
                        }
                        Room newRoom = zone.createRoom(rqMatchNew.roomName, rqMatchNew.uid, phongId);
                        newRoom.setIndex(rqMatchNew.tableIndex);
                        newRoom.join(aSession);
                        newRoom.setPassword(rqMatchNew.password);
                        newRoom.setName(rqMatchNew.roomName);
                        newRoom.setPlayerSize(rqMatchNew.size);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(zoneID);
                        newRoom.setZone(zone);
                        aSession.setRoom(newRoom);

                        PikachuPlayer owner = new PikachuPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.isReady = true;

                        PikachuTable table = new PikachuTable(owner, moneyBet, newRoom.getRoomId(), newRoom);
                        table.name = rqMatchNew.roomName;
                        table.setOwnerSession(aSession);
                        table.setTableIndex(rqMatchNew.tableIndex);
                        table.setMaximumPlayer(4);
                        // index = rqMatchNew.tableIndex;
                        table.setPhongID(rqMatchNew.phongID);
                        table.setRoom(newRoom);
                        newRoom.setAttachmentData(table);
                        resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), rqMatchNew.uid, table.firstCashBet, rqMatchNew.size);

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                        CacheMatch.add(entity);
                    }
                    break;

                    case ZoneID.AILATRIEUPHU: {

                        if ((user.money < TIMES * moneyBet)) {
                            throw new BusinessException(
                                    MSG_NOT_ENOUGH_MONEY_TO_CREATE);
                        }
//                        rqMatchNew.roomName = "[ALTP] " + XEGameConstants.getRandomName();
                        rqMatchNew.size = 4;

                        TrieuPhuPlayer owner = new TrieuPhuPlayer(user.mUid, moneyBet, false);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = aSession;
                        owner.moneyForBet = rqMatchNew.moneyBet;
                        //owner.currentMatchID = newRoom.getRoomId();
                        owner.isReady = true;

                        TrieuPhuTable table = new TrieuPhuTable(owner, moneyBet);
                        table.setMaximumPlayer(4);
                        table.newTable(user, rqMatchNew, aSession);
                        resMatchNew.setSuccess(ResponseCode.SUCCESS, table.matchID,
                                rqMatchNew.uid, moneyBet, rqMatchNew.size);

                        break;

                    }

                    case ZoneID.NEW_PIKA: {
//                        rqMatchNew.roomName = "[Pikachu] " + XEGameConstants.getRandomName();
                        rqMatchNew.size = 4;
                        NewPikaPlayer owner = new NewPikaPlayer(user.mUid);
                        // moneyBet /=2;
                        NewPikaTable table = new NewPikaTable(owner, moneyBet);
                        //owner.setTable(table);
                        table.setMaximumPlayer(4);
                        table.newTable(user, rqMatchNew, aSession);
                        table.init(rqMatchNew.advevntureMode, rqMatchNew.matrixSize, rqMatchNew.pikaLevel);
                        resMatchNew.setSuccess(ResponseCode.SUCCESS, table.matchID,
                                rqMatchNew.uid, moneyBet, rqMatchNew.size);
                        resMatchNew.session = aSession;
                        break;
                    }

                    case ZoneID.BINH: {
                        if ((user.money < TIMES * moneyBet)) {
                            resMatchNew.setFailure(ResponseCode.FAILURE,
                                    MSG_NOT_ENOUGH_MONEY);
                        } else {
                            rqMatchNew.isTai = true;
                            rqMatchNew.isKhan = true;
                            Room newRoom = zone.createRoom(rqMatchNew.roomName,
                                    rqMatchNew.uid, phongId);
                            newRoom.setPhongID(rqMatchNew.phongID);
                            newRoom.setIndex(rqMatchNew.tableIndex);
                            newRoom.join(aSession);
                            newRoom.setZoneID(zoneID);

                            newRoom.setPassword(rqMatchNew.password);
                            newRoom.setName(rqMatchNew.roomName);
                            newRoom.setPlayerSize(rqMatchNew.size);
                            newRoom.setOwnerName(user.mUsername);
                            // mLog.debug("RoomName:" + rqMatchNew.roomName);
                            // mLog.debug("Size:" + rqMatchNew.zise);
                            BinhPlayer player = new BinhPlayer(user.mUid);
                            player.setUserCash(user.money);
                            player.avatarID = user.avatarID;
                            player.level = user.level;
                            player.username = user.mUsername;
                            player.currentSession = aSession;

                            player.setReady(true);
                            if (rqMatchNew.moneyBet > 0) {
                                moneyBet = rqMatchNew.moneyBet;
                            }

                            BinhTable table = new BinhTable(player, rqMatchNew.roomName, moneyBet, newRoom);
                            table.setTableIndex(rqMatchNew.tableIndex);
                            // index = rqMatchNew.tableIndex;
                            table.setPhongID(rqMatchNew.phongID);
                            table.setOwnerSession(aSession);
                            table.setMatchID(newRoom.getRoomId());
                            player.currentMatchID = newRoom.getRoomId();
                            table.name = rqMatchNew.roomName;
                            table.setRoom(newRoom);
                            aSession.setRoom(newRoom);
                            table.setMaximumPlayer(4);
                            newRoom.setAttachmentData(table);
                            resMatchNew.setSuccess(ResponseCode.SUCCESS, newRoom.getRoomId(), player.id, moneyBet, rqMatchNew.size);
                            resMatchNew.session = aSession;
                            MatchEntity entity = new MatchEntity(newRoom.getRoomId(), zoneID, newRoom, phongId);
                            CacheMatch.add(entity);
                        }
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (BusinessException be) {
            resMatchNew.setFailure(ResponseCode.FAILURE, be.getMessage());
            mLog.error("Process message " + aReqMsg.getID() + " error.", be);
        } catch (Throwable t) {
            resMatchNew.setFailure(ResponseCode.FAILURE,
                    "Bị lỗi " + t.toString());
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resMatchNew != null)) {
                aResPkg.addMessage(resMatchNew);
            }
        }
        return 1;
    }

    private void correctWrongTable(ISession aSession, MessageFactory msgFactory) {
        try {
            if (aSession.getRoom() != null && aSession.getRoom().getAttactmentData() != null) {

                IResponsePackage responsePkg = aSession.getDirectMessages();
                IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);

                CancelRequest cancelRq = (CancelRequest) msgFactory
                        .getRequestMessage(MessagesID.MATCH_CANCEL);
                cancelRq.mMatchId = aSession.getRoom().getRoomId();
                cancelRq.isSendMe = false;
                business.handleMessage(aSession, cancelRq, responsePkg);

            }
        } catch (Throwable ex) {
            if (ex != null) {
                mLog.error(ex.getMessage(), ex);
            }
        }

    }

    private boolean checkInvalidName(String name) {
        if (name == null) {
            return false;
        }

        for (String badWord : XEGameConstants.BLACKLIST_WORDS) {
            if (name.contains(badWord)) {
                return true;
            }
        }

        return false;
    }

}
