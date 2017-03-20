package com.bosssoft.platform.installer.core.initdb;

import java.sql.Connection;

public interface IDatabaseInitializer
{
  public String initialize(Connection connection, String[] paramArrayOfString);

  public String[] getComponentNames();

  public boolean isInitialized(Connection connection, String paramString);
}