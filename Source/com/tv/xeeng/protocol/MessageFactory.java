package com.tv.xeeng.protocol;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;

public class MessageFactory {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(MessageFactory.class);
    @SuppressWarnings("unused")
    private final String DEFAULT_MESSAGES_CONFIG = "conf/message-config.xml";
    @SuppressWarnings("unused")
    private final String BUSINESS_COMPONENT = "business";
    @SuppressWarnings("unused")
    private final String MESSAGE_COMPONENT = "protocol.messages";
    @SuppressWarnings("unused")
    private final String PACKAGE_COMPONENT = "protocol";
    @SuppressWarnings("unused")
    private final int MSG_TYPE_REQUEST = 1;
    @SuppressWarnings("unused")
    private final int MSG_TYPE_RESPONSE = 2;
    @SuppressWarnings("unused")
    private final int MSG_TYPE_ALL = 0;
    private Hashtable<Integer, IBusiness> mBusinesses;
    private Hashtable<Integer, IRequestMessage> mMessageRequests;
    private Hashtable<Integer, IResponseMessage> mMessageResponses;
    private Hashtable<String, IPackageProtocol> mPackageProtocols;
    private Vector<String> mUsedFormats;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MessageFactory()
            throws ServerException {
        this.mBusinesses = new Hashtable();
        this.mMessageRequests = new Hashtable();
        this.mMessageResponses = new Hashtable();
        this.mPackageProtocols = new Hashtable();
        this.mUsedFormats = new Vector();
    }

