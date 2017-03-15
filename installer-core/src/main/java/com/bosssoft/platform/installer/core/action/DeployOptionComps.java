package com.bosssoft.platform.installer.core.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.CompDeployer;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;
import com.bosssoft.platform.installer.core.option.ModuleDef;

public class DeployOptionComps implements IAction {
	private transient Logger logger = Logger.getLogger(getClass());
	private CompDeployer deployer = null;

	public void execute(IContext context, Map parameters) throws InstallException {
		this.deployer = new CompDeployer(context);
		List<ModuleDef> compsList = ComponentsDefHelper.getOptionCompsDef();
		ModuleDef[] comps = (ModuleDef[]) compsList.toArray(new ModuleDef[0]);
		Arrays.sort(comps);

		List<String> module_options = Arrays.asList(context.getStringValue("MODULE_OPTIONS").split(","));
		for (ModuleDef md : comps)
			if (module_options.contains(md.getId()))
				deployComp(md, md.getModulePath(), context, module_options);
	}

	private void deployComp(CompDef oc, String compsDir, IContext context, List<String> module_options) {
		if ((oc.getFilesPath() != null) && (oc.getFilesPath().length() > 0)) {
			compsDir = compsDir + "/" + oc.getFilesPath();
		}

		this.logger.debug("deploy component:" + compsDir);

		if (module_options.contains(oc.getId())) {
			this.deployer.deploy(compsDir);
		}

		List<CompDef> subComps = oc.getComps();
		if ((subComps != null) && (subComps.size() > 0)) {
			for (CompDef sc : subComps)
				deployComp(sc, compsDir, context, module_options);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}