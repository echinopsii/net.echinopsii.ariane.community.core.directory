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
from components.directory.CUDirectoryJPAProviderManagedServiceProcessor import DirectoryJPAProviderManagedServiceSyringe
from components.directory.DBDirectoryMySQLInitiator import DBDirectoryMySQLInitiator
from components.directory.DBIDMMySQLPopulator import DBIDMMySQLPopulator

__author__ = 'mffrench'


class DirectoryProcessor:

    def __init__(self, home_dir_path, dist_dep_type, directory_db_conf, idm_db_conf, bus_processor, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--"
              "%--%--%--%--%--%--%--%--%--\n")
        print("%-- Directory configuration : \n")
        self.silent = silent
        self.homeDirPath = home_dir_path
        self.dist_dep_type = dist_dep_type
        self.idmDBConfig = idm_db_conf
        self.directoryDBConfig = directory_db_conf
        self.busProcessor = bus_processor

        kernel_repository_dir_path = self.homeDirPath + "/repository/ariane-core/"
        if not os.path.exists(kernel_repository_dir_path):
            os.makedirs(kernel_repository_dir_path, 0o755)
        self.directoryJPAProviderSyringe = DirectoryJPAProviderManagedServiceSyringe(kernel_repository_dir_path, silent)
        self.directoryJPAProviderSyringe.shoot_builder()
        self.directoryDBConfig = self.directoryJPAProviderSyringe.get_db_config_from_shoot()
        self.directorySQLInitiator = DBDirectoryMySQLInitiator(self.directoryDBConfig)
        self.directoryIDMSQLPopulator = DBIDMMySQLPopulator(idm_db_conf)

    def process(self):
        self.directoryJPAProviderSyringe.inject()
        self.directorySQLInitiator.process()
        self.directoryIDMSQLPopulator.process()
        return self