    @SuppressWarnings("rawtypes")
	public void initModeling() throws ServerException {
        File fConfig;
        try {
            fConfig = new File("conf/message-config.xml");
            this.mLog.info("[MF] From file = conf/message-config.xml");

            if (!(fConfig.exists())) {
                throw new IOException("File " + fConfig.getName() + " is not exist.");
            }
            if (!(fConfig.canRead())) {
                throw new IOException("File " + fConfig.getName() + " must be readable.");
            }

            DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuildFac.newDocumentBuilder();
            Document aDoc = docBuilder.parse(fConfig);

            Element root = aDoc.getDocumentElement();

            Element usedFormats = (Element) root.getElementsByTagName("usedformats").item(0);
            NodeList formatList = usedFormats.getElementsByTagName("format");
            int formatCount = formatList.getLength();

            this.mUsedFormats.clear();

            for (int iCount = 0; iCount < formatCount; ++iCount) {
                Element formatNode = (Element) formatList.item(iCount);
                String formatName = formatNode.getAttribute("name").toLowerCase();

                this.mUsedFormats.add(formatName);
            }

            NodeList pkgList = root.getElementsByTagName("package");
            int pkgCount = pkgList.getLength();

            this.mBusinesses.clear();
            this.mPackageProtocols.clear();

            int iCount = 0;
            while (true) {
                if (iCount >= pkgCount) {
                    break;
                }

                Element pkgNode = (Element) pkgList.item(iCount);
                String pkgPackage = pkgNode.getAttribute("package");
                String pkgName = pkgNode.getAttribute("name");
                String msgPkg = pkgNode.getAttribute("msgpkg");
                String pkgModule = pkgNode.getAttribute("pkgModule");

                for (Iterator i$ = this.mUsedFormats.iterator(); i$.hasNext();) {
                    String pkgFormat = (String) i$.next();

                    if (!(this.mPackageProtocols.containsKey(pkgFormat.toLowerCase()))) {
                        String pkgProtocolClass = "";
                        if ((pkgModule != null) && (!(pkgModule.trim().equals("")))) {
                            pkgProtocolClass = pkgPackage + "." + "protocol" + "." + pkgFormat.toLowerCase() + "." + pkgModule + "." + pkgName + pkgFormat.toUpperCase();
                        } else {
                            pkgProtocolClass = pkgPackage + "." + "protocol" + "." + pkgFormat.toLowerCase() + "." + pkgName + pkgFormat.toUpperCase();
                        }

                        IPackageProtocol pkgProtocol = (IPackageProtocol) Class.forName(pkgProtocolClass).newInstance();

                        this.mPackageProtocols.put(pkgFormat.toLowerCase(), pkgProtocol);
                    }

                }

                NodeList msgList = pkgNode.getElementsByTagName("message");
                int msgCount = msgList.getLength();
                for (int msgIdx = 0; msgIdx < msgCount; ++msgIdx) {
                    Element msgNode = (Element) msgList.item(msgIdx);

                    int msgId = Integer.parseInt(msgNode.getAttribute("mid"));

                    String msgName = msgNode.getAttribute("name");
                    String msgModule = null;
                    if (msgNode.hasAttribute("module")) {
                        msgModule = msgNode.getAttribute("module");
                    }

                    boolean needLoggedIn = true;
                    if (msgNode.hasAttribute("needloggedin")) {
                        needLoggedIn = Boolean.parseBoolean(msgNode.getAttribute("needloggedin"));
                    }

                    int msgType = 0;
                    if (msgNode.hasAttribute("msgtype")) {
                        msgType = Integer.parseInt(msgNode.getAttribute("msgtype"));
                        if ((msgType != 1) && (msgType != 2)) {
                            msgType = 0;
                        }

                    }

                    String requestClass = "";

                    String responseClass = "";

                    String businessClass = "";
                    
                    if ((msgModule != null) && (!(msgModule.trim().equals("")))) {
                        requestClass = msgPkg + "." + "protocol.messages" + "." + msgModule + "." + msgName + "Request";
                        responseClass = msgPkg + "." + "protocol.messages" + "." + msgModule + "." + msgName + "Response";
                        businessClass = msgPkg + "." + "business" + "." + msgModule + "." + msgName + "Business";
                    } else {
                        requestClass = msgPkg + "." + "protocol.messages" + "." + msgName + "Request";
                        responseClass = msgPkg + "." + "protocol.messages" + "." + msgName + "Response";
                        businessClass = msgPkg + "." + "business" + "." + msgName + "Business";
                    }

                    for (Iterator i$ = this.mUsedFormats.iterator(); i$.hasNext();) {
                        String pkgFormat = (String) i$.next();

                        String messageProtocolClass = "";
                        if ((msgModule != null) && (!(msgModule.trim().equals("")))) {
                            messageProtocolClass = msgPkg + "." + "protocol.messages" + "." + msgModule + "." + pkgFormat.toLowerCase() + "." + msgName + pkgFormat.toUpperCase();
                        } else {
                            messageProtocolClass = msgPkg + "." + "protocol.messages" + "." + pkgFormat.toLowerCase() + "." + msgName + pkgFormat.toUpperCase();
                        }

                        IMessageProtocol messageProtocol = (IMessageProtocol) Class.forName(messageProtocolClass).newInstance();

                        AbstractPackageProtocol pkgProtocol = (AbstractPackageProtocol) this.mPackageProtocols.get(pkgFormat.toLowerCase());
                        pkgProtocol.addMessageProtocol(msgId, messageProtocol);
                    }

                    if ((msgType == 1) || (msgType == 0)) {
                        AbstractRequestMessage requestMsg = (AbstractRequestMessage) (IRequestMessage) Class.forName(requestClass).newInstance();
                        requestMsg.setID(msgId);

                        requestMsg.setNeedLoggedIn(needLoggedIn);

                        int dbFlag = 1;
                        if (!(msgNode.hasAttribute("dbflag"))) {
                            dbFlag = 3;
                        } else {
                            dbFlag = Integer.parseInt(msgNode.getAttribute("dbflag"));
                            if ((dbFlag != 1) && (dbFlag != 2)) {
                                dbFlag = 3;
                            }
                        }

                        requestMsg.setDBFlag(dbFlag);

                        this.mMessageRequests.put(Integer.valueOf(msgId), requestMsg);
                        
                        IBusiness business = (IBusiness) Class.forName(businessClass).newInstance();
                        this.mBusinesses.put(Integer.valueOf(msgId), business);
                    }

                    if ((msgType == 2) || (msgType == 0)) {
                        AbstractResponseMessage responseMessage = (AbstractResponseMessage) (IResponseMessage) Class.forName(responseClass).newInstance();
                        responseMessage.setID(msgId);
                        this.mMessageResponses.put(Integer.valueOf(msgId), responseMessage);
                    }
                }
                ++iCount;
            }

        } catch (Throwable t) {
            throw new ServerException(t);
        }
    }

    public IBusiness getBusiness(int aMsgId) {
        IBusiness business = (IBusiness) this.mBusinesses.get(Integer.valueOf(aMsgId));
        return business;
    }

    public IMessageProtocol getMessageProtocol(int aMsgId, String aFormat) {
        IPackageProtocol pkgProtocol = (IPackageProtocol) this.mPackageProtocols.get(aFormat);
        IMessageProtocol msgProtocol = pkgProtocol.getMessageProtocol(aMsgId);
        return msgProtocol;
    }

    public IRequestMessage getRequestMessage(int aMsgId) {
        AbstractRequestMessage rqMsg = (AbstractRequestMessage) this.mMessageRequests.get(Integer.valueOf(aMsgId));
        return rqMsg.clone();
    }

    public IResponseMessage getResponseMessage(int aMsgId) {
        AbstractResponseMessage resMsg = (AbstractResponseMessage) this.mMessageResponses.get(Integer.valueOf(aMsgId));
        return resMsg.clone();
    }

    public IPackageProtocol getPackageProtocol(String aFormat) {
        return ((IPackageProtocol) this.mPackageProtocols.get(aFormat));
    }
}
