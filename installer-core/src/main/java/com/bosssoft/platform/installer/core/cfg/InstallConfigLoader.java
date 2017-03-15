package com.bosssoft.platform.installer.core.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.core.action.Copy;
import com.bosssoft.platform.installer.core.action.Copydir;
import com.bosssoft.platform.installer.core.action.Copyfile;
import com.bosssoft.platform.installer.core.action.Delete;
import com.bosssoft.platform.installer.core.action.Echo;
import com.bosssoft.platform.installer.core.action.GroupAction;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.action.If;
import com.bosssoft.platform.installer.core.action.MkDir;
import com.bosssoft.platform.installer.core.action.Move;
import com.bosssoft.platform.installer.core.action.SetValue;
import com.bosssoft.platform.installer.core.action.Unzip;
import com.bosssoft.platform.installer.core.action.UserAction;
import com.bosssoft.platform.installer.core.action.War;
import com.bosssoft.platform.installer.core.action.Zip;

public class InstallConfigLoader {
	private static final String TAG_VARIABLE = "variable";
	private static final String TAG_LISTENER = "listener";
	private static final String TAG_STEPS = "steps";
	private static final String TAG_STEP = "step";
	private static final String TAG_LOADPROPERTIES = "loadproperties";
	private static final String TAG_ACTION = "action";
	private static final String TAG_ACTIONS = "actions";
	private static final String TAG_ACTIONSCALL = "actionscall";
	private static final String TAG_NEXTSTEP = "nextstep";
	private static final String TAG_BRANCH = "branch";
	private static final String TAG_GUI = "gui";
	private static final String TAG_MKDIR = "mkdir";
	private static final String TAG_COPY = "copy";
	private static final String TAG_ZIP = "zip";
	private static final String TAG_UNZIP = "unzip";
	private static final String TAG_MOVE = "move";
	private static final String TAG_WAR = "war";
	private static final String TAG_DELETE = "delete";
	private static final String TAG_PARAMETER = "parameter";
	private static final String TAG_FILESET = "fileset";
	private static final String TAG_COPYDIR = "copydir";
	private static final String TAG_COPYFILE = "copyfile";
	private static final String TAG_EXCLUDE = "exclude";
	private static final String TAG_INCLUDE = "include";
	private static final String TAG_RENDERER = "renderer";
	private static final String TAG_ECHO = "echo";
	private static final String TAG_IF = "if";
	private static final String TAG_EQUALS = "equals";
	private static final String TAG_THEN = "then";
	private static final String TAG_ELSE = "else";
	private static final String TAG_SETVALUE = "setvalue";

