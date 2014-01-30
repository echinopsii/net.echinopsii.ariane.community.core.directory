/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 31/12/13 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spectral.cc.core.directory.commons.persistence;

import org.osgi.framework.Bundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.util.List;
import java.util.Map;

/**
 * The directory JPA provider provide tools to create EntityManager for the cc-directory persistence unit. It also add a feature to extend the cc-directory persistance unit through CC plugins. <br/><br/>
 * To make work this feature you must have the spectral hibernate distribution which enables this feature.<br/>
 *
 * @see <a href="https://github.com/mffrench/hibernate-orm/tree/4.3.0.Final.spectral">spectral hibernate distribution</a>
 *
 */
public interface DirectoryJPAProvider {
    /**
     * Create entity manager for cc-directory pu
     *
     * @return entity manager for cc-directory pu
     */
    public EntityManager createEM();

    /**
     * Add a persistence bundle to cc-directory pu
     *
     * @param persistenceBundle
     */
    public void addSubPersistenceBundle(Bundle persistenceBundle);
}