package com.primeton.installer.eos.action;

import com.primeton.install.ext.appsvr.IJeeServerEnvIniter;
import com.primeton.install.jee.server.IDataSource;
import com.primeton.install.jee.server.impl.tomcat.TomcatDataSourceConfig;
import com.primeton.install.jee.server.impl.tomcat.TomcatEnv;
import com.primeton.install.jee.server.impl.tomcat.TomcatServerImpl;

public class DataSourceConfig4Tomcat
  implements IJeeServerEnvIniter
{
  private IDataSource dataSource;
  private String tomcatHome;

  public DataSourceConfig4Tomcat(IDataSource dataSource, String tomcatHome)
  {
    this.dataSource = dataSource;
    this.tomcatHome = tomcatHome;
  }

  public boolean excute()
  {
    try
    {
      TomcatEnv env = new TomcatEnv(this.tomcatHome);
      TomcatServerImpl server = new TomcatServerImpl(env);
      TomcatDataSourceConfig dsConfig = new TomcatDataSourceConfig();
      dsConfig.setDataSource(this.dataSource);
      server.config(dsConfig);

      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}