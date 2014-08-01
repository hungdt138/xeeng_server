package com.tv.xeeng.protocol;

import java.util.ArrayList;
import org.json.JSONObject;

import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;

public abstract interface IPackageProtocol {

    public abstract IMessageProtocol getMessageProtocol(int paramInt);

    public abstract IRequestPackage decode(ISession paramISession, IByteBuffer paramIByteBuffer)
            throws ServerException;

    public abstract IRequestPackage decodeNew(ISession paramISession, IByteBuffer paramIByteBuffer)
            throws ServerException; //Modify in order to support old version

    public abstract IByteBuffer encode(ISession paramISession, IResponsePackage paramIResponsePackage)
            throws ServerException;

    public abstract ArrayList<JSONObject> encodeToJSON(ISession aSession, IResponsePackage aResPkg)
            throws ServerException;//for new structure 
}