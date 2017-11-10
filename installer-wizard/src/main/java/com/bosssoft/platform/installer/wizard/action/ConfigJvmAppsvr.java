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

import java_cup.symbol_set;

public class ConfigJvmAppsvr implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		try{
			String appsvrType = context.getStringValue("APP_SERVER_TYPE");
			if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
				configTomcat(context, params);
				configForServer(context);//server.bat
				
			}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
				
			}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
				configweblogic(context, params);
			}
		}catch(Exception e){
			throw new InstallException(e);
		}
		
		
	}

    //tomcat若是 注册成服务，jvm参数需要配置在server.bat
	private void configForServer(IContext context) throws Exception{

			File serverFile=new File(context.getStringValue("AS_TOMCAT_HOME")+"/bin/service.bat");
			
			if("true".equals(context.getStringValue("IS_WINDOWS"))){
				if(!serverFile.exists()) throw new InstallException("tomcat can not register as service,because "+serverFile.getPath()+" cannot find");
				StringBuffer result=new StringBuffer();
				BufferedReader br=new BufferedReader(new FileReader(serverFile));
				String s=null;
				while((s=br.readLine())!=null){
					if(s.trim().startsWith("--JvmMs")){
						result.append(System.lineSeparator()+"  --JvmMs 512");
					}else if(s.trim().startsWith("--JvmMx")){
						result.append(System.lineSeparator()+"  --JvmMx 1024");
					}else if(s.trim().startsWith("--JvmOptions")){
						 int index=s.lastIndexOf("\"");
						 StringBuffer buffer=new StringBuffer(s);
					     buffer.insert(index, ";-DBOSSSOFT_HOME="+context.getStringValue("BOSSSOFT_HOME"));
					     result.append(System.lineSeparator()+buffer.toString());
					}else{
						result.append(System.lineSeparator()+s);
					}
				}
				br.close();
				
				/*s=result.toString();
				s=s.replace("${BOSSSOFT_HOME}", context.getStringValue("BOSSSOFT_HOME")).replace("${jvmMs}", "512").replace("${jvmMx}", "1024");*/
	            
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serverFile)));
				bw.write(result.toString());
				bw.close();
			}
		
	}


	private void configweblogic(IContext context, Map params) {
		if("true".equals(context.getStringValue("IS_WINDOWS"))){
			config4windows(context,params);
		}else{
			config4linux(context,params);
		}
		
	}


	private void config4linux(IContext context, Map params) {
		String file=context.getStringValue("AS_WL_DOMAIN_HOME")+"/bin"+"/setDomainEnv.sh";
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               if(s.trim().startsWith("WLS_MEM_ARGS_64BIT")){
            	   result.append(System.lineSeparator()+"WLS_MEM_ARGS_64BIT=\"-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"");
               }else if(s.trim().startsWith("WLS_MEM_ARGS_32BIT")){
            	   result.append(System.lineSeparator()+"WLS_MEM_ARGS_32BIT=\"-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"");
               }else result.append(System.lineSeparator()+s);
            }
            br.close(); 
            
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
        }catch(Exception e){
        	this.logger.error(e);
        }
		
	}


	private void config4windows(IContext context, Map params) {
		String file=context.getStringValue("AS_WL_DOMAIN_HOME")+"/bin"+"/setDomainEnv.cmd";
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               if(s.trim().startsWith("set WLS_MEM_ARGS_64BIT")){
            	   result.append(System.lineSeparator()+"set WLS_MEM_ARGS_64BIT=-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
               }else if(s.trim().startsWith("set WLS_MEM_ARGS_32BIT")){
            	   result.append(System.lineSeparator()+"set WLS_MEM_ARGS_32BIT=-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
               }else result.append(System.lineSeparator()+s);
            }
            br.close(); 
            
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
        }catch(Exception e){
        	this.logger.error(e);
        }
        
	}


	private void configTomcat(IContext context, Map params) {
		String file=null;
		StringBuffer addcontext=new StringBuffer();
		StringBuffer result=new StringBuffer();
		Integer inserIndex=null;
		if("true".equals(context.getStringValue("IS_WINDOWS"))){
			file=context.getStringValue("AS_TOMCAT_HOME")+"/bin/catalina.bat";
			result=new StringBuffer(readSource(file));
			addcontext.append("set ");
			inserIndex=0;
			addcontext.append("JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx1024m");
			addcontext.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
		}else{
			file=context.getStringValue("AS_TOMCAT_HOME")+"/bin/catalina.sh";
			result=new StringBuffer(readSource(file));
			inserIndex=result.indexOf("#!/bin/sh")+"#!/bin/sh".length()+1;
			addcontext.append(System.lineSeparator());
			addcontext.append("JAVA_OPTS=\"$JAVA_OPTS -Xms512m -Xmx1024m");
			addcontext.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"").append(System.lineSeparator());
		}
		try{
			
			if(!result.toString().contains(addcontext))
			   result.insert(inserIndex, addcontext);
			
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
		}catch(Exception e){
			this.logger.error(e);
		}
	}


	public void rollback(IContext context, Map params) throws InstallException {
		
	}

    private String readSource(String sourceFile) {
		
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
        	this.logger.error(e);
        }
        return result.toString();
	}
	
}
