package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.action.IAction;
import com.primeton.install.ext.util.PropertiesUtil;
import com.primeton.install.io.FileUtils;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class UpdateDomainConfigByRepeatInstall
  implements IAction
{
  private Logger logger = Logger.getLogger(getClass());

  public void execute(IContext context, Map parameters) throws InstallException
  {
    String ip = context.getStringValue("USER_IP");
    String port = context.getStringValue("USER_PORT");
    String appName = context.getStringValue("DEFAULT_APP_NAME");
    String appServerType = context.getStringValue("APP_SERVER_NAME");
    String run_path = "";
    String domain_path = "";
    String deployRootDir = null;
    if (appServerType.equalsIgnoreCase("Tomcat")) {
      String tomcat_home = context.getStringValue("AS_TOMCAT_HOME");
      deployRootDir = tomcat_home + "/webapps";
      domain_path = tomcat_home + "/webapps/governor/WEB-INF/_srv/domain/domain.xml";
      run_path = tomcat_home + "/webapps/governor/WEB-INF/.run";
    }
    if (appServerType.equalsIgnoreCase("JBoss")) {
      String jboss_home = context.getStringValue("AS_JBOSS_HOME");
      deployRootDir = jboss_home + "/server/default/deploy";
      domain_path = jboss_home + "/server/default/deploy/governor.war/WEB-INF/_srv/domain/domain.xml";
      run_path = jboss_home + "/server/default/deploy/governor.war/WEB-INF/.run";
    }
    if (appServerType.equalsIgnoreCase("WebLogic")) {
      String weblogic_home = context.getStringValue("AS_WL_DOMAIN_HOME");
      deployRootDir = weblogic_home + "/applications";
      domain_path = weblogic_home + "/applications/governor.war/WEB-INF/_srv/domain/domain.xml";
      run_path = weblogic_home + "/applications/governor.war/WEB-INF/.run";
    }
    if (appServerType.equalsIgnoreCase("WebSphere")) {
      String was_home = context.getStringValue("AS_WAS_PROFILE_HOME");
      String cell_name = context.getStringValue("AS_WAS_CELL_NAME");
      deployRootDir = was_home + "/installedApps/" + cell_name;
      domain_path = was_home + "/installedApps/" + cell_name + "/governor.ear/governor.war/WEB-INF/_srv/domain/domain.xml";
      run_path = was_home + "/installedApps/" + cell_name + "/governor.ear/governor.war/WEB-INF/.run";
    }
    Properties run_pros = PropertiesUtil.readProperties(run_path);
    String externalConfigDir = run_pros.getProperty("ExternalConfigDir");
    File externalConfigDirFile = null;
    boolean hasExternalConfigDir = false;

    if ((null != externalConfigDir) && (!"".equals(externalConfigDir))) {
      externalConfigDirFile = new File(externalConfigDir);
      if ((externalConfigDirFile.exists()) && (new File(externalConfigDir + "/governor/domain/domain.xml").exists()))
      {
        domain_path = externalConfigDir + "/governor/domain/domain.xml";
        hasExternalConfigDir = true;
      }
    }

    File domainfile = new File(domain_path);
    if (!domainfile.exists()) {
      this.logger.error("Application 'governor' is not exists");
      return;
    }

    if ((null == appName) || ("".equals(appName))) {
      this.logger.error("Not to deploy the default application");
      return;
    }

    UpdateDomainConfig config = new UpdateDomainConfig();
    try {
      config.addServer2Domain(domain_path, ip, port, appName, appServerType);
    }
    catch (Exception e) {
      this.logger.error("Modify Governor's domain.xml failed!", e);
    }

    if (hasExternalConfigDir) {
      File[] subDirs = externalConfigDirFile.listFiles();
      for (File dir : subDirs) {
        if ((dir.isDirectory()) && (!dir.getName().equals("governor"))) {
          File targetDomainFile = new File(externalConfigDir + "/" + dir.getName() + "/domain/domain.xml");
          if (targetDomainFile.exists()) {
            try {
              FileUtils.copy(domainfile, targetDomainFile, true, null, null);
            } catch (Exception e) {
              this.logger.error("Modify " + dir.getName() + "'s domain.xml failed!", e);
            }
          }
        }
      }
    }
    if ((deployRootDir != null) && (new File(deployRootDir).exists())) {
      File[] subDirs = new File(deployRootDir).listFiles();

      for (File dir : subDirs)
        if ((dir.isDirectory()) && (!dir.getName().startsWith("governor"))) {
          File targetDomainFile = null;
          if (appServerType.equalsIgnoreCase("Tomcat")) {
            targetDomainFile = new File(deployRootDir + "/" + dir.getName() + "/WEB-INF/_srv/domain/domain.xml");
          } else if (appServerType.equalsIgnoreCase("JBoss")) {
            if (dir.getName().endsWith(".ear")) {
              String tempAppName = dir.getName().substring(0, dir.getName().lastIndexOf(".ear"));
              targetDomainFile = new File(deployRootDir + "/" + dir.getName() + "/" + tempAppName + ".war/WEB-INF/_srv/domain/domain.xml");
            }
          } else if (appServerType.equalsIgnoreCase("WebLogic")) {
            if (dir.getName().endsWith(".ear")) {
              String tempAppName = dir.getName().substring(0, dir.getName().lastIndexOf(".ear"));
              targetDomainFile = new File(deployRootDir + "/" + dir.getName() + "/" + tempAppName + ".war/WEB-INF/_srv/domain/domain.xml");
            }
          } else if ((appServerType.equalsIgnoreCase("WebSphere")) && 
            (dir.getName().endsWith(".ear"))) {
            String tempAppName = dir.getName().substring(0, dir.getName().lastIndexOf(".ear"));
            targetDomainFile = new File(deployRootDir + "/" + dir.getName() + "/" + tempAppName + ".war/WEB-INF/_srv/domain/domain.xml");
          }

          if ((targetDomainFile != null) && (targetDomainFile.exists()))
            try {
              FileUtils.copy(domainfile, targetDomainFile, true, null, null);
            } catch (Exception e) {
              this.logger.error("Modify " + dir.getName() + "'s domain.xml failed!", e);
            }
        }
    }
  }

  public void rollback(IContext context, Map parameters)
    throws InstallException
  {
  }
}