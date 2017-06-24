package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.util.XmlUtil;


/**
 * 在boss_home下记录产品的版本信息
 * @author Windows
 *
 */
public class SetVersion implements IAction{

	public void execute(IContext context, Map params) throws InstallException {
		String fileName=params.get("versionFile").toString();
		File versionFile=new File(fileName);
		recordInsatllDir(versionFile,context);
		
	}
	private void recordInsatllDir(File versionFile, IContext context) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(versionFile);
			Element product= document.getRootElement();
			//创建产品的版本信息文件
			createProductVersion(context,product);
			
			Iterator<Element> it= product.elementIterator();
			while(it.hasNext()){
				Element app=it.next();
				//创建应用的版本信息文件
				createAppVersion(context,app);
			}
		} catch (Exception e) {
			throw new InstallException("faild to recordInsatllDir",e);
		}
		
	}



	private void createAppVersion(IContext context, Element app) {
	try{
		String appVFile=context.getStringValue("BOSSSOFT_HOME")+File.separator+app.attributeValue("name")+File.separator+"version.xml";
		 
		BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(appVFile)));
		bw.write (app.asXML());
		bw.close();
	}  catch(Exception e){
		e.printStackTrace();
	}
		
	}
	private void createProductVersion(IContext context, Element element) {
		String productVFile=context.getStringValue("BOSSSOFT_HOME")+File.separator+context.getStringValue("PRODUCT_NAME")+"_version.xml";
		Document document = DocumentHelper.createDocument();
		  Element root = document.addElement("product");
		   List<Attribute> list=element.attributes();
		   for (Attribute attribute : list) {
			 root.addElement(attribute.getQualifiedName()).addText(attribute.getValue());
		 }
		 //添加产品的安装路径以及应用的部署路径
		root.addElement("installDir").addText(context.getStringValue("INSTALL_DIR"));
	    root.addElement("deployDir").addText(context.getStringValue("APP_SERVER_DEPLOY_DIR"));
	    root.addElement("serverPort").addText(context.getStringValue("APP_SERVER_PORT"));
			try{
				OutputFormat format =OutputFormat.createPrettyPrint(); 
				  format.setEncoding("utf-8");//设置编码格式  
				  format.setNewLineAfterDeclaration(false);
			    XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(productVFile),format);

			     xmlWriter.write(document);
			     xmlWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

	
}
