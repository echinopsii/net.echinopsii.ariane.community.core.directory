/**
 * Directory base
 * JPA provider Interface
 * Copyright (C) 2013 Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.directory.base.persistence;

import org.osgi.framework.Bundle;

import javax.persistence.EntityManager;

/**
 * The directory JPA provider provide tools to create EntityManager for the ariane-directory persistence unit. It also add a feature to extend the ariane-directory persistance unit through Ariane plugins. <br/><br/>
 * To make work this feature you must have the spectral hibernate distribution which enables this feature.<br/>
 *
 * @see <a href="https://github.com/mffrench/hibernate-orm/tree/4.3.0.Final.spectral">spectral hibernate distribution</a>
 *
 */
public interface DirectoryJPAProvider {
    /**
     * Create entity manager for ariane-directory pu
     *
     * @return entity manager for ariane-directory pu
     */
    public EntityManager createEM();

    /**
     * Add a persistence bundle to ariane-directory pu
     *
     * @param persistenceBundle
     */
    public void addSubPersistenceBundle(Bundle persistenceBundle);
}