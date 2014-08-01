package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XENewRequest;
import com.tv.xeeng.base.protocol.messages.XENewResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.binh.data.BinhPlayer;
import com.tv.xeeng.game.binh.data.BinhTable;
import com.tv.xeeng.game.data.MatchEntity;
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
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XENewBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XENewBusiness.class);

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
    public int handleMessage(ISession paramISession, IRequestMessage paramIRequestMessage, IResponsePackage paramIResponsePackage) throws ServerException {
        XENewRequest req = (XENewRequest) paramIRequestMessage;
        MessageFactory msgFactory = paramISession.getMessageFactory();
        XENewResponse resp = (XENewResponse) msgFactory.getResponseMessage(req.getID());

        Long uid = paramISession.getUID();

        resp.session = paramISession;
        Zone zone = paramISession.findZone(req.zoneID);
        if (zone != null) {
            paramISession.setCurrentZone(req.zoneID);
            try {
                paramISession.setChatRoom(0);

                CacheUserInfo cacheUser = new CacheUserInfo();
                UserEntity user = cacheUser.getUserInfo(uid.longValue());
                if (user == null) {
                    resp.mCode = ResponseCode.FAILURE;
                    resp.setErrorMsg("Session không hợp lệ (không thấy thông tin user");
//                    paramIResponsePackage.addMessage(resp);
                    return 1;
                }

                resp.setOwnerCash(user.money);

                Phong phong = zone.xeGetPhong(req.levelID);
                if (phong == null) {
                    resp.mCode = ResponseCode.FAILURE;
                    resp.setErrorMsg("Không tìm thấy phòng. LevelID không hợp lệ");
//                    paramIResponsePackage.addMessage(resp);
                    return 1;
                }

                int phongId = phong.id;

                int tableIndex = phong.avaiableTable();

                RoomDB roomDB = new RoomDB();
                NRoomEntity roomEntity = roomDB.getRoomEntity(req.zoneID, phongId);
                if (roomEntity == null) {
                    phongId = paramISession.getPhongID();
                    roomEntity = roomDB.getRoomEntity(req.zoneID, phongId);
                    if (roomEntity == null) {
                        throw new BusinessException("Bị lỗi bạn thử cập nhật lại xem sao");
                    }
                }

                long roomMinBet = roomEntity.getMinCash();
                req.moneyBet = (req.moneyBet < roomMinBet) ? roomMinBet : req.moneyBet;
                if ((user.money < 4 * req.moneyBet)) {
                    resp.mCode = ResponseCode.FAILURE;
                    resp.setErrorMsg("Bạn không đủ tiền để tạo bàn chơi. Số tiền bạn có nhỏ hơn 4 lần tiền bàn");
//                    paramIResponsePackage.addMessage(resp);
                    return 1;
                }
                // long matchID = phong.isCreableTable(tableIndex);

                paramISession.setRoomID(phongId);

                /* nếu không đặt tên bàn thì tạo ngẫu nhiên*/
                if (req.tableName == null || req.tableName.length() == 0) {
                    req.tableName = getRandomRoomName();
                }

//                if (checkInvalidName(req.tableName)) {
//                    throw new BusinessException("Tên bàn không hợp lệ");
//                }

                req.tableName = beautyTableName(req.tableName);

                switch (req.zoneID) {
                    case ZoneID.PHOM: {
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setPhongID(phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);
                        newRoom.setZoneID(req.zoneID);

                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        
                        PhomPlayer player = new PhomPlayer(user.mUid);
                        player.setCash(user.money);
                        player.avatarID = user.avatarID;
                        player.level = user.level;
                        player.username = user.mUsername;
                        player.currentSession = paramISession;

                        player.setReady(true);

                        PhomTable table = new PhomTable(player, req.tableName, req.moneyBet, newRoom);
                        table.setTableIndex(tableIndex);
                        // index = req.tableIndex;
                        table.setPhongID(phongId);
                        table.setAnCayMatTien(true);
                        table.setTai(true);
                        table.setUKhan(true);
                        table.setOwnerSession(paramISession);
			// table.testCode = req.testCode;
                        // newRoom.setRoomId(matchIDAuto);
                        table.setMatchID(newRoom.getRoomId());
                        player.currentMatchID = newRoom.getRoomId();
                        table.name = req.tableName;
                        table.setRoom(newRoom);
                        table.setMaximumPlayer(req.maxPlayers);
                        
			// DatabaseDriver.logUserMatch(player.id,
                        // newRoom.getRoomId(), "ban la chu room",
                        // table.firstCashBet, false, newRoom.getRoomId());
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);
                        
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(newRoom.getRoomId());
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);
                        
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);
                        
                        break;
                    }
                    
                    case ZoneID.TIENLEN: {
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);
                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(req.zoneID);
                        newRoom.setPhongID(phongId);

                        TienLenPlayer owner = new TienLenPlayer(user.mUid);
                        owner.setCash(user.money);
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);
                        
                        TienLenTable table = new TienLenTable(owner, req.moneyBet, newRoom.getRoomId(), req.maxPlayers);
                        table.name = req.tableName;
                        table.setOwnerSession(paramISession);
                        table.setTableIndex(tableIndex);
                        table.setRoom(newRoom);
                        // index = req.tableIndex;
                        table.setMaximumPlayer(req.maxPlayers);
                        table.setPhongID(phongId);
                        
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);
                        
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        //resp.isHidePoker = table.isHidePoker();
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                        break;
                    }
                    
                    case ZoneID.SAM: {
                        req.maxPlayers = (req.maxPlayers < 5) ? req.maxPlayers : 5;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);
                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(req.zoneID);
                        newRoom.setPhongID(phongId);
                        
                        SamPlayer owner = new SamPlayer(user.mUid);
                        owner.setCash(user.money);
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);
                        
                        SamTable table = new SamTable(owner, req.moneyBet, newRoom.getRoomId());
                        table.name = req.tableName;
                        table.setOwnerSession(paramISession);
                        table.setTableIndex(tableIndex);
                        table.setRoom(newRoom);
                        // index = req.tableIndex;
                        table.setMaximumPlayer(req.maxPlayers);
                        table.setPhongID(phongId);
                        
                        owner.setTable(table);
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);
                        
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        //resp.isHidePoker = table.isHidePoker();
                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);
                        
                        break;
                    }

                    case ZoneID.NEW_BA_CAY: {
                        req.maxPlayers = (req.maxPlayers < 6) ? req.maxPlayers : 6;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);
                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(req.zoneID);

                        NewBaCayPlayer owner = new NewBaCayPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);

                        NewBaCayTable table = new NewBaCayTable(owner, req.moneyBet, newRoom.getRoomId());
                        table.name = req.tableName;
                        table.matchID = newRoom.getRoomId();
                        table.setOwnerSession(paramISession);
                        table.setTableIndex(tableIndex);
                        table.setRoom(newRoom);
                        table.setMaximumPlayer(req.maxPlayers);
                        // index = req.tableIndex;
                        table.setPhongID(phongId);
                        
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);

                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                        break;
                    }

                    case ZoneID.BAU_CUA_TOM_CA: {
                        req.maxPlayers = (req.maxPlayers < 6) ? req.maxPlayers : 6;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);

                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(req.zoneID);

                        // Khoi tao player
                        BauCuaTomCaPlayer owner = new BauCuaTomCaPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);

                        // tao ban choi bau cua gan voi phong
                        BauCuaTomCaTable table = new BauCuaTomCaTable(owner, req.moneyBet, newRoom.getRoomId());
                        table.name = req.tableName;
                        table.setOwnerSession(paramISession);
                        table.setTableIndex(tableIndex);
                        table.setRoom(newRoom);
                        table.setMaximumPlayer(req.maxPlayers);
                        table.setPhongID(phongId);
                        table.matchID = newRoom.getRoomId();
                        
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);

                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                        break;
                    }

                    case ZoneID.PIKACHU: {
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);

                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);
                        newRoom.setZoneID(req.zoneID);
                        newRoom.setZone(zone);

                        PikachuPlayer owner = new PikachuPlayer(user.mUid);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.currentMatchID = newRoom.getRoomId();
                        owner.setReady(true);

                        PikachuTable table = new PikachuTable(owner, req.moneyBet, newRoom.getRoomId(), newRoom);
                        table.name = req.tableName;
                        table.setOwnerSession(paramISession);
                        table.setTableIndex(tableIndex);
                        table.setMaximumPlayer(req.maxPlayers);
                        // index = req.tableIndex;
                        table.setPhongID(phongId);
                        
                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);
                        
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);
                        break;
                    }

                    case ZoneID.AILATRIEUPHU: {
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        TrieuPhuPlayer owner = new TrieuPhuPlayer(user.mUid, req.moneyBet, false);
                        owner.cash = user.money;
                        owner.avatarID = user.avatarID;
                        owner.level = user.level;
                        owner.username = user.mUsername;
                        owner.currentSession = paramISession;
                        owner.moneyForBet = req.moneyBet;
                        owner.setReady(true);

                        TrieuPhuTable table = new TrieuPhuTable(owner, req.moneyBet);
                        table.xeNewTable(user, req, paramISession, phongId, tableIndex);
                        
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        break;
                    }

                    case ZoneID.NEW_PIKA: {
			// req.tableName = "[Pikachu] " + XEGameConstants.getRandomName();
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        NewPikaPlayer owner = new NewPikaPlayer(user.mUid);
                        // moneyBet /=2;
                        NewPikaTable table = new NewPikaTable(owner, req.moneyBet);
                        // owner.setTable(table);
                        table.setMaximumPlayer(req.maxPlayers);
                        table.xeNewTable(user, req, paramISession, phongId, tableIndex);
                        //table.init(req.advevntureMode, req.matrixSize, req.pikaLevel);
                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(table.matchID);
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);
                        break;
                    }

                    case ZoneID.BINH: {
                        req.maxPlayers = (req.maxPlayers < 4) ? req.maxPlayers : 4;
                        Room newRoom = zone.createRoom(req.tableName, uid.longValue(), phongId);
                        newRoom.setPhongID(phongId);
                        newRoom.setIndex(tableIndex);
                        newRoom.join(paramISession);
                        newRoom.setZoneID(req.zoneID);

                        newRoom.setPassword(null);
                        newRoom.setName(req.tableName);
                        newRoom.setPlayerSize(req.maxPlayers);
                        newRoom.setOwnerName(user.mUsername);

                        BinhPlayer player = new BinhPlayer(user.mUid);
                        player.setUserCash(user.money);
                        player.avatarID = user.avatarID;
                        player.level = user.level;
                        player.username = user.mUsername;
                        player.currentSession = paramISession;

                        player.setReady(true);

                        BinhTable table = new BinhTable(player, req.tableName, req.moneyBet, newRoom);
                        table.setTableIndex(tableIndex);
                        table.setPhongID(phongId);
                        table.setOwnerSession(paramISession);
                        table.setMatchID(newRoom.getRoomId());
                        player.currentMatchID = newRoom.getRoomId();
                        table.name = req.tableName;
                        table.setRoom(newRoom);
                        table.setMaximumPlayer(req.maxPlayers);

                        newRoom.setAttachmentData(table);
                        paramISession.setRoom(newRoom);

                        resp.mCode = ResponseCode.SUCCESS;
                        resp.setMatchID(newRoom.getRoomId());
                        resp.setMoneyBet(req.moneyBet);
                        resp.setOwnerCash(user.money);

                        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), req.zoneID, newRoom, phongId);
                        CacheMatch.add(entity);

                        break;
                    }

                    default:
                        break;
                }

            } catch (BusinessException be) {
                resp.mCode = ResponseCode.FAILURE;
                resp.setErrorMsg(be.getMessage());
                mLog.error("Process message " + req.getID() + " error.", be);
            } catch (Throwable t) {
                resp.mCode = ResponseCode.FAILURE;
                resp.setErrorMsg("Bị lỗi " + t.toString());
                mLog.error("Process message " + req.getID() + " error.", t);
            } finally {
                if ((resp != null)) {
                    paramIResponsePackage.addMessage(resp);
                }
            }
        } else {
            resp.mCode = ResponseCode.FAILURE;
            resp.setErrorMsg("zone ID không hợp lệ");
            paramIResponsePackage.addMessage(resp);
        }
        
        return 1;
    }

    private String beautyTableName(String name) {
        if (name == null) {
            return null;
        }

        for (String badWord : XEGameConstants.BLACKLIST_WORDS) {
            if (name.contains(badWord)) {
                name = name.replace(badWord, "***");
            }
        }

        return name;
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
