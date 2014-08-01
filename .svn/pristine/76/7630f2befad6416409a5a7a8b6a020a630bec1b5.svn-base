package com.tv.xeeng.base.business;


import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GuiTangLinkRequest;
import com.tv.xeeng.base.protocol.messages.GuiTangLinkResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;







public class GuiTangLinkBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GuiTangLinkBusiness.class);
        
        

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		
		MessageFactory msgFactory = aSession.getMessageFactory();
		GuiTangLinkResponse resBoc = (GuiTangLinkResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
                    GuiTangLinkRequest rqLink = (GuiTangLinkRequest) aReqMsg;
                    if(aSession.isMobileDevice())
                    {
                        String pattern="^0\\d{9,10}$";
                        if(!rqLink.phoneNumber.matches(pattern)|| rqLink.phoneNumber.equals(""))
                        {
                            throw new BusinessException("Số điện thoại không hợp lệ");
                        }
                    }
                    
			resBoc.mCode = ResponseCode.SUCCESS;
                        StringBuilder sb = new StringBuilder();
                        int typeDevice = AIOConstants.FLASH_DEVICE;
                        if(aSession.isMobileDevice())
                            typeDevice = AIOConstants.MOBILE_DEVICE;
                        
                        Connection con = DBPoolConnection.getConnection();
                        long refGioithieuId = 0;
                        try
                        {
                            FriendDB db = new FriendDB(con);
                            if(aSession.isMobileDevice())
                            {
                                refGioithieuId = db.guiTang(rqLink.phoneNumber, aSession.getUID(), 
                                    typeDevice, aSession.getCurrentZone(), 
                                    aSession.getUserName(), aSession.getUserEntity().partnerId);
                            }
                            else
                            {
                            
                                rqLink.phoneNumber = rqLink.phoneNumber.replaceAll(";", ",");
                                String[] arr = rqLink.phoneNumber.split(",");
                                int emailSize = arr.length;
//                                List<String> diffEmail = new ArrayList<String>();

                                for(int i = 0; i< emailSize; i++)
                                {
                                    
                                    refGioithieuId = db.guiTang(arr[i], aSession.getUID(), 
                                        typeDevice, aSession.getCurrentZone(), 
                                        aSession.getUserName(), aSession.getUserEntity().partnerId);
                                }
                            }

                        }
                        finally
                        {
                            con.close();
                        }
                            
                        if(aSession.isMobileDevice())
                        {
                            String link = "http://gt.vipgame.mobi/?par=" + Long.toString(refGioithieuId);

//                            StringBuilder param = new StringBuilder();
//                            param.append(Long.toString(aSession.getUID())).append("&").
//                                    append(aSession.getUserEntity().partnerId).append("&")
//                                    .append(rqLink.phoneNumber);
//
//    //                        link = link + param.toString();
//                            byte[] encrypted = Utils.getCipherEncrypt().doFinal(param.toString().getBytes());
//                            String parEncrypt = new String(Base64.encodeBase64(encrypted, false));

                           
//                            String contentMessge = "Gửi tặng để nhận 10% số Pi khi người mà bạn giới thiệu nạp tiền";
                            String contentMessge = "";
//                            sb.append(link).append(parEncrypt).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(link).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(contentMessge);
                        }
                        
                        resBoc.value = sb.toString();
                        
		}
                  
                 
                 
                catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                catch (BusinessException ex) {
                        resBoc.message = ex.getMessage();
                }
                
                finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	
}
