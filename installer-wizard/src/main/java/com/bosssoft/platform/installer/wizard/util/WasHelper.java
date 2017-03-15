package com.bosssoft.platform.installer.wizard.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.XmlHelper;

public class WasHelper {
	protected static transient Logger logger = Logger.getLogger(WasHelper.class.getName());
	public static final int WAS6_1 = 3;
	public static final int WAS6_1NDCLUSTER = 6;
	public static final String VAR_APP_INSTALL_ROOT = "APP_INSTALL_ROOT";
	private IWasIdentifier identifier = null;

	public WasHelper(int version) {
		if (version == 3)
			this.identifier = new Was61Identifier();
		else if (version == 6)
			this.identifier = new Was61NDIdentifier();
		else
			throw new IllegalArgumentException("Not supported WAS version as " + version);
	}

	public String setWasHome(String profileHome) {
		return this.identifier.setWasHome(profileHome);
	}

	public int matches(String profileHome) {
		return this.identifier.matches(profileHome);
	}

	public String[] getCells(String profileHome) {
		return this.identifier.getCells(profileHome);
	}

	public String[] getNodes(String cellPath) {
		return this.identifier.getNodes(cellPath);
	}

	public String[] getServers(String nodePath) {
		return this.identifier.getServers(nodePath);
	}

	public String[] getClusters(String cellPath) {
		return this.identifier.getClusters(cellPath);
	}

	public String getNodePath(String cellPath, String nodeName) {
		return this.identifier.getNodePath(cellPath, nodeName);
	}

	public String getCellPath(String profileHome, String cellName) {
		return this.identifier.getCellPath(profileHome, cellName);
	}

	public String[] listClusterMemberProfileHome(String dmgrProfileHome, String cellName) throws DocumentException {
		String cellPath = this.identifier.getCellPath(dmgrProfileHome, cellName);
		String[] nodeNames = this.identifier.getNodes(cellPath);
		String[] memberProfileHomes = new String[nodeNames.length];
		for (int i = 0; i < nodeNames.length; i++) {
			String variables_file = this.identifier.getNodePath(cellPath, nodeNames[i]) + File.separator + "variables.xml";
			Document doc = XmlHelper.parse(new File(variables_file));
			Node v = doc.selectSingleNode("xmi:XMI/variables:VariableMap/entries[@symbolicName='USER_INSTALL_ROOT']");
			if (v == null)
				logger.error("can not find USER_INSTALL_ROOT of " + nodeNames[i]);
			else {
				memberProfileHomes[i] = v.valueOf("@value");
			}
		}
		return memberProfileHomes;
	}

	public String getNodeProfileHome(String nodePath) throws DocumentException {
		String currNodeProfile = null;
		String variables_file = nodePath + File.separator + "variables.xml";
		Document doc = XmlHelper.parse(new File(variables_file));
		Node v = doc.selectSingleNode("xmi:XMI/variables:VariableMap/entries[@symbolicName='USER_INSTALL_ROOT']");
		if (v == null)
			logger.error("can not find USER_INSTALL_ROOT of " + nodePath);
		else {
			currNodeProfile = v.valueOf("@value");
		}
		return currNodeProfile;
	}

	public WASServer[] listClusterMemberServers(String dmgrProfileHome, String cellName, String clusterName) throws DocumentException {
		String path = dmgrProfileHome + "/config/cells/" + cellName + "/clusters/" + clusterName + "/cluster.xml";
		Document doc = XmlHelper.parse(new File(path));
		List list = doc.selectNodes("topology.cluster:ServerCluster/members");
		WASServer[] memberServers = new WASServer[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Node node = (Node) list.get(i);
			WASServer server = new WASServer();
			server.setName(node.valueOf("@memberName"));
			server.setNode(node.valueOf("@nodeName"));

			String cellpath = this.identifier.getCellPath(dmgrProfileHome, cellName);
			String nodePath = this.identifier.getNodePath(cellpath, node.valueOf("@nodeName"));
			String serverPath = this.identifier.getServerPath(nodePath, server.getName());

			server.setPath(serverPath);

			String profileHome = getNodeProfileHome(nodePath);

			server.setProfileHome(profileHome);

			memberServers[i] = server;
		}
		return memberServers;
	}

	public static boolean executeCmd(String command, String wasProfileHome, String username, String password) throws Exception {
		/* 字符常量：判断jacl脚本是否执行正确 */
		String SCRIPT_EXECUTE_FLAG = "@!Script Executed Succeed!@";
		/* 字符常量：判断jacl脚本是否找不到资源 */
		String SCRIPT_RESOURCENOTFOUND_FLAG = "EOS_ResourceNotFoundException";

		String cmd = "";
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("windows") >= 0) {
			cmd = "\"" + wasProfileHome + "/bin/wsadmin.bat\"" + " -user " + username + " -password " + password + command;
			cmd = cmd.replaceAll("\\\\", "/");
		} else {
			cmd = wasProfileHome + "/bin/wsadmin.sh " + " -user " + username + " -password " + password + command;
		}
		logger.debug("wsadmin statement: " + cmd);
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(cmd);

