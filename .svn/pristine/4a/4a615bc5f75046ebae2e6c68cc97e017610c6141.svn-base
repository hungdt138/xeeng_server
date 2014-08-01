package com.tv.xeeng.base.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServerUtils
{
  public static String formatDate(Date aDate)
  {
    String pattern = "dd/MM/yy HH:mm:ss";
    Locale locale = new Locale("en", "US");
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
    return formatter.format(aDate);
  }

  public static String formatTime(long aTime)
  {
    if (aTime < 0L)
    {
      return "00:00:00";
    }

    String result = "";

    int hours = (int)(aTime / 3600000L);
    int minutes = (int)(aTime / 60000L);
    int seconds = (int)(aTime / 1000L);
    seconds -= minutes * 60;
    minutes -= hours * 60;

    if (hours < 10)
    {
      result = result + "0" + hours;
    }
    else {
      result = result + hours;
    }

    if (minutes < 10)
    {
      result = result + ":0" + minutes;
    }
    else {
      result = result + ":" + minutes;
    }

    if (seconds < 10)
    {
      result = result + ":0" + seconds;
    }
    else {
      result = result + ":" + seconds;
    }

    return result;
  }
}