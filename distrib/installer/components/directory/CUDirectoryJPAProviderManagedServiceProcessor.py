# installer directory JPA provider configuration parameters
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


class CPHibernateConnectionPassword(AConfParamNotNone):
    name = "##hibernateConnectionPassword"
    description = "Directory DB connection password"
    hide = True

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super(CPHibernateConnectionPassword, self).is_valid()


class CPHibernateConnectionURL(AConfParamNotNone):
    name = "##hibernateConnectionURL"
    description = "Directory DB connection URL"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super(CPHibernateConnectionURL, self).is_valid()


class CPHibernateConnectionUsername(AConfParamNotNone):
    name = "##hibernateConnectionUsername"
    description = "Directory DB username"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super(CPHibernateConnectionUsername, self).is_valid()


class CPHibernateDialect(AConfParamNotNone):
    name = "##hibernateDialect"
    description = "Directory DB dialect"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super(CPHibernateDialect, self).is_valid()


class CPHibernateDriverClass(AConfParamNotNone):
    name = "##hibernateDriverClass"
    description = "Directory DB driver class"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super(CPHibernateDriverClass, self).is_valid()


class CUDirectoryJPAProviderManagedServiceProcessor(AConfUnit):
    def __init__(self, target_conf_dir):
        self.confUnitName = "Directory JPA provider"
        self.confTemplatePath = os.path.abspath(
            "resources/templates/components/"
            "net.echinopsii.ariane.community.core.DirectoryJPAProviderManagedService.properties.tpl"
        )
        self.confFinalPath = target_conf_dir + \
                             "net.echinopsii.ariane.community.core.DirectoryJPAProviderManagedService.properties"
        driver_class = CPHibernateDriverClass()
        dialect = CPHibernateDialect()
        connection_url = CPHibernateConnectionURL()
        connection_username = CPHibernateConnectionUsername()
        connection_password = CPHibernateConnectionPassword()
        self.paramsDictionary = {
            driver_class.name: driver_class,
            dialect.name: dialect,
            connection_url.name: connection_url,
            connection_username.name: connection_username,
            connection_password.name: connection_password,
        }

    def set_key_param_value(self, key, value):
        return super(CUDirectoryJPAProviderManagedServiceProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUDirectoryJPAProviderManagedServiceProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUDirectoryJPAProviderManagedServiceProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUDirectoryJPAProviderManagedServiceProcessor, self).get_param_from_key(key)


