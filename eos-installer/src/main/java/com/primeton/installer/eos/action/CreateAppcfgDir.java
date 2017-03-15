package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.action.IAction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class CreateAppcfgDir
  implements IAction
{
  transient Logger logger = Logger.getLogger(getClass());

  public void execute(IContext context, Map parameters) throws InstallException
  {
    String isCluster = context.getStringValue("IS_CLUSTER");
    if ((isCluster != null) && (Boolean.TRUE.toString().equalsIgnoreCase(isCluster))) {
      return;
    }

    String appName = parameters.get("APP_NAME").toString();

    String installDir = context.getStringValue("INSTALL_DIR");
    String appcfgDirPath = installDir + "/apps_config";
    if ((appName.equals("{DEFAULT_APP_NAME}")) || ("".equals(appName))) {
      mkdir(appcfgDirPath);
      return;
    }
    String appDirPath = appcfgDirPath + "/" + appName;

    if (mkdir(appcfgDirPath)) {
      mkdir(appDirPath);
    }
    String startupConfPath = appDirPath + "/startup.conf";
    String ip = parameters.get("IP").toString();
    String port = parameters.get("PORT").toString();

    createStarupConf(startupConfPath, ip, port);
  }

  private boolean mkdir(String path) {
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    else if (file.isFile()) {
      this.logger.error("Not a Directory:" + path);
      return false;
    }

    return true;
  }

  private void createStarupConf(String filePath, String ip, String port)
  {
    Properties p = new Properties();
    p.setProperty("LocalIP", ip);
    p.setProperty("AdminPort", port);
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(filePath);
      p.store(out, null);
    } catch (IOException e) {
      this.logger.error(e);
    } finally {
      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void rollback(IContext arg0, Map arg1)
    throws InstallException
  {
  }
}