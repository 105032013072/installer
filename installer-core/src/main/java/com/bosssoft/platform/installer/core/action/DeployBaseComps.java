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

public class DeployBaseComps implements IAction {
	private transient Logger logger = Logger.getLogger(getClass());

	private CompDeployer deployer = null;

	public void execute(IContext context, Map parameters) throws InstallException {
		this.deployer = new CompDeployer(context);

		List<ModuleDef> compsList = ComponentsDefHelper.getBaseCompsDef();
		ModuleDef[] comps = (ModuleDef[]) compsList.toArray(new ModuleDef[0]);
		Arrays.sort(comps);

		for (ModuleDef md : comps) {
			deployComp(md, md.getModulePath(), context);
		}
	}

	private void deployComp(CompDef oc, String compsDir, IContext context) {
		if ((oc.getFilesPath() != null) && (oc.getFilesPath().length() > 0)) {
			compsDir = compsDir + "/" + oc.getFilesPath();
		}
		this.logger.debug("deploy component:" + compsDir);
		this.deployer.deploy(compsDir);

		List<CompDef> subComps = oc.getComps();
		if ((subComps != null) && (subComps.size() > 0)) {
			for (CompDef sc : subComps)
				deployComp(sc, compsDir, context);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}