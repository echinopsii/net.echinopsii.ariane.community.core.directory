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

package com.spectral.cc.core.directory.commons.controller.organisational.application;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.company.CompanysListController;
import com.spectral.cc.core.directory.commons.controller.organisational.team.TeamsListController;
import com.spectral.cc.core.directory.commons.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.organisational.Team;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

    private HashMap<Long, Application> rollback = new HashMap<Long, Application>();

    private LazyDataModel<Application> lazyModel ;
    private Application[]              selectedApplicationList ;

    private HashMap<Long, String> changedCompany       = new HashMap<Long, String>();
    private HashMap<Long, String> changedTeam          = new HashMap<Long, String>();

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    public ApplicationsListController() {
        lazyModel = new ApplicationLazyModel().setEntityManager(JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM());
    }

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
    public HashMap<Long, Application> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, Application> rollback) {
        this.rollback = rollback;
    }

    public HashMap<Long, String> getChangedCompany() {
        return changedCompany;
    }

    public void setChangedCompany(HashMap<Long, String> changedCompany) {
        this.changedCompany = changedCompany;
    }

    public void syncCompany(Application application) throws NotSupportedException, SystemException {
        for(Company company: CompanysListController.getAll()) {
            if (company.getName().equals(changedCompany.get(application.getId()))) {
                application.setCompany(company);
                break;
            }
        }
    }

    public HashMap<Long, String> getChangedTeam() {
        return changedTeam;
    }

    public void setChangedTeam(HashMap<Long, String> changedTeam) {
        this.changedTeam = changedTeam;
    }

    public void syncTeam(Application application) throws NotSupportedException, SystemException {
        for(Team team: TeamsListController.getAll()) {
            if (team.getName().equals(changedTeam.get(application.getId()))) {
                application.setTeam(team);
                break;
            }
        }
    }

    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    public void syncAddedOSInstance(Application osType) throws NotSupportedException, SystemException {
        for (OSInstance osInstance: OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.addedOSInstance.get(osType.getId()))) {
                osType.getOsInstances().add(osInstance);
            }
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSInstances() {
        return removedOSInstances;
    }

    public void setRemovedOSInstances(HashMap<Long, List<OSInstance>> removedOSInstances) {
        this.removedOSInstances = removedOSInstances;
    }

    public void syncRemovedOSInstances(Application osType) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSInstances.get(osType.getId());
        for (OSInstance osInstance : osInstances) {
            osType.getOsInstances().remove(osInstance);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Application eventApplication = ((Application) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventApplication {} clone from rollback map...", eventApplication.getId());
            rollback.remove(eventApplication.getId());
            changedCompany.remove(eventApplication.getId());
            changedTeam.remove(eventApplication.getId());
            addedOSInstance.remove(eventApplication.getId());
            removedOSInstances.remove(eventApplication.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventApplication {} clone into rollback map...", eventApplication.getId());
            rollback.put(eventApplication.getId(), eventApplication.clone());
            changedCompany.put(eventApplication.getId(),"");
            changedTeam.put(eventApplication.getId(),"");
            addedOSInstance.put(eventApplication.getId(),"");
            removedOSInstances.put(eventApplication.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(Application application) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            if (application.getCompany()!=rollback.get(application.getId()).getCompany()) {
                if (rollback.get(application.getId()).getCompany()!=null) {
                    rollback.get(application.getId()).getCompany().getApplications().remove(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(rollback.get(application.getId()).getCompany());
                }
                if (application.getCompany()!=null) {
                    application.getCompany().getApplications().add(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(application.getCompany());
                }
            }
            if (application.getTeam()!=rollback.get(application.getId()).getTeam()) {
                if (rollback.get(application.getId()).getTeam()!=null) {
                    rollback.get(application.getId()).getTeam().getApplications().remove(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(rollback.get(application.getId()).getTeam());
                }
                if (application.getTeam()!=null) {
                    application.getTeam().getApplications().add(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(application.getTeam());
                }
            }
            for (OSInstance osInstance : rollback.get(application.getId()).getOsInstances()) {
                if (!application.getOsInstances().contains(osInstance)) {
                    osInstance.setOsType(null);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            for (OSInstance osInstance : application.getOsInstances()) {
                if (!rollback.get(application.getId()).getOsInstances().contains(osInstance)){
                    osInstance.getApplications().add(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(application);
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            rollback.put(application.getId(), application);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Application updated successfully !",
                                                       "Application name : " + application.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updeting Application " + rollback.get(application.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().isActive())
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().rollback();
/*
            FacesMessage msg2;
            int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
            switch(txStatus) {
                case Status.STATUS_NO_TRANSACTION:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled !",
                                                   "Operation : Application " + rollback.get(application.getId()).getName() + " update.");
                    break;
                case Status.STATUS_MARKED_ROLLBACK:
                    try {
                        log.debug("Rollback operation !");
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation rollbacked !",
                                                       "Operation : Application " + rollback.get(application.getId()) + " update.");

                    } catch (Throwable t2) {
                        t2.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Error while rollbacking operation !",
                                                       "Operation : Application " + rollback.get(application.getId()) + " update.");
                    }
                    break;
                default:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled ! ("+txStatus+")",
                                                   "Operation : Application " + rollback.get(application.getId()) + " update.");
                    break;
            }
            FacesContext.getCurrentInstance().addMessage(null, msg2);
*/
        }
    }

    /*
     * Application delete tool
     */
    public void delete() {
        log.debug("Remove selected Application !");
        for (Application application: selectedApplicationList) {
            try {
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
                for (OSInstance osInstance : application.getOsInstances()) {
                    osInstance.getApplications().remove(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
                if (application.getTeam()!=null) {
                    application.getTeam().getApplications().remove(application);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(application.getTeam());
                }
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().remove(application);
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
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
                if (JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().isActive())
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().rollback();
/*
                try {
                    FacesMessage msg2;
                    int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
                    switch(txStatus) {
                        case Status.STATUS_NO_TRANSACTION:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled !",
                                                           "Operation : Application " + application.getName() + " deletion.");
                            break;
                        case Status.STATUS_MARKED_ROLLBACK:
                            try {
                                log.debug("Rollback operation !");
                                JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                               "Operation rollbacked !",
                                                               "Operation : Application " + application.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            } catch (SystemException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                               "Error while rollbacking operation !",
                                                               "Operation : Application " + application.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            }
                            break;
                        default:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled ! ("+txStatus+")",
                                                           "Operation : Application " + application.getName() + " deletion.");
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg2);
                } catch (SystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
*/
            }
        }
        selectedApplicationList=null;
    }

    /*
     * Application join tool
     */
    public static List<Application> getAll() throws SystemException, NotSupportedException {
        CriteriaBuilder builder = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }

    public static List<Application> getAllForSelector() throws SystemException, NotSupportedException {
        CriteriaBuilder builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Application> list =  JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
        list.add(0, new Application().setNameR("Select Application"));
        return list;
    }
}