package com.tv.xeeng.base.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggerFactory extends AbstractLoggerFactory
{  
  @SuppressWarnings("rawtypes")
public Logger getLogger(Class aClass)
  {
    return LoggerFactory.getLogger(aClass);
  }
}