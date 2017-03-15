package com.bosssoft.platform.installer.jee.server.spi.manager.internal;

public class ServiceLocatorHelper
{
  public static IServiceLocator getServiceLocator()
  {
    return ServerviceLocatorImpl.getInstance();
  }
}