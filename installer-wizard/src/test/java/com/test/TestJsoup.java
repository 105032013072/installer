package com.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.wizard.util.XmlUtil;

public class TestJsoup {
   public static void main(String[] args){
	   try{
		   String vfn="D://test//version.xml";
		   Document doc = XmlUtil.getDocument(new File(vfn));
			Elements products = XmlUtil.findElements(doc, "product");
			for (Element element : products) {
				//setProductVersion(element);
				Elements apps=element.select("app");
				for (Element app : apps) {
					createAppVersion(app);
				}
				
			}
			
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   
  }
   private static void createAppVersion(Element apps) {
		try{
			String productFile="D://test//app.xml";
			System.out.println(apps.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
   
   private static void setProductVersion(Element product){
	   String productFile="D://test//product.xml";
	   org.dom4j.Document document = DocumentHelper.createDocument();
	   org.dom4j.Element root = document.addElement("product");
	   Attributes  list=product.attributes();
		for (org.jsoup.nodes.Attribute attribute : list) {
			root.addAttribute(attribute.getKey(),attribute.getValue());
		}
		root.addAttribute("installDir", "d:boss_home");
		
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
