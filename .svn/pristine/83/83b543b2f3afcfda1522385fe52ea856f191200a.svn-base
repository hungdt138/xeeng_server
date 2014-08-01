package com.tv.xeeng.protocol.json;

import com.tv.xeeng.base.bytebuffer.ByteBufferFactory;
import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.AbstractPackageProtocol;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IRequestPackage;
import com.tv.xeeng.protocol.IResponseMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.protocol.SimpleRequestPackage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public class PackageProtocolJSON extends AbstractPackageProtocol {

    private final Logger mLog;

    public PackageProtocolJSON() {
        this.mLog = LoggerContext.getLoggerFactory().getLogger(PackageProtocolJSON.class);
    }

    public void logLocal(ISession aSession, String s) {
        try {
            s = s.replace("com.tv.xeeng.protocol.messages.", "");
            Vector<Room> joinedRoom = aSession.getJoinedRooms();
            if (joinedRoom.size() > 0) {
                Room r = joinedRoom.firstElement();

                SimpleTable p = (SimpleTable) r.getAttactmentData();
                if (s.contains("receive")) {
                    p.getOutCodeSB().append(SimpleTable.NEW_LINE);
                }
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");
//                p.getOutCodeSB().append(format.format(new Date())).append(s).append(SimpleTable.NEW_LINE).append(SimpleTable.NEW_LINE);

            }

        } catch (Exception e) {
        }
    }

    @Override
    public IRequestPackage decode(ISession aSession, IByteBuffer aEncodedObj) throws ServerException {
        String reqData;
        try {
            reqData = aEncodedObj.getString();
            JSONObject jsonPkg = new JSONObject(reqData);

            IRequestPackage pkgRequest = new SimpleRequestPackage();

            aSession.receiveMessage();
            JSONArray requests;

            if (jsonPkg.has("requests")) {
                requests = jsonPkg.getJSONArray("requests");
            } else {
                requests = jsonPkg.getJSONArray("r");
            }

            int size = requests.length();

            for (int i = 0; i < size; ++i) {

                JSONObject jsonMsg = (JSONObject) requests.get(i);

                int msgId = jsonMsg.getInt("mid");
                this.mLog.debug("[Received]" + aSession.userInfo() + msgId + " : " + jsonMsg);

                IMessageProtocol msgProtocol = getMessageProtocol(msgId);

                MessageFactory msgFactor = aSession.getMessageFactory();
                IRequestMessage requestMsg = msgFactor.getRequestMessage(msgId);
                logLocal(aSession, "[Received]" + aSession.userInfo() + "[ msg : " + msgId + " : " + requestMsg + " ] : " + jsonMsg);

                boolean decodedResult = msgProtocol.decode(jsonMsg, requestMsg);

                requestMsg.setObject(jsonMsg);
                if (decodedResult) {
                    pkgRequest.addMessage(requestMsg);
                }
            }

            return pkgRequest;
        } catch (Exception e) {
            mLog.error("Decode error : ", e);
            throw new ServerException(e);
        }
    }

    @Override
    public IRequestPackage decodeNew(ISession aSession, IByteBuffer aEncodedObj) throws ServerException {
        String reqData;
        try {
            reqData = aEncodedObj.getString();
            JSONObject jsonPkg = new JSONObject(reqData);

            IRequestPackage pkgRequest = new SimpleRequestPackage();

            aSession.receiveMessage();
            JSONArray requests;

            requests = jsonPkg.getJSONArray("r");

            int size = requests.length();

            for (int i = 0; i < size; ++i) {
                JSONObject jsonMsg = (JSONObject) requests.get(i);
                String value = jsonMsg.getString("v");
                String[] arrV = value.split(AIOConstants.SEPERATOR_NEW_MID);

                int msgId = Integer.parseInt(arrV[0]);
                if (msgId != 2 && !aSession.isUploadAvatar()) {
                    this.mLog.debug("[Received]" + aSession.userInfo() + msgId + " : " + jsonMsg);
                } else {
                    this.mLog.debug("[Received]" + aSession.userInfo() + msgId + " : upload avatar ");
                }

                IMessageProtocol msgProtocol = getMessageProtocol(msgId);

                MessageFactory msgFactor = aSession.getMessageFactory();
                IRequestMessage requestMsg = msgFactor.getRequestMessage(msgId);
                logLocal(aSession, "[Received]" + aSession.userInfo() + "[ msg : " + msgId + " : " + requestMsg + " ] : " + jsonMsg);
                String v = "";
                if (arrV.length > 1) {
                    v = arrV[1];
                }
                jsonMsg.put("v", v);

                boolean decodedResult = msgProtocol.decode(jsonMsg, requestMsg);

                JSONObject objectPut = new JSONObject();
                objectPut.put("v", value);
                requestMsg.setObject(objectPut);
                if (decodedResult) {
                    pkgRequest.addMessage(requestMsg);
                }
            }

            return pkgRequest;
        } catch (Exception e) {
            e.printStackTrace();
            mLog.error("Decode error : ", e);
            throw new ServerException(e);
        }
    }

    @Override
    public ArrayList<JSONObject> encodeToJSON(ISession aSession, IResponsePackage aResPkg) throws ServerException {
        try {
            Vector pkgData = aResPkg.optAllMessages();
            ArrayList<JSONObject> responses = new ArrayList<>();
            int size = pkgData.size();
            for (int i = 0; i < size; ++i) {
                IResponseMessage resMsg = (IResponseMessage) pkgData.get(i);

                IMessageProtocol msgProtocol = getMessageProtocol(resMsg.getID());

                JSONObject jsonMsg = (JSONObject) msgProtocol.encode(resMsg);

                String debugInfo;

                debugInfo = "[Send] [id " + aSession.getUserName() + "][" + aSession.getUID() + "]" + jsonMsg;
                mLog.debug(debugInfo);
                if (jsonMsg != null) {

                    responses.add(jsonMsg);
                }
            }
            return responses;
        } catch (Throwable t) {
            mLog.error("Encode error : " + t);
            return null;
        }
    }

    @Override
    public IByteBuffer encode(ISession aSession, IResponsePackage aResPkg) throws ServerException {
        JSONObject jsonPkg;
        try {
            jsonPkg = new JSONObject();

            Vector pkgData = aResPkg.optAllMessages();

            JSONArray responses = new JSONArray();

            int size = pkgData.size();

            for (int i = 0; i < size; ++i) {
                IResponseMessage resMsg = (IResponseMessage) pkgData.get(i);

                IMessageProtocol msgProtocol = getMessageProtocol(resMsg.getID());

                JSONObject jsonMsg = (JSONObject) msgProtocol.encode(resMsg);

                String debugInfo;

                debugInfo = "[Send] [id " + aSession.getUserName() + "][" + aSession.getUID() + "]" + jsonMsg;
                int maxLength = debugInfo.length() - 1;
                //mLog.error(debugInfo);
                debugInfo = debugInfo.substring(0, maxLength > 500 ? 500 : maxLength);

                logLocal(aSession, debugInfo);
                mLog.debug(debugInfo);

                if (jsonMsg != null) {
                    responses.put(jsonMsg);
                }
            }

            jsonPkg.put("r", responses);

            String resData = jsonPkg.toString();

            String pkgFormat = aSession.getPackageFormat();

            int dataSize = resData.getBytes("utf-8").length + 2 + pkgFormat.getBytes("utf-8").length + 2;

            IByteBuffer encodingBuffer = ByteBufferFactory.allocate(dataSize + 4);

            encodingBuffer.putInt(dataSize);

            encodingBuffer.putString(pkgFormat);

            encodingBuffer.putString(resData);

            encodingBuffer.flip();

            return encodingBuffer;

        } catch (Throwable t) {
            mLog.error("Encode error : " + t);
            throw new ServerException(t);
        }
    }
    final static Charset ENCODING = StandardCharsets.UTF_8;

    void writeLargerTextFile(String aFileName, String aLine) throws IOException {
        Path path = Paths.get(aFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
            writer.write(aLine);
        }
    }
}
