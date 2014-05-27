/**
 * Directory JSF Commons
 * Directories Application RUD Controller
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

package net.echinopsii.ariane.community.core.directory.wat.controller.organisational.application;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.company.CompanysListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.team.TeamsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
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

public class ApplicationsListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ApplicationsListController.class);

    private LazyDataModel<Application> lazyModel = new ApplicationLazyModel();
    private Application[]              selectedApplicationList ;

    private HashMap<Long, String> changedCompany       = new HashMap<Long, String>();
    private HashMap<Long, String> changedTeam          = new HashMap<Long, String>();

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<Application> getLazyModel() {
        return lazyModel;
    }

    public Application[] getSelectedApplicationList() {
        return selectedApplicationList;
    }

    public void setSelectedApplicationList(Application[] selectedApplicationList) {
        this.selectedApplicationList = selectedApplicationList;
    }

    /*
     * OS Type update tools
     */
    public HashMap<Long, String> getChangedCompany() {
        return changedCompany;
    }

    public void setChangedCompany(HashMap<Long, String> changedCompany) {
        this.changedCompany = changedCompany;
    }

    public void syncCompany(Application application) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for(Company company: CompanysListController.getAll()) {
                if (company.getName().equals(changedCompany.get(application.getId()))) {
                    em.getTransaction().begin();
                    application = em.find(application.getClass(), application.getId());
                    company     = em.find(company.getClass(), company.getId());
                    application.setCompany(company);
                    company.getApplications().add(application);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Application updated successfully !",
                                                               "Application name : " + application.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Application " + application.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getApplicationCompanyName(Application application) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        application = em.find(application.getClass(), application.getId());
        String companyName = ((application.getCompany()!=null) ? application.getCompany().getName() : "None");
        em.close();
        return companyName;
    }

    public HashMap<Long, String> getChangedTeam() {
        return changedTeam;
    }

    public void setChangedTeam(HashMap<Long, String> changedTeam) {
        this.changedTeam = changedTeam;
    }

    public void syncTeam(Application application) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for(Team team: TeamsListController.getAll()) {
                if (team.getName().equals(changedTeam.get(application.getId()))) {
                    em.getTransaction().begin();
                    team = em.find(team.getClass(), team.getId());
                    application = em.find(application.getClass(), application.getId());
                    application.setTeam(team);
                    team.getApplications().add(application);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                        "Application updated successfully !",
                                                        "Application name : " + application.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Application " + application.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getApplicationTeamName(Application application) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        application = em.find(application.getClass(), application.getId());
        String teamName = ((application.getTeam()!=null) ? application.getTeam().getName() : "None");
        em.close();
        return teamName;
    }

    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    public void syncAddedOSInstance(Application application) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            for (OSInstance osInstance: OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(this.addedOSInstance.get(application.getId()))) {
                    application = em.find(application.getClass(), application.getId());
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    application.getOsInstances().add(osInstance);
                    osInstance.getApplications().add(application);
                }
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Application updated successfully !",
                                                       "Application name : " + application.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Application " + application.getName() + " !",
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

    public void syncRemovedOSInstances(Application application) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            List<OSInstance> osInstances = this.removedOSInstances.get(application.getId());
            for (OSInstance osInstance : osInstances) {
                application = em.find(application.getClass(), application.getId());
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                application.getOsInstances().remove(osInstance);
                osInstance.getApplications().remove(application);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Application updated successfully !",
                                                       "Application name : " + application.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Application " + application.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Application eventApplication = ((Application) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedCompany.remove(eventApplication.getId());
            changedTeam.remove(eventApplication.getId());
            addedOSInstance.remove(eventApplication.getId());
            removedOSInstances.remove(eventApplication.getId());
        } else {
            changedCompany.put(eventApplication.getId(),"");
            changedTeam.put(eventApplication.getId(),"");
            addedOSInstance.put(eventApplication.getId(),"");
            removedOSInstances.put(eventApplication.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(Application application) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            application = em.find(application.getClass(), application.getId()).setNameR(application.getName()).
                                                                               setShortNameR(application.getShortName()).
                                                                               setDescriptionR(application.getDescription()).
                                                                               setColorCodeR(application.getColorCode());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Application updated successfully !",
                                                       "Application name : " + application.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Application " + application.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /*
     * Application delete tool
     */
    public void delete() {
        log.debug("Remove selected Application !");
        for (Application application: selectedApplicationList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                application = em.find(application.getClass(), application.getId());
                for (OSInstance osInstance : application.getOsInstances())
                    osInstance.getApplications().remove(application);
                if (application.getTeam()!=null)
                    application.getTeam().getApplications().remove(application);
                em.remove(application);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Application deleted successfully !",
                                                           "Application name : " + application.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating Application " + application.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedApplicationList=null;
    }

    /*
     * Application join tool
     */
    public static List<Application> getAll() throws SystemException, NotSupportedException {
        EntityManager  em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all applications from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Application> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    public static List<Application> getAllForSelector() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all applications from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Application> list =  em.createQuery(criteria).getResultList();
        list.add(0, new Application().setNameR("Select Application"));
        return list;
    }
}