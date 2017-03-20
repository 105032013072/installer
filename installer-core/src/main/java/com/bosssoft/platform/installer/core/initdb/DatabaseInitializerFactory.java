package com.bosssoft.platform.installer.core.initdb;
public class DatabaseInitializerFactory
{
  public static IDatabaseInitializer getDatabaseInitializer()
  {
    return new DatabaseInitializerImpl();
  }
}