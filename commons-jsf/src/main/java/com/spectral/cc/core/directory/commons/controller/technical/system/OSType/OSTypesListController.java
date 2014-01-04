/**
 * Directory JSF Commons
 * Directories OSType RUD Controller
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
package com.spectral.cc.core.directory.commons.controller.technical.system.OSType;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.company.CompanysListController;
import com.spectral.cc.core.directory.commons.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
import com.spectral.cc.core.directory.commons.model.technical.system.OSType;
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

public class OSTypesListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSTypesListController.class);

    private HashMap<Long, OSType> rollback = new HashMap<Long, OSType>();

    private LazyDataModel<OSType> lazyModel ;
    private OSType[]              selectedOSTypeList ;

    private HashMap<Long, String> changedCompany       = new HashMap<Long, String>();

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

    public OSTypesListController() {
        lazyModel = new OSTypeLazyModel().setEntityManager(JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM());
    }

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<OSType> getLazyModel() {
        return lazyModel;
    }

    public OSType[] getSelectedOSTypeList() {
        return selectedOSTypeList;
    }

    public void setSelectedOSTypeList(OSType[] selectedOSTypeList) {
        this.selectedOSTypeList = selectedOSTypeList;
    }

    /*
     * OS Type update tools
     */
    public HashMap<Long, OSType> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, OSType> rollback) {
        this.rollback = rollback;
    }

    public HashMap<Long, String> getChangedCompany() {
        return changedCompany;
    }

    public void setChangedCompany(HashMap<Long, String> changedCompany) {
        this.changedCompany = changedCompany;
    }

    public void syncCompany(OSType osType) throws NotSupportedException, SystemException {
        for(Company company: CompanysListController.getAll()) {
            if (company.getName().equals(changedCompany.get(osType.getId()))) {
                osType.setCompany(company);
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

    public void syncAddedOSInstance(OSType osType) throws NotSupportedException, SystemException {
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

    public void syncRemovedOSInstances(OSType osType) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSInstances.get(osType.getId());
        for (OSInstance osInstance : osInstances) {
            osType.getOsInstances().remove(osInstance);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        OSType eventOSType = ((OSType) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventOSType {} clone from rollback map...", eventOSType.getId());
            rollback.remove(eventOSType.getId());
            changedCompany.remove(eventOSType.getId());
            addedOSInstance.remove(eventOSType.getId());
            removedOSInstances.remove(eventOSType.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventOSType {} clone into rollback map...", eventOSType.getId());
            rollback.put(eventOSType.getId(), eventOSType.clone());
            changedCompany.put(eventOSType.getId(),"");
            addedOSInstance.put(eventOSType.getId(),"");
            removedOSInstances.put(eventOSType.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(OSType osType) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            if (osType.getCompany()!=rollback.get(osType.getId()).getCompany()) {
                if (rollback.get(osType.getId()).getCompany()!=null) {
                    rollback.get(osType.getId()).getCompany().getOsTypes().remove(osType);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(rollback.get(osType.getId()).getCompany());
                }
                if (osType.getCompany()!=null) {
                    osType.getCompany().getOsTypes().add(osType);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osType.getCompany());
                }
            }
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osType);
            for (OSInstance osInstance : rollback.get(osType.getId()).getOsInstances()) {
                if (!osType.getOsInstances().contains(osInstance)) {
                    osInstance.setOsType(null);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            for (OSInstance osInstance : osType.getOsInstances()) {
                if (!rollback.get(osType.getId()).getOsInstances().contains(osInstance)){
                    OSType previousOSType = osInstance.getOsType();
                    if (previousOSType!=null && previousOSType.getName()!=osType.getName()) {
                        previousOSType.getOsInstances().remove(osInstance);
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(previousOSType);
                    }
                    osInstance.setOsType(osType);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            rollback.put(osType.getId(), osType);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Type updated successfully !",
                                                       "OS Type name : " + osType.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updeting OS Type " + rollback.get(osType.getId()).getName() + " !",
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
                                                   "Operation : OS Type " + rollback.get(osType.getId()).getName() + " update.");
                    break;
                case Status.STATUS_MARKED_ROLLBACK:
                    try {
                        log.debug("Rollback operation !");
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation rollbacked !",
                                                       "Operation : OS Type " + rollback.get(osType.getId()) + " update.");

                    } catch (Throwable t2) {
                        t2.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Error while rollbacking operation !",
                                                       "Operation : OS Type " + rollback.get(osType.getId()) + " update.");
                    }
                    break;
                default:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled ! ("+txStatus+")",
                                                   "Operation : OS Type " + rollback.get(osType.getId()) + " update.");
                    break;
            }
            FacesContext.getCurrentInstance().addMessage(null, msg2);
*/
        }
    }

    /*
     * OSType delete tool
     */
    public void delete() {
        log.debug("Remove selected OS Type !");
        for (OSType osType: selectedOSTypeList) {
            try {
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().remove(osType);
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "OS Type deleted successfully !",
                                                           "OS Type name : " + osType.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating OS Type " + osType.getName() + " !",
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
                                                           "Operation : OS Type " + osType.getName() + " deletion.");
                            break;
                        case Status.STATUS_MARKED_ROLLBACK:
                            try {
                                log.debug("Rollback operation !");
                                JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                               "Operation rollbacked !",
                                                               "Operation : OS Type " + osType.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            } catch (SystemException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                               "Error while rollbacking operation !",
                                                               "Operation : OS Type " + osType.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            }
                            break;
                        default:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled ! ("+txStatus+")",
                                                           "Operation : OS Type " + osType.getName() + " deletion.");
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg2);
                } catch (SystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
*/
            }
        }
        selectedOSTypeList=null;
    }

    /*
     * OSType join tool
     */
    public static List<OSType> getAll() throws SystemException, NotSupportedException {
        CriteriaBuilder builder = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<OSType> criteria = builder.createQuery(OSType.class);
        Root<OSType> root = criteria.from(OSType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }

    public static List<OSType> getAllForSelector() throws SystemException, NotSupportedException {
        CriteriaBuilder builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<OSType> criteria = builder.createQuery(OSType.class);
        Root<OSType> root = criteria.from(OSType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<OSType> list =  JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
        list.add(0, new OSType().setNameR("Select OS Type for this OS Instance"));
        return list;
    }
}