package com.bosssoft.platform.installer.jee.server.internal;

import java.util.Properties;

import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.ProductDefination;

public class JDBCDataSourceImpl implements IDataSource {
	private String driver;
	private String jndiName;
	private String name;
	private String user;
	private String password;
	private String url;
	private String testTable;
	private ProductDefination productDefination;
	private Properties properties = new Properties();

	public JDBCDataSourceImpl() {
	}

	public JDBCDataSourceImpl(String driver, String jndiName, String name, String user, String password, String url, ProductDefination productDefination) {
		this.driver = driver;
		this.jndiName = jndiName;
		this.name = name;
		this.user = user;
		this.password = password;
		this.url = url;
		this.productDefination = productDefination;
	}

	public ProductDefination getProductDefination() {
		return this.productDefination;
	}

	public void setProductDefination(ProductDefination productDefination) {
		this.productDefination = productDefination;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getJndiName() {
		return this.jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	public String removeProperty(String key) {
		return (String) this.properties.remove(key);
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public int hashCode() {
		int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.driver == null ? 0 : this.driver.hashCode());
		result = PRIME * result + (this.jndiName == null ? 0 : this.jndiName.hashCode());
		result = PRIME * result + (this.name == null ? 0 : this.name.hashCode());
		result = PRIME * result + (this.password == null ? 0 : this.password.hashCode());
		result = PRIME * result + (this.url == null ? 0 : this.url.hashCode());
		result = PRIME * result + (this.user == null ? 0 : this.user.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JDBCDataSourceImpl other = (JDBCDataSourceImpl) obj;
		if (this.driver == null) {
			if (other.driver != null)
				return false;
		} else if (!this.driver.equals(other.driver))
			return false;
		if (this.jndiName == null) {
			if (other.jndiName != null)
				return false;
		} else if (!this.jndiName.equals(other.jndiName))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.password == null) {
			if (other.password != null)
				return false;
		} else if (!this.password.equals(other.password))
			return false;
		if (this.url == null) {
			if (other.url != null)
				return false;
		} else if (!this.url.equals(other.url))
			return false;
		if (this.user == null) {
			if (other.user != null)
				return false;
		} else if (!this.user.equals(other.user))
			return false;
		return true;
	}

	public void setTestTable(String testTable) {
		this.testTable = testTable;
	}

	public String getTestTable() {
		return this.testTable;
	}
}