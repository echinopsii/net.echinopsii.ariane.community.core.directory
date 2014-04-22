/**
 * Directory wat
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

package com.spectral.cc.core.directory.wat.controller.organisational.team;

import com.spectral.cc.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import com.spectral.cc.core.directory.wat.controller.organisational.application.ApplicationsListController;
import com.spectral.cc.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.base.model.organisational.Application;
import com.spectral.cc.core.directory.base.model.organisational.Team;
import com.spectral.cc.core.directory.base.model.technical.system.OSInstance;
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
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a teams list in a PrimeFaces data table, display teams, update a team and remove teams
 */
public class TeamsListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TeamsListController.class);

    private LazyDataModel<Team> lazyModel = new TeamLazyModel() ;
    private Team[]              selectedTeamList ;

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    private HashMap<Long,String>            addedApplication    = new HashMap<Long, String>();
    private HashMap<Long,List<Application>> removedApplications = new HashMap<Long, List<Application>>();

    public LazyDataModel<Team> getLazyModel() {
        return lazyModel;
    }

    public Team[] getSelectedTeamList() {
        return selectedTeamList;
    }

    public void setSelectedTeamList(Team[] selectedTeamList) {
        this.selectedTeamList = selectedTeamList;
    }

    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    /**
     * Synchronize added OS Instance into a team to database
     *
     * @param team bean UI is working on
     */
    public void syncAddedOSInstance(Team team) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (OSInstance osInstance: OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(this.addedOSInstance.get(team.getId()))) {
                    em.getTransaction().begin();
                    team = em.find(team.getClass(), team.getId());
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    team.getOsInstances().add(osInstance);
                    osInstance.getTeams().add(team);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Team updated successfully !",
                                                               "Team name : " + team.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + team.getName() + " !",
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
     * Synchronize removed OS Instance from a team to database
     *
     * @param team bean UI is working on
     */
    public void syncRemovedOSInstances(Team team) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            team = em.find(team.getClass(), team.getId());
            List<OSInstance> osInstances = this.removedOSInstances.get(team.getId());
            for (OSInstance osInstance : osInstances) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                team.getOsInstances().remove(osInstance);
                osInstance.getTeams().remove(team);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Team updated successfully !",
                                                       "Team name : " + team.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + team.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedApplication() {
        return addedApplication;
    }

    public void setAddedApplication(HashMap<Long, String> addedApplication) {
        this.addedApplication = addedApplication;
    }

    /**
     * Synchronize added application into a team to database
     *
     * @param team bean UI is working on
     */
    public void syncAddedApplication(Team team) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Application application: ApplicationsListController.getAll()) {
                if (application.getName().equals(this.addedApplication.get(team.getId()))) {
                    em.getTransaction().begin();
                    application = em.find(application.getClass(), application.getId());
                    team = em.find(team.getClass(), team.getId());
                    team.getApplications().add(application);
                    if (application.getTeam()!=null)
                        application.getTeam().getApplications().remove(application);
                    application.setTeam(team);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Team updated successfully !",
                                                               "Team name : " + team.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + team.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Application>> getRemovedApplications() {
        return removedApplications;
    }

    public void setRemovedApplications(HashMap<Long, List<Application>> removedApplications) {
        this.removedApplications = removedApplications;
    }

    /**
     * Synchronize removed application from a team to database
     *
     * @param team bean UI is working on
     */
    public void syncRemovedApplications(Team team) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            team = em.find(team.getClass(), team.getId());
            List<Application> applications = this.removedApplications.get(team.getId());
            for (Application application : applications) {
                application = em.find(application.getClass(), application.getId());
                team.getApplications().remove(application);
                application.setTeam(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                "Team updated successfully !",
                                                "Team name : " + team.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + team.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedOSInstance, removedOSInstances, addedApplication, removedApplications lists
     * with the correct team id <br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedOSInstance, removedOSInstances, addedApplication, removedApplications lists
     * with the correct team id <br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Team eventTeam = ((Team) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedOSInstance.remove(eventTeam.getId());
            removedOSInstances.remove(eventTeam.getId());
            addedApplication.remove(eventTeam.getId());
            removedApplications.remove(eventTeam.getId());
        } else {
            addedOSInstance.put(eventTeam.getId(),"");
            removedOSInstances.put(eventTeam.getId(),new ArrayList<OSInstance>());
            addedApplication.put(eventTeam.getId(),"");
            removedApplications.put(eventTeam.getId(), new ArrayList<Application>());
        }
    }

    /**
     * When UI actions an update merge the corresponding team bean with the correct team instance in the DB and save this instance
     *
     * @param team bean UI is working on
     */
    public void update(Team team) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            team = em.find(team.getClass(), team.getId()).setNameR(team.getName()).setDescriptionR(team.getDescription()).setColorCodeR(team.getColorCode());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Team updated successfully !",
                                                       "Team name : " + team.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Team " + team.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected teams
     */
    public void delete() {
        log.debug("Remove selected Team !");
        for (Team team: selectedTeamList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                team = em.find(team.getClass(),team.getId());
                for (Application application : team.getApplications())
                    application.setTeam(null);
                for (OSInstance osInstance : team.getOsInstances())
                    osInstance.getTeams().remove(team);
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
            } finally {
                em.close();
            }
        }
        selectedTeamList=null;
    }

    /**
     * Get all teams from the db
     *
     * @return all teams from the db
     */
    public static List<Team> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all teams from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Team> criteria = builder.createQuery(Team.class);
        Root<Team> root = criteria.from(Team.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Team> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    /**
     * Get all teams from the db + selection string
     *
     * @return all teams from the db + selection string
     */
    public static List<Team> getAllForSelector() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all teams from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Team> criteria = builder.createQuery(Team.class);
        Root<Team> root = criteria.from(Team.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Team> list =  em.createQuery(criteria).getResultList();
        list.add(0, new Team().setNameR("Select team"));
        em.close();
        return list;
    }
}