# CC installer directory JPA provider configuration parameters
#
# Copyright (C) 2014 Mathilde Ffrench
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
import getpass
import json
import os
import mysql.connector
from mysql.connector import errorcode
from tools.AConfParamNotNone import AConfParamNotNone
from tools.AConfUnit import AConfUnit

__author__ = 'echinopsii'


class cpHibernateConnectionPassword(AConfParamNotNone):

    name = "##hibernateConnectionPassword"
    description = "CC directory hibernate connection password"
    hide = True

    def __init__(self):
        self.value = None


class cpHibernateConnectionURL(AConfParamNotNone):

    name = "##hibernateConnectionURL"
    description = "CC directory hibernate connection URL"
    hide = False

    def __init__(self):
        self.value = None


class cpHibernateConnectionUsername(AConfParamNotNone):

    name = "##hibernateConnectionUsername"
    description = "CC directory hibernate username"
    hide = False

    def __init__(self):
        self.value = None


class cpHibernateDialect(AConfParamNotNone):

    name = "##hibernateDialect"
    description = "CC directory hibernate dialect"
    hide = False

    def __init__(self):
        self.value = None


class cpHibernateDriverClass(AConfParamNotNone):

    name = "##hibernateDriverClass"
    description = "CC directory hibernate driver class"
    hide = False

    def __init__(self):
        self.value = None


class cuDirectoryJPAProviderManagedServiceProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "CC directory JPA provider"
        self.confTemplatePath = os.path.abspath("resources/templates/components/com.spectral.cc.core.DirectoryJPAProviderManagedService.properties.tpl")
        self.confFinalPath = targetConfDir + "com.spectral.cc.core.DirectoryJPAProviderManagedService.properties"
        hibernateDriverClass = cpHibernateDriverClass()
        hibernateDialect = cpHibernateDialect()
        hibernateConnectionURL = cpHibernateConnectionURL()
        hibernateConnectionUsername = cpHibernateConnectionUsername()
        hibernateConnectionPassword = cpHibernateConnectionPassword()
        self.paramsDictionary = {
            hibernateDriverClass.name: hibernateDriverClass,
            hibernateDialect.name: hibernateDialect,
            hibernateConnectionURL.name: hibernateConnectionURL,
            hibernateConnectionUsername.name: hibernateConnectionUsername,
            hibernateConnectionPassword.name: hibernateConnectionPassword,
        }


