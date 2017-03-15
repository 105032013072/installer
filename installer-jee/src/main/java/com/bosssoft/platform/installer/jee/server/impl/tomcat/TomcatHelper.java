package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.io.util.OS;

public class TomcatHelper
{
  public static final String UNKOWN = "unkown";

  public static boolean supportVersion(String tomcatHome)
    throws IOException
  {
    File file = new File(tomcatHome);
    if (!file.exists()) {
      throw new FileNotFoundException("tomcat home not exist!" + tomcatHome);
    }
    String version = getVersion(tomcatHome);

    if ((!version.startsWith("5.")) && 
      (!version.startsWith("6."))) {
      throw new UnsupportedOperationException("this tomcat version(" + version + ") unsupport!");
    }
    return true;
  }

  public static String getVersion(String tomcatHome)
    throws IOException
  {
    String version = "unkown";
    String cmdFile = OS.isMicroWindows() ? "version.bat" : "version.sh";
    File versionExc = new File(tomcatHome, "bin" + File.separator + cmdFile);

    if (!versionExc.exists())
      throw new FileNotFoundException("the tomcat version command could not be find!" + versionExc.getAbsolutePath());
    try
    {
      FileUtils.chmod(versionExc, 1, "+");
    }
    catch (OperationException localOperationException)
    {
    }

    BufferedReader reader = null;
    Process process = null;
    try {
      process = Runtime.getRuntime().exec(versionExc.getAbsolutePath(), null, 
        versionExc.getParentFile());
      InputStream inputStream = process.getInputStream();
      reader = new BufferedReader(new InputStreamReader(inputStream));
      String line = "";
      while ((line = reader.readLine()) != null) {
        line = line.toLowerCase();
        if (line.indexOf("server number:") >= 0) {
          version = line.substring(14).trim();
          break;
        }
      }
    } finally {
      if (reader != null)
        try {
          reader.close();
        }
        catch (IOException localIOException) {
        }
      if (process != null) {
        process.destroy();
      }
    }
    return version;
  }
}