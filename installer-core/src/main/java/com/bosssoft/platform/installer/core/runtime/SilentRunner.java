package com.bosssoft.platform.installer.core.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.core.action.GroupAction;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.action.UserAction;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.cfg.Step;
import com.bosssoft.platform.installer.core.event.IStepInterceptor;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.core.util.ReflectUtil;

public class SilentRunner extends AbstractRunner {
	Logger logger = Logger.getLogger(getClass());
	private static final String DEFAULT_CONFIG_FILE_NAME = "silent_install.properties";
	public static final String IS_SILENT_INSTALL = "IS_SILENT_INSTALL";

	public SilentRunner(InstallConfig config) {
		super(config);
		InstallRuntime.INSTANCE.getContext().setValue("IS_SILENT_INSTALL", "true");
	}

	//加载安装配置（/core/silent_install.properties）
	protected void init() {
		String silentConfigPath = InstallerFileManager.getConfigDir() + File.separator + DEFAULT_CONFIG_FILE_NAME;

		Properties propers = new Properties();
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(silentConfigPath);
			propers.load(inStream);
			Enumeration keys = propers.keys();
			String key = null;
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				this.context.setValue(key, propers.get(key));
			}
		} catch (Exception e) {
			this.logger.error(e);
			throw new InstallException("The silent install config file not found!" + silentConfigPath);
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected void processStep(String stepID) {
		this.logger.debug("process Step .... " + stepID);
		this.currentStep = this.installConfig.getStep(stepID);

		if (this.currentStep == null) {
			return;
		}

		next();
	}

	private void next() {
		try {
			executeStepActions(this.currentStep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeStepActions(Step step) throws Exception {
		doActions();
	}

	void stepBegin(Step step) {
	}

	protected void doActions() {
		IContext context = InstallRuntime.INSTANCE.getContext();
		List actionsList = this.currentStep.getActions();

		Map parameters = null;
		AbstractAction action = null;

		ArrayList allActions = new ArrayList();
		int i = 0;
		for (int j = actionsList.size(); i < j; i++) {
			action = (AbstractAction) actionsList.get(i);
			if ((action instanceof GroupAction)) {
				List<IAction> subActions = ((GroupAction) action).getActions();
				for (IAction a : subActions)
					allActions.add(a);
			} else {
				allActions.add(action);
			}
		}
		i = 0;
		for (int j = allActions.size(); i < j; i++) {
			action = (AbstractAction) allActions.get(i);
			try {
				if ((action instanceof UserAction))
					parameters = parseParameters(context, ((UserAction) action).getParameters());
				else {
					parameters = null;
				}
				this.actionsTrack.addAction(action, parameters);
				action.execute(context, parameters);
			} catch (Throwable ex) {
				String msg = "execute action error:" + action;
				if (action.getStrategy().equals("ignore")) {
					this.logger.warn(msg);
				} else {
					this.logger.error(msg, ex);
					rollback();
					System.exit(0);
				}
			}
		}

		actionsFinished();
	}

	private void actionsFinished() {
		gotoNextStep(this.currentStep);
	}

	//判断是否有拦截器拦截
	private void gotoNextStep(Step step) {
		String nextStepID = getNextStepID(step);

		if (nextStepID == null) {
			return;
		}
		Step nextStep = this.installConfig.getStep(nextStepID);
		String interceptorClassName = nextStep.getInterceptorClassName();
		if (interceptorClassName != null) {
			IStepInterceptor interceptor = (IStepInterceptor) ReflectUtil.newInstanceBy(interceptorClassName);
			if (interceptor.isIgnoreThis(this.context)) {
				gotoNextStep(nextStep);
				return;
			}
			interceptor.beforeStep(this.context);
		}

		if (this.currentStep.getID().equals(nextStepID)) {
			return;
		}

		nextStep.setCallerID(this.currentStep.getID());
		processStep(nextStepID);
	}

	private void rollback() {
	}
}