			InputStream is = proc.getInputStream();
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			int b;
			while ((b = is.read()) >= 0) {
				bao.write(b);
			}
			bao.flush();
			String cmdResult = bao.toString();
			logger.debug(cmdResult);
			is.close();
			// 消息检测方法

			if (cmdResult.indexOf(SCRIPT_EXECUTE_FLAG) > 0) {
				logger.debug("the script executed successfully.");
			} else {
				if (cmdResult.indexOf(SCRIPT_RESOURCENOTFOUND_FLAG) > 0) {
				} else {
					logger.debug("script execute fail.");
					return false;
				}
			}

		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return true;
	}

	public String getNodeVariableVal(String nodePath, String varName) throws DocumentException {
		Document doc = getVariableDoc(nodePath + File.separator + "variables.xml");
		Node v = doc.selectSingleNode("xmi:XMI/variables:VariableMap/entries[@symbolicName='" + varName + "']");
		if (v == null) {
			v = doc.selectSingleNode("variables:VariableMap/entries[@symbolicName='" + varName + "']");
			if (v == null) {
				logger.error("can not find " + varName + " of in " + nodePath + ".Variable not define or unknown elmemt format.");
			}
		}
		String val = v.valueOf("@value");
		return getVariableValue(doc, val);
	}

	private static String getVariableValue(Document variableDoc, String valExp) {
		String value = "";
		int start = valExp.indexOf("${");
		int end = valExp.indexOf("}");
		if ((start < 0) || (end < 0) || (start > end)) {
			return valExp;
		}
		String key = valExp.substring(start + 2, end);
		Node v = variableDoc.selectSingleNode("xmi:XMI/variables:VariableMap/entries[@symbolicName='" + key + "']");
		if (v == null) {
			v = variableDoc.selectSingleNode("variables:VariableMap/entries[@symbolicName='" + key + "']");
			if (v == null) {
				logger.error(".Variable not define or unknown elmemt format.");
			}
		}

		if (v != null) {
			String objValue = v.valueOf("@value");
			value = valExp.substring(0, start) + objValue.toString() + valExp.substring(end + 1);
			return getVariableValue(variableDoc, value);
		}

		return null;
	}

	private Document getVariableDoc(String filePath) throws DocumentException {
		return XmlHelper.parse(new File(filePath));
	}

	public static void main(String[] args) throws DocumentException {
		WasHelper h = new WasHelper(3);
		String path = "d:/share/cluster.xml";
		h.listClusterMemberServers("d:/profiles/Dmgr01", "2003was61tCell01", "cluster0");
	}

	abstract class AbstractWasIdentifier implements WasHelper.IWasIdentifier {
		AbstractWasIdentifier() {
		}

		public abstract int matches(String paramString);

		public String setWasHome(String profileHome) {
			Properties prop = new Properties();
			String filePath = profileHome + "/properties/wsadmin.properties";
			try {
				prop.load(new BufferedInputStream(new FileInputStream(new File(filePath))));
				String path = prop.getProperty("com.ibm.ws.scripting.profiles");
				String[] paths = path.split(";");
				if (paths.length > 0) {
					String wasHome = paths[0].replace("\\", "/");
					wasHome = wasHome.substring(0, wasHome.lastIndexOf("/"));

					return wasHome.substring(0, wasHome.lastIndexOf("/"));
				}
			} catch (FileNotFoundException fe) {
				WasHelper.logger.error("'" + filePath + "' not found!");
				throw new InstallException("'" + filePath + "'not found!");
			} catch (IOException ioe) {
				WasHelper.logger.error("Can't access WAS_HOME!");
			}
			return null;
		}

		public String[] getCells(String profileHome) {
			StringBuffer cellsPath = new StringBuffer();
			cellsPath.append(profileHome).append(File.separator).append("config").append(File.separator).append("cells");
			WasHelper.logger.debug("CELLSPATH = " + cellsPath);
			File file = new File(cellsPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			ArrayList list = new ArrayList();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if (fs[i].isDirectory())
					list.add(fs[i].getName());
			}
			return (String[]) list.toArray(new String[0]);
		}

		public String getCellPath(String profileHome, String cellName) {
			StringBuffer cellsPath = new StringBuffer();
			cellsPath.append(profileHome).append(File.separator).append("config").append(File.separator).append("cells");
			WasHelper.logger.debug("CELLSPATH = " + cellsPath);
			File file = new File(cellsPath.toString());
			if ((!file.exists()) || (!file.isDirectory()))
				return null;
			File[] fs = file.listFiles();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if ((fs[i].isDirectory()) && (fs[i].getName().equals(cellName))) {
					return fs[i].getAbsolutePath();
				}
			}

			return null;
		}

		public String[] getNodes(String cellPath) {
			StringBuffer nodesPath = new StringBuffer();
			nodesPath.append(cellPath).append(File.separator).append("nodes");
			WasHelper.logger.debug("NODES_PATH = " + nodesPath);
			File file = new File(nodesPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			ArrayList list = new ArrayList();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if (fs[i].isDirectory())
					list.add(fs[i].getName());
			}
			return (String[]) list.toArray(new String[0]);
		}

		public String getNodePath(String cellPath, String nodeName) {
			StringBuffer nodesPath = new StringBuffer();
			nodesPath.append(cellPath).append(File.separator).append("nodes");
			WasHelper.logger.debug("NODES_PATH = " + nodesPath);
			File file = new File(nodesPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if ((fs[i].isDirectory()) && (fs[i].getName().equals(nodeName))) {
					return fs[i].getAbsolutePath();
				}
			}
			return null;
		}

		public String[] getServers(String nodePath) {
			StringBuffer serversPath = new StringBuffer();
			serversPath.append(nodePath).append(File.separator).append("servers");
			WasHelper.logger.debug("SERVERS_PATH = " + serversPath);
			File file = new File(serversPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			ArrayList list = new ArrayList();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if (fs[i].isDirectory())
					list.add(fs[i].getName());
			}
			return (String[]) list.toArray(new String[0]);
		}

		public String getServerPath(String nodePath, String serverName) {
			StringBuffer serversPath = new StringBuffer();
			serversPath.append(nodePath).append(File.separator).append("servers");
			WasHelper.logger.debug("SERVERS_PATH = " + serversPath);
			File file = new File(serversPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if ((fs[i].isDirectory()) && (fs[i].getName().equals(serverName))) {
					return fs[i].getAbsolutePath();
				}
			}
			return null;
		}

		public String[] getClusters(String cellPath) {
			StringBuffer clustersPath = new StringBuffer();
			clustersPath.append(cellPath).append(File.separator).append("clusters");
			WasHelper.logger.debug("CLUSTERS_PATH = " + clustersPath);
			File file = new File(clustersPath.toString());
			if ((!file.exists()) || (!file.isDirectory())) {
				return null;
			}
			File[] fs = file.listFiles();
			ArrayList list = new ArrayList();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if (fs[i].isDirectory())
					list.add(fs[i].getName());
			}
			return (String[]) list.toArray(new String[0]);
		}

		public String getClusterPath(String cellPath, String clusterName) {
			StringBuffer clustersPath = new StringBuffer();
			clustersPath.append(cellPath).append(File.separator).append("clusters");
			WasHelper.logger.debug("CLUSTERS_PATH = " + clustersPath);
			File file = new File(clustersPath.toString());
			if ((!file.exists()) || (!file.isDirectory()))
				return null;
			File[] fs = file.listFiles();
			int i = 0;
			for (int j = fs.length; i < j; i++) {
				if ((fs[i].isDirectory()) && (fs[i].getName().equals(clusterName))) {
					return fs[i].getAbsolutePath();
				}
			}
			return null;
		}
	}

	private static abstract interface IWasIdentifier {
		public abstract String setWasHome(String paramString);

		public abstract int matches(String paramString);

		public abstract String[] getCells(String paramString);

		public abstract String[] getNodes(String paramString);

		public abstract String[] getServers(String paramString);

		public abstract String[] getClusters(String paramString);

		public abstract String getCellPath(String paramString1, String paramString2);

		public abstract String getNodePath(String paramString1, String paramString2);

		public abstract String getServerPath(String paramString1, String paramString2);

		public abstract String getClusterPath(String paramString1, String paramString2);
	}

	public static class WASServer {
		String name;
		String node;
		String path;
		String profileHome;

		public String getProfileHome() {
			return this.profileHome;
		}

		public void setProfileHome(String profileHome) {
			this.profileHome = profileHome;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNode() {
			return this.node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getPath() {
			return this.path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}

	class Was61Identifier extends WasHelper.AbstractWasIdentifier {
		Was61Identifier() {
			super();
		}

		public int matches(String profileHome) {
			return 10;
		}

		public String[] getClusters(String cellPath) {
			throw new UnsupportedOperationException("Method getClusters() not supported.");
		}
	}

	class Was61NDIdentifier extends WasHelper.AbstractWasIdentifier {
		Was61NDIdentifier() {
			super();
		}

		public int matches(String profileHome) {
			return 10;
		}

	}
}