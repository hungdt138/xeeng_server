package com.tv.xeeng.base.bytebuffer;

import java.nio.ByteBuffer;

import com.tv.xeeng.base.common.ServerException;

public class ByteBufferImpl
  implements IByteBuffer
{
  protected ByteBuffer mBuffer = null;

  public byte[] array()
  {
    return this.mBuffer.array();
  }

  public int limit()
  {
    return this.mBuffer.limit();
  }

  public void limit(int aNewLimit)
  {
    this.mBuffer.limit(aNewLimit);
  }

  public IByteBuffer duplicate()
  {
    ByteBufferImpl newBuffer = new ByteBufferImpl();
    newBuffer.mBuffer = this.mBuffer.duplicate();

    return newBuffer;
  }

  public IByteBuffer slice()
  {
    ByteBufferImpl newBuffer = new ByteBufferImpl();
    newBuffer.mBuffer = this.mBuffer.slice();

    return newBuffer;
  }

  public void skip(int aLen)
  {
    int newPos = position() + aLen;
    position(newPos);
  }

  public void flip()
  {
    this.mBuffer.flip();
  }

  public void compact()
  {
    this.mBuffer.compact();
  }

  public void clear()
  {
    this.mBuffer.clear();
  }

  public boolean hasRemaining()
  {
    return this.mBuffer.hasRemaining();
  }

  public int remaining()
  {
    return this.mBuffer.remaining();
  }

  public int position()
  {
    return this.mBuffer.position();
  }

  public void position(int aPos)
  {
    this.mBuffer.position(aPos);
  }

  public int capacity()
  {
    return this.mBuffer.capacity();
  }

  public final IByteBuffer capacity(int aNewCapacity)
  {
    if (aNewCapacity > capacity())
    {
      int pos = position();
      int limit = limit();

      ByteBuffer newBuf = ByteBuffer.allocate(aNewCapacity);
      this.mBuffer.clear();
      newBuf.put(this.mBuffer);
      this.mBuffer.put(newBuf);

      this.mBuffer.limit(limit);
      this.mBuffer.position(pos);
    }

    return this;
  }

  public IByteBuffer expand(int aExpectedRemaining)
  {
    return expand(position(), aExpectedRemaining);
  }

  public IByteBuffer expand(int aFromPos, int aExpectedRemaining)
  {
    int newCapacity = aFromPos + aExpectedRemaining;

    if (newCapacity > capacity())
    {
      capacity(newCapacity);
    }

    if (newCapacity > limit())
    {
      this.mBuffer.limit(newCapacity);
    }
    return this;
  }

  public byte get()
  {
    return this.mBuffer.get();
  }

  public boolean getBoolean()
  {
    byte byteValue = this.mBuffer.get();
    return (byteValue == 1);
  }

  public short getShort()
  {
    return this.mBuffer.getShort();
  }

  public char getChar()
  {
    return this.mBuffer.getChar();
  }

  public char getChar(int i)
  {
    return this.mBuffer.getChar(i);
  }

  public int getInt()
  {
    return this.mBuffer.getInt();
  }

  public long getLong()
  {
    return this.mBuffer.getLong();
  }

  public String getString() throws ServerException
  {
    int len;
    try {
      len = this.mBuffer.getShort();
      byte[] data = new byte[len];
      this.mBuffer.get(data);
      String result = new String(data, "UTF-8");
      return result;
    }
    catch (Throwable t) {
      throw new ServerException(t);
    }
  }

  public void get(byte[] aData)
  {
    this.mBuffer.get(aData);
  }

  public void get(byte[] aData, int aOffset, int aLength)
  {
    this.mBuffer.get(aData, aOffset, aLength);
  }

  public void put(IByteBuffer aSource)
  {
    if (aSource != null)
    {
      expand(aSource.remaining());
      this.mBuffer.put(((ByteBufferImpl)aSource).mBuffer);
    }
  }

  public void put(byte aByte)
  {
    expand(1);
    this.mBuffer.put(aByte);
  }

  public void putChar(char aChar)
  {
    expand(2);
    this.mBuffer.putChar(aChar);
  }

  public void putInt(int aInt)
  {
    expand(4);
    this.mBuffer.putInt(aInt);
  }

  public void putLong(long aLong)
  {
    expand(8);
    this.mBuffer.putLong(aLong);
  }

  public void putShort(short aShort)
  {
    expand(2);
    this.mBuffer.putShort(aShort);
  }

  public void putBoolean(boolean aBoolean)
  {
    byte byteValue =(byte)((aBoolean) ? 1 : 0);
    expand(1);
    this.mBuffer.put(byteValue);
  }

  public void put(byte[] aData)
  {
    if (aData != null)
    {
      expand(aData.length);
      this.mBuffer.put(aData);
    }
  }

  public void put(byte[] aData, int aOffset, int aLength)
  {
    if (aData != null)
    {
      expand(aLength);
      this.mBuffer.put(aData, aOffset, aLength);
    }
  }

  public void putString(String aString)
    throws ServerException
  {
    byte[] data;
    try
    {
      data = aString.getBytes("UTF-8");
      int len = data.length;

      expand(len + 2);

      this.mBuffer.putShort((short)len);
      this.mBuffer.put(data);
    }
    catch (Throwable t) {
      throw new ServerException(t);
    }
  }
}