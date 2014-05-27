/**
 * Directory wat
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

package net.echinopsii.ariane.community.core.directory.wat.controller.organisational.company;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.application.ApplicationsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSType.OSTypesListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;
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
 * This class provide stuff to display a companies list in a PrimeFaces data table, display companies, update a company and remove companies
 */
public class CompanysListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CompanysListController.class);

    private LazyDataModel<Company> lazyModel = new CompanyLazyModel();
    private Company[]              selectedCompanyList ;

    private HashMap<Long,String>       addedOSType    = new HashMap<Long, String>();
    private HashMap<Long,List<OSType>> removedOSTypes = new HashMap<Long, List<OSType>>();

    private HashMap<Long,String>            addedApplication    = new HashMap<Long, String>();
    private HashMap<Long,List<Application>> removedApplications = new HashMap<Long, List<Application>>();

    public LazyDataModel<Company> getLazyModel() {
        return lazyModel;
    }

    public Company[] getSelectedCompanyList() {
        return selectedCompanyList;
    }

    public void setSelectedCompanyList(Company[] selectedCompanyList) {
        this.selectedCompanyList = selectedCompanyList;
    }

    public HashMap<Long, String> getAddedOSType() {
        return addedOSType;
    }

    public void setAddedOSType(HashMap<Long, String> addedOSType) {
        this.addedOSType = addedOSType;
    }

    /**
     * Synchronize added OS Type into a company to database
     *
     * @param company bean UI is working on
     */
    public void syncAddedOSType(Company company) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (OSType osType: OSTypesListController.getAll()) {
                log.debug("syncAddedOSType: {}{}", new Object[]{osType.getName(),this.addedOSType});
                if (osType.getName().equals(this.addedOSType.get(company.getId()))) {
                    em.getTransaction().begin();
                    company = em.find(company.getClass(), company.getId());
                    osType = em.find(osType.getClass(), osType.getId());
                    if (osType.getCompany() !=null) {
                        osType.getCompany().getOsTypes().remove(osType);
                        osType.setCompany(company);
                    }
                    company.getOsTypes().add(osType);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Company updated successfully !",
                                                               "Company name : " + company.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + company.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<OSType>> getRemovedOSTypes() {
        return removedOSTypes;
    }

    public void setRemovedOSTypes(HashMap<Long, List<OSType>> removedOSTypes) {
        this.removedOSTypes = removedOSTypes;
    }

    /**
     * Synchronize removed OS Type from a company to database
     *
     * @param company bean UI is working on
     */
    public void syncRemovedOSTypes(Company company) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            company = em.find(company.getClass(), company.getId());
            List<OSType> osTypes = this.removedOSTypes.get(company.getId());
            for (OSType osType : osTypes) {
                osType = em.find(osType.getClass(), osType.getId());
                log.debug("syncRemovedOSType: {}", new Object[]{osType.getName()});
                company.getOsTypes().remove(osType);
                osType.setCompany(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Company updated successfully !",
                                                       "Company name : " + company.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + company.getName() + " !",
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
     * Synchronize added application into a company to database
     *
     * @param company bean UI is working on
     */
    public void syncAddedApplication(Company company) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Application application: ApplicationsListController.getAll()) {
                log.debug("syncAddedApplication: {} {}", new Object[]{application.getName(),this.addedApplication});
                if (application.getName().equals(this.addedApplication.get(company.getId()))) {
                    em.getTransaction().begin();
                    company     = em.find(company.getClass(), company.getId());
                    application = em.find(application.getClass(), application.getId());
                    if (application.getCompany() != null) {
                        application.getCompany().getApplications().remove(application);
                        application.setCompany(company);
                    }
                    company.getApplications().add(application);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Company updated successfully !",
                                                               "Company name : " + company.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + company.getName() + " !",
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
     * Synchronize removed application from a company to database
     *
     * @param company bean UI is working on
     */
    public void syncRemovedApplications(Company company) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            company = em.find(company.getClass(), company.getId());
            List<Application> applications = this.removedApplications.get(company.getId());
            for (Application application : applications) {
                log.debug("syncRemovedApplication: {}", new Object[]{application.getName()});

                application = em.find(application.getClass(), application.getId());
                company.getApplications().remove(application);
                application.setCompany(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Company updated successfully !",
                                                       "Company name : " + company.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + company.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedOSType, removedOSTypes, addedApplication and removedApplications lists
     * with the correct company id <br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedOSType, removedOSTypes, addedApplication and removedApplications lists
     * with the correct company id <br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Company eventCompany = ((Company) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedOSType.remove(eventCompany.getId());
            removedOSTypes.remove(eventCompany.getId());
            addedApplication.remove(eventCompany.getId());
            removedApplications.remove(eventCompany.getId());
        } else {
            addedOSType.put(eventCompany.getId(),"");
            removedOSTypes.put(eventCompany.getId(),new ArrayList<OSType>());
            addedApplication.put(eventCompany.getId(), "");
            removedApplications.put(eventCompany.getId(),new ArrayList<Application>());
        }
    }

    /**
     * When UI actions an update merge the corresponding company bean with the correct company instance in the DB and save this instance
     *
     * @param company bean UI is working on
     */
    public void update(Company company) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            company = em.find(company.getClass(), company.getId()).setDescriptionR(company.getDescription()).setNameR(company.getName());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Company updated successfully !",
                                                       "Company name : " + company.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating Company " + company.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /**
     * Remove selected companies
     */
    public void delete() {
        log.debug("Remove selected Company !");
        for (Company company: selectedCompanyList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                company = em.find(company.getClass(), company.getId());
                for (OSType osType : company.getOsTypes())
                    osType.setCompany(null);
                for (Application application : company.getApplications())
                    application.setCompany(null);
                em.remove(company);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Company deleted successfully !",
                                                           "Company name : " + company.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating Company " + company.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedCompanyList=null;
    }

    /**
     * Get all companies from the db
     *
     * @return all companies from the db
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<Company> getAll() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all companies from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Company> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    /**
     * Get all companies from the db + selection string
     *
     * @return all companies from the db + selection string
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<Company> getAllForSelector() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all companies from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<Company> list =  em.createQuery(criteria).getResultList();
        list.add(0, new Company().setNameR("Select company"));
        em.close();
        return list;
    }
}