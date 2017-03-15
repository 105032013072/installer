#!/usr/bin/python
# Module: WeblogicOperation
'''
Created on 2010-3-9

@author: yangmd
'''
import time
import sys
from java.lang import System
from java.lang import Integer
from java.lang import Throwable

# @see com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool 
RESULT_SUCCEED = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_SUCCEED"
RESULT_FAILED = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_FAILED"
RESULT_EXCEPTION = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_EXCEPTION"

## resultFile
##
def writeResultToFile(resultStr):
    tempFile = open(resultFile,'w')
    tempFile.write(resultStr)
    tempFile.write('\n')
    tempFile.close()

#resultFile
#userName
#passWord
#adminServerURL like "t3://192.168.0.45:7001"
#domainHome
#domainName,adminServer,
#adminServerURL,username,password,domainDir,timeOut,resultFile
#_jvmArgs
def startAdminServer():
    if connected == 'false':
        try:
            print "connect to Admin Server" + adminServerURL
            #URL="t3://"+adminServerListenAddress+":"+adminServerListenPort
            connect(userName, passWord, adminServerURL)
            print "connect succeed, It will not be start the Admin Server."
        except WLSTException:
            print 'No server is running at '+adminServerURL+', the script will start a new server'
    if connected == 'false':
        cd("/") #@UndefinedVariable
        adminServer = get("AdminServerName") #@UndefinedVariable
        _timeout = 180000
        print "try to start Admin Server"
        print "adminServer:" + adminServer
        print "domainDir:" + domainDir
        print "domainName:" + domainName
        print "adminServerURL:" + adminServerURL
        print "userName:" + userName
        print "resultFile:" + resultFile
        if (timeOut is not None) and (len(timeOut) > 0):
            _timeout = Integer(timeOut).intValue()
        if (_jvmArgs is not None) and (len(_jvmArgs) > 0):
            print "_timeout:" + Integer(_timeout).toString()
            print "_jvmArgs:" + _jvmArgs
            startServer(adminServer,domainName,adminServerURL,userName,passWord,domainDir,timeout=_timeout,jvmArgs=_jvmArgs,serverLog=resultFile)
        else:
            print "_timeout" + Integer(_timeout).toString()
            startServer(adminServer,domainName,adminServerURL,userName,passWord,domainDir,timeout=_timeout,serverLog=resultFile) #@UndefinedVariable

        print "Started Server. Trying to connect to the server ... "
        try:
            connect(userName, passWord, adminServerURL)
        finally:
            if connected=='false':
                writeResultToFile(RESULT_FAILED)
            else:
                writeResultToFile(RESULT_SUCCEED)
    else:
        writeResultToFile(RESULT_SUCCEED)
def stop():
    if(connected == 'false'):
        try:
            connect(userName, passWord, adminServerURL)
        except:
            writeResultToFile(RESULT_SUCCEED)
    if(connected == 'false'):
        writeResultToFile(RESULT_SUCCEED)
    else:
        try:
            shutdown(force='true')
            writeResultToFile(RESULT_SUCCEED)
        except:
            dumpStack()
            writeResultToFile(RESULT_FAILED)

def serverStatus():
        try:
            print "connect to Admin Server" + adminServerURL            
            connect(userName, passWord, adminServerURL)
            print "connect succeed, It will not be start the Admin Server."
        except WLSTException:
            print 'No server is running at '+adminServerURL+', the script will start a new server'            
        if(connected == 'false'):
            writeResultToFile(RESULT_FAILED)
        else:
            writeResultToFile(RESULT_SUCCEED)
    
#excute the operations
#Get the operation value,the operation value split with ","

tmpOperations = _operations.split(',')

readDomain(domainHome)

for temp in tmpOperations:
    try:
        if temp == 'start':
            startAdminServer()
        elif temp =='stop':
            stop()
        elif temp == 'serverStatus':
        	serverStatus()
    except Throwable,e:
        e.printStackTrace()
        try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
            writeResultToFile(RESULT_FAILED) 
