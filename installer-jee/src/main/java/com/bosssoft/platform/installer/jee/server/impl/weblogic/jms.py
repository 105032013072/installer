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
from javax.management import InstanceAlreadyExistsException
from java.lang import Exception
from java.lang import Throwable
from jarray import array
from weblogic.descriptor import BeanAlreadyExistsException
from java.lang.reflect import UndeclaredThrowableException
from java.lang import System
import javax
from javax import management
from java.io import File
from javax.management import MBeanException
from javax.management import RuntimeMBeanException
import javax.management.MBeanException
from java.lang import UnsupportedOperationException


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
#targetServers split by ","
#jmsServerName
#jmsModuleName
#jmsSubDeploymentName
#jmsConnectionFactoryName, jmsConnectionFactoryJndi
#jmsQueueName, jmsQueueJndi
#jmsTopicName, jmsTopicJndi
#jmsTemplateName, jmsTemplateMaxSize
#_storeName, storeDirectory
#_domainHome
def connectAdmin():
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

def _createJMSServer(_jmsServerName, _target):
    cd("/JMSServers")
    jmsServerMB = getMBean(_jmsServerName)
    if jmsServerMB is None:
        jmsServerMB = cmo.createJMSServer(_jmsServerName)
    jmsServerMB.addTarget(_target)
    return jmsServerMB

def _createFileStore(_storeName, _storeDictory, _target):
    cd("/FileStores")
    #_storeName, storeDirectory
    if (_storeName is not None) and (len(_storeName) > 0):
        jmsMyFileStore = getMBean(_storeName)
        print "_storeName:" + _storeName
        if jmsMyFileStore is None:
            jmsMyFileStore = cmo.createFileStore(_storeName)
        if (_storeDictory is not None) and (len(_storeDictory) > 0):
            try:
                myFile = File(_storeDictory)
                myFile.mkdirs()
            except Exception,ex:
                ex.printStackTrace()
                print 'could not crate directory' + _storeDictory
            jmsMyFileStore.setDirectory(_storeDictory)
        jmsMyFileStore.addTarget(_target)
        return jmsMyFileStore

def _createJDBCStore(_storeName, _dataSourceName, _tablePrefix, _target):
    cd("/JDBCStores")
    if (_storeName is not None) and (len(_storeName) > 0):
        _jmsStore = getMBean(_storeName)
        print "jdbcStoreName:" + _storeName
        if _jmsStore is None:
            _jmsStore = cmo.createJDBCStore(_storeName)
        if (_dataSourceName is not None) and (len(_dataSourceName) > 0):
            _dspath = "/" + _dataSourceName
            cd("/JDBCSystemResources")
            _dsMBean = getMBean(_dataSourceName)
            _jmsStore.setDataSource(_dsMBean)
        if (_tablePrefix is not None) and (len(_tablePrefix) > 0):
            _jmsStore.setPrefixName(_tablePrefix)
        _jmsStore.addTarget(_target)
        return _jmsStore

def _getMigratableTarget(clusterMB, serverName):
    cd("/")
    clusterName = clusterMB.getName()
    _serverMBS = clusterMB.getServers()
    mts = cmo.getMigratableTargets()
    for mttemp in mts:
        if clusterName == mttemp.getCluster().getName():
            tempServer = mttemp.getUserPreferredServer()
            if serverName == tempServer.getName():
                candidateServerMBS = mttemp.getConstrainedCandidateServers()

                for _tempServer in _serverMBS:
                    __contain = "false"
                    for tempCandidate in candidateServerMBS:
                        if tempCandidate.getName() == _tempServer.getName():
                            __contain = "true"
                            break
                    if __contain == "false":
                        mttemp.addConstrainedCandidateServer(_tempServer)

                return mttemp

