package com.bosssoft.platform.installer.wizard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerUtil {
     public static Set<Integer> searchProcess4Win(List<Integer> ports){
    	 Set<Integer> pids=new HashSet();
    	 for (Integer port : ports) {
    		 try{
    			 Runtime runtime = Runtime.getRuntime();
            	 Process p = runtime.exec("cmd /c netstat -ano | findstr \""+port+"\"");
                 InputStream inputStream = p.getInputStream();
                 List<String> read = read(inputStream, "UTF-8",ports,"windows");
                 
                 for (String line : read) {
                     int offset = line.lastIndexOf(" ");
                     String spid = line.substring(offset);
                     spid = spid.replaceAll(" ", "");
                     int pid = 0;
                     try {
                         pid = Integer.parseInt(spid);
                     } catch (NumberFormatException e) {
                         System.out.println("获取的进程号错误:" + spid);
                     }
                     pids.add(pid);
                 }
    		 }catch(Exception e){
    			 e.printStackTrace();
    		 }
		}
    	 return pids;
    	 
     }
     
     public static Set<Integer> searchProcess4Linux(List<Integer> ports){
    	 Set<Integer> pids=new HashSet();
    	 for (Integer port : ports) {
    		 try{
    			 Runtime runtime = Runtime.getRuntime();
    			 String cmd="netstat -anp|grep \""+port+"\"";
    	         String[] cmdA = { "/bin/sh", "-c", cmd }; 
    	         Process p = runtime.exec(cmdA);
                 InputStream inputStream = p.getInputStream();
                 List<String> read = read(inputStream, "UTF-8",ports,"linux");
                 
                 for (String line : read) {
                	 line=line.trim();
                     int offset = line.lastIndexOf(" ");
                     String spid = line.substring(offset).split("/")[0];
                     spid = spid.replaceAll(" ", "");
                     int pid = 0;
                     try {
                         pid = Integer.parseInt(spid);
                     } catch (NumberFormatException e) {
                         System.out.println("获取的进程号错误:" + spid);
                     }
                     pids.add(pid);
                 }
    		 }catch(Exception e){
    			 e.printStackTrace();
    		 }
		}
    	 return pids;
    	 
     }
     
     
     /**
      * 一次性杀除所有的进程
      * @param pids
      */
     public static void killWithPid4Win(Set<Integer> pids){
         for (Integer pid : pids) {
             try {
                 Process process = Runtime.getRuntime().exec("taskkill /F /pid "+pid+"");
                 InputStream inputStream = process.getInputStream();
                 String txt = readTxt(inputStream, "GBK");
                 System.out.println(txt);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
     
     public static void killWithPid4Linux(Set<Integer> pids){
         for (Integer pid : pids) {
             try {
                 Process process = Runtime.getRuntime().exec("kill -9 "+pid+"");
                 process.waitFor();
                 InputStream inputStream = process.getInputStream();
                 String txt = readTxt(inputStream, "GBK");
                 System.out.println(txt);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
     
     private static String readTxt(InputStream in,String charset) throws IOException{
         BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
         StringBuffer sb = new StringBuffer();
         String line;
         while((line = reader.readLine()) != null){
             sb.append(line);
         }
         reader.close();
         return sb.toString();
     }
     
     private static List<String> read(InputStream in,String charset, List<Integer> ports,String os) throws IOException{
         List<String> data = new ArrayList<String>();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
         String line;
         while((line = reader.readLine()) != null){
        	 boolean validPort=false;
        	 if("windows".equals(os)) validPort = validPortWin(line,ports);
        	 else validPort=validPortLinux(line, ports);
             if(validPort){
                 data.add(line);
             }
         }
         reader.close();
         return data;
     }
     
     /**
      * 验证此行是否为指定的端口，因为 findstr命令会是把包含的找出来，例如查找80端口，但是会把8099查找出来
      * @param str
     * @param ports 
      * @return
      */
     private static boolean validPortWin(String str, List<Integer> ports){
    	return  valid(str, ports, "^ *[a-zA-Z]+ +\\S+");
     }
     
     private static boolean validPortLinux(String str, List<Integer> ports){
     	return  valid(str, ports, "^ *[a-zA-Z0-9]+ +\\S+ +\\S+ +\\S+");
      }
    
     private static boolean valid(String str, List<Integer> ports,String regxStr){
         Pattern pattern = Pattern.compile(regxStr);
         Matcher matcher = pattern.matcher(str);

         matcher.find();
         String find = matcher.group();
         int spstart = find.lastIndexOf(":");
         find = find.substring(spstart + 1);
         
         int port = 0;
         try {
             port = Integer.parseInt(find);
         } catch (NumberFormatException e) {
             System.out.println("查找到错误的端口:" + find);
             return false;
         }
         if(ports.contains(port)){
             return true;
         }else{
             return false;
         }
     }
}