class directoryJPAProviderManagedServiceSyringe:

    def __init__(self, targetConfDif, silent):
        self.silent = silent
        self.directoryJPAProviderManagedServiceCUProcessor = cuDirectoryJPAProviderManagedServiceProcessor(targetConfDif)
        directoryJPAProviderManagedServiceCUJSON = open("resources/configvalues/components/cuDirectoryJPAProviderManagedService.json")
        self.directoryJPAProviderManagedServiceCUValues = json.load(directoryJPAProviderManagedServiceCUJSON)
        directoryJPAProviderManagedServiceCUJSON.close()
        self.dbConfig = None

    def shootBuilder(self):
        directoryJPAProviderManagedServiceDBTypeDefined = False
        directoryJPAProviderManagedServiceConnectionDefined = False
        for key in self.directoryJPAProviderManagedServiceCUProcessor.getParamsKeysList():

            if (key == cpHibernateDriverClass.name or key == cpHibernateDialect.name) and not directoryJPAProviderManagedServiceDBTypeDefined:
                self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateDriverClass.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateDriverClass.name])
                self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateDialect.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateDialect.name])
                directoryJPAProviderManagedServiceDBTypeDefined = True

            elif (key == cpHibernateConnectionURL.name or key == cpHibernateConnectionUsername.name or key == cpHibernateConnectionPassword.name) and not directoryJPAProviderManagedServiceConnectionDefined:

                tmpurl = self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionURL.name].split("://")[1]

                dbServerFQDNDefault = tmpurl.split(":")[0]
                dbServerFQDNDefaultUI = "[default - " + dbServerFQDNDefault + "] "

                tmpurl = tmpurl.split(":")[1]

                dbServerPortDefault = tmpurl.split("/")[0]
                dbServerPortDefaultUI = "[default - " + dbServerPortDefault + "] "

                dbNameDefault = tmpurl.split("/")[1]
                dbNameDefaultUI = "[default - " + dbNameDefault + "] "

                while not directoryJPAProviderManagedServiceConnectionDefined:

                    if not self.silent:
                        dbServerFQDN = input("%-- >> Define CC directory DB FQDN " + dbServerFQDNDefaultUI + ": ")
                        if dbServerFQDN == "" or dbServerFQDN is None:
                            dbServerFQDN = dbServerFQDNDefault
                        else:
                            dbServerFQDNDefaultUI = "[default - " + dbServerFQDN + "] "
                            dbServerFQDNDefault = dbServerFQDN
                    else:
                        dbServerFQDN = dbServerFQDNDefault

                    if not self.silent:
                        serverPortIsValid = False
                        dbServerPortStr = ""
                        while not serverPortIsValid:
                            dbServerPort = 0
                            dbServerPortStr = input("%-- >> Define CC directory DB port " + dbServerPortDefaultUI + ": ")
                            if dbServerPortStr == "" or dbServerPortStr is None:
                                dbServerPortStr = dbServerPortDefault
                                dbServerPort = int(dbServerPortDefault)
                                serverPortIsValid = True
                            else:
                                try:
                                    dbServerPort = int(dbServerPortStr)
                                    if (dbServerPort <= 0) and (dbServerPort > 65535):
                                        print("%-- !! Invalid DB port " + str(dbServerPort) + ": not in port range")
                                    else:
                                        dbServerPortDefaultUI = "[default - " + dbServerPortStr + "] "
                                        dbServerPortDefault = dbServerPortStr
                                        serverPortIsValid = True
                                except ValueError:
                                    print("%-- !! Invalid DB port " + dbServerPortStr + " : not a number")
                    else:
                        dbServerPortStr = dbServerPortDefault
                        dbServerPort = int(dbServerPortStr)

                    if not self.silent:
                        dbNameIsValid = False
                        dbName = ""
                        while not dbNameIsValid:
                            dbName = input("%-- >> Define CC directory DB name " + dbNameDefaultUI + ": ")
                            if dbName != "":
                                dbNameIsValid = True
                                dbNameDefault = dbName
                                dbNameDefaultUI = "[default - " + dbName + "] "
                            elif dbNameDefault != "":
                                dbName = dbNameDefault
                                dbNameIsValid = True
                    else:
                        dbName = dbNameDefault

                    if not self.silent:
                        self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionURL.name] = "jdbc:mysql://" + dbServerFQDN + ":" + dbServerPortStr + "/" + dbName
                    self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionURL.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionURL.name])

                    if not self.silent:
                        dbServerUsernameDefault = self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name]
                        dbServerUsernameDefaultUI = "[default - " + dbServerUsernameDefault + "] "

                        dbServerUsernameIsValid = False
                        while not dbServerUsernameIsValid:
                            self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name] = input("%-- >> Define CC directory DB username " + dbServerUsernameDefaultUI + ": ")
                            if self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name] != "":
                                dbServerUsernameIsValid = True
                                dbServerUsernameDefault = self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name]
                                dbServerUsernameDefaultUI = "[default - " + dbServerUsernameDefault + "]"
                            elif dbServerUsernameDefault != "":
                                self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name] = dbServerUsernameDefault
                                dbServerUsernameIsValid = True
                    else:
                        self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionUsername.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name])

                    if not self.silent:
                        dbServerPasswordIsValid = False
                        while not dbServerPasswordIsValid:
                            self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name] = getpass.getpass("%-- >> Define CC directory DB password : ")
                            if self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name] != "":
                                dbServerPasswordIsValid = True
                    else:
                        self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionPassword.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name])

                    try:
                        cnx = mysql.connector.connect(
                            user=self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name],
                            password=self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name],
                            host=dbServerFQDN,
                            port=dbServerPort,
                            database=dbName
                        )
                        directoryJPAProviderManagedServiceConnectionDefined = True
                        self.dbConfig = {
                            'user': self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name],
                            'password': self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name],
                            'host': dbServerFQDN,
                            'port': dbServerPort,
                            'database': dbName
                        }
                    except mysql.connector.Error as err:
                        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
                            print("Something is wrong with your user name or password")
                        elif err.errno == errorcode.ER_BAD_DB_ERROR:
                            print("Database does not exists")
                        else:
                            print(err)
                    else:
                        cnx.close()

                self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionURL.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionURL.name])
                self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionUsername.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionUsername.name])
                self.directoryJPAProviderManagedServiceCUProcessor.setKeyParamValue(cpHibernateConnectionPassword.name, self.directoryJPAProviderManagedServiceCUValues[cpHibernateConnectionPassword.name])

    def getDBConfigFromShoot(self):
        return self.dbConfig

    def inject(self):
        directoryJPAProviderManagedServiceCUJSON = open("resources/configvalues/components/cuDirectoryJPAProviderManagedService.json", "w")
        jsonStr = json.dumps(self.directoryJPAProviderManagedServiceCUValues, sort_keys=True, indent=4, separators=(',', ': '))
        directoryJPAProviderManagedServiceCUJSON.write(jsonStr)
        directoryJPAProviderManagedServiceCUJSON.close()
        self.directoryJPAProviderManagedServiceCUProcessor.process()