/**
 * Directory base
 * Directory Menu Roots Tree Registry iPojo impl test
 * Copyright (C) 2014 Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.directory.base.registry;

import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.model.MenuEntityType;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TreeMenuRootsRegistryTest extends TestCase {

    private static TreeMenuRootsRegistry treeMenuRootsRegistry;
    private static TreeMenuEntity commonRootTreeMenuEntity;
    private static TreeMenuEntity organisationalTreeMenuEntity;

    @BeforeClass
    public static void testSetup() throws Exception {
        treeMenuRootsRegistry = new DirectoryTreeMenuRootsRegistryImpl();
        commonRootTreeMenuEntity = new TreeMenuEntity().setId("commonsDir").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU);

        organisationalTreeMenuEntity = new TreeMenuEntity().setId("commonsOrgDir").setValue("Organisation").
                                                            setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                            setParentTreeMenuEntity(commonRootTreeMenuEntity);

        commonRootTreeMenuEntity.addChildTreeMenuEntity(organisationalTreeMenuEntity);

        organisationalTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("applicationTreeID").setValue("Application").
                                                                                 setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-cogs").
                                                                                 setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main/application.jsf").
                                                                                 setDescription("Your IT applications definitions")).
                                     addChildTreeMenuEntity(new TreeMenuEntity().setId("teamTreeID").setValue("Team").
                                                                                                                             setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-group").
                                                                                                                                                                                                                setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main/team.jsf").
                                                                                                                                                                                                                                                                                                       setDescription("Your teams (ops, devs, ...) definitions")).
                                     addChildTreeMenuEntity(new TreeMenuEntity().setId("companyTreeID").setValue("Company").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-building").
                                                                                                                                                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main/company.jsf").
                                                                                                                                                                                                                                                                                                            setDescription("Definition of companies involved in your IT system (yours included)")).
                                     addChildTreeMenuEntity(new TreeMenuEntity().setId("environmentTreeID").setValue("Environment").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-tag").
                                                                                                                                                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main/environment.jsf").
                                                                                                                                                                                                                                                                                                                   setDescription("Your IT environment (development, homologation, QA, production ...)"));
    }

    @AfterClass
    public static void testCleanup() {
        ((DirectoryTreeMenuRootsRegistryImpl) treeMenuRootsRegistry).invalidate();
    }

    @Test
    public void testBasicDirectoriesRegistration() throws Exception {
        treeMenuRootsRegistry.registerTreeMenuRootEntity(commonRootTreeMenuEntity);
        assertTrue(treeMenuRootsRegistry.getTreeMenuRootsEntities().contains(commonRootTreeMenuEntity));
        assertFalse(treeMenuRootsRegistry.getTreeMenuRootsEntities().contains(organisationalTreeMenuEntity));
        assertTrue(treeMenuRootsRegistry.getTreeMenuEntityFromID("commonsOrgDir").equals(organisationalTreeMenuEntity));
        assertNotNull(treeMenuRootsRegistry.getTreeMenuEntityFromValue("Team"));
    }

    @Test
    public void testBasicDirectoriesUnregistration() throws Exception {
        treeMenuRootsRegistry.unregisterTreeMenuRootEntity(commonRootTreeMenuEntity);
        assertFalse(treeMenuRootsRegistry.getTreeMenuRootsEntities().contains(commonRootTreeMenuEntity));
        assertNull(treeMenuRootsRegistry.getTreeMenuEntityFromID("commonsOrgDir"));
        assertNull(treeMenuRootsRegistry.getTreeMenuEntityFromValue("Team"));
    }
}