def configJMS():
    connectAdmin()

    #########
    ### connect to weblogic @see userName,passWord,adminServerURL
    #########
    if connected == 'false':
        return

    #########
    ## compute Servers @see targetServers
    #########
    tempTargs = targetServers.split(',')

    serverMBs = []
    for _tmp in tempTargs:
        if _tmp is None:
            continue
        if len(_tmp) == 0:
            continue
        servermb = getMBean("/Servers/" + _tmp)
        if servermb is not None:
            serverMBs.append(servermb)

    tempTargs = targetClusters.split(',')
    clusterMBs = []
    for _tmp in tempTargs:
        if (_tmp is None) or (len(_tmp) == 0):
            continue
        clusterMb = getMBean("/Clusters/" + _tmp)
        if clusterMb is not None:
            clusterMBs.append(clusterMb)

    if (len(serverMBs) < 0) and (len(clusterMBs) < 0):
        writeResultToFile(RESULT_FAILED + "\n" + "Could not find the target servers!" + targetServers)
        writeResultToFile(RESULT_FAILED + "\n" + "Could not find the target clusters!" + targetClusters)
        return

    edit()
    startEdit()

    print "Target Servers:" + targetServers
    print "Target Clusters:" + targetClusters

    #########
    ## config JMS Server and File Store @see jmsServerName,_storeName,storeDirectory
    #########

    jmsServerMBs = []
    jmsMyFileStoreMBs = []

    if len(serverMBs) > 0:
        ##jms server
        print "create Jms Server:" + jmsServerName
        _tempJMSServerMB = _createJMSServer(jmsServerName,serverMBs[0])
        jmsServerMBs.append(_tempJMSServerMB)

        ##file store
        print "Create file store:" + _storeName
        _tempFileStore = _createFileStore(_storeName, storeDirectory, serverMBs[0])
        _tempJMSServerMB.setPersistentStore(_tempFileStore)
        jmsMyFileStoreMBs.append(_tempFileStore)

    if len(clusterMBs) > 0:
        clusterName = clusterMBs[0].getName()
        tempServerMBs = clusterMBs[0].getServers()
        for temp in tempServerMBs:
            servername = temp.getName()
            mttemp = _getMigratableTarget(clusterMBs[0],servername)

            ##jms server
            _jmsServerName = servername + "-" + jmsServerName
            _tempJMSServerMB = None
            print "create Jms Server:" + _jmsServerName
            if mttemp is None:
                _tempJMSServerMB = _createJMSServer(_jmsServerName, temp)
            else:
                _tempJMSServerMB = _createJMSServer(_jmsServerName, mttemp)
            jmsServerMBs.append(_tempJMSServerMB)

            #########
            ## config FileStores @see _storeName,storeDirectory
            #########
            tempStoreName = _storeName+"-"+servername
            #tempStoreDir = _domainHome + "/servers/" + servername+"/data/store/"+tempStoreName
            tempTablePreFix = None
            if (tablePrefix is not None) and (len(tablePrefix) > 0):
                tempTablePreFix = tablePrefix + servername
            print "Create file store:" + tempStoreName
            #print "file store directory:" + tempStoreDir
            _tempStore = None
            if mttemp is None:
                _tempStore = _createJDBCStore(tempStoreName, _dataSourceName, tempTablePreFix, temp)
            else:
                _tempStore = _createJDBCStore(tempStoreName, _dataSourceName, tempTablePreFix, mttemp)
            _tempJMSServerMB.setPersistentStore(_tempStore)
            jmsMyFileStoreMBs.append(_tempStore)


    #########
    ## config JMS System Resource @see jmsModuleName
    #########
    cd("/JMSSystemResources")
    print "jmsModuleName:" + jmsModuleName
    jmsMySystemResourceMB = getMBean(jmsModuleName)
    if jmsMySystemResourceMB is None:
        jmsMySystemResourceMB = cmo.createJMSSystemResource(jmsModuleName)
        for temp in serverMBs:
            jmsMySystemResourceMB.addTarget(temp)
        for temp in clusterMBs:
            jmsMySystemResourceMB.addTarget(temp)

    #########
    ## config SubDeployment @see jmsSubDeploymentName
    #########

    cd("/JMSSystemResources/"+jmsModuleName+"/SubDeployments")
    if (jmsSubDeploymentName is not None) and ( len(jmsSubDeploymentName) >= 1 ):
        subDep1mb = getMBean(jmsSubDeploymentName)
        print "jmsSubDeploymentName:" + jmsSubDeploymentName
        if subDep1mb is None:
            subDep1mb = jmsMySystemResourceMB.createSubDeployment(jmsSubDeploymentName)
            for _tempServer in jmsServerMBs:
                subDep1mb.addTarget(_tempServer)


    #########
    ## config connection factory @see jmsConnectionFactoryName, jmsConnectionFactoryJndi
    #########
    theJMSResource = jmsMySystemResourceMB.getJMSResource()
    if (jmsConnectionFactoryName is not None) and ( len(jmsConnectionFactoryName) >= 1 ):
        connNames = jmsConnectionFactoryName.split(',')
        connJndis = jmsConnectionFactoryJndi.split(',')
        print "jmsConnectionFactoryName:" + jmsConnectionFactoryName
        print "jmsConnectionFactoryJndi:" + jmsConnectionFactoryJndi
        for i in range(len(connNames)):
            connfact1 = theJMSResource.lookupConnectionFactory(connNames[i])
            if (connfact1 is None):
                connfact1 = theJMSResource.createConnectionFactory(connNames[i])
                connfact1.setJNDIName(connJndis[i])
                if len(clusterMBs) > 0:
                    connfact1.setSubDeploymentName(jmsSubDeploymentName)
                else:
                    connfact1.setDefaultTargetingEnabled(true)


    #########
    ## config Queue @see jmsQueueName, jmsQueueJndi
    #########
    if (jmsQueueName is not None) and ( len(jmsQueueName) >= 1 ):
        queueNames = jmsQueueName.split(',')
        queueJndis = jmsQueueJndi.split(',')
        print "jmsQueueName:" + jmsQueueName
        print "jmsQueueJndi:" + jmsQueueJndi
        for i in range(len(queueNames)):
            if len(clusterMBs) > 0:
                jmsqueue1 = theJMSResource.lookupUniformDistributedQueue(queueNames[i])
                if (jmsqueue1 is None ):
                    jmsqueue1 = theJMSResource.createUniformDistributedQueue(queueNames[i])
                    jmsqueue1.setJNDIName(queueJndis[i])
                    jmsqueue1.setSubDeploymentName(jmsSubDeploymentName)
            else:
                jmsqueue1 = theJMSResource.lookupQueue(queueNames[i])
                if (jmsqueue1 is None ):
                    jmsqueue1 = theJMSResource.createQueue(queueNames[i])
                    jmsqueue1.setJNDIName(queueJndis[i])
                    jmsqueue1.setSubDeploymentName(jmsSubDeploymentName)

    #########
    ## config Topic @see jmsTopicName, jmsTopicJndi
    #########
    if (jmsTopicName is not None) and ( len(jmsTopicName) >= 1 ):
        topicNames = jmsTopicName.split(',')
        topicJndis = jmsTopicJndi.split(',')
        print "topicNames:" + jmsTopicName
        print "jmsTopicJndi:" + jmsTopicJndi
        for i in range(len(topicNames)):
            if len(clusterMBs) > 0:
                jmstopic1 = theJMSResource.lookupUniformDistributedTopic(queueNames[i])
                if (jmstopic1 is None ):
                    jmstopic1 = theJMSResource.createUniformDistributedTopic(topicNames[i])
                    jmstopic1.setJNDIName(topicJndis[i])
                    jmstopic1.setSubDeploymentName(jmsSubDeploymentName)
            else:
                jmstopic1 = theJMSResource.lookupTopic(queueNames[i])
                if (jmstopic1 is None ):
                    jmstopic1 = theJMSResource.createTopic(topicNames[i])
                    jmstopic1.setJNDIName(topicJndis[i])
                    jmstopic1.setSubDeploymentName(jmsSubDeploymentName)

    #########
    ## config Queue @see jmsTemplateName, jmsTemplateMaxSize
    #########
    #if (jmsTemplateName is not None) and ( len(jmsTemplateName) >= 1 ):
        #jmstemplate = theJMSResource.createTemplate(jmsTemplateName)
        #maxSize = Integer(jmsTemplateMaxSize).intValue()
        #jmstemplate.setMaximumMessageSize(maxSize)

    try:
        save()
        activate(block="true")
        writeResultToFile(RESULT_SUCCEED)
    except Exception, e:
        e.printStackTrace()
        try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        except:
            print 'undo'
        writeResultToFile(RESULT_EXCEPTION+"\nError while trying to save and/or activate!!!")

