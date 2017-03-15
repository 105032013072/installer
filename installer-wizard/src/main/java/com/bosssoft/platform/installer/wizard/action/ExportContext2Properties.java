package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class ExportContext2Properties implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String exportFilePath = parameters.get("EXPORT_FILE").toString();
		String keys = parameters.get("KEYS").toString();

		export(exportFilePath, keys, context);
	}

	private void export(String exportFilePath, String keys, IContext context) {
		File f = new File(exportFilePath);
		Properties p = new Properties();
		String[] ks = keys.split(",");

		for (String key : ks) {
			p.setProperty(key, context.getStringValue(key));
		}
		try {
			p.store(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			this.logger.warn(e);
		} catch (IOException e) {
			this.logger.warn(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}