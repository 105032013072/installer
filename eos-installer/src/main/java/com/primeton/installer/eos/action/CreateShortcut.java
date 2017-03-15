package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.MainFrameController;
import com.primeton.install.action.IAction;
import com.primeton.install.ext.util.Win32RegKeyUtil;
import com.primeton.install.io.FileUtils;
import com.primeton.install.util.I18nUtil;
import com.primeton.install.util.InstallerFileManager;
import com.primeton.install.util.ShortCutUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class CreateShortcut
  implements IAction
{
  transient Logger logger = Logger.getLogger(getClass().getName());
  public static final String EOS_STA_CMD = "startServer.cmd";
  public static final String EOS_WLCLUSTER_CMD = "install_wlcluster.cmd";
  public static final String EOS_STOP_CMD = "stopServer.cmd";
  public static final String EOS_UNINSTALL_CMD = "uninstall.sh";

  public void execute(IContext context, Map paramters)
    throws InstallException
  {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.indexOf("window") < 0)
    {
      return;
    }

    String eosVersion = context.getStringValue("VERSION_NUMBER");
    String installDir = context.getStringValue("INSTALL_DIR");

    String imageDir = InstallerFileManager.getImageDir();
    String iconDir = installDir + "/ico";
    try
    {
      File src = new File(imageDir);
      File dest = new File(iconDir);
      FileUtils.copy(src, dest, null, null);
    } catch (Exception e) {
      this.logger.error(e);
      throw new InstallException(e);
    }
    String userDir = System.getProperty("user.home");
    Registry registry = new Registry(userDir);

    String install_count = String.valueOf(registry.theLargestFlag(eosVersion) + 1);

    String groupName = "Primeton EOS " + eosVersion + "(EOS Home" + install_count + ")";

    String programMenu = Win32RegKeyUtil.getStartMenuPath();

    if (null == programMenu)
    {
      MainFrameController.showMessageDialog(I18nUtil.getString("SHORTCUT.CREATE.ERROR"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 2);

      return;
    }
    String groupPath = programMenu + "/" + groupName;

    context.setValue("STARTMENU_PATH", programMenu + "/");
    context.setValue("SHORTCUT_GROUP", groupName);
    context.setValue("SHORTCUT_DIR", groupPath);
    this.logger.info("GROUP_PATH = " + groupPath);

    new File(groupPath).mkdirs();

    String edition = context.getStringValue("EDITION");

    if ("DE".equalsIgnoreCase(edition)) {
      ShortCutUtil.createExeShortcut(groupPath + File.separator + "EOS Studio.lnk", installDir + File.separator + "ide/eclipse/studio.exe", iconDir + "/EOSstudio.ico", installDir + File.separator + "ide/eclipse");
    }

    String asType = context.getStringValue("APP_SERVER_TYPE").toLowerCase();

    String runEos = "startServer.cmd";
    String webPort = null;
    String workDir = installDir;

    Map parameters = new HashMap();

    parameters = context.getAll();
    try
    {
      webPort = ApplicationServerHelper.getWebPort(asType, parameters);
    } catch (Exception e) {
      throw new InstallException(e.getMessage(), e);
    }

    if (null != runEos) {
      ShortCutUtil.createExeShortcut(groupPath + File.separator + I18nUtil.getString("SHORTCUT.START.EOSSERVER") + ".lnk", installDir + File.separator + runEos, iconDir + "/EOSserver.ico", workDir);
    }
    ShortCutUtil.createExeShortcut(groupPath + File.separator + I18nUtil.getString("SHORTCUT.STOP.EOSSERVER") + ".lnk", installDir + File.separator + "stopServer.cmd", iconDir + "/Stop-Server.ico", workDir);
    if (null != webPort) {
      ShortCutUtil.createUrlShortcut(groupPath + "/" + "EOS Governor" + ".url", "http://localhost:" + webPort + "/governor/index.jsp", iconDir + "/EOSconsole.ico");
    }
    else
    {
      this.logger.warn("Can not create url link because webPort is null");
    }

    if ("DE".equalsIgnoreCase(edition))
      ShortCutUtil.createExeShortcut(groupPath + File.separator + I18nUtil.getString("SHORTCUT.UNINSTALL.EOS") + ".lnk", installDir + File.separator + "uninstall/uninstall.cmd", iconDir + "/Uninstall EOS.ico", installDir + File.separator + "uninstall");
  }

  public void rollback(IContext arg0, Map arg1)
    throws InstallException
  {
  }
}