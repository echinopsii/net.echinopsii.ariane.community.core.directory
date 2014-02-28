/**
 * Directory JSF Commons
 * Directories OS Instance RUD Controller
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
package com.spectral.cc.core.directory.wat.controller.technical.system.OSInstance;

import com.spectral.cc.core.directory.wat.consumer.DirectoryJPAProviderConsumer;
import com.spectral.cc.core.directory.wat.controller.organisational.application.ApplicationsListController;
import com.spectral.cc.core.directory.wat.controller.organisational.environment.EnvironmentsListController;
import com.spectral.cc.core.directory.wat.controller.organisational.team.TeamsListController;
import com.spectral.cc.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import com.spectral.cc.core.directory.wat.controller.technical.system.OSType.OSTypesListController;
import com.spectral.cc.core.directory.base.model.organisational.Application;
import com.spectral.cc.core.directory.base.model.organisational.Environment;
import com.spectral.cc.core.directory.base.model.organisational.Team;
import com.spectral.cc.core.directory.base.model.technical.network.Subnet;
import com.spectral.cc.core.directory.base.model.technical.system.OSInstance;
import com.spectral.cc.core.directory.base.model.technical.system.OSType;
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
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OSInstancesListController implements Serializable{

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSInstancesListController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    private HashMap<Long, OSInstance> rollback = new HashMap<Long, OSInstance>();

    private LazyDataModel<OSInstance> lazyModel = new OSInstanceLazyModel().setEntityManager(em);
    private OSInstance[]              selectedOSInstanceList ;

    private HashMap<Long, String> changedOSType       = new HashMap<Long, String>();
    private HashMap<Long, String> changedEmbeddingOSI = new HashMap<Long, String>();

    private HashMap<Long,String>       addedSubnet    = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    private HashMap<Long,String>            addedEnv    = new HashMap<Long, String>();
    private HashMap<Long,List<Environment>> removedEnvs = new HashMap<Long, List<Environment>>();

    private HashMap<Long,String>           addedEmbeddedOSI   = new HashMap<Long,String>();
    private HashMap<Long,List<OSInstance>> removedEmbeddedOSI = new HashMap<Long,List<OSInstance>>();

    private HashMap<Long,String>            addedApplication   = new HashMap<Long,String>();
    private HashMap<Long,List<Application>> removedApplication = new HashMap<Long,List<Application>>();

    private HashMap<Long,String>     addedTeam   = new HashMap<Long,String>();
    private HashMap<Long,List<Team>> removedTeam = new HashMap<Long,List<Team>>();

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
    public HashMap<Long, OSInstance> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, OSInstance> rollback) {
        this.rollback = rollback;
    }

    public LazyDataModel<OSInstance> getLazyModel() {
        return lazyModel;
    }

    public OSInstance[] getSelectedOSInstanceList() {
        return selectedOSInstanceList;
    }

    /*
     * OSInstance update tools
     */
    public void setSelectedOSInstanceList(OSInstance[] selectedOSInstanceList) {
        this.selectedOSInstanceList = selectedOSInstanceList;
    }

    public HashMap<Long, String> getChangedOSType() {
        return changedOSType;
    }

    public void setChangedOSType(HashMap<Long, String> changedOSType) {
        this.changedOSType = changedOSType;
    }

    public void syncOSType(OSInstance osInstance) throws NotSupportedException, SystemException {
        for(OSType osType: OSTypesListController.getAll(em)) {
            String longName = osType.getName() + " - " + osType.getArchitecture();
            if (longName.equals(changedOSType.get(osInstance.getId()))) {
                osInstance.setOsType(osType);
                break;
            }
        }
    }

    public HashMap<Long, String> getChangedEmbeddingOSI() {
        return changedEmbeddingOSI;
    }

    public void setChangedEmbeddingOSI(HashMap<Long, String> changedEmbeddingOSI) {
        this.changedEmbeddingOSI = changedEmbeddingOSI;
    }

    public void syncEmbeddingOSI(OSInstance osInstance) throws NotSupportedException, SystemException {
        for(OSInstance osInstance1: OSInstancesListController.getAll(em)) {
            if (osInstance1.getName().equals(changedEmbeddingOSI.get(osInstance.getId())) && !osInstance1.getName().equals(osInstance.getName())) {
                osInstance.setEmbeddingOSInstance(osInstance1);
                break;
            }
        }
    }

    public HashMap<Long, String> getAddedSubnet() {
        return addedSubnet;
    }

    public void setAddedSubnet(HashMap<Long, String> addedSubnet) {
        this.addedSubnet = addedSubnet;
    }

    public void syncAddedSubnet(OSInstance osInstance) throws NotSupportedException, SystemException {
        for (Subnet subnet : SubnetsListController.getAll(em))
            if (subnet.getName().equals(this.addedSubnet.get(osInstance.getId())))
                osInstance.getNetworkSubnets().add(subnet);
    }

    public HashMap<Long, List<Subnet>> getRemovedSubnets() {
        return removedSubnets;
    }

    public void setRemovedSubnets(HashMap<Long, List<Subnet>> removedSubnets) {
        this.removedSubnets = removedSubnets;
    }

    public void syncRemovedSubnets(OSInstance osInstance) throws NotSupportedException, SystemException {
        List<Subnet> subnets = this.removedSubnets.get(osInstance.getId());
        for (Subnet subnet : subnets)
            osInstance.getNetworkSubnets().remove(subnet);
    }

    public HashMap<Long, String> getAddedEnv() {
        return addedEnv;
    }

    public void setAddedEnv(HashMap<Long, String> addedEnv) {
        this.addedEnv = addedEnv;
    }

    public void syncAddedEnv(OSInstance osInstance) throws NotSupportedException, SystemException {
        for (Environment environment: EnvironmentsListController.getAll(em))
            if (environment.getName().equals(this.addedEnv.get(osInstance.getId())))
                osInstance.getEnvironments().add(environment);
    }

    public HashMap<Long, List<Environment>> getRemovedEnvs() {
        return removedEnvs;
    }

    public void setRemovedEnvs(HashMap<Long, List<Environment>> removedEnvs) {
        this.removedEnvs = removedEnvs;
    }

    public void syncRemovedEnvs(OSInstance osInstance) throws NotSupportedException, SystemException {
        List<Environment> environments = this.removedEnvs.get(osInstance.getId());
        for (Environment environment : environments)
            osInstance.getEnvironments().remove(environment);
    }

    public HashMap<Long, String> getAddedEmbeddedOSI() {
        return addedEmbeddedOSI;
    }

    public void setAddedEmbeddedOSI(HashMap<Long, String> addedEmbeddedOSI) {
        this.addedEmbeddedOSI = addedEmbeddedOSI;
    }

    public void syncAddedEmbeddedOSI(OSInstance osInstance) throws NotSupportedException, SystemException {
        for (OSInstance osInstance1: OSInstancesListController.getAll(em))
            if (osInstance1.getName().equals(this.addedEmbeddedOSI.get(osInstance.getId())))
                osInstance.getEmbeddedOSInstances().add(osInstance1);
    }

    public HashMap<Long, List<OSInstance>> getRemovedEmbeddedOSI() {
        return removedEmbeddedOSI;
    }

    public void setRemovedEmbeddedOSI(HashMap<Long, List<OSInstance>> removedEmbeddedOSI) {
        this.removedEmbeddedOSI = removedEmbeddedOSI;
    }

    public void syncRemovedEmbeddedOSI(OSInstance osInstance) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedEmbeddedOSI.get(osInstance.getId());
        for (OSInstance osInstance1 : osInstances)
            osInstance.getEmbeddedOSInstances().remove(osInstance1);
    }

    public HashMap<Long, String> getAddedApplication() {
        return addedApplication;
    }

    public void setAddedApplication(HashMap<Long, String> addedApplication) {
        this.addedApplication = addedApplication;
    }

    public void syncAddedApplication(OSInstance osInstance) throws NotSupportedException, SystemException {
        for (Application application: ApplicationsListController.getAll(em))
            if (application.getName().equals(this.addedApplication.get(osInstance.getId())))
                osInstance.getApplications().add(application);
    }

    public HashMap<Long, List<Application>> getRemovedApplication() {
        return removedApplication;
    }

    public void setRemovedApplication(HashMap<Long, List<Application>> removedApplication) {
        this.removedApplication = removedApplication;
    }

    public void syncRemovedApplication(OSInstance osInstance) throws NotSupportedException, SystemException {
        List<Application> applications = this.removedApplication.get(osInstance.getId());
        for (Application application : applications)
            osInstance.getApplications().remove(application);
    }

    public HashMap<Long, String> getAddedTeam() {
        return addedTeam;
    }

    public void setAddedTeam(HashMap<Long, String> addedTeam) {
        this.addedTeam = addedTeam;
    }

    public void syncAddedTeam(OSInstance osInstance) throws NotSupportedException, SystemException {
        for (Team team: TeamsListController.getAll(em))
            if (team.getName().equals(this.addedTeam.get(osInstance.getId())))
                osInstance.getTeams().add(team);
    }

    public HashMap<Long, List<Team>> getRemovedTeam() {
        return removedTeam;
    }

    public void setRemovedTeam(HashMap<Long, List<Team>> removedTeam) {
        this.removedTeam = removedTeam;
    }

    public void syncRemovedTeam(OSInstance osInstance) throws NotSupportedException, SystemException {
        List<Team> teams = this.removedTeam.get(osInstance.getId());
        for (Team team : teams)
            osInstance.getTeams().remove(team);
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        OSInstance osInstance = ((OSInstance) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove osInstance {} clone from rollback map...", osInstance.getId());
            rollback.remove(osInstance.getId());
            changedOSType.remove(osInstance.getId());
            changedEmbeddingOSI.remove(osInstance.getId());
            addedSubnet.remove(osInstance.getId());
            removedSubnets.remove(osInstance.getId());
            addedEmbeddedOSI.remove(osInstance.getId());
            removedEmbeddedOSI.remove(osInstance.getId());
            addedEnv.remove(osInstance.getId());
            removedEnvs.remove(osInstance.getId());
            addedApplication.remove(osInstance.getId());
            removedApplication.remove(osInstance.getId());
            addedTeam.remove(osInstance.getId());
            removedTeam.remove(osInstance.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current osInstance {} clone into rollback map...", osInstance.getId());
            rollback.put(osInstance.getId(), osInstance.clone());
            changedOSType.put(osInstance.getId(), "");
            changedEmbeddingOSI.put(osInstance.getId(),"");
            addedSubnet.put(osInstance.getId(), "");
            removedSubnets.put(osInstance.getId(), new ArrayList<Subnet>());
            addedEmbeddedOSI.put(osInstance.getId(),"");
            removedEmbeddedOSI.put(osInstance.getId(),new ArrayList<OSInstance>());
            addedEnv.put(osInstance.getId(),"");
            removedEnvs.put(osInstance.getId(),new ArrayList<Environment>());
            addedApplication.put(osInstance.getId(),"");
            removedApplication.put(osInstance.getId(),new ArrayList<Application>());
            addedTeam.put(osInstance.getId(),"");
            removedTeam.put(osInstance.getId(),new ArrayList<Team>());
        }
    }

    public void update(OSInstance osInstance) {
        try {
            em.getTransaction().begin();
            if (osInstance.getOsType()!=rollback.get(osInstance.getId()).getOsType()) {
                if (rollback.get(osInstance.getId()).getOsType()!=null) {
                    rollback.get(osInstance.getId()).getOsType().getOsInstances().remove(osInstance);
                    em.merge(rollback.get(osInstance.getId()).getOsType());
                }
                if (osInstance.getOsType()!=null) {
                    osInstance.getOsType().getOsInstances().add(osInstance);
                    em.merge(osInstance.getOsType());
                }
            }
            if (osInstance.getEmbeddingOSInstance()!=rollback.get(osInstance.getId()).getEmbeddingOSInstance()) {
                if (rollback.get(osInstance.getId()).getEmbeddingOSInstance()!=null) {
                    rollback.get(osInstance.getId()).getEmbeddingOSInstance().getEmbeddedOSInstances().remove(osInstance);
                    em.merge(rollback.get(osInstance.getId()).getOsType());
                }
                if (osInstance.getEmbeddingOSInstance()!=null) {
                    osInstance.getEmbeddingOSInstance().getEmbeddedOSInstances().add(osInstance);
                    em.merge(osInstance.getEmbeddingOSInstance());
                }
            }
            em.merge(osInstance);
            for (Environment environment: osInstance.getEnvironments()) {
                if (!rollback.get(osInstance.getId()).getEnvironments().contains(environment)) {
                    environment.getOsInstances().add(osInstance);
                    em.merge(environment);
                }
            }
            for (Environment environment: rollback.get(osInstance.getId()).getEnvironments()) {
                if (!osInstance.getEnvironments().contains(environment)) {
                    environment.getOsInstances().remove(osInstance);
                    em.merge(environment);
                }
            }
            for (Subnet subnet : osInstance.getNetworkSubnets()) {
                if (!rollback.get(osInstance.getId()).getNetworkSubnets().contains(subnet)) {
                    subnet.getOsInstances().add(osInstance);
                    em.merge(subnet);
                }
            }
            for (Subnet subnet : rollback.get(osInstance.getId()).getNetworkSubnets()) {
                if (!osInstance.getNetworkSubnets().contains(subnet)) {
                    subnet.getOsInstances().remove(osInstance);
                    em.merge(subnet);
                }
            }
            for (OSInstance osi : osInstance.getEmbeddedOSInstances()) {
                if (!rollback.get(osInstance.getId()).getEmbeddedOSInstances().contains(osi)) {
                    osi.setEmbeddingOSInstance(osInstance);
                    em.merge(osi);
                }
            }
            for(OSInstance osi : rollback.get(osInstance.getId()).getEmbeddedOSInstances()) {
                if (!osInstance.getEmbeddedOSInstances().contains(osi)) {
                    osi.setEmbeddingOSInstance(null);
                    em.merge(osi);
                }
            }
            for (Application application : osInstance.getApplications()) {
                if (!rollback.get(osInstance.getId()).getApplications().contains(application)) {
                    application.getOsInstances().add(osInstance);
                    em.merge(application);
                }
            }
            for(Application application : rollback.get(osInstance.getId()).getApplications()) {
                if (!osInstance.getApplications().contains(application)) {
                    application.getOsInstances().remove(osInstance);
                    em.merge(application);
                }
            }
            for (Team team : osInstance.getTeams()) {
                if (!rollback.get(osInstance.getId()).getTeams().contains(team)) {
                    team.getOsInstances().add(osInstance);
                    em.merge(team);
                }
            }
            for(Team team : rollback.get(osInstance.getId()).getTeams()) {
                if (!osInstance.getTeams().contains(team)) {
                    team.getOsInstances().remove(osInstance);
                    em.merge(team);
                }
            }


            em.flush();
            em.getTransaction().commit();
            rollback.put(osInstance.getId(), osInstance);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instanc name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + rollback.get(osInstance.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * Subnet delete tool
     */
    public void delete() {
        log.debug("Remove selected Subnet !");
        for (OSInstance osInstance: selectedOSInstanceList) {
            try {
                em.getTransaction().begin();
                if (osInstance.getOsType()!=null) {
                    osInstance.getOsType().getOsInstances().remove(osInstance);
                    em.merge(osInstance.getOsType());
                }
                for (Environment environment : osInstance.getEnvironments()) {
                    environment.getOsInstances().remove(osInstance);
                    em.merge(environment);
                }
                for (Subnet subnet : osInstance.getNetworkSubnets()) {
                    subnet.getOsInstances().remove(osInstance);
                    em.merge(subnet);
                }
                if (osInstance.getEmbeddingOSInstance()!=null) {
                    osInstance.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(osInstance);
                    em.merge(osInstance.getEmbeddingOSInstance());
                }
                for (OSInstance osi: osInstance.getEmbeddedOSInstances()) {
                    osi.setEmbeddingOSInstance(null);
                    em.merge(osi);
                }
                for (Application application: osInstance.getApplications()) {
                    application.getOsInstances().remove(osInstance);
                    em.merge(application);
                }
                for (Team team: osInstance.getTeams()) {
                    team.getOsInstances().remove(osInstance);
                    em.merge(team);
                }
                em.remove(osInstance);

                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "OS Instance deleted successfully !",
                                                           "OS Instance name : " + osInstance.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while deleting OS Instance " + osInstance.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
        selectedOSInstanceList=null;
    }


    /*
     * OSInstance join tools
     */
    public static List<OSInstance> getAll(EntityManager em) {
        log.debug("Get all OSInstances from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder        builder  = em.getCriteriaBuilder();
        CriteriaQuery<OSInstance> criteria = builder.createQuery(OSInstance.class);
        Root<OSInstance>       root     = criteria.from(OSInstance.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<OSInstance> ret = em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(OSInstance osi : ret) {
            em.refresh(osi);
        }
        return ret;
    }

    public static List<OSInstance> getAllForSelector(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all OSInstances from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<OSInstance> criteria = builder.createQuery(OSInstance.class);
        Root<OSInstance> root = criteria.from(OSInstance.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<OSInstance> list =  em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(OSInstance osi : list) {
            em.refresh(osi);
        }
        list.add(0, new OSInstance().setNameR("None"));
        return list;
    }
}
