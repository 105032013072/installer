package com.bosssoft.platform.installer.core.impl.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.track.ActionsTrack;
import com.bosssoft.platform.installer.core.util.VariableUtil;

public class InstallContext implements IContext, Serializable {
	private TreeMap values = new TreeMap();
	private transient boolean isSilentInstall = false;
	private transient AbstractSetupPanel currentSetupPanel = null;
	private boolean isDebug = false;

	private ActionsTrack actionsTrack = null;

	public InstallContext() {
		String is_debug = System.getProperty("EOS_DEBUG", "false");
		if (is_debug.equals("true"))
			this.isDebug = true;
		else
			this.isDebug = false;
	}

	public String getStringValue(Object key) {
		Object obj = this.values.get(key);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	public Object getVariableValue(Object key) {
		String strKey = VariableUtil.getVariableKey(key);
		return this.values.get(strKey);
	}

	public void setVariableValue(Object key, Object value) {
		String strKey = VariableUtil.getVariableKey(key);
		setValueIgnorSpace(strKey, value);
	}

	public Object getValue(Object key) {
		return this.values.get(key);
	}

	public void setValue(Object key, Object value) {
		setValueIgnorSpace(key, value);
	}

	public void putAll(Map map) {
		Set keySets = map.keySet();
		for (Iterator localIterator = keySets.iterator(); localIterator.hasNext();) {
			Object key = localIterator.next();
			Object value = map.get(key);
			setValueIgnorSpace(key, value);
		}
	}

	public Map getAll() {
		return this.values;
	}

	public List getKeysLike(String prefix) {
		List keys = new ArrayList();
		Iterator allKeys = this.values.keySet().iterator();
		String key = null;
		while (allKeys.hasNext()) {
			key = allKeys.next().toString().trim();
			if (key.startsWith(prefix)) {
				keys.add(key);
			}
		}
		return keys;
	}

	public boolean isSilentInstall() {
		return this.isSilentInstall;
	}

	public void setSilentInstall(boolean b) {
		this.isSilentInstall = b;
	}

	public AbstractSetupPanel getCurrentSetupPane() {
		return this.currentSetupPanel;
	}

	public void setCurrentSetupPane(AbstractSetupPanel panel) {
		this.currentSetupPanel = panel;
	}

	public ActionsTrack getActionsTrack() {
		return this.actionsTrack;
	}

	public void setActionsTrack(ActionsTrack track) {
		this.actionsTrack = track;
	}

	public Object remove(Object key) {
		return this.values.remove(key);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator allKeys = this.values.keySet().iterator();
		String key = null;
		while (allKeys.hasNext()) {
			key = allKeys.next().toString().trim();

			if ((StringUtils.contains(key.toLowerCase(), "password")) || (StringUtils.contains(key, "EOS_SYSADMIN_PWD")))
				sb.append(key).append("=").append("******").append("\n");
			else {
				sb.append(key).append("=").append(this.values.get(key)).append("\n");
			}
		}
		return sb.toString();
	}

	public boolean isDebug() {
		return this.isDebug;
	}

	private void setValueIgnorSpace(Object key, Object value) {
		if ((value != null) && (value.getClass().isAssignableFrom(String.class))) {
			value = StringUtils.trim((String) value);
			this.values.put(key, value);
		} else {
			this.values.put(key, value);
		}
	}
}