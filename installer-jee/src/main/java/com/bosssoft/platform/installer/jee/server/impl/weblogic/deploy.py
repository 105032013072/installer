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

#userName,passWord
#adminServerURL
#appName,earPath
#targetServers,targetClusters
#timeOut
#
def deploy():
    if connected == 'false':
        connect(userName, passWord, adminServerURL)
    if connected == 'false':
        writeResultToFile(RESULT_FAILED + "\n Could not connecte the Server!" + adminServerURL)
    edit()
    startEdit()

    #_targets
    _targets =""
    if (targetServers is not None) and (len(targetServers) > 0):
        _targets = targetServers
    if (targetClusters is not None) and (len(targetClusters) > 0):
        if len(_targets) > 0:
            _targets = _targets + "," + targetClusters
        else:
            _targets = targetClusters

    #timeout
    _timeout = 1200000
    if (timeOut is not None) and (len(timeOut) > 0):
        try:
            _timeout = Integer(timeOut).intValue()
        except:
            print "timeOut not set."
    print "appName:" + appName
    print "earPath:" + earPath
    print "targets:" + _targets
    print "_timeout: " + Integer(_timeout).toString()
    progressMonitor = None
    ##cluster must use stage model
    if (targetClusters is not None) and (len(targetClusters) > 0):
        if (_stageModel is not None) and (len(_stageModel) > 0):
            progressMonitor = deploy(appName,earPath,_targets,stageMode=_stageModel,timeout=_timeout)
        else:
            progressMonitor = deploy(appName,earPath,_targets,stageMode="nostage",timeout=_timeout)
    else:
        progressMonitor = deploy(appName,earPath,_targets,timeout=_timeout)
    
##    while(progressMonitor.isRunning()):
##        print time.sleep(10)
#running, completed, failed, or released
        
    result_state = progressMonitor.getState()
    print "deploy's state:" + result_state
    if result_state == 'failed':
    	try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
           writeResultToFile(RESULT_FAILED) 
    else :        
        try:	
            save()
            activate()
            writeResultToFile(RESULT_SUCCEED)
        except:
            writeResultToFile(RESULT_EXCEPTION)

#userName,passWord,adminServerURL
def undeploy():
    if connected == 'false':
        try:
            connect(userName, passWord, adminServerURL)
        except:
            writeResultToFile(RESULT_FAILED)
            return
    if connected == 'false':
        writeResultToFile(RESULT_FAILED)
        return
    edit()
    startEdit()
    progressMonitor = undeploy(appName)
##    while(progressMonitor.isRunning()):
##        print time.sleep(10)
##    #running, completed, failed, or released
    result_state = progressMonitor.getState()

    if result_state == 'failed':
    	try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
           writeResultToFile(RESULT_FAILED) 
    else:
        save()
        activate()
        writeResultToFile(RESULT_SUCCEED)
            
#excute the operations
#Get the operation value,the operation value split with ","

tmpOperations = _operations.split(',')

for temp in tmpOperations:
    try:
        if temp == 'deploy':
            deploy()
        elif temp =='undeploy':
            undeploy()
    except Throwable,e:
        e.printStackTrace()
        try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
            dumpStack()
            writeResultToFile(RESULT_FAILED) 

