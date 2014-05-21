/**
 * Directory wat
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

package net.echinopsii.ariane.core.directory.wat.controller.organisational.environment;

import net.echinopsii.ariane.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.core.directory.base.model.technical.system.OSInstance;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a environments list in a PrimeFaces data table, display environments, update a environment and remove environments
 */
public class EnvironmentsListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EnvironmentsListController.class);

    private LazyDataModel<Environment> lazyModel = new EnvironmentLazyModel();
    private Environment[]              selectedEnvironmentList ;

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    public LazyDataModel<Environment> getLazyModel() {
        return lazyModel;
    }

    public Environment[] getSelectedEnvironmentList() {
        return selectedEnvironmentList;
    }

    public void setSelectedEnvironmentList(Environment[] selectedEnvironmentList) {
        this.selectedEnvironmentList = selectedEnvironmentList;
    }

    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    /**
     * Synchronize added OS Instance into an environment to database
     *
     * @param environment bean UI is working on
     */
    public void syncAddedOSInstance(Environment environment) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (OSInstance osInstance: OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(this.addedOSInstance.get(environment.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    environment = em.find(environment.getClass(), environment.getId());
                    environment.getOsInstances().add(osInstance);
                    osInstance.getEnvironments().add(environment);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Environment updated successfully !",
                                                               "Environment name : " + environment.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Environment " + environment.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSInstances() {
        return removedOSInstances;
    }

    public void setRemovedOSInstances(HashMap<Long, List<OSInstance>> removedOSInstances) {
        this.removedOSInstances = removedOSInstances;
    }

    /**
     * Synchronize removed OS Instance from an environment to database
     *
     * @param environment bean UI is working on
     */
    public void syncRemovedOSInstances(Environment environment) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            environment = em.find(environment.getClass(), environment.getId());
            List<OSInstance> osInstances = this.removedOSInstances.get(environment.getId());
            for (OSInstance osInstance : osInstances) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                environment.getOsInstances().remove(osInstance);
                osInstance.getEnvironments().remove(environment);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Environment updated successfully !",
                                                       "Environment name : " + environment.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Environment " + environment.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedOSInstance, removedOSInstances lists with the correct environment id <br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedOSInstance, removedOSInstances lists with the correct environment id <br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Environment eventEnvironment = ((Environment) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedOSInstance.remove(eventEnvironment.getId());
            removedOSInstances.remove(eventEnvironment.getId());
        } else {
            addedOSInstance.put(eventEnvironment.getId(),"");
            removedOSInstances.put(eventEnvironment.getId(),new ArrayList<OSInstance>());
        }
    }

    /**
     * When UI actions an update merge the corresponding environment bean with the correct environment instance in the DB and save this instance
     *
     * @param environment bean UI is working on
     */
    public void update(Environment environment) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            environment = em.find(environment.getClass(), environment.getId()).setNameR(environment.getName()).setDescriptionR(environment.getDescription());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Environment updated successfully !",
                                                       "Environment name : " + environment.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Environment " + environment.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected environments
     */
    public void delete() {
        log.debug("Remove selected Environment !");
        for (Environment environment: selectedEnvironmentList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                environment = em.find(environment.getClass(), environment.getId());
                for (OSInstance osInstance : environment.getOsInstances())
                    osInstance.getEnvironments().remove(environment);
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
            } finally {
                em.close();
            }
        }
        selectedEnvironmentList=null;
    }

    /**
     * Get all environments from the db
     *
     * @return all environments from the db
     */
    public static List<Environment> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
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
        em.close();
        return ret;
    }

    /**
     * Get all environments from the db + selection string
     *
     * @return all environments from the db + selection string
     */
    public static List<Environment> getAllForSelector() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
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
        em.close();
        list.add(0, new Environment().setNameR("Select environment"));
        return list;
    }
}