package com.tv.xeeng.protocol;

import org.json.JSONObject;

public abstract class AbstractRequestMessage
  implements IRequestMessage
{
  private int mMsgId;
  private boolean mIsNeedLoggedIn;
  private int mDBFlag;
  private IRequestMessage mNext;
  private IRequestMessage mPrevious;
  private JSONObject object;

    @Override
    public JSONObject getJSONObject() {
        return object;
    }
@Override
    public void setObject(JSONObject object) {
        this.object = object;
    }
  @Override
  public int getID()
  {
    return this.mMsgId;
  }

  void setID(int aMsgId)
  {
    this.mMsgId = aMsgId;
  }
@Override
  public boolean isNeedLoggedIn()
  {
    return this.mIsNeedLoggedIn;
  }

  void setNeedLoggedIn(boolean aIsNeedLoggedIn)
  {
    this.mIsNeedLoggedIn = aIsNeedLoggedIn;
  }
@Override
  public int getDBFlag()
  {
    return this.mDBFlag;
  }

  void setDBFlag(int aDBFlag)
  {
    this.mDBFlag = aDBFlag;
  }
@Override
  public IRequestMessage getNext()
  {
    return this.mNext;
  }
@Override
  public IRequestMessage getPrevious()
  {
    return this.mPrevious;
  }
@Override
  public void setNext(IRequestMessage aRequestMsg)
  {
    this.mNext = aRequestMsg;
  }
@Override
  public void setPrevious(IRequestMessage aRequestMsg)
  {
    this.mPrevious = aRequestMsg;
  }
@Override
  protected AbstractRequestMessage clone()
  {
    AbstractRequestMessage newMsg = (AbstractRequestMessage)createNew();
    newMsg.setID(this.mMsgId);
    newMsg.setNeedLoggedIn(this.mIsNeedLoggedIn);
    newMsg.setDBFlag(this.mDBFlag);
    return newMsg;
  }
  
}