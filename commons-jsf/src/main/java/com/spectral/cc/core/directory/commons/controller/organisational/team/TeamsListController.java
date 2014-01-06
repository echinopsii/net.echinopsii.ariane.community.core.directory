/**
 * Directory JSF Commons
 * Directories Team RUD Controller
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

package com.spectral.cc.core.directory.commons.controller.organisational.team;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.application.ApplicationsListController;
import com.spectral.cc.core.directory.commons.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Environment;
import com.spectral.cc.core.directory.commons.model.organisational.Team;
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

public class TeamsListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TeamsListController.class);

    private EntityManager em = JPAProviderConsumer.getInstance().getJpaProvider().createEM();

    private HashMap<Long, Team> rollback = new HashMap<Long, Team>();

    private LazyDataModel<Team> lazyModel = new TeamLazyModel().setEntityManager(em) ;
    private Team[]              selectedTeamList ;

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    private HashMap<Long,String>            addedApplication    = new HashMap<Long, String>();
    private HashMap<Long,List<Application>> removedApplications = new HashMap<Long, List<Application>>();

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
    public HashMap<Long, Team> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, Team> rollback) {
        this.rollback = rollback;
    }

    public LazyDataModel<Team> getLazyModel() {
        return lazyModel;
    }

    public Team[] getSelectedTeamList() {
        return selectedTeamList;
    }

    public void setSelectedTeamList(Team[] selectedTeamList) {
        this.selectedTeamList = selectedTeamList;
    }

    /*
     * Team update tools
     */
    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    public void syncAddedOSInstance(Team team) throws NotSupportedException, SystemException {
        for (OSInstance osInstance: OSInstancesListController.getAll(em)) {
            if (osInstance.getName().equals(this.addedOSInstance.get(team.getId()))) {
                team.getOsInstances().add(osInstance);
            }
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSInstances() {
        return removedOSInstances;
    }

    public void setRemovedOSInstances(HashMap<Long, List<OSInstance>> removedOSInstances) {
        this.removedOSInstances = removedOSInstances;
    }

    public void syncRemovedOSInstances(Team team) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSInstances.get(team.getId());
        for (OSInstance osInstance : osInstances) {
            team.getOsInstances().remove(osInstance);
        }
    }

    public HashMap<Long, String> getAddedApplication() {
        return addedApplication;
    }

    public void setAddedApplication(HashMap<Long, String> addedApplication) {
        this.addedApplication = addedApplication;
    }

    public void syncAddedApplication(Team team) throws NotSupportedException, SystemException {
        for (Application application: ApplicationsListController.getAll(em)) {
            if (application.getName().equals(this.addedApplication.get(team.getId()))) {
                team.getApplications().add(application);
            }
        }
    }

    public HashMap<Long, List<Application>> getRemovedApplications() {
        return removedApplications;
    }

    public void setRemovedApplications(HashMap<Long, List<Application>> removedApplications) {
        this.removedApplications = removedApplications;
    }

    public void syncRemovedApplications(Team team) throws NotSupportedException, SystemException {
        List<Application> applications = this.removedApplications.get(team.getId());
        for (Application application : applications) {
            team.getApplications().remove(application);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Team eventTeam = ((Team) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventTeam {} clone from rollback map...", eventTeam.getId());
            rollback.remove(eventTeam.getId());
            addedOSInstance.remove(eventTeam.getId());
            removedOSInstances.remove(eventTeam.getId());
            addedApplication.remove(eventTeam.getId());
            removedApplications.remove(eventTeam.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventTeam {} clone into rollback map...", eventTeam.getId());
            rollback.put(eventTeam.getId(), eventTeam.clone());
            addedOSInstance.put(eventTeam.getId(),"");
            removedOSInstances.put(eventTeam.getId(),new ArrayList<OSInstance>());
            addedApplication.put(eventTeam.getId(),"");
            removedApplications.put(eventTeam.getId(), new ArrayList<Application>());
        }
    }

    public void update(Team team) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            em.getTransaction().begin();
            for (OSInstance osInstance : rollback.get(team.getId()).getOsInstances()) {
                if (!team.getOsInstances().contains(osInstance)) {
                    osInstance.getTeams().remove(team);
                    em.merge(osInstance);
                }
            }
            for (OSInstance osInstance : team.getOsInstances()) {
                if (!rollback.get(team.getId()).getOsInstances().contains(osInstance)){
                    osInstance.getTeams().add(team);
                    em.merge(osInstance);
                }
            }
            for (Application application : rollback.get(team.getId()).getApplications()) {
                if (!team.getApplications().contains(application)) {
                    application.setTeam(null);
                    em.merge(application);
                }
            }
            for (Application application : team.getApplications()) {
                if (!rollback.get(team.getId()).getApplications().contains(application)){
                    application.setTeam(team);
                    em.merge(application);
                }
            }
            em.merge(team);

            em.flush();
            em.getTransaction().commit();
            rollback.put(team.getId(), team);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Team updated successfully !",
                                                       "Team name : " + team.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + rollback.get(team.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * Team delete tool
     */
    public void delete() {
        log.debug("Remove selected Team !");
        for (Team team: selectedTeamList) {
            try {
                em.getTransaction().begin();
                em.remove(team);

                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Team deleted successfully !",
                                                           "Team name : " + team.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating Team " + team.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
        selectedTeamList=null;
    }

    /*
     * Team join tool
     */
    public static List<Team> getAll(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all teams from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Team> criteria = builder.createQuery(Team.class);
        Root<Team> root = criteria.from(Team.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Team> ret = em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Team team : ret) {
            em.refresh(team);
        }
        return ret;
    }

    public static List<Team> getAllForSelector(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all teams from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder  = em.getCriteriaBuilder();
        CriteriaQuery<Team> criteria = builder.createQuery(Team.class);
        Root<Team> root = criteria.from(Team.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Team> list =  em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Team team : list) {
            em.refresh(team);
        }
        list.add(0, new Team().setNameR("Select team"));
        return list;
    }
}