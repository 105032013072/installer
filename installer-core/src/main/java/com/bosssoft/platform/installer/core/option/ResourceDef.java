package com.bosssoft.platform.installer.core.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceDef {
  private String name;
  
  private String sourcePath;
  
  private String destPath;
  
  private String home;
  
  private Map<String,String> params=new HashMap<String, String>();
  
  private Boolean isInstall;
  
  private String InstallFiles;//silent安装

  private Boolean required=false;//是否必须安装
  
  private List<Integer> ports=new ArrayList<Integer>();
  
  private String portConfigFile;//该应用端口的配置文件
  
  private List<String> portConfigKeys;
  
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

public Boolean getIsInstall() {
	return isInstall;
}

public void setIsInstall(Boolean isInstall) {
	this.isInstall = isInstall;
}

public Map<String, String> getParams() {
	return params;
}

public String getInstallFiles() {
	return InstallFiles;
}

public void setInstallFiles(String installFiles) {
	InstallFiles = installFiles;
}

public Boolean getRequired() {
	return required;
}

public void setRequired(Boolean required) {
	this.required = required;
}

public List<Integer> getPorts() {
	return ports;
}
  
public void addPort(Integer port){
	this.ports.add(port);
}

public void setPorts(List<Integer> ports) {
	this.ports = ports;
}

public String getPortConfigFile() {
	return portConfigFile;
}

public void setPortConfigFile(String portConfigFile) {
	this.portConfigFile = portConfigFile;
}

public List<String> getPortConfigKeys() {
	return portConfigKeys;
}

public void setPortConfigKeys(List<String> portConfigKeys) {
	this.portConfigKeys = portConfigKeys;
}


}
