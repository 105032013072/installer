package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.action.IAction;
import java.util.Map;

public class RegisterProduct
  implements IAction
{
  private RegisterEOS registerEOS = new RegisterEOS();

  public void execute(IContext context, Map map) throws InstallException {
    this.registerEOS.execute(context, map);
  }

  public void rollback(IContext context, Map map) throws InstallException {
    this.registerEOS.rollback(context, map);
  }
}