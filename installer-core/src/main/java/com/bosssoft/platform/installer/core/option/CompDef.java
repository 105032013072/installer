package com.bosssoft.platform.installer.core.option;

import java.util.ArrayList;
import java.util.List;

public class CompDef {
	private String id = null;
	private String longid = null;

	private String size = null;
	private String editable = null;
	private String selected = null;
	private String nameKey = null;
	private String descKey = null;
	private String dependedBy = null;
	private String depends = "";
	private String filesPath = null;
	private String type = null;

	private List<CompDef> comps = new ArrayList();

	public String getDependedBy() {
		return this.dependedBy;
	}

	public void setDependedBy(String dependedBy) {
		this.dependedBy = dependedBy;
	}

	public String getDepends() {
		return this.depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
	}

	public String getDescKey() {
		return this.descKey;
	}

	public void setDescKey(String desckey) {
		this.descKey = this.descKey;
	}

	public String getFilesPath() {
		return this.filesPath;
	}

	public void setFilesPath(String filesPath) {
		this.filesPath = filesPath;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLongid() {
		return this.longid;
	}

	public void setLongid(String longid) {
		this.longid = longid;
	}

	public String getNameKey() {
		return this.nameKey;
	}

	public void setNameKey(String namekey) {
		this.nameKey = namekey;
	}

	public String getEditable() {
		return this.editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getSelected() {
		return this.selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getSize() {
		return this.size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addComp(CompDef comp) {
		this.comps.add(comp);
	}

	public CompDef getComp(String compName) {
		for (CompDef comp : this.comps) {
			if (comp.getId().equals(compName)) {
				return comp;
			}
		}
		return null;
	}

	public void setComps(List<CompDef> comps) {
		this.comps = comps;
	}

	public List<CompDef> getComps() {
		return this.comps;
	}

	public boolean isEditable() {
		if("true".equalsIgnoreCase(editable)) return true;
		else return false;
	}

	public boolean isSelected() {
		return (this.selected.equalsIgnoreCase("true"));
	}

	public String toString() {
		return getNameKey();
	}
}