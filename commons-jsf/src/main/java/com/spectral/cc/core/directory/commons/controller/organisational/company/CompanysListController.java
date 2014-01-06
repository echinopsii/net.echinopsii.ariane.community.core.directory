/**
 * Directory JSF Commons
 * Directories Company RUD Controller
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

package com.spectral.cc.core.directory.commons.controller.organisational.company;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.application.ApplicationsListController;
import com.spectral.cc.core.directory.commons.controller.technical.system.OSType.OSTypesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.technical.system.OSType;
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

public class CompanysListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CompanysListController.class);

    private EntityManager em = JPAProviderConsumer.getInstance().getJpaProvider().createEM();

    private HashMap<Long, Company> rollback = new HashMap<Long, Company>();

    private LazyDataModel<Company> lazyModel = new CompanyLazyModel().setEntityManager(em);
    private Company[]              selectedCompanyList ;

    private HashMap<Long,String>       addedOSType    = new HashMap<Long, String>();
    private HashMap<Long,List<OSType>> removedOSTypes = new HashMap<Long, List<OSType>>();

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
    public HashMap<Long, Company> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, Company> rollback) {
        this.rollback = rollback;
    }

    public LazyDataModel<Company> getLazyModel() {
        return lazyModel;
    }

    public Company[] getSelectedCompanyList() {
        return selectedCompanyList;
    }

    public void setSelectedCompanyList(Company[] selectedCompanyList) {
        this.selectedCompanyList = selectedCompanyList;
    }

    /*
     * Company update tools
     */
    public HashMap<Long, String> getAddedOSType() {
        return addedOSType;
    }

    public void setAddedOSType(HashMap<Long, String> addedOSType) {
        this.addedOSType = addedOSType;
    }

    public void syncAddedOSType(Company company) throws NotSupportedException, SystemException {
        for (OSType osType: OSTypesListController.getAll(em)) {
            log.debug("syncAddedOSType: {}{}", new Object[]{osType.getName(),this.addedOSType});
            if (osType.getName().equals(this.addedOSType.get(company.getId()))) {
                company.getOsTypes().add(osType);
            }
        }
    }

    public HashMap<Long, List<OSType>> getRemovedOSTypes() {
        return removedOSTypes;
    }

    public void setRemovedOSTypes(HashMap<Long, List<OSType>> removedOSTypes) {
        this.removedOSTypes = removedOSTypes;
    }

    public void syncRemovedOSTypes(Company company) throws NotSupportedException, SystemException {
        List<OSType> osTypes = this.removedOSTypes.get(company.getId());
        for (OSType osType : osTypes) {
            log.debug("syncRemovedOSType: {}", new Object[]{osType.getName()});
            company.getOsTypes().remove(osType);
        }
    }

    public HashMap<Long, String> getAddedApplication() {
        return addedApplication;
    }

    public void setAddedApplication(HashMap<Long, String> addedApplication) {
        this.addedApplication = addedApplication;
    }

    public void syncAddedApplication(Company company) throws NotSupportedException, SystemException {
        for (Application application: ApplicationsListController.getAll(em)) {
            log.debug("syncAddedApplication: {} {}", new Object[]{application.getName(),this.addedApplication});
            if (application.getName().equals(this.addedApplication.get(company.getId()))) {
                company.getApplications().add(application);
            }
        }
    }

    public HashMap<Long, List<Application>> getRemovedApplications() {
        return removedApplications;
    }

    public void setRemovedApplications(HashMap<Long, List<Application>> removedApplications) {
        this.removedApplications = removedApplications;
    }

    public void syncRemovedApplications(Company company) throws NotSupportedException, SystemException {
        List<Application> applications = this.removedApplications.get(company.getId());
        for (Application application : applications) {
            log.debug("syncRemovedApplication: {}", new Object[]{application.getName()});
            company.getApplications().remove(application);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Company eventCompany = ((Company) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventCompany {} clone from rollback map...", eventCompany.getId());
            rollback.remove(eventCompany.getId());
            addedOSType.remove(eventCompany.getId());
            removedOSTypes.remove(eventCompany.getId());
            addedApplication.remove(eventCompany.getId());
            removedApplications.remove(eventCompany.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventCompany {} clone into rollback map...", eventCompany.getId());
            rollback.put(eventCompany.getId(), eventCompany.clone());
            addedOSType.put(eventCompany.getId(),"");
            removedOSTypes.put(eventCompany.getId(),new ArrayList<OSType>());
            addedApplication.put(eventCompany.getId(), "");
            removedApplications.put(eventCompany.getId(),new ArrayList<Application>());
        }
    }

    public void update(Company company) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            em.getTransaction().begin();
            for (OSType osType : rollback.get(company.getId()).getOsTypes()) {
                if (!company.getOsTypes().contains(osType)) {
                    osType.setCompany(null);
                    em.merge(osType);
                }
            }
            for (OSType osType : company.getOsTypes()) {
                if (!rollback.get(company.getId()).getOsTypes().contains(osType)){
                    osType.setCompany(company);
                    em.merge(osType);
                }
            }
            for (Application application : rollback.get(company.getId()).getApplications()) {
                if (!company.getApplications().contains(application)) {
                    application.setCompany(null);
                    em.merge(application);
                }
            }
            for (Application osType : company.getApplications()) {
                if (!rollback.get(company.getId()).getApplications().contains(osType)){
                    osType.setCompany(company);
                    em.merge(osType);
                }
            }
            em.merge(company);

            em.flush();
            em.getTransaction().commit();
            rollback.put(company.getId(), company);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Company updated successfully !",
                                                       "Company name : " + company.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + rollback.get(company.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * Company delete tool
     */
    public void delete() {
        log.debug("Remove selected Company !");
        for (Company environment: selectedCompanyList) {
            try {
                em.getTransaction().begin();
                em.remove(environment);

                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Company deleted successfully !",
                                                           "Company name : " + environment.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating Company " + environment.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
        selectedCompanyList=null;
    }

    /*
     * Company join tool
     */
    public static List<Company> getAll(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all companies from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Company> ret = em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Company company : ret) {
            em.refresh(company);
        }
        return ret;
    }

    public static List<Company> getAllForSelector(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all companies from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Company> list =  em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Company company : list) {
            em.refresh(company);
        }
        list.add(0, new Company().setNameR("Select company"));
        return list;
    }
}