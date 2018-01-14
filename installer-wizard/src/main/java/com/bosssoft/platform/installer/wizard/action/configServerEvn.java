package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

/**
 * 为应用设置环境（eg:tomcat,zookeeper）
 * @author huangxw
 *
 */
public class configServerEvn implements IAction{
 
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
					int insertLocation=getLocation(context,cf,source);
					StringBuffer result=new StringBuffer(source);
					result.insert(insertLocation, JavaPath);
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

	 private int getLocation(IContext context, File file,String source) throws Exception {
		 if("true".equals(context.getStringValue("IS_WINDOWS"))) return 0;
		 else{
			 BufferedReader br=new BufferedReader(new FileReader(file));
			 String s = null;
		     while((s = br.readLine())!=null){//使用readLine方法，一次读一行
		          if(s.startsWith("#!")) break;
		    }
			 return source.indexOf(s)+s.length()+1;
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
		String [] ens=params.get("environments").toString().split(",");
		for (String en : ens) {
			if("true".equals(context.getStringValue("IS_WINDOWS"))){
				javacontent.append("set "+en+"="+context.getStringValue(en)+System.getProperty("line.separator"));
			}else{
				javacontent.append(en+"="+context.getStringValue(en)+System.getProperty("line.separator"));
			}
			   
		}
		return javacontent.toString();
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
