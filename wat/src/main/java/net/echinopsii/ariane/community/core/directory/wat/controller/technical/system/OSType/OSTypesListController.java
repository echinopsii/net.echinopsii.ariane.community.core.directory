/**
 * Directory wat
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSType;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.company.CompanysListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
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
 * This class provide stuff to display a OS Types list in a PrimeFaces data table, display OS Types, update a OS Type and remove OS Types
 */
public class OSTypesListController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSTypesListController.class);

    private LazyDataModel<OSType> lazyModel = new OSTypeLazyModel();
    private OSType[]              selectedOSTypeList ;

    private HashMap<Long, String> changedCompany       = new HashMap<Long, String>();

    private HashMap<Long,String>           addedOSInstance    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSInstances = new HashMap<Long, List<OSInstance>>();

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
    public HashMap<Long, String> getChangedCompany() {
        return changedCompany;
    }

    public void setChangedCompany(HashMap<Long, String> changedCompany) {
        this.changedCompany = changedCompany;
    }

    public void syncCompany(OSType osType) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for(Company company: CompanysListController.getAll()) {
                if (company.getName().equals(changedCompany.get(osType.getId()))) {
                    em.getTransaction().begin();
                    company = em.find(company.getClass(), company.getId());
                    osType = em.find(osType.getClass(), osType.getId());
                    osType.setCompany(company);
                    company.getOsTypes().add(osType);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                        "OS Type updated successfully !",
                                                        "OS Type name : " + osType.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Type " + osType.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getOSTypeCompanyName(OSType osType) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        osType = em.find(osType.getClass(), osType.getId());
        String companyName = (osType.getCompany()!=null) ? osType.getCompany().getName() : "None";
        em.close();
        return  companyName;
    }

    public HashMap<Long, String> getAddedOSInstance() {
        return addedOSInstance;
    }

    public void setAddedOSInstance(HashMap<Long, String> addedOSInstance) {
        this.addedOSInstance = addedOSInstance;
    }

    public void syncAddedOSInstance(OSType osType) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (OSInstance osInstance: OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(this.addedOSInstance.get(osType.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    osType = em.find(osType.getClass(), osType.getId());
                    osType.getOsInstances().add(osInstance);
                    if (osInstance.getOsType()!=null)
                        osInstance.getOsType().getOsInstances().remove(osInstance);
                    osInstance.setOsType(osType);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                        "OS Type updated successfully !",
                                                        "OS Type name : " + osType.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while updating OS Type " + osType.getName() + " !",
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

    public void syncRemovedOSInstances(OSType osType) throws NotSupportedException, SystemException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osType = em.find(osType.getClass(), osType.getId());
            List<OSInstance> osInstances = this.removedOSInstances.get(osType.getId());
            for (OSInstance osInstance : osInstances) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                osType = em.find(osType.getClass(), osType.getId());
                osType.getOsInstances().remove(osInstance);
                osInstance.setOsType(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                "OS Type updated successfully !",
                                                "OS Type name : " + osType.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while updating OS Type " + osType.getName() + " !",
                                                "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the changedCompany, addedOSInstance, removedOSInstances lists with the correct OSType id<br/>
     * When a PrimeFaces data table row is untoogled remove reference from the changedCompany, addedOSInstance, removedOSInstances lists with the correct OSType id<br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        OSType eventOSType = ((OSType) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedCompany.remove(eventOSType.getId());
            addedOSInstance.remove(eventOSType.getId());
            removedOSInstances.remove(eventOSType.getId());
        } else {
            changedCompany.put(eventOSType.getId(),"");
            addedOSInstance.put(eventOSType.getId(),"");
            removedOSInstances.put(eventOSType.getId(),new ArrayList<OSInstance>());
        }
    }

    /**
     * When UI actions an update merge the corresponding OS Type bean with the correct OS Type instance in the DB and save this instance
     *
     * @param osType bean UI is working on
     */
    public void update(OSType osType) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osType = em.find(osType.getClass(), osType.getId()).setArchitectureR(osType.getArchitecture()).setNameR(osType.getName());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                "OS Type updated successfully !",
                                                "OS Type name : " + osType.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while updating OS Type " + osType.getName() + " !",
                                                "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected OS Types
     */
    public void delete() {
        log.debug("Remove selected OS Type !");
        for (OSType osType: selectedOSTypeList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                osType = em.find(osType.getClass(), osType.getId());
                if (osType.getCompany()!=null)
                    osType.getCompany().getOsTypes().remove(osType);
                for (OSInstance osInstance : osType.getOsInstances())
                    osInstance.setOsType(null);
                em.remove(osType);
                em.flush();
                em.getTransaction().commit();
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
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedOSTypeList=null;
    }

    /**
     * Get all OS Types from the db
     *
     * @return all OS Types from the db
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<OSType> getAll() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all OSTypes from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<OSType> criteria = builder.createQuery(OSType.class);
        Root<OSType> root = criteria.from(OSType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<OSType> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    /**
     * Get all OS Types from the db + select string
     *
     * @return all OS Types from the db + select string
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<OSType> getAllForSelector() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all OSTypes from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<OSType> criteria = builder.createQuery(OSType.class);
        Root<OSType> root = criteria.from(OSType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<OSType> list = em.createQuery(criteria).getResultList();
        list.add(0, new OSType().setNameR("Select OS Type for this OS Instance"));
        em.close();
        return list;
    }
}