class DirectoryJPAProviderManagedServiceSyringe:
    def __init__(self, target_conf_dir, silent):
        self.silent = silent
        self.directoryJPAProviderManagedServiceCUProcessor = CUDirectoryJPAProviderManagedServiceProcessor(
            target_conf_dir
        )
        cujson = open("resources/configvalues/components/cuDirectoryJPAProviderManagedService.json")
        self.directoryJPAProviderManagedServiceCUValues = json.load(cujson)
        cujson.close()
        self.dbConfig = None

    def shoot_builder(self):
        db_type_defined = False
        connection_defined = False
        for key in self.directoryJPAProviderManagedServiceCUProcessor.get_params_keys_list():

            if (key == CPHibernateDriverClass.name or key == CPHibernateDialect.name) and not db_type_defined:
                self.directoryJPAProviderManagedServiceCUProcessor. \
                    set_key_param_value(
                        CPHibernateDriverClass.name,
                        self.directoryJPAProviderManagedServiceCUValues[CPHibernateDriverClass.name]
                    )
                self.directoryJPAProviderManagedServiceCUProcessor.set_key_param_value(
                    CPHibernateDialect.name,
                    self.directoryJPAProviderManagedServiceCUValues[CPHibernateDialect.name]
                )
                db_type_defined = True

            elif (key == CPHibernateConnectionURL.name or key == CPHibernateConnectionUsername.name or
                  key == CPHibernateConnectionPassword.name) and not connection_defined:

                tmp_url = self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionURL.name].split("://")[1]

                db_server_fqdn_default = tmp_url.split(":")[0]
                db_server_fqdn_default_ui = "[default - " + db_server_fqdn_default + "] "

                tmp_url = tmp_url.split(":")[1]

                db_server_port_default = tmp_url.split("/")[0]
                db_server_port_default_ui = "[default - " + db_server_port_default + "] "

                db_name_default = tmp_url.split("/")[1]
                db_name_default_ui = "[default - " + db_name_default + "] "

                while not connection_defined:

                    if not self.silent:
                        db_server_fqdn = input("%-- >> Define Directory DB FQDN " + db_server_fqdn_default_ui + ": ")
                        if db_server_fqdn == "" or db_server_fqdn is None:
                            db_server_fqdn = db_server_fqdn_default
                        else:
                            db_server_fqdn_default_ui = "[default - " + db_server_fqdn + "] "
                            db_server_fqdn_default = db_server_fqdn
                    else:
                        db_server_fqdn = db_server_fqdn_default

                    if not self.silent:
                        server_port_is_valid = False
                        db_server_port_str = ""
                        while not server_port_is_valid:
                            db_server_port = 0
                            db_server_port_str = input("%-- >> Define Directory DB port " +
                                                       db_server_port_default_ui + ": ")
                            if db_server_port_str == "" or db_server_port_str is None:
                                db_server_port_str = db_server_port_default
                                db_server_port = int(db_server_port_default)
                                server_port_is_valid = True
                            else:
                                try:
                                    db_server_port = int(db_server_port_str)
                                    if (db_server_port <= 0) and (db_server_port > 65535):
                                        print("%-- !! Invalid DB port " + str(db_server_port) + ": not in port range")
                                    else:
                                        db_server_port_default_ui = "[default - " + db_server_port_str + "] "
                                        db_server_port_default = db_server_port_str
                                        server_port_is_valid = True
                                except ValueError:
                                    print("%-- !! Invalid DB port " + db_server_port_str + " : not a number")
                    else:
                        db_server_port_str = db_server_port_default
                        db_server_port = int(db_server_port_str)

                    if not self.silent:
                        db_name_is_valid = False
                        db_name = ""
                        while not db_name_is_valid:
                            db_name = input("%-- >> Define Directory DB name " + db_name_default_ui + ": ")
                            if db_name != "":
                                db_name_is_valid = True
                                db_name_default = db_name
                                db_name_default_ui = "[default - " + db_name + "] "
                            elif db_name_default != "":
                                db_name = db_name_default
                                db_name_is_valid = True
                    else:
                        db_name = db_name_default

                    if not self.silent:
                        self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionURL.name] = \
                            "jdbc:mysql://" + db_server_fqdn + ":" + db_server_port_str + "/" + db_name
                    self.directoryJPAProviderManagedServiceCUProcessor. \
                        set_key_param_value(
                            CPHibernateConnectionURL.name,
                            self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionURL.name]
                        )

                    if not self.silent:
                        db_server_username_default = self.directoryJPAProviderManagedServiceCUValues[
                            CPHibernateConnectionUsername.name
                        ]
                        db_server_username_default_ui = "[default - " + db_server_username_default + "] "

                        db_server_username_is_valid = False
                        while not db_server_username_is_valid:
                            self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name] = \
                                input("%-- >> Define Directory DB username " + db_server_username_default_ui + ": ")
                            if self.directoryJPAProviderManagedServiceCUValues[
                                    CPHibernateConnectionUsername.name
                            ] != "":
                                db_server_username_is_valid = True
                                db_server_username_default = self.directoryJPAProviderManagedServiceCUValues[
                                    CPHibernateConnectionUsername.name
                                ]
                                db_server_username_default_ui = "[default - " + db_server_username_default + "]"
                            elif db_server_username_default != "":
                                self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name] = \
                                    db_server_username_default
                                db_server_username_is_valid = True
                    else:
                        self.directoryJPAProviderManagedServiceCUProcessor. \
                            set_key_param_value(
                                CPHibernateConnectionUsername.name,
                                self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name]
                            )

                    if not self.silent:
                        db_server_password_is_valid = False
                        while not db_server_password_is_valid:
                            self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionPassword.name] = \
                                getpass.getpass("%-- >> Define Directory DB password : ")
                            if self.directoryJPAProviderManagedServiceCUValues[
                                CPHibernateConnectionPassword.name
                            ] != "":
                                db_server_password_is_valid = True
                    else:
                        self.directoryJPAProviderManagedServiceCUProcessor. \
                            set_key_param_value(
                                CPHibernateConnectionPassword.name,
                                self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionPassword.name]
                            )

                    try:
                        cnx = mysql.connector.connect(
                            user=self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name],
                            password=self.directoryJPAProviderManagedServiceCUValues[
                                CPHibernateConnectionPassword.name
                            ],
                            host=db_server_fqdn,
                            port=db_server_port,
                            database=db_name
                        )
                        connection_defined = True
                        self.dbConfig = {
                            'user': self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name],
                            'password': self.directoryJPAProviderManagedServiceCUValues[
                                CPHibernateConnectionPassword.name
                            ],
                            'host': db_server_fqdn,
                            'port': db_server_port,
                            'database': db_name
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

                self.directoryJPAProviderManagedServiceCUProcessor.set_key_param_value(
                    CPHibernateConnectionURL.name,
                    self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionURL.name]
                )
                self.directoryJPAProviderManagedServiceCUProcessor.set_key_param_value(
                    CPHibernateConnectionUsername.name,
                    self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionUsername.name]
                )
                self.directoryJPAProviderManagedServiceCUProcessor.set_key_param_value(
                    CPHibernateConnectionPassword.name,
                    self.directoryJPAProviderManagedServiceCUValues[CPHibernateConnectionPassword.name]
                )

    def get_db_config_from_shoot(self):
        return self.dbConfig

    def inject(self):
        directory_jpa_provider_managed_service_cujson = open(
            "resources/configvalues/components/cuDirectoryJPAProviderManagedService.json", "w"
        )
        json_str = json.dumps(self.directoryJPAProviderManagedServiceCUValues,
                              sort_keys=True, indent=4, separators=(',', ': '))
        directory_jpa_provider_managed_service_cujson.write(json_str)
        directory_jpa_provider_managed_service_cujson.close()
        self.directoryJPAProviderManagedServiceCUProcessor.process()
