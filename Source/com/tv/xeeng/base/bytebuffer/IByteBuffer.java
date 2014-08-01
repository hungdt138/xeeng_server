package com.tv.xeeng.base.bytebuffer;

import com.tv.xeeng.base.common.ServerException;

public abstract interface IByteBuffer
{
  public static final String CHARSET_NAME = "UTF-8";
  public static final byte TRUE_BYTE_VALUE = 1;
  public static final byte FALSE_BYTE_VALUE = 0;

  public abstract byte[] array();

  public abstract int limit();

  public abstract void limit(int paramInt);

  public abstract IByteBuffer duplicate();

  public abstract IByteBuffer slice();

  public abstract void skip(int paramInt);

  public abstract void flip();

  public abstract void compact();

  public abstract void clear();

  public abstract boolean hasRemaining();

  public abstract int remaining();

  public abstract int position();

  public abstract void position(int paramInt);

  public abstract int capacity();

  public abstract IByteBuffer capacity(int paramInt);

  public abstract IByteBuffer expand(int paramInt);

  public abstract IByteBuffer expand(int paramInt1, int paramInt2);

  public abstract byte get();

  public abstract boolean getBoolean();

  public abstract short getShort();

  public abstract char getChar();

  public abstract char getChar(int paramInt);

  public abstract int getInt();

  public abstract long getLong();

  public abstract void get(byte[] paramArrayOfByte);

  public abstract void get(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract String getString() throws ServerException;

  public abstract void put(IByteBuffer paramIByteBuffer);

  public abstract void put(byte paramByte);

  public abstract void putBoolean(boolean paramBoolean);

  public abstract void putShort(short paramShort);

  public abstract void putChar(char paramChar);

  public abstract void putInt(int paramInt);

  public abstract void putLong(long paramLong);

  public abstract void put(byte[] paramArrayOfByte);

  public abstract void put(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract void putString(String paramString) throws ServerException;
}