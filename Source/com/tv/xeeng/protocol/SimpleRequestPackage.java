package com.tv.xeeng.protocol;

import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;


public class SimpleRequestPackage
  implements IRequestPackage
{
  private IRequestMessage mCurrent;
  private IRequestMessage mLast;
  private int mSize;
  private PackageHeader mHeader;

  public SimpleRequestPackage()
  {
    this.mCurrent = null;
    this.mLast = null;
    this.mSize = 0;
    this.mHeader = new PackageHeader();
  }

  public IRequestMessage next()
  {
    IRequestMessage result = this.mCurrent;
    this.mCurrent = this.mCurrent.getNext();

    return result;
  }

  public int size()
  {
    return this.mSize;
  }

  public void reset()
  {
    this.mCurrent = null;
    this.mLast = null;
    this.mSize = 0;
  }

  public IRequestMessage lastMessage()
  {
    return this.mLast;
  }

  public static boolean canDecode(ISession aSession, IByteBuffer aEncodedObj)
  {
    //int adding = aEncodedObj.getInt();
    int length = aEncodedObj.getInt();

    int remainingBuffer = aEncodedObj.remaining();
//    System.out.println("packet size " + length);
    return (length == remainingBuffer);
  }

  public boolean contains(IRequestMessage aMsg)
  {
    IRequestMessage msg = this.mLast;
    while (msg != null)
    {
      if (msg == aMsg)
      {
        return true;
      }

      msg = msg.getPrevious();
    }

    return false;
  }

  public boolean hasNext()
  {
    return (this.mCurrent != null);
  }

  public void addMessage(IRequestMessage aMsg)
    throws ServerException
  {
    if (contains(aMsg))
    {
      throw new ServerException("Message was added already - " + aMsg.toString());
    }

    if (this.mSize == 0)
    {
      this.mCurrent = aMsg;
      this.mLast = aMsg;
    }
    else
    {
      this.mLast.setNext(aMsg);
      aMsg.setPrevious(this.mLast);
      this.mLast = aMsg;
    }

    this.mSize += 1;
  }

  public PackageHeader getRequestHeader()
  {
    if (this.mHeader == null)
    {
      this.mHeader = new PackageHeader();
    }
    return this.mHeader;
  }
}