package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public final class XmlHelper {
	public static Document parse(File filePath) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(filePath);
		return doc;
	}

	public static Document parse(InputStream inputStream) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = null;
		doc = reader.read(inputStream);

		return doc;
	}

	public static Document createDocument(String rootNode) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(rootNode);
		return doc;
	}

	public static void saveAs(String filePath, Document doc, String encoding) throws IOException {
		FileOutputStream os = new FileOutputStream(filePath);
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(encoding);
			XMLWriter writer = new XMLWriter(os, format);
			writer.write(doc);
			writer.close();
		} finally {
			os.close();
		}
	}
}