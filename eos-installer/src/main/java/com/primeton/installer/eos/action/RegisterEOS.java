package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.action.IAction;
import java.util.Map;
import org.apache.log4j.Logger;

public class RegisterEOS
  implements IAction
{
  private transient Logger logger = Logger.getLogger(RegisterEOS.class.getName());

  public int strategyWhenException(Exception arg0)
  {
    return 0;
  }

  public static String getEOSREGPath(IContext context)
  {
    return System.getProperty("user.home");
  }

  public void execute(IContext context, Map map) throws InstallException {
    String userdir = getEOSREGPath(context);
    Registry r = new Registry(userdir);
    this.logger.info("Register the installed information to " + userdir);
    r.addInstalledProduct(context);
  }

  public void rollback(IContext context, Map map)
    throws InstallException
  {
  }
}