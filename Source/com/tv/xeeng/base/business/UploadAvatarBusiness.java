package com.tv.xeeng.base.business;

import com.sun.midp.io.Base64;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.UploadAvatarRequest;
import com.tv.xeeng.base.protocol.messages.UploadAvatarResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.QueueNewsEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UploadAvatarEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class UploadAvatarBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(UploadAvatarBusiness.class);
    private static final int MAX_LENGTH = 600;
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Get Category] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        UploadAvatarResponse resUpload =
                (UploadAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        UploadAvatarRequest rqRaoVat = (UploadAvatarRequest) aReqMsg;
        try {
            aSession.setUploadBytePart(null);
            aSession.setUploadAvatar(true);
            if(rqRaoVat.maxParts>0)
            {
                //first message for upload image
                UploadAvatarEntity entity = new UploadAvatarEntity();
                entity.setAlbumId(rqRaoVat.albumId);
                entity.setImageId(rqRaoVat.imageId);
                entity.setMaxParts(rqRaoVat.maxParts);
                aSession.setUploadAvatarEntity(entity);
                resUpload.setSuccess("");
                aResPkg.addMessage(resUpload);
                return 1;
            }
//            System.out.println("sequence " + rqRaoVat.sequence);
            //upload image processing
            UploadAvatarEntity entity = aSession.getUploadAvatarEntity();
            if(entity== null)
            {
                throw new BusinessException("Co loi xay ra ban thu upload lai xem sao");
            }
            byte[] arrPart = Base64.decode(rqRaoVat.detail);
            
//            entity.getContent().append(new String(arrPart));
            entity.appendRawData(arrPart);
            
            StringBuilder sb = new StringBuilder();
            sb.append(rqRaoVat.sequence);
            if(rqRaoVat.sequence == entity.getMaxParts() -1)
            {
                //put into news queue to process and dont' send anything
                QueueNewsEntity queueEntity = new QueueNewsEntity(entity, aSession, resUpload);
            }
            else
            {
                resUpload.setSuccess(sb.toString());
                aResPkg.addMessage(resUpload);
            }
            
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
            mLog.debug("Co loi xay ra    " + e.getCause());
            resUpload.setFailure(ResponseCode.FAILURE, "DB Error");
            if ((resUpload != null)) {
                aResPkg.addMessage(resUpload);
            }
        } finally {
            
        }
        return 1;
    }
}
