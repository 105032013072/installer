package com.bosssoft.platform.installer.jee.server.internal;

import java.util.ArrayList;
import java.util.List;

import com.bosssoft.platform.installer.jee.server.ITargetModel;

public class TargetModelImpl implements ITargetModel {
	private String name;
	private boolean cluster = false;
	private List<ITargetModel> children = new ArrayList<ITargetModel>(2);

	public TargetModelImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isCluster() {
		return this.cluster;
	}

	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}

	public ITargetModel[] getChildren() {
		return (ITargetModel[]) this.children.toArray(new ITargetModel[this.children.size()]);
	}

	public void addChild(ITargetModel targetModel) {
		this.children.add(targetModel);
	}

	public void removeChild(ITargetModel targetModel) {
		this.children.remove(targetModel);
	}

	public int hashCode() {
		int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.children == null ? 0 : this.children.hashCode());
		result = PRIME * result + (this.cluster ? 1231 : 1237);
		result = PRIME * result + (this.name == null ? 0 : this.name.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetModelImpl other = (TargetModelImpl) obj;
		if (this.children == null) {
			if (other.children != null)
				return false;
		} else if (!this.children.equals(other.children))
			return false;
		if (this.cluster != other.cluster)
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}
}