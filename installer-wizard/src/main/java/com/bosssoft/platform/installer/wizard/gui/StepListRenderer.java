package com.bosssoft.platform.installer.wizard.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.cfg.Step;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.gui.IRenderer;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.core.util.ReflectUtil;

public class StepListRenderer implements IRenderer {
	private static List<StepItem> stepList = new ArrayList();
	private static final String CONFIG_FILE_NAME = "steps-config.xml";
	private static final String OPTIONS_STEP_ID = "options";
	private static final String TAG_STEP_ITEM = "stepitem";

	static {
		loadConfig();
	}

	private static void loadConfig() {
		File file = InstallerFileManager.getConfigFile(CONFIG_FILE_NAME);

		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			return;
		}
		InputStream stream=null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Document doc = null;
		try {
			doc = getXmlDocument(stream);
		} catch (DocumentException e1) {
			e1.printStackTrace();

			return;
		}

		if (doc == null) {
			return;
		}
		Element root = doc.getRootElement();
		Element element = null;
		StepItem stepItem = null;
		for (Iterator i = root.elementIterator(TAG_STEP_ITEM); i.hasNext();) {
			element = (Element) i.next();
			stepItem = new StepItem();
			stepItem.setId(element.attributeValue("id"));
			if ((!stepItem.getId().toLowerCase().equals(OPTIONS_STEP_ID)) || (!noOptions())) {
				stepItem.setStepId(element.attributeValue("stepid"));
				stepItem.setNameKey(element.attributeValue("namekey"));

				stepList.add(stepItem);
			}
		}
	}

	private static boolean noOptions() {
		String optionDirPath = InstallerFileManager.getOptionCompsDir();
		File optionDir = new File(optionDirPath);

		if ((!optionDir.exists()) || (optionDir.isFile())) {
			return true;
		}
		boolean noOption = true;
		File[] files = optionDir.listFiles();
		File optionDescFile = null;
		for (File f : files) {
			if (!f.isFile()) {
				optionDescFile = new File(f, "module_info.xml");
				if ((optionDescFile.exists()) && (optionDescFile.isFile())) {
					noOption = false;
					break;
				}
			}
		}
		return noOption;
	}

	private static Document getXmlDocument(InputStream inputStream) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(inputStream);

		return doc;
	}

	public AbstractControlPanel getControlPanel(Step step) {
		if (step.getControlPanelClassName() == null)
			return null;
		String clazzName = step.getControlPanelClassName();
		Object instance = ReflectUtil.newInstanceBy(clazzName);
		AbstractControlPanel panel = null;
		if (instance == null) {
			return null;
		}
		panel = (AbstractControlPanel) instance;

		return panel;
	}

	public AbstractSetupPanel getSetupPanel(Step step) {
		AbstractSetupPanel panel = null;
		if (step.getSetupPanelClassName() == null)
			return null;
		String clazzName = step.getSetupPanelClassName();
		Object instance = ReflectUtil.newInstanceBy(clazzName);

		if (instance == null) {
			return null;
		}
		panel = (AbstractSetupPanel) instance;
		AbstractSetupPanel spanel = new StepListSetupPanel(step.getID(), panel, stepList);

		return spanel;
	}

	public static class StepItem {
		private String id = null;
		private String nameKey = null;
		private String stepId = null;

		public String getId() {
			return this.id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return I18nUtil.getString(this.nameKey);
		}

		public void setNameKey(String k) {
			this.nameKey = k;
		}

		public String getStepId() {
			return this.stepId;
		}

		public void setStepId(String sid) {
			this.stepId = sid;
		}

		public String toString() {
			return getName();
		}
	}
}