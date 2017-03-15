package com.bosssoft.platform.installer.core.option;

import java.util.HashMap;
import java.util.List;

import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class ComponentsDefHelper {
	private static List<ModuleDef> baseComps = null;
	private static List<ModuleDef> optionComps = null;

	private static HashMap<String, CompDef> compsMap = new HashMap();

	public static List<ModuleDef> getBaseCompsDef() {
		if (baseComps == null) {
			String path = InstallerFileManager.getBaseCompsDir();
			baseComps = ComponentsDefLoader.loadOptionComps(path);
			if (baseComps != null) {
				for (CompDef comp : baseComps) {
					loadCompDef2Map(comp);
				}
			}
		}
		return baseComps;
	}

	private static void loadCompDef2Map(CompDef comp) {
		compsMap.put(comp.getId(), comp);
		List<CompDef> list = comp.getComps();
		if ((list == null) || (list.size() == 0))
			return;
		for (CompDef c : list)
			loadCompDef2Map(c);
	}

	public static List<ModuleDef> getOptionCompsDef() {
		if (optionComps == null) {
			String path = InstallerFileManager.getOptionCompsDir();
			optionComps = ComponentsDefLoader.loadOptionComps(path);
			if (optionComps != null) {
				for (CompDef comp : optionComps) {
					loadCompDef2Map(comp);
				}
			}
		}

		return optionComps;
	}

	public static CompDef getCompDef(String id) {
		return (CompDef) compsMap.get(id);
	}

	public static void main(String[] args) {
		List list = getBaseCompsDef();

		System.out.println("...");
	}
}