/**
 * Directory Main
 * PersistenceConsumer
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

package com.spectral.cc.core.directory.commons.persistence.iPojo;

import com.spectral.cc.core.directory.commons.model.technical.network.SubnetType;
import com.spectral.cc.core.directory.commons.persistence.JPAProvider;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.util.*;

@Component(managedservice="com.spectral.cc.core.directory.commons.JPAProvider")
@Provides
@Instantiate
public class JPAProviderImpl implements JPAProvider {

    private static final String DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME = "Directory TX Persistence Consumer";
    private static final Logger log = LoggerFactory.getLogger(JPAProviderImpl.class);

    private static EntityManagerFactory            sharedEMF  = null;
    private static EntityManager                   sharedEM   = null;
    @Requires
    private PersistenceProvider persistenceProvider = null;

    private void close() {
        if (sharedEM != null) sharedEM.close();
        if (sharedEMF != null) sharedEMF.close();
    }

    private void initSubnetType() {
        List<SubnetType> subnetTypes = new ArrayList<SubnetType>();
        SubnetType wanT = new SubnetType().setNameR("WAN").setDescriptionR("WAN type Subnet"); subnetTypes.add(wanT);
        SubnetType manT = new SubnetType().setNameR("MAN").setDescriptionR("MAN type Subnet"); subnetTypes.add(manT);
        SubnetType subnetT = new SubnetType().setNameR("LAN").setDescriptionR("LAN type Subnet"); subnetTypes.add(subnetT);

        try {
            this.getSharedEM().getTransaction().begin();
            for (SubnetType ltype : subnetTypes)
                this.getSharedEM().persist(ltype);
            this.getSharedEM().getTransaction().commit();
        } catch (Exception E) {
            log.error("Fail to init subnettype db ! " + E.getMessage());
        }
    }

    @Bind
    public void bindPersistenceProvider(PersistenceProvider pprovider) {
        log.debug("Bound to persistence provider...");
        persistenceProvider = pprovider;
    }

    @Unbind
    public void unbindPersistenceProvider() {
        log.debug("Unbound from persistence provider...");
        persistenceProvider = null;
    }

    @Updated
    public synchronized void updated(final Dictionary properties) {
        this.close();

        HashMap<String,String> hibernateConf = null;
        Enumeration<String> dicEnum = properties.keys();
        while (dicEnum.hasMoreElements()) {
            if (hibernateConf==null)
                hibernateConf = new HashMap<String,String>();
            String key = (String) dicEnum.nextElement();
            if (key.contains("hibernate")) {
                String value = (String) properties.get(key);
                log.debug("Hibernate conf to update : ({},{})", new Object[]{key,(key.equals("hibernate.connection.password") ? "*****" : value)});
                hibernateConf.put(key, value);
            }
        }

        sharedEMF = persistenceProvider.createEntityManagerFactory("cc-directory",hibernateConf);
        sharedEM  = sharedEMF.createEntityManager();

        this.initSubnetType();
    }

    @Invalidate
    public void invalidate() {
        this.close();
    }

    @Validate
    public void validate() {
        log.debug("{} is started !", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
        if (persistenceProvider==null)
            log.warn("{} has been started but persistence provider is not bound !", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
    }

    @Override
    public EntityManager createEM() {
        log.debug("Create new entity manager from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        return sharedEMF.createEntityManager();
    }

    @Override
    public EntityManager getSharedEM() {
        return sharedEM;
    }
}