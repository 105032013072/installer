package com.bosssoft.platform.installer.io.xml;

import com.sun.org.apache.xml.internal.serializer.DOMSerializer;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.sun.org.apache.xml.internal.serializer.Serializer;
import com.sun.org.apache.xml.internal.serializer.SerializerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlFile {
	public Logger logger = Logger.getLogger(getClass().getName());
	private static final String DEFAULT_ENCODING = "UTF-8";
	private Document document = null;
	private String sourceFilePath = null;
	private DocumentBuilder builder = null;

	private static char[] chars = { 'n', '\t', '\n', '\r', ' ' };

	public XmlFile(File file) throws IOException {
		if (file == null)
			throw new IllegalArgumentException("The file must not null!");
		if (!file.exists())
			throw new IllegalArgumentException("The file not exsit!" + file.getAbsolutePath());
		if (file.isDirectory()) {
			throw new IllegalArgumentException("The file could not be a directory!" + file.getAbsolutePath());
		}

		this.sourceFilePath = file.getCanonicalPath();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		try {
			this.builder = factory.newDocumentBuilder();

			this.builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});
			this.document = this.builder.parse(file);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public NodeList findNodes(String xql) throws XPathExpressionException {
		XPathFactory xfactory = XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		XPathExpression expr = xpath.compile(xql);

		Object result = expr.evaluate(this.document, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes;
	}

	public Node findNode(String xql) throws XPathExpressionException {
		NodeList nodes = findNodes(xql);
		if ((nodes == null) || (nodes.getLength() == 0))
			return null;
		return nodes.item(0);
	}

	public String getNodeValue(String xql) throws XPathExpressionException {
		Node node = findNode(xql);
		return node.getTextContent();
	}

	public String getNodeAttribute(String xql, String attributeName) throws XPathExpressionException {
		Node node = findNode(xql);
		if ((node != null) && ((node instanceof Element)))
			return ((Element) node).getAttribute(attributeName);
		return null;
	}

	public void setNodeValue(String xql, String value) throws XPathExpressionException {
		Node node = findNode(xql);
		if (node == null) {
			return;
		}
		node.setTextContent(value);
	}

	public void removeNode(String xql) throws XPathExpressionException {
		Node node = findNode(xql);
		Node parent = node.getParentNode();
		parent.removeChild(node);
	}

	public void addNode(String xql, String xmlStr) throws XPathExpressionException, SAXException, IOException {
		Node parent = findNode(xql);

		NodeList nodeList = getNodeList(xmlStr);
		Node n = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			n = nodeList.item(i);
			n = this.document.importNode(n, true);
			parent.appendChild(n);
		}
	}

	public void insertNodeBefore(String beforeNodeXql, String xmlStr) throws XPathExpressionException, SAXException, IOException {
		Node refNode = findNode(beforeNodeXql);

		Element root = this.document.getDocumentElement();
		NodeList nodeList = getNodeList(xmlStr);
		Node n = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			n = nodeList.item(i);
			n = this.document.importNode(n, true);
			root.insertBefore(n, refNode);
		}
	}

	public void setNodeAttribute(String xql, String attributeName, String value) throws XPathExpressionException {
		Node node = findNode(xql);
		if (node == null) {
			return;
		}
		((Element) node).setAttribute(attributeName, value);
	}

	public void save() throws TransformerException, IOException {
		saveAs(this.sourceFilePath);
	}

	public void saveAs(String path) throws TransformerException, IOException {
		saveAs(path, "UTF-8");
	}

	public void saveAs(String path, String encoding) throws TransformerException, IOException {
		FileOutputStream os = new FileOutputStream(path);
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Source source = new DOMSource(this.document);
			Result result = new StreamResult(os);

			Transformer transformer = transformerFactory.newTransformer();
			DocumentType type = this.document.getDoctype();

			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

			if (type != null) {
				if (type.getPublicId() != null) {
					transformer.setOutputProperty("doctype-public", type.getPublicId());
				}
				if (type.getSystemId() != null) {
					transformer.setOutputProperty("doctype-system", type.getSystemId());
				}
			}
			transformer.transform(source, result);
		} finally {
			os.close();
		}
	}

	public void saveDocument(Document doc, File file, boolean isFormat, String encoding) throws IOException {
		if ((doc == null) || (file == null)) {
			return;
		}

		String content = null;
		content = node2String(doc, isFormat, true, "UTF-8");

		if (!file.exists()) {
			file.createNewFile();
			file.renameTo(file);
		}

		BufferedReader in = new BufferedReader(new CharArrayReader(content.toCharArray()));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));

			String tmp = in.readLine();

			if (tmp != null) {
				writer.write(tmp);
				writer.write("\r\n");
				writer.flush();

				tmp = in.readLine();
			}
		} finally {
			try {
				writer.close();
			} catch (IOException localIOException) {
			}
		}
	}

	public final String node2String(Node node, boolean isFormat, boolean hasHead, String encoding) throws IOException {
		if (node == null) {
			return null;
		}

		if (node.getNodeType() == 9) {
			Node elem = ((Document) node).getDocumentElement();
			if (elem != null) {
				node = elem;
			}
		}

		CharArrayWriter writer = new CharArrayWriter();

		Properties prop = OutputPropertiesFactory.getDefaultMethodProperties("xml");
		prop.setProperty("encoding", encoding);

		Serializer serializer = SerializerFactory.getSerializer(prop);

		if (isFormat) {
			prop.setProperty("indent", "yes");
			prop.setProperty("{http://xml.apache.org/xalan}indent-amount", "4");
			removeSpace(node);
		} else {
			prop.setProperty("indent", "no");
		}

		serializer.setOutputFormat(prop);
		serializer.setWriter(writer);

		serializer.asDOMSerializer().serialize(node);

		String str = new String(writer.toCharArray());
		if (hasHead) {
			if (!str.startsWith("<?xml")) {
				str = ("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n").concat(str);
			}
			return str;
		}

		if (!str.startsWith("<?xml")) {
			return str;
		}
		int op = str.indexOf("?>");
		str = str.substring(op + "?>".length()).trim();
		if (str.startsWith("\n")) {
			str = str.substring(1);
		}

		return str;
	}

	public NodeList getNodeList(String xmlStr) throws SAXException, IOException {
		NodeList nl = null;
		xmlStr = "<config>" + xmlStr + "</config>";

		ByteArrayInputStream bais = null;
		bais = new ByteArrayInputStream(xmlStr.getBytes());

		Document doc = this.builder.parse(bais);
		Element root = doc.getDocumentElement();

		nl = root.getChildNodes();
		return nl;
	}

	private static Node removeSpace(Node node) {
		NodeList list = node.getChildNodes();
		if ((list == null) || (list.getLength() == 0)) {
			return node;
		}
		if ((list.getLength() == 1) && (list.item(0).getNodeType() == 3) && (node.getNodeType() == 1)) {
			Text text = (Text) node.getChildNodes().item(0);
			String data = text.getData();
			if (data != null) {
				text.setData(data.trim());
			}
			return node;
		}

		for (int i = 0; i < list.getLength(); i++) {
			Node tmpNode = list.item(i);
			if (tmpNode.getNodeType() == 3) {
				String str = tmpNode.getNodeValue();
				if (isSpace(str)) {
					tmpNode.getParentNode().removeChild(tmpNode);
					i--;
				}
			} else if (tmpNode.getNodeType() == 1) {
				removeSpace(tmpNode);
			}
		}
		return node;
	}

	private static boolean isSpace(String str) {
		if (str == null) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			for (int j = 0; j < chars.length; j++) {
				if (!isSpace(str.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isSpace(char c) {
		for (int j = 0; j < chars.length; j++) {
			if (chars[j] == c) {
				return true;
			}
		}
		return false;
	}
}