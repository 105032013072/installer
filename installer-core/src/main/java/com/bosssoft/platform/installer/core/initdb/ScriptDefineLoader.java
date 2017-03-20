package com.bosssoft.platform.installer.core.initdb;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ScriptDefineLoader {
	private static Logger logger = LoggerFactory.getLogger(ScriptDefineLoader.class);

	private static Map<String, Map<String, List<String[]>>> scriptDefineMap = null;

	private static Map<String, String> testTableMap = null;

	private static ClassLoader loader = ScriptDefineLoader.class.getClassLoader();

	private static DocumentBuilder docbuilder = null;

	private static void init() {
		try {
			Enumeration e = loader.getResources("META-INF/scripts/script-define.xml");
			while (e.hasMoreElements()) {
				URL defineUrl = (URL) e.nextElement();
				loadScript(null, defineUrl);
			}
		} catch (Throwable t) {
			logger.debug("init database script error!", t);
		}
	}

	public static void loadScript(String rootDir, URL defineUrl) {
		if (scriptDefineMap == null) {
			scriptDefineMap = new LinkedHashMap();
		}

		if (testTableMap == null) {
			testTableMap = new LinkedHashMap();
		}

		if (docbuilder == null) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				docbuilder = dbf.newDocumentBuilder();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		InputStream in = null;
		try {
			in = defineUrl.openStream();
			InputSource isrc = new InputSource(new InputStreamReader(in, "UTF-8"));
			Document doc = docbuilder.parse(isrc);
			Element docElec = doc.getDocumentElement();
			NodeList componentNodeList = docElec.getElementsByTagName("component");
			for (int i = 0; i < componentNodeList.getLength(); i++) {
				Element componentElem = (Element) componentNodeList.item(i);
				String componentName = componentElem.getAttribute("name");
				Map componentMap = (Map) scriptDefineMap.get(componentName);
				if (componentMap == null) {
					componentMap = new LinkedHashMap();
					scriptDefineMap.put(componentName, componentMap);
				}
				String testTableName = componentElem.getAttribute("test-table");
				testTableMap.put(componentName, testTableName);

				NodeList groupNodeList = componentElem.getElementsByTagName("group");
				for (int j = 0; j < groupNodeList.getLength(); j++) {
					Element groupElem = (Element) groupNodeList.item(j);
					String groupType = groupElem.getAttribute("type");
					List scriptList = (List) componentMap.get(groupType);
					if (scriptList == null) {
						scriptList = new ArrayList();
						componentMap.put(groupType, scriptList);
					}
					NodeList scriptNodeList = groupElem.getElementsByTagName("script");
					for (int k = 0; k < scriptNodeList.getLength(); k++) {
						Element scriptElem = (Element) scriptNodeList.item(k);
						addScript(scriptList, rootDir, scriptElem.getAttribute("uri"), scriptElem.getAttribute("encoding"));
					}
				}
			}
		} catch (Throwable t) {
			logger.debug(defineUrl.getFile(), t);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Throwable ignore) {
			}
		}
	}

	private static void addScript(List<String[]> scriptList, String rootDir, String scirptUri, String encoding) {
		try {
			if ((scirptUri == null) || (scirptUri.length() == 0)) {
				return;
			}
			Enumeration e = loader.getResources(scirptUri);
			while (e.hasMoreElements()) {
				URL defineUrl = (URL) e.nextElement();
				scriptList.add(new String[] { defineUrl.toExternalForm(), encoding });
			}
			if ((rootDir == null) || (rootDir.length() == 0)) {
				return;
			}
			File scriptFile = new File(rootDir, scirptUri);
			if (scriptFile.exists())
				scriptList.add(new String[] { scriptFile.toURL().toExternalForm(), encoding });
		} catch (Throwable e) {
			logger.debug("addScript error!", e);
		}
	}

	public static void destory() {
		if (scriptDefineMap != null) {
			scriptDefineMap.clear();
			scriptDefineMap = null;
		}

		if (testTableMap != null) {
			testTableMap.clear();
			testTableMap = null;
		}
		docbuilder = null;
	}

	public static List<String[]> getDefineScripts(String componentName, String groupType) {
		List scriptList = new ArrayList();
		if (scriptDefineMap != null) {
			Map map = (Map) scriptDefineMap.get(componentName);
			if (map != null) {
				List list = (List) map.get(groupType);
				if (list != null) {
					scriptList.addAll(list);
				}
			}
		}

		return scriptList;
	}

	public static String[] getComponentNames() {
		if (scriptDefineMap != null) {
			return (String[]) scriptDefineMap.keySet().toArray(new String[0]);
		}
		return new String[0];
	}

	public static String getTestTableName(String componentName) {
		if (testTableMap != null) {
			return (String) testTableMap.get(componentName);
		}
		return null;
	}

	static {
		init();
	}
}