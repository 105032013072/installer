package com.bosssoft.platform.installer.core.event;

import com.bosssoft.platform.installer.core.IContext;

public interface IStepInterceptor
{
  public void beforeStep(IContext context);

  public boolean isIgnoreThis(IContext context);
}