package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

/**
 * 为应用设置Java环境（eg:tomcat,zookeeper）
 * @author huangxw
 *
 */
public class configJavaServer implements IAction{
 
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String[] configfiles=params.get("configFiles").toString().split(",");
		String JavaPath=getJavaContent(context,params);
		BufferedWriter bw =null;
		try{
			for (String file : configfiles) {
				File cf=new File(file);
				if(!cf.exists()) continue;
				String source=readSource(cf);
				if(!source.contains(JavaPath)){
					StringBuffer result=new StringBuffer(source);
					result.insert(0, JavaPath);
					bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
					bw.write (result.toString());
					bw.close();
				}
				
			}
		}catch (Exception e) {
			if(bw!=null)
				try {
					bw.close();
				} catch (IOException e1) {
					logger.error(e1);
					throw new InstallException(e1);
				}
			throw new InstallException(e);
		}
		
	}

	 private String readSource(File file) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader br=null;
	   try{
	       br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
	       String s = null;
	       while((s = br.readLine())!=null){//使用readLine方法，一次读一行
	           result.append(System.lineSeparator()+s);
	        }
	              
	     }catch(Exception e){
	       this.logger.error(e);
	     }finally {
	        if(br!=null) br.close();
		}
	      return result.toString();
	}

	private String getJavaContent(IContext context, Map params) {
		StringBuffer javacontent=new StringBuffer();
		String [] ens=params.get("javaEnvironments").toString().split(",");
		for (String en : ens) {
			javacontent.append("set "+en+"="+context.getStringValue(en)+System.getProperty("line.separator"));
		}
		return javacontent.toString();
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
