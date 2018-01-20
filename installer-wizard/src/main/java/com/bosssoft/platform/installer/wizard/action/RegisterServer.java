package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.RegisterDef;
import com.bosssoft.platform.installer.core.option.RegisterDefHelper;

public class RegisterServer implements IAction{
    
	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		try{
			String[] evns={"path=%SystemRoot%/system32;%SystemRoot%;%SystemRoot%;%path%"};
			List<RegisterDef> list=RegisterDefHelper.getRegisterList(context);
			Runtime runtime=Runtime.getRuntime();
			//Process p=null;
			for (RegisterDef registerDef : list) {
				if("zookeeper".equals(registerDef.getName())){
					zookeeperCheck(registerDef);
				}
				
				String workPath=registerDef.getWorkDir();
				Process p=runtime.exec("cmd /c "+registerDef.getRegisterCmd(), evns, new File(workPath));
				//输出操作结果
				InputStream inputStream = p.getInputStream();
		        List<String> read = read(inputStream, "UTF-8");
		        for (String string : read) {
					logger.info(string);
				}
		        p.waitFor();
			}
			
		}catch(Exception e){
			throw new InstallException(e);
		}
		
	}

	private void zookeeperCheck(RegisterDef registerDef) {
		String workPath=registerDef.getWorkDir();
		File f1=new File(workPath+File.separator+"prunmgr.exe");
		File f2=new File(workPath+File.separator+"prunsrv.exe");
		File f3=new File(workPath+File.separator+"install.bat");
		File f4=new File(workPath+File.separator+"zkServerStop.cmd");
		if(!(f1.exists()&&f2.exists()&&f3.exists()&&f3.exists())) 
			throw new InstallException("zookeeper can not register as service");
		
	}

	private List<String> read(InputStream inputStream, String charset) throws IOException {
		
		   List<String> data = new ArrayList<String>();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
	        String line;
	        while((line = reader.readLine()) != null){
	        	data.add(line);
	        }
	        reader.close();
	        return data;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
