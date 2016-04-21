# installer directory processor
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
import os
from components.directory.cuDirectoryJPAProviderManagedService import directoryJPAProviderManagedServiceSyringe
from components.directory.dbDirectoryMySQLInitiator import dbDirectoryMySQLInitiator
from components.directory.dbIDMMySQLPopulator import dbIDMMySQLPopulator

__author__ = 'mffrench'


class directoryProcessor:

    def __init__(self, home_dir_path, directory_db_conf, idm_db_conf, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--\n")
        print("%-- Directory configuration : \n")
        self.silent = silent
        self.homeDirPath = home_dir_path
        self.idmDBConfig = idm_db_conf
        self.directoryDBConfig = directory_db_conf

        kernelRepositoryDirPath = self.homeDirPath + "/repository/ariane-core/"
        if not os.path.exists(kernelRepositoryDirPath):
            os.makedirs(kernelRepositoryDirPath, 0o755)
        self.directoryJPAProviderSyringe = directoryJPAProviderManagedServiceSyringe(kernelRepositoryDirPath, silent)
        self.directoryJPAProviderSyringe.shootBuilder()
        self.directoryDBConfig = self.directoryJPAProviderSyringe.getDBConfigFromShoot()
        self.directorySQLInitiator = dbDirectoryMySQLInitiator(self.directoryDBConfig)
        self.directoryIDMSQLPopulator = dbIDMMySQLPopulator(idm_db_conf)

    def process(self):
        self.directoryJPAProviderSyringe.inject()
        self.directorySQLInitiator.process()
        self.directoryIDMSQLPopulator.process()
        return self
