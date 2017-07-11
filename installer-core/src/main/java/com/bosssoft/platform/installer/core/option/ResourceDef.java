package com.bosssoft.platform.installer.core.option;

import java.util.HashMap;
import java.util.Map;

public class ResourceDef {
  private String name;
  
  private String sourcePath;
  
  private String destPath;
  
  private String home;
  
  private Map<String,String> params=new HashMap<String, String>();

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getSourcePath() {
	return sourcePath;
}

public void setSourcePath(String sourcePath) {
	this.sourcePath = sourcePath;
}

public String getDestPath() {
	return destPath;
}

public void setDestPath(String destPath) {
	this.destPath = destPath;
}

public String getHome() {
	return home;
}

public void setHome(String home) {
	this.home = home;
}

public void setParams(String key,String value) {
	params.put(key, value);
}
  
  
}