def removeJMS():

    connectAdmin()

    #########
    ### connect to weblogic @see userName,passWord,adminServerURL
    #########
    if connected == 'false':
        return
    edit()
    startEdit()
    cd('/JMSSystemResources')
    print "jmsModuleName:" + jmsModuleName
    jmsMySystemResourceMB = getMBean(jmsModuleName)
    if jmsMySystemResourceMB is not None:
        delete(jmsModuleName,'JMSSystemResource')
    cd('/JMSServers')
    print "jmsServerName:" + jmsServerName
    jmsMyServer1 = getMBean(jmsServerName)
    if jmsMyServer1 is not None:
        delete(jmsServerName,'JMSServer')


    if (_storeName is not None) and (len(_storeName) > 0):
        cd('/FileStores')
        print "_storeName:" + _storeName
        jmsfileStoreMb = getMBean(_storeName)
        if jmsfileStoreMb is not None:
            delete(_storeName,'FileStore')

    try:
        save()
        activate(block="true")
        writeResultToFile(RESULT_SUCCEED)
    except Exception,e:
        e.printStackTrace()
        writeResultToFile(RESULT_EXCEPTION+"\nError while trying to save and/or activate!!!")

#excute the operations
#Get the operation value,the operation value split with ","

tmpOperations = _operations.split(',')

for temp in tmpOperations:
    try:
        if temp == 'addJMS':
            configJMS()
        elif temp =='removeJMS':
            removeJMS()
    except Throwable,e:
        e.printStackTrace()
        try:
            undo(defaultAnswer='y')
            cancelEdit(defaultAnswer='y')
        finally:
            writeResultToFile(RESULT_FAILED)
