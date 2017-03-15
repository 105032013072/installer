package com.bosssoft.platform.installer.core.option;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ActionsUtil;

public class CompDeployer implements Serializable {
	transient Logger logger = Logger.getLogger(getClass());

	private IContext context = null;
	public static final String DEPLOYDEF_FILENAME = "deploy.xml";

	public CompDeployer(IContext context) {
		this.context = context;
	}

	public void deploy(String optionDir) throws InstallException {
		if ((!optionDir.endsWith("\\")) && (!optionDir.endsWith("/"))) {
			optionDir = optionDir + "/";
		}

		String optionDeployFile = optionDir + "deploy.xml";
		DeployDef deploy=null;
		try {
			deploy = DeployDefLoader.getDeploy(optionDeployFile);
		} catch (FileNotFoundException e) {
			this.logger.error(e);
			throw new InstallException("Not Found File as " + optionDeployFile, e);
		}
		
		if (deploy == null) {
			this.logger.warn("Load deploy.xml failed!" + optionDeployFile);
			return;
		}

		List<IAction> actions = deploy.getActions();
		if (actions == null) {
			return;
		}
		this.context.setValue("CURRENT_COMP_DIR", optionDir);
		ActionsUtil.run(this.context, actions);
	}
}