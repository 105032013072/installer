package com.bosssoft.platform.installer.core.option;

public class RegisterDef {
   private String name;
   
   private String workDir;
   
   private String registerCmd;
   
   private String startCmd;
   
   private  String stopCmd;
   
   private String removeCmd;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getWorkDir() {
	return workDir;
}

public void setWorkDir(String workDir) {
	this.workDir = workDir;
}

public String getRegisterCmd() {
	return registerCmd;
}

public void setRegisterCmd(String registerCmd) {
	this.registerCmd = registerCmd;
}

public String getStartCmd() {
	return startCmd;
}

public void setStartCmd(String startCmd) {
	this.startCmd = startCmd;
}

public String getStopCmd() {
	return stopCmd;
}

public void setStopCmd(String stopCmd) {
	this.stopCmd = stopCmd;
}

public String getRemoveCmd() {
	return removeCmd;
}

public void setRemoveCmd(String removeCmd) {
	this.removeCmd = removeCmd;
}
   
   
}
