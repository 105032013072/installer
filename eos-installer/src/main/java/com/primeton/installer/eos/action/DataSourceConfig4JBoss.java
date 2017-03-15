package com.primeton.installer.eos.action;

import com.primeton.install.ext.appsvr.IJeeServerEnvIniter;
import com.primeton.install.jee.server.IDataSource;
import com.primeton.install.jee.server.impl.jboss.JBossDatasourcConfig;
import com.primeton.install.jee.server.impl.jboss.JBossEnv;
import com.primeton.install.jee.server.impl.jboss.JBossServerImpl;

public class DataSourceConfig4JBoss
  implements IJeeServerEnvIniter
{
  private IDataSource dataSource;
  private String jbossHome;

  public DataSourceConfig4JBoss(IDataSource dataSource, String jbossHome)
  {
    this.dataSource = dataSource;
    this.jbossHome = jbossHome;
  }

  public boolean excute() {
    try {
      JBossEnv env = new JBossEnv(this.jbossHome, "default");
      JBossServerImpl server = new JBossServerImpl(env);
      JBossDatasourcConfig dsConfig = new JBossDatasourcConfig();
      dsConfig.setDataSource(this.dataSource);
      server.config(dsConfig);

      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}