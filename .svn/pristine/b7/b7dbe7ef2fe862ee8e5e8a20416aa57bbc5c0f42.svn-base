package com.tv.xeeng.base.bytebuffer;

import java.nio.ByteBuffer;

public class ByteBufferFactory
{
  public static IByteBuffer wrap(byte[] aData)
  {
    ByteBufferImpl result = new ByteBufferImpl();
    result.mBuffer = ByteBuffer.wrap(aData);

    return result;
  }

  public static IByteBuffer allocate(int aSize)
  {
    ByteBufferImpl result = new ByteBufferImpl();
    result.mBuffer = ByteBuffer.allocate(aSize);

    return result;
  }
}