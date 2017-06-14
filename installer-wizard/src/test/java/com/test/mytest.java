package com.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class mytest {

	public static void main(String[] args) {
		try {
			String vfn="D://test//version.xml";
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(vfn));
			Element product= document.getRootElement();
			setProductVersion(product);
			
			Iterator<Element> it= product.elementIterator();
			while(it.hasNext()){
				Element element=it.next();
				element.addAttribute("installDir", "f://bosshome");
				createAppVersion(element);
			}
			
			XMLWriter writer = new XMLWriter(new FileWriter(vfn));
			   writer.write(document);
			   writer.close();

		} catch (Exception e) {
			throw new InstallException("faild to recordInsatllDir",e);
		}

	}

	private static void createAppVersion(Element element) {
		System.out.println(element.asXML());
		try{
			String productFile="d://test//app.xml";
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(productFile)));
			bw.write (element.asXML());
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	private static void setProductVersion(Element product) {
		String productFile="D://test//product.xml";
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("product");
		List<Attribute> list=product.attributes();
		for(int i=0;i<list.size();i++){
			Attribute attr=list.get(i);
		    //root.addAttribute(attr.getQualifiedName(), attr.getValue());
			root.addElement(attr.getQualifiedName()).addText(attr.getValue());
		}
		root.addElement("installDir").addText("d:boss_home");
		
		
		try{
			 OutputFormat format = new OutputFormat("    ",true);
			  format.setEncoding("utf-8");//设置编码格式  
		    XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(productFile),format);

		     xmlWriter.write(document);
		     xmlWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
     
	}
	
	

}
