package com.bosssoft.platform.installer.core.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.cfg.Actions;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.ActionsUtil;

public class GroupAction extends AbstractAction {
	private String actionGroupID = null;

	public GroupAction(String id) {
		this.actionGroupID = id;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if (this.actionGroupID == null)
			return;
		Actions actions = InstallRuntime.INSTANCE.getConfig().getActions(this.actionGroupID);

		if (actions != null) {
			List list = actions.getActions();

			ActionsUtil.run(context, list);
		}
	}

	public List<IAction> getActions() {
		Actions actions = InstallRuntime.INSTANCE.getConfig().getActions(this.actionGroupID);

		if (actions != null) {
			return actions.getActions();
		}
		return new ArrayList();
	}
}