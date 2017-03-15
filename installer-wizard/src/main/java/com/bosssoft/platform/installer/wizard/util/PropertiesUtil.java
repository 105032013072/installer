package com.bosssoft.platform.installer.wizard.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil
{
  public static Properties readProperties(String filePath)
  {
    InputStream ips = null;
    try {
      ips = new BufferedInputStream(new FileInputStream(filePath));
      Properties props = new Properties();
      props.load(ips);
      return props;
    }
    catch (IOException localIOException1) {
    }
    finally {
      if (ips != null)
        try {
          ips.close();
        }
        catch (IOException localIOException3)
        {
        }
    }
    return null;
  }
}