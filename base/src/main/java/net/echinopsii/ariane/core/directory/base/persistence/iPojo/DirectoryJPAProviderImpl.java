/**
 * Directory base
 * JPA provider impl
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

package net.echinopsii.ariane.core.directory.base.persistence.iPojo;

import net.echinopsii.ariane.core.directory.base.model.technical.network.SubnetType;
import net.echinopsii.ariane.core.directory.base.persistence.DirectoryJPAProvider;
import org.apache.felix.ipojo.annotations.*;
import org.hibernate.osgi.HibernateOSGiService;
import org.hibernate.osgi.OsgiScanner;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceProvider;
import java.util.*;

/**
 * The directory JPA provider provide tools to create EntityManager for the cc-directory persistence unit. It also add a feature to extend the cc-directory persistance unit through Ariane plugins. <br/><br/>
 * To make work this feature you must have the spectral hibernate distribution which enables this feature.<br/>
 *
 * @see <a href="https://github.com/mffrench/hibernate-orm/tree/4.3.0.Final.spectral">spectral hibernate distribution</a>
 *
 * This is the iPojo implementation of {@link DirectoryJPAProvider}. The component is instantiated at commons-services bundle startup.
 * It provides the {@link DirectoryJPAProvider} service.
 */
@Component(managedservice="net.echinopsii.ariane.core.DirectoryJPAProviderManagedService")
@Provides
@Instantiate
public class DirectoryJPAProviderImpl implements DirectoryJPAProvider {

    private static final String DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME = "Ariane Directory JPA Persistence Provider";
    private static final String DIRECTORY_TXPERSISTENCE_PERSISTENCE_UNIT_NAME = "ariane-directory";
    private static final Logger log = LoggerFactory.getLogger(DirectoryJPAProviderImpl.class);

    private volatile static HashMap<String,Object> hibernateConf    = null;
    private volatile static boolean isStarted = false;

    private volatile static EntityManagerFactory   sharedEMF        = null;
    private volatile static OsgiScanner            hibernateScanner = new OsgiScanner(FrameworkUtil.getBundle(DirectoryJPAProviderImpl.class));

    @Requires
    private PersistenceProvider persistenceProvider = null;
    @Requires
    private HibernateOSGiService hibernateOSGiService = null;

    private void initSubnetType() {
        SubnetType lan = null;
        SubnetType man = null;
        SubnetType wan = null;

        EntityManager em = this.createEM();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<SubnetType> cmpCriteria = builder.createQuery(SubnetType.class);
        Root<SubnetType> cmpRoot = cmpCriteria.from(SubnetType.class);

        cmpCriteria.select(cmpRoot).where(builder.equal(cmpRoot.<String>get("name"), "LAN"));
        TypedQuery<SubnetType> cmpQuery = em.createQuery(cmpCriteria);
        try {
            lan = cmpQuery.getSingleResult();
            log.debug("LAN subnet type already defined ...");
        } catch (NoResultException e) {
            log.debug("LAN subnet type will be defined ...");
        } catch (Exception e) {
            throw e;
        }

        cmpCriteria.select(cmpRoot).where(builder.equal(cmpRoot.<String>get("name"), "MAN"));
        cmpQuery = em.createQuery(cmpCriteria);
        try {
            man = cmpQuery.getSingleResult();
            log.debug("MAN subnet type already defined ...");
        } catch (NoResultException e) {
            log.debug("LAN subnet type will be defined ...");
        } catch (Exception e) {
            throw e;
        }

        cmpCriteria.select(cmpRoot).where(builder.equal(cmpRoot.<String>get("name"), "WAN"));
        cmpQuery = em.createQuery(cmpCriteria);
        try {
            wan = cmpQuery.getSingleResult();
            log.debug("WAN subnet type already defined ...");
        } catch (NoResultException e) {
            log.debug("WAN subnet type will be defined ...");
        } catch (Exception e) {
            throw e;
        }

        em.getTransaction().begin();

        if (lan==null) {
            lan = new SubnetType().setNameR("LAN").setDescriptionR("LAN type Subnet");
            em.persist(lan);
        }

        if (man==null) {
            man = new SubnetType().setNameR("MAN").setDescriptionR("MAN type Subnet");
            em.persist(man);
        }

        if (wan==null) {
            wan = new SubnetType().setNameR("WAN").setDescriptionR("WAN type Subnet");
            em.persist(wan);
        }

        em.flush();
        em.getTransaction().commit();
        log.debug("Close entity manager ...");
        em.close();
    }

    private static boolean isConfigurationValid() {
        if (hibernateConf.containsKey("hibernate.connection.password"))
            return true;
        else
            return false;
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

    @Bind
    public void bindHibernateOSGiService(HibernateOSGiService hosgi) {
        log.debug("Bound to hibernate osgi service...");
        this.hibernateOSGiService = hosgi;
    }

    @Unbind
    public void unbindHibernateOSGiService() {
        log.debug("Unbound from hibernate osgi service...");
        this.hibernateOSGiService = null;
    }

    private void start() {
        log.debug("Create shared entity manager factory from persistence provider {}...", persistenceProvider.toString());
        sharedEMF = persistenceProvider.createEntityManagerFactory(DIRECTORY_TXPERSISTENCE_PERSISTENCE_UNIT_NAME, hibernateConf);
        initSubnetType();
    }

    @Validate
    public void validate() throws InterruptedException {
        while (persistenceProvider==null && isConfigurationValid()) {
            log.debug("Persistence provider or valid config is missing for {}. Sleep some times...", DIRECTORY_TXPERSISTENCE_PERSISTENCE_UNIT_NAME);
            Thread.sleep(10);
        }
        this.start();
        isStarted = true;
        log.info("{} is started", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
    }

    private void stop() {
        if (sharedEMF != null) sharedEMF.close();
    }

    @Invalidate
    public void invalidate() {
        this.stop();
        isStarted = false;
        log.info("{} is stopped", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
    }

    @Updated
    public synchronized void updated(final Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME, Thread.currentThread().toString()});
        if (hibernateConf != null) hibernateConf.clear();
        Enumeration<String> dicEnum = properties.keys();
        while (dicEnum.hasMoreElements()) {
            if (hibernateConf==null)
                hibernateConf = new HashMap<String,Object>();
            String key = (String) dicEnum.nextElement();
            String value = (String) properties.get(key);
            log.debug("Hibernate conf to update : ({},{})", new Object[]{key,(key.equals("hibernate.connection.password") ? "*****" : value)});
            hibernateConf.put(key, value);
        }
        hibernateConf.put(org.hibernate.jpa.AvailableSettings.SCANNER, hibernateScanner);

        if (isStarted && isConfigurationValid()) {
            final Runnable applyConfigUpdate = new Runnable() {
                @Override
                public void run() {
                    log.debug("{} will be restart to apply configuration changes...", DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME);
                    stop();
                    start();
                }
            };
            new Thread(applyConfigUpdate).start();
        }
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
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        return sharedEMF.createEntityManager();
    }

    public void addSubPersistenceBundle(Bundle persistenceBundle) {
        hibernateScanner.addPersistenceBundle(persistenceBundle);
        hibernateOSGiService.addPersistenceBundle(persistenceBundle);
        log.debug("Reinit shared entity manager factory because new persistence bundle has been added {}...", persistenceBundle.getSymbolicName());
        sharedEMF.close();
        sharedEMF = persistenceProvider.createEntityManagerFactory(DIRECTORY_TXPERSISTENCE_PERSISTENCE_UNIT_NAME, hibernateConf);
    }
}