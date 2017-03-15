package com.bosssoft.platform.installer.jee.server;

public interface IDataSource {
	public String getName();

	public String getUrl();

	public String getJndiName();

	public String getUser();

	public String getPassword();

	public String getDriver();

	public ProductDefination getProductDefination();

	public void setProperty(String key, String value);

	public String removeProperty(String key);

	public String getProperty(String key);

	public String getTestTable();
}