	public static InstallConfig loadConfig(String configFilePath) throws FileNotFoundException {
		File file = new File(configFilePath);

		if (!file.exists()) {
			throw new FileNotFoundException(configFilePath);
		}
		if (file.isDirectory()) {
			throw new IllegalStateException("Not is a file." + configFilePath);
		}
		InputStream stream = new FileInputStream(file);

		Document doc = null;
		try {
			doc = getXmlDocument(stream);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		if (doc == null) {
			return null;
		}
		return loadConfig(doc);
	}

	private static InstallConfig loadConfig(Document doc) {
		InstallConfig installConfig = new InstallConfig();

		Element root = null;
		Element level1 = null;
		Element level2 = null;

		root = doc.getRootElement();

		for (Iterator i = root.elementIterator(TAG_VARIABLE); i.hasNext();) {
			level1 = (Element) i.next();
			installConfig.addVariable(level1.attributeValue("name"), level1.attributeValue("value"));
		}

		for (Iterator i = root.elementIterator(TAG_LISTENER); i.hasNext();) {
			level1 = (Element) i.next();
			installConfig.addListener(level1.attributeValue("classname"));
		}

		for (Iterator i = root.elementIterator(TAG_LOADPROPERTIES); i.hasNext();) {
			level1 = (Element) i.next();
			installConfig.addLoadPropeties(level1.attributeValue("srcfile"));
		}

		level1 = root.element(TAG_RENDERER);
		if (level1 != null) {
			String rendererClassName = level1.attributeValue("classname");
			if (rendererClassName != null) {
				installConfig.setRenderer(rendererClassName);
			}

		}

		level1 = root.element(TAG_STEPS);
		parseSteps(level1, installConfig);

		Actions actions = null;
		IAction action = null;
		for (Iterator i = root.elementIterator(TAG_ACTIONS); i.hasNext();) {
			level1 = (Element) i.next();

			actions = new Actions();
			actions.setID(level1.attributeValue("id"));
			for (Iterator j = level1.elementIterator(); j.hasNext();) {
				level2 = (Element) j.next();

				action = parseAction(level2);

				if (action != null)
					actions.addAction(action);
			}
			installConfig.putActions(actions.getID(), actions);
		}

		return installConfig;
	}

	private static void parseSteps(Element level1, InstallConfig installConfig) {
		String firstStepID = level1.attributeValue("firststep");
		if (firstStepID == null) {
			throw new InstallException("The attribute value is invalid: firststep");
		}
		installConfig.setFirstStepID(firstStepID);
		Element stepEle = null;
		for (Iterator i = level1.elementIterator(); i.hasNext();) {
			stepEle = (Element) i.next();
			if (stepEle.getName().equals("step")) {
				Step step = parseStep(stepEle);
				installConfig.putStep(step.getID(), step);
			}
		}
	}

	private static Step parseStep(Element element) {
		String auto = null;
		Step step = new Step();
		step.setID(element.attributeValue("id"));
		auto = element.attributeValue("auto");

		if (auto != null) {
			step.setAuto("true".equalsIgnoreCase(auto));
		}
		step.setInterceptorClassName(element.attributeValue("interceptor"));

		String elementName = null;
		Element subEle = null;
		IAction action = null;
		for (Iterator j = element.elementIterator(); j.hasNext();) {
			subEle = (Element) j.next();
			elementName = subEle.getName();
			if (elementName.equals(TAG_GUI)) {
				step.setSetupPanelClassName(subEle.attributeValue("setuppanel"));
				step.setControllPanelClassName(subEle.attributeValue("controlpanel"));
				String render = subEle.attributeValue("render");
				step.setRender((render == null) || (!render.equalsIgnoreCase("false")));
				step.setHasGUI(true);
			} else if (elementName.equals(TAG_ACTION)) {
				action = parseUserAction(subEle);
				if (action != null)
					step.addAction(action);
			} else if (elementName.equals(TAG_NEXTSTEP)) {
				parseNextStep(subEle, step);
			} else if (elementName.equals(TAG_ACTIONSCALL)) {
				step.addActionsRef(subEle.attributeValue("id"));
			} else {
				action = parseSysAction(subEle);
				if (action != null)
					step.addAction(action);
			}
		}
		return step;
	}

	public static IAction parseAction(Element element) {
		IAction action = null;
		if (element.getName().equals(TAG_ACTION))
			action = parseUserAction(element);
		else {
			action = parseSysAction(element);
		}
		return action;
	}

	private static IAction parseSysAction(Element element) {
		AbstractAction action = null;

		String tagName = element.getName();
		if (tagName.equals(TAG_COPY))
			action = parseActionCopy(element);
		else if (tagName.equals(TAG_MKDIR))
			action = parseActionMkDir(element);
		else if (tagName.equals(TAG_COPYDIR))
			action = parseActionCopydir(element);
		else if (tagName.equals(TAG_COPYFILE))
			action = parseActionCopyfile(element);
		else if (tagName.equals(TAG_ZIP))
			action = parseActionZip(element);
		else if (tagName.equals(TAG_UNZIP))
			action = parseActionUnzip(element);
		else if (tagName.equals(TAG_MOVE))
			action = parseActionMove(element);
		else if (tagName.equals(TAG_WAR))
			action = parseActionWar(element);
		else if (tagName.equals(TAG_DELETE))
			action = parseActionDelete(element);
		else if (tagName.equals(TAG_ECHO))
			action = parseActionEcho(element);
		else if (tagName.equals(TAG_IF))
			action = parseActionIf(element);
		else if (tagName.equals(TAG_ACTIONSCALL))
			action = parseActionCall(element);
		else if (tagName.equals(TAG_SETVALUE)) {
			action = parseActionSetValue(element);
		}

		if (action != null) {
			if (element.attributeValue("scale") != null) {
				String scale = element.attributeValue("scale");
				action.setScale(getScaleIntValue(scale));
			}

			if (element.attributeValue("strategy") != null) {
				action.setStrategy(element.attributeValue("strategy"));
			}
		}
		return action;
	}

	private static void parseNextStep(Element element, Step step) {
		String defaultNextStepID = element.attributeValue("default");
		String discriminator = element.attributeValue("discriminator");
		if (defaultNextStepID != null)
			step.setDefaultNextStepID(defaultNextStepID);
		if (discriminator != null) {
			step.setNextStepDiscriminator(discriminator);

			Element branch = null;
			for (Iterator j = element.elementIterator(TAG_BRANCH); j.hasNext();) {
				branch = (Element) j.next();

				step.setNextTaskID(branch.attributeValue("id"), branch.attributeValue("stepid"));
			}
		}
	}

	private static IAction parseUserAction(Element element) {
		UserAction action = new UserAction();
		String className = element.attributeValue("classname");

		if (className == null) {
			throw new InstallException("Config file error, <action/>'s attribute 'classname' is null!");
		}
		action.setActionClassName(className);
		action.setName(element.attributeValue("name"));
		if (element.attributeValue("scale") != null) {
			String scale = element.attributeValue("scale");
			action.setScale(getScaleIntValue(scale));
		}

		if (element.attributeValue("strategy") != null) {
			action.setStrategy(element.attributeValue("strategy"));
		}

		Element param = null;
		for (Iterator i = element.elementIterator(TAG_PARAMETER); i.hasNext();) {
			param = (Element) i.next();
			action.putParameter(param.attributeValue("name"), param.attributeValue("value"));
		}
		return action;
	}

	private static int getScaleIntValue(String s) {
		if ((s == null) || (s.trim().equals(""))) {
			return 0;
		}
		int i = 1;
		try {
			i = Integer.parseInt(s);
		} catch (Exception e) {
			i = 1;
		}
		return i;
	}

	private static Copy parseActionCopy(Element element) {
		Copy copy = new Copy();
		copy.setFile(element.attributeValue("file"));
		copy.setDestFile(element.attributeValue("tofile"));
		copy.setDestDir(element.attributeValue("todir"));
		copy.setOverwrite(element.attributeValue("overwrite") == null ? false : element.attributeValue("overwrite").equalsIgnoreCase("true"));

		return copy;
	}

	private static Move parseActionMove(Element element) {
		Move move = new Move();
		move.setFile(element.attributeValue("file"));
		move.setDestFile(element.attributeValue("tofile"));
		move.setDestDir(element.attributeValue("todir"));
		move.setOverwrite(element.attributeValue("overwrite") == null ? false : element.attributeValue("overwrite").equalsIgnoreCase("true"));

		return move;
	}

	private static MkDir parseActionMkDir(Element element) {
		MkDir md = new MkDir();
		md.setDir(element.attributeValue("dir"));
		return md;
	}

	private static Unzip parseActionUnzip(Element element) {
		Unzip unzip = new Unzip();

		unzip.setSrc(element.attributeValue("src"));
		unzip.setDest(element.attributeValue("dest"));
		unzip.setOverwrite(element.attributeValue("overwrite") == null ? false : element.attributeValue("overwrite").equalsIgnoreCase("true"));

		return unzip;
	}

	private static Zip parseActionZip(Element element) {
		Zip zip = new Zip();
		zip.setDestFile(element.attributeValue("destfile"));

		zip.setBaseDir(element.attributeValue("basedir"));
		zip.setIncludes(element.attributeValue("includes"));
		zip.setExcludes(element.attributeValue("excludes"));

		return zip;
	}

	private static War parseActionWar(Element element) {
		War war = new War();
		return war;
	}

	private static Delete parseActionDelete(Element element) {
		Delete delete = new Delete();
		delete.setDir(element.attributeValue("dir"));
		delete.setFile(element.attributeValue("file"));

		return delete;
	}

	private static Echo parseActionEcho(Element element) {
		Echo echo = new Echo();
		echo.setMessage(element.attributeValue("message"));
		echo.setLevel(element.attributeValue("level"));

		return echo;
	}

	private static SetValue parseActionSetValue(Element element) {
		SetValue sv = new SetValue();
		sv.setKey(element.attributeValue("key"));
		sv.setValue(element.attributeValue("value"));
		return sv;
	}

	private static If parseActionIf(Element element) {
		If ifa = new If();
		Element subele = element.element("equals");
		ifa.setLeftValue(subele.attributeValue("arg1"));
		ifa.setRightValue(subele.attributeValue("arg2"));

		IAction action = null;
		for (Iterator i = element.element("then").elementIterator(); i.hasNext();) {
			subele = (Element) i.next();
			action = parseAction(subele);
			if (action != null) {
				ifa.addThenAction(action);
			}
		}
		if (element.element("else") != null) {
			for (Iterator i = element.element("else").elementIterator(); i.hasNext();) {
				subele = (Element) i.next();
				action = parseAction(subele);
				if (action != null)
					ifa.addElseAtion(action);
			}
		}
		return ifa;
	}

	private static Copydir parseActionCopydir(Element element) {
		Copydir cpdir = new Copydir();
		cpdir.setSrcDir(element.attributeValue("src"));
		cpdir.setDestDir(element.attributeValue("dest"));

		return cpdir;
	}

	private static GroupAction parseActionCall(Element element) {
		GroupAction ga = new GroupAction(element.attributeValue("id"));
		return ga;
	}

	private static Copyfile parseActionCopyfile(Element element) {
		Copyfile cf = new Copyfile();
		cf.setSrc(element.attributeValue("src"));
		cf.setDest(element.attributeValue("dest"));
		cf.setOverwrite(element.attributeValue("overwrite") == null ? false : element.attributeValue("overwrite").equalsIgnoreCase("true"));

		return cf;
	}

	public static Document getXmlDocument(InputStream inputStream) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(inputStream);

		return doc;
	}

	public static void main(String[] args) {
		String path = "D:\\dev\\workspace\\eos63\\install-template\\installer\\config\\install.xml";
		try {
			InstallConfig config = loadConfig(path);
			System.out.println("...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}