package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import com.bosssoft.platform.installer.jee.server.AbstractJEEServerEnv;

public class TomcatEnv extends AbstractJEEServerEnv
{
  private String tomcatHome;

  public TomcatEnv(String tomcatHome)
  {
    this.tomcatHome = tomcatHome;
  }

  public String getTomcatHome()
  {
    return this.tomcatHome;
  }

  public void setTomcatHome(String tomcatHome)
  {
    this.tomcatHome = tomcatHome;
  }
}