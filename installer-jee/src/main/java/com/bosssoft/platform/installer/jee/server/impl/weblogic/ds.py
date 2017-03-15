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
#targetServers,targetClusters  split by ","
#dsname,jndiName,dbUrl,dbDriver,dbPassword,dbUser
def addDataSource():
    #########
    ### connect to weblogic @see userName,passWord,adminServerURL
    #########
    if connected == 'false':
        try:
            print "connect(userName, passWord, adminServerURL)"
            print "userName:" + userName
            print "passWord:" + passWord
            print "adminServerURL:" + adminServerURL
            connect(userName, passWord, adminServerURL)
            print "connected sucessed!"
        except:
            writeResultToFile(RESULT_FAILED + "\n" + "Could not connect the AdminServer!")
            return
    if connected == 'false':
        writeResultToFile(RESULT_FAILED + "\n" + "Could not connect the AdminServer!")
	return
	
    #########
    ## compute Servers @see targetServers,targetClusters
    #########
    tempTargs = targetServers.split(',')
    serverMBs = []
    for _tmp in tempTargs:
        if (_tmp is None) or (len(_tmp) == 0):
            continue
        servermb = getMBean("/Servers/" + _tmp)
        if servermb is not None:
            serverMBs.append(servermb)
            
    tempTargs = targetClusters.split(',')
    for _tmp in tempTargs:
        if (_tmp is None) or (len(_tmp) == 0):
            continue
        clusterMb = getMBean("/Clusters/" + _tmp)
        if clusterMb is not None:
            serverMBs.append(clusterMb)
            
    print "Targets:" + targetServers + "," + targetClusters
    
    edit()
    startEdit()
    print 'Creating JDBCSystemResource with name' + dsname
    cd('/JDBCSystemResources')
    jdbcSR = cmo.createJDBCSystemResource(dsname)
    theJDBCResource = jdbcSR.getJDBCResource()
    theJDBCResource.setName(dsname)
    
    connectionPoolParams = theJDBCResource.getJDBCConnectionPoolParams()
    connectionPoolParams.setConnectionReserveTimeoutSeconds(25)
    connectionPoolParams.setMaxCapacity(100)
    if (testTable is not None) and (len(testTable) > 0):
        connectionPoolParams.setTestTableName(testTable)

    dsParams = theJDBCResource.getJDBCDataSourceParams()
    dsParams.addJNDIName(jndiName)

    driverParams = theJDBCResource.getJDBCDriverParams()
    if dbUrl is not None:
        driverParams.setUrl(dbUrl)
    if dbDriver is not None:
        driverParams.setDriverName(dbDriver)
    # driverParams.setUrl("jdbc:oracle:thin:@my-oracle-server:my-oracle-server-port:my-oracle-sid")
    # driverParams.setDriverName("oracle.jdbc.driver.OracleDriver")

    if dbPassword is not None:
        driverParams.setPassword(dbPassword)
    # driverParams.setLoginDelaySeconds(60)
    driverProperties = driverParams.getProperties()

    if dbUser is not None:
        proper = driverProperties.createProperty("user")
        proper.setName("user")
        proper.setValue(dbUser)

    #proper1 = driverProperties.createProperty("DatabaseName")
    #proper1.setName("DatabaseName")
    #proper1.setValue("jdbc:pointbase:server://localhost/demo")

    for temp in serverMBs:
        jdbcSR.addTarget(temp)
    save()
    try:
        ##ingore not activate problm
        activate()
        writeResultToFile(RESULT_SUCCEED)
    except:
        writeResultToFile(RESULT_SUCCEED)
    #print 'Done configuring the data source' + dsName

def removeDataSource():
    #########
    ### connect to weblogic @see userName,passWord,adminServerURL
    #########
    if connected == 'false':
        try:
            print "connect(userName, passWord, adminServerURL)"
            print "userName:" + userName
            print "passWord:" + passWord
            print "adminServerURL:" + adminServerURL
            connect(userName, passWord, adminServerURL)
            print "connected sucessed!"
        except:
            writeResultToFile(RESULT_FAILED + "\n" + "Could not connect the AdminServer!")
    if connected == 'false':
        writeResultToFile(RESULT_FAILED + "\n" + "Could not connect the AdminServer!")
	return
    
    edit()
    startEdit()
    cd('/JDBCSystemResources')
    delete(dsname,'JDBCSystemResource')
    try:
        save()
        activate()
        writeResultToFile(RESULT_SUCCEED)
    except:
        writeResultToFile(RESULT_EXCEPTION)
    #print 'Done remove the data source' + dsName

    
#excute the operations
#Get the operation value,the operation value split with ","

tmpOperations = _operations.split(',')

for temp in tmpOperations:
    try:
        if temp == 'addDataSource':
            addDataSource()
        elif temp =='removeDataSource':
            removeDataSource()
    except:
        try:
            dumpStack()
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
            writeResultToFile(RESULT_FAILED) 
