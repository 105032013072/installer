package com.bosssoft.platform.installer.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IContext extends Serializable {
	public String getStringValue(Object key);

	public Object getValue(Object key);

	public void setValue(Object key, Object value);

	public Object getVariableValue(Object key);

	public void setVariableValue(Object key, Object value);

	public Object remove(Object key);

	public void putAll(Map paramMap);

	public Map getAll();

	public List getKeysLike(String key);

	public boolean isSilentInstall();

	public boolean isDebug();
}