/**
 * Directory JSF Commons
 * Directories Environment RUD Controller
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

package com.spectral.cc.core.directory.commons.controller.organisational.environment;

import com.spectral.cc.core.directory.commons.consumer.DirectoryJPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Environment;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnvironmentsListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EnvironmentsListController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    private HashMap<Long, Environment> rollback = new HashMap<Long, Environment>();

    private LazyDataModel<Environment> lazyModel = new EnvironmentLazyModel().setEntityManager(em);
    private Environment[]              selectedEnvironmentList ;

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    public EntityManager getEm() {
        return em;
    }

    /*
     * PrimeFaces table tools
     */
    public HashMap<Long, Environment> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, Environment> rollback) {
        this.rollback = rollback;
    }

    public LazyDataModel<Environment> getLazyModel() {
        return lazyModel;
    }

    public Environment[] getSelectedEnvironmentList() {
        return selectedEnvironmentList;
    }

    public void setSelectedEnvironmentList(Environment[] selectedEnvironmentList) {
        this.selectedEnvironmentList = selectedEnvironmentList;
    }

    /*
     * Environment update tools
     */
    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    public void syncAddedOSInstance(Environment environment) throws NotSupportedException, SystemException {
        for (OSInstance osInstance: OSInstancesListController.getAll(em)) {
            if (osInstance.getName().equals(this.addedOSInstance.get(environment.getId()))) {
                environment.getOsInstances().add(osInstance);
            }
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSInstances() {
        return removedOSInstances;
    }

    public void setRemovedOSInstances(HashMap<Long, List<OSInstance>> removedOSInstances) {
        this.removedOSInstances = removedOSInstances;
    }

    public void syncRemovedOSInstances(Environment environment) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSInstances.get(environment.getId());
        for (OSInstance osInstance : osInstances) {
            environment.getOsInstances().remove(osInstance);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Environment eventEnvironment = ((Environment) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventEnvironment {} clone from rollback map...", eventEnvironment.getId());
            rollback.remove(eventEnvironment.getId());
            addedOSInstance.remove(eventEnvironment.getId());
            removedOSInstances.remove(eventEnvironment.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventEnvironment {} clone into rollback map...", eventEnvironment.getId());
            rollback.put(eventEnvironment.getId(), eventEnvironment.clone());
            addedOSInstance.put(eventEnvironment.getId(),"");
            removedOSInstances.put(eventEnvironment.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(Environment environment) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            em.getTransaction().begin();
            em.merge(environment);
            for (OSInstance osInstance : rollback.get(environment.getId()).getOsInstances()) {
                if (!environment.getOsInstances().contains(osInstance)) {
                    osInstance.getEnvironments().remove(environment);
                    em.merge(osInstance);
                }
            }
            for (OSInstance osInstance : environment.getOsInstances()) {
                if (!rollback.get(environment.getId()).getOsInstances().contains(osInstance)){
                    osInstance.getEnvironments().add(environment);
                    em.merge(osInstance);
                }
            }

            em.flush();
            em.getTransaction().commit();
            rollback.put(environment.getId(), environment);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Environment updated successfully !",
                                                       "Environment name : " + environment.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Environment " + rollback.get(environment.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * OSType delete tool
     */
    public void delete() {
        log.debug("Remove selected Environment !");
        for (Environment environment: selectedEnvironmentList) {
            try {
                em.getTransaction().begin();
                em.remove(environment);

                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Environment deleted successfully !",
                                                           "Environment name : " + environment.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating Environment " + environment.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if(em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
        selectedEnvironmentList=null;
    }

    /*
     * Environment join tool
     */
    public static List<Environment> getAll(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all environments from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Environment> criteria = builder.createQuery(Environment.class);
        Root<Environment> root = criteria.from(Environment.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Environment> ret = em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Environment environment : ret) {
            em.refresh(environment);
        }
        return ret;
    }

    public static List<Environment> getAllForSelector(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all environments from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder  = em.getCriteriaBuilder();
        CriteriaQuery<Environment> criteria = builder.createQuery(Environment.class);
        Root<Environment> root = criteria.from(Environment.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Environment> list =  em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Environment environment : list) {
            em.refresh(environment);
        }
        list.add(0, new Environment().setNameR("Select environment"));
        return list;
    }
}