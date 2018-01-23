package com.bosssoft.platform.installer.core.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.cfg.Step;
import com.bosssoft.platform.installer.core.track.ActionsTrack;
import com.bosssoft.platform.installer.core.util.VariableUtil;

public abstract class AbstractRunner
  implements IRunner
{
  protected InstallConfig installConfig = null;
  protected Step currentStep = null;
  protected ActionsTrack actionsTrack = new ActionsTrack();
  protected IContext context = null;

  public AbstractRunner(InstallConfig config) { this.context = InstallRuntime.INSTANCE.getContext();
    this.installConfig = config;
  }

  public void execute()
  {
    init();

    startInstall();
  }

  abstract void init();

  protected void startInstall() {
    String firstID = this.installConfig.getFirstStepID();
    if (firstID == null)
    {
      return;
    }

    processStep(firstID);
  }

  abstract void processStep(String paramString);

  abstract void stepBegin(Step paramStep);

  public static Map parseParameters(IContext context, Map map)
  {
    Map parameters = new HashMap();

    Iterator keys = map.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next().toString();
      String value = map.get(key).toString();
      value = VariableUtil.getVariableValue(
        context, value);
      parameters.put(key, value);
    }
    return parameters;
  }

  protected String getNextStepID(Step step)
  {
    String nextStepID = null;
    String discriminator = step.getNextStepDiscriminator();
    if (discriminator != null)
    {
      Object obj=InstallRuntime.INSTANCE.getContext().getVariableValue(discriminator);//mytest
    		  
      String branch = InstallRuntime.INSTANCE.getContext().getVariableValue(discriminator).toString();
      nextStepID = this.installConfig.getStep(step.getID()).getNextTaskID(branch);
      if (nextStepID == null)
        nextStepID = step.getDefaultNextStepID();
    }
    else {
      nextStepID = step.getDefaultNextStepID();
    }
    return nextStepID;
  }
  
  public Step getCurrentStep(){
	  return currentStep;
  }
  
  public InstallConfig getInstallConfig(){
	  return this.installConfig;
  }
}