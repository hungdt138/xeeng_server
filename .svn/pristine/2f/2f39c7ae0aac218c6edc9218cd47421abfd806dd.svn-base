/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


import java.io.File;
import java.io.IOException;

import java.util.Scanner;

import java.util.UUID;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetFileDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetFileDetailResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheFileInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetFileDetailBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetFileDetailBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get file detail");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetFileDetailResponse resFile = (GetFileDetailResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {

            GetFileDetailRequest rqAlb = (GetFileDetailRequest) aReqMsg;

//            AlbumDB db = new AlbumDB();
            CacheFileInfo cache = new CacheFileInfo();

//            UUID imgRequest = UUID.randomUUID();
//            aSession.setImageRequest(imgRequest);

            FileEntity fileEntity = cache.getFileDetail(rqAlb.fileId);
            if (fileEntity != null) {
                resFile.mCode = ResponseCode.SUCCESS;
                if (fileEntity.getContent() == null) {

                    String fileName = fileEntity.getLocation() + fileEntity.getFileName() + AIOConstants.DEFAUL_NORMAL_BASE64_FILE;
//                    BufferedImage img = null;
//
//                    img = ImageIO.read(new File(fileName));

//                    String imgbase64 = Utils.convertImageBase64(img);
                    String imgbase64 = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                    
                    fileEntity.setContent(imgbase64);

                    cache.updateCacheFileDetail(fileEntity);
                }

                StringBuilder sb = new StringBuilder();
                sb.append(fileEntity.getRateEntity().getViewCount()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(fileEntity.getRateEntity().getLikeCount()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(fileEntity.getRateEntity().getCommentCount()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(fileEntity.getContent());
                
                resFile.value = sb.toString();
            } else {
                resFile.mCode = ResponseCode.FAILURE;
                resFile.errMsg = "Ảnh không tồn tại";
            }


        } catch (IOException ex) {
            mLog.error(ex.getMessage(), ex);
            resFile.mCode = ResponseCode.FAILURE;
//            resalbum.errMsg = "";
        } 
        catch (Throwable t){
            resFile.mCode = ResponseCode.FAILURE;
        }//        }
        //        catch(BusinessException ex)
        //        {
        //            mLog.warn(ex.getMessage());
        //            resalbum.setFailure(ex.getMessage());
        //            
        //        }
        finally {
            if (resFile != null) {
                try {
                    aSession.write(resFile);
                } catch (ServerException ex) {
                    mLog.error(ex.getMessage(), ex);
                }

            }
        }
        return 1;
    }

    private String getImage() throws Throwable {
        BufferedImage img = ImageIO.read(new File("IMG_8509.jpg"));
        
        //return convertImageBase64(Scalr.resize(img, Scalr.Mode.AUTOMATIC, 1280, 720));
        return convertImageBase64(img);
    }

    private String convertImageBase64(BufferedImage imageBuf) throws IOException {
        ByteArrayOutputStream f = new ByteArrayOutputStream();
        ImageIO.write(imageBuf, "jpg", f);
        byte[] arrByte = f.toByteArray();
//            ByteArrayInputStream in = new ByteArrayInputStream(arrByte);
//            f = new ByteArrayOutputStream();
//            LZCOutputStream.compress(in, f);
//            arrByte = f.toByteArray();

        return new String(com.sun.midp.io.Base64.encode(arrByte, 0, arrByte.length));

    }
    public  BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }
}
