package com.bosssoft.platform.installer.jee.server.internal;

import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.ITargetModel;

public class ApplicationModelImpl implements IApplicationModel {
	private String appName;
	private String deployPath;
	private ITargetModel targetModel;

	public ApplicationModelImpl(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return this.appName;
	}

	public String getDeployPath() {
		return this.deployPath;
	}

	public void setDeployPath(String deployPath) {
		this.deployPath = deployPath;
	}

	public ITargetModel getTargetModel() {
		return this.targetModel;
	}

	public void setTargetModel(ITargetModel targetModel) {
		this.targetModel = targetModel;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int hashCode() {
		int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.appName == null ? 0 : this.appName.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationModelImpl other = (ApplicationModelImpl) obj;
		if (this.appName == null) {
			if (other.appName != null)
				return false;
		} else if (!this.appName.equals(other.appName))
			return false;
		return true;
	}
}