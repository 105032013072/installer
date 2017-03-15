package com.bosssoft.platform.installer.core.option;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bosssoft.platform.installer.core.action.IAction;

public class DeployDef
  implements Serializable
{
  private List<IAction> actions = new ArrayList();

  public List<IAction> getActions() {
    return this.actions;
  }

  public void setActions(List<IAction> actions) {
    this.actions = actions;
  }

  public void addAction(IAction action) {
    this.actions.add(action);
  }

  public IAction getAction(String actionName) {
    IAction rtn = null;
    IAction localIAction1;
    for (Iterator localIterator = this.actions.iterator(); localIterator.hasNext(); localIAction1 = (IAction)localIterator.next());
    return rtn;
  }
}