package com.bosssoft.platform.installer.core.util;

import java.util.List;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.action.UserAction;
import com.bosssoft.platform.installer.core.runtime.AbstractRunner;

public class ActionsUtil {
	public static void run(IContext context, List<IAction> actions) {
		if ((actions == null) || (actions.size() == 0))
			return;
		Map parameters = null;
		for (IAction action : actions) {
			if ((action instanceof UserAction))
				parameters = AbstractRunner.parseParameters(context, ((UserAction) action).getParameters());
			else {
				parameters = null;
			}
			action.execute(context, parameters);
		}
	}
}