/**
 * Directory wat
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress.IPAddressListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.application.ApplicationsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.environment.EnvironmentsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.team.TeamsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSType.OSTypesListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
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
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a OS Instances list in a PrimeFaces data table, display OS Instances, update a OS Instance and remove OS Instances
 */
public class OSInstancesListController implements Serializable{

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSInstancesListController.class);

    private LazyDataModel<OSInstance> lazyModel = new OSInstanceLazyModel();
    private OSInstance[]              selectedOSInstanceList ;

    private HashMap<Long, String> changedOSType       = new HashMap<Long, String>();
    private HashMap<Long, String> changedEmbeddingOSI = new HashMap<Long, String>();

    private HashMap<Long,String>       addedSubnet    = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    private HashMap<Long,String>       addedIPAddress    = new HashMap<Long, String>();
    private HashMap<Long,List<IPAddress>> removedIPAddresses = new HashMap<Long, List<IPAddress>>();

    private HashMap<Long,String>            addedEnv    = new HashMap<Long, String>();
    private HashMap<Long,List<Environment>> removedEnvs = new HashMap<Long, List<Environment>>();

    private HashMap<Long,String>           addedEmbeddedOSI   = new HashMap<Long,String>();
    private HashMap<Long,List<OSInstance>> removedEmbeddedOSI = new HashMap<Long,List<OSInstance>>();

    private HashMap<Long,String>            addedApplication   = new HashMap<Long,String>();
    private HashMap<Long,List<Application>> removedApplication = new HashMap<Long,List<Application>>();

    private HashMap<Long,String>     addedTeam   = new HashMap<Long,String>();
    private HashMap<Long,List<Team>> removedTeam = new HashMap<Long,List<Team>>();

    public LazyDataModel<OSInstance> getLazyModel() {
        return lazyModel;
    }

    public OSInstance[] getSelectedOSInstanceList() {
        return selectedOSInstanceList;
    }

    public void setSelectedOSInstanceList(OSInstance[] selectedOSInstanceList) {
        this.selectedOSInstanceList = selectedOSInstanceList;
    }

    public HashMap<Long, String> getChangedOSType() {
        return changedOSType;
    }

    public void setChangedOSType(HashMap<Long, String> changedOSType) {
        this.changedOSType = changedOSType;
    }

    /**
     * Synchronize changed OS Type from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncOSType(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for(OSType osType: OSTypesListController.getAll()) {
                osType = em.find(osType.getClass(), osType.getId());
                String longName = osType.getName() + " - " + osType.getArchitecture();
                if (longName.equals(changedOSType.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    osInstance.setOsType(osType);
                    osType.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instanc name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getChangedEmbeddingOSI() {
        return changedEmbeddingOSI;
    }

    public void setChangedEmbeddingOSI(HashMap<Long, String> changedEmbeddingOSI) {
        this.changedEmbeddingOSI = changedEmbeddingOSI;
    }

    /**
     * Synchronize changed embedding OS instance from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncEmbeddingOSI(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for(OSInstance osInstance1: OSInstancesListController.getAll()) {
                if (osInstance1.getName().equals(changedEmbeddingOSI.get(osInstance.getId())) && !osInstance1.getName().equals(osInstance.getName())) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    osInstance1 = em.find(osInstance1.getClass(), osInstance1.getId());
                    osInstance.setEmbeddingOSInstance(osInstance1);
                    osInstance1.getEmbeddedOSInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getOSInstanceEmbeddingOSI(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        osInstance = em.find(osInstance.getClass(), osInstance.getId());
        String name = (osInstance.getEmbeddingOSInstance()!=null) ? osInstance.getEmbeddingOSInstance().getName() : "None";
        em.close();
        return name;
    }

    public HashMap<Long, String> getAddedSubnet() {
        return addedSubnet;
    }

    public void setAddedSubnet(HashMap<Long, String> addedSubnet) {
        this.addedSubnet = addedSubnet;
    }

    /**
     * Synchronize added subnet into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedSubnet(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Subnet subnet : SubnetsListController.getAll())
                if (subnet.getName().equals(this.addedSubnet.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    osInstance.getNetworkSubnets().add(subnet);
                    subnet.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Subnet>> getRemovedSubnets() {
        return removedSubnets;
    }

    public void setRemovedSubnets(HashMap<Long, List<Subnet>> removedSubnets) {
        this.removedSubnets = removedSubnets;
    }

    public HashMap<Long, String> getAddedIPAddress() {
        return addedIPAddress;
    }

    /**
     * Synchronize removed subnets from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedSubnets(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<Subnet> subnets = this.removedSubnets.get(osInstance.getId());
            for (Subnet subnet : subnets) {
                subnet = em.find(subnet.getClass(), subnet.getId());
                osInstance.getNetworkSubnets().remove(subnet);
                subnet.getOsInstances().remove(osInstance);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "OS Instance updated successfully !",
                    "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void setAddedIPAddress(HashMap<Long, String> addedIPAddress) {
        this.addedIPAddress = addedIPAddress;
    }

    /**
     * Synchronize added ipAddress into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedIPAddress(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (IPAddress ipAddress : IPAddressListController.getAll())
                if (ipAddress.getIpAddress().equals(this.addedIPAddress.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
                    osInstance.getIpAddress().add(ipAddress);
                    ipAddress.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "OS Instance updated successfully !",
                            "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<IPAddress>> getRemovedIPAddresses() {
        return removedIPAddresses;
    }

    public void setRemovedIPAddresses(HashMap<Long, List<IPAddress>> removedIPAddresses) {
        this.removedIPAddresses = removedIPAddresses;
    }

    /**
     * Synchronize removed ipAddress from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedIPAddress(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<IPAddress> ipAddresses = this.removedIPAddresses.get(osInstance.getId());
            for (IPAddress ipAddress : ipAddresses) {
                ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
                osInstance.getIpAddress().remove(ipAddress);
                ipAddress.getOsInstances().remove(osInstance);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedEnv() {
        return addedEnv;
    }

    public void setAddedEnv(HashMap<Long, String> addedEnv) {
        this.addedEnv = addedEnv;
    }

    /**
     * Synchronize added environment into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedEnv(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Environment environment: EnvironmentsListController.getAll())
                if (environment.getName().equals(this.addedEnv.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    environment = em.find(environment.getClass(), environment.getId());
                    osInstance.getEnvironments().add(environment);
                    environment.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Environment>> getRemovedEnvs() {
        return removedEnvs;
    }

    public void setRemovedEnvs(HashMap<Long, List<Environment>> removedEnvs) {
        this.removedEnvs = removedEnvs;
    }

    /**
     * Synchronize removed environments from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedEnvs(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<Environment> environments = this.removedEnvs.get(osInstance.getId());
            for (Environment environment : environments) {
                environment = em.find(environment.getClass(), environment.getId());
                osInstance.getEnvironments().remove(environment);
                environment.getOsInstances().remove(osInstance);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedEmbeddedOSI() {
        return addedEmbeddedOSI;
    }

    public void setAddedEmbeddedOSI(HashMap<Long, String> addedEmbeddedOSI) {
        this.addedEmbeddedOSI = addedEmbeddedOSI;
    }

    /**
     * Synchronize added embedded OS instance into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedEmbeddedOSI(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (OSInstance osInstance1: OSInstancesListController.getAll()) {
                if (osInstance1.getName().equals(this.addedEmbeddedOSI.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    osInstance1 = em.find(osInstance1.getClass(), osInstance1.getId());
                    osInstance.getEmbeddedOSInstances().add(osInstance1);
                    if (osInstance1.getEmbeddingOSInstance()!=null)
                        osInstance1.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(osInstance1);
                    osInstance1.setEmbeddingOSInstance(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedEmbeddedOSI() {
        return removedEmbeddedOSI;
    }

    public void setRemovedEmbeddedOSI(HashMap<Long, List<OSInstance>> removedEmbeddedOSI) {
        this.removedEmbeddedOSI = removedEmbeddedOSI;
    }

    /**
     * Synchronize removed embedded OS instance from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedEmbeddedOSI(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<OSInstance> osInstances = this.removedEmbeddedOSI.get(osInstance.getId());
            for (OSInstance osInstance1 : osInstances) {
                osInstance1 = em.find(osInstance1.getClass(), osInstance1.getId());
                osInstance.getEmbeddedOSInstances().remove(osInstance1);
                osInstance1.setEmbeddingOSInstance(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
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
     * Synchronize added application into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedApplication(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Application application: ApplicationsListController.getAll())
                if (application.getName().equals(this.addedApplication.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    application = em.find(application.getClass(), application.getId());
                    osInstance.getApplications().add(application);
                    application.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Application>> getRemovedApplication() {
        return removedApplication;
    }

    public void setRemovedApplication(HashMap<Long, List<Application>> removedApplication) {
        this.removedApplication = removedApplication;
    }

    /**
     * Synchronize removed applications from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedApplication(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<Application> applications = this.removedApplication.get(osInstance.getId());
            for (Application application : applications) {
                application = em.find(application.getClass(), application.getId());
                osInstance.getApplications().remove(application);
                application.getOsInstances().remove(osInstance);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                "OS Instance updated successfully !",
                                                "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedTeam() {
        return addedTeam;
    }

    public void setAddedTeam(HashMap<Long, String> addedTeam) {
        this.addedTeam = addedTeam;
    }

    /**
     * Synchronize added team into an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncAddedTeam(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Team team: TeamsListController.getAll())
                if (team.getName().equals(this.addedTeam.get(osInstance.getId()))) {
                    em.getTransaction().begin();
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    team = em.find(team.getClass(), team.getId());
                    osInstance.getTeams().add(team);
                    team.getOsInstances().add(osInstance);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "OS Instance updated successfully !",
                                                               "OS Instance name : " + osInstance.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Team>> getRemovedTeam() {
        return removedTeam;
    }

    public void setRemovedTeam(HashMap<Long, List<Team>> removedTeam) {
        this.removedTeam = removedTeam;
    }

    /**
     * Synchronize removed teams from an OS instance to database
     *
     * @param osInstance bean UI is working on
     */
    public void syncRemovedTeam(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId());
            List<Team> teams = this.removedTeam.get(osInstance.getId());
            for (Team team : teams) {
                team = em.find(team.getClass(), team.getId());
                osInstance.getTeams().remove(team);
                team.getOsInstances().remove(osInstance);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the changedOSType, changedEmbeddingOSI, addedSubnet,
     * removedSubnets, addedEmbeddedOSI, removedEmbeddedOSIs, addedEnv, removedEnvs, addedApplication, removedApplications,
     * addedTeam, removedTeams lists with the correct OSInstance id<br/>
     * When a PrimeFaces data table row is untoogled remove reference from the changedOSType, changedEmbeddingOSI, addedSubnet,
     * removedSubnets, addedEmbeddedOSI, removedEmbeddedOSIs, addedEnv, removedEnvs, addedApplication, removedApplications,
     * addedTeam, removedTeams lists with the correct OSInstance id<br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        OSInstance osInstance = ((OSInstance) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedOSType.remove(osInstance.getId());
            changedEmbeddingOSI.remove(osInstance.getId());
            addedSubnet.remove(osInstance.getId());
            removedSubnets.remove(osInstance.getId());
            addedIPAddress.remove(osInstance.getId());
            removedIPAddresses.remove(osInstance.getId());
            addedEmbeddedOSI.remove(osInstance.getId());
            removedEmbeddedOSI.remove(osInstance.getId());
            addedEnv.remove(osInstance.getId());
            removedEnvs.remove(osInstance.getId());
            addedApplication.remove(osInstance.getId());
            removedApplication.remove(osInstance.getId());
            addedTeam.remove(osInstance.getId());
            removedTeam.remove(osInstance.getId());
        } else {
            changedOSType.put(osInstance.getId(), "");
            changedEmbeddingOSI.put(osInstance.getId(),"");
            addedSubnet.put(osInstance.getId(), "");
            removedSubnets.put(osInstance.getId(), new ArrayList<Subnet>());
            addedIPAddress.put(osInstance.getId(), "");
            removedIPAddresses.put(osInstance.getId(), new ArrayList<IPAddress>());
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

    /**
     * When UI actions an update merge the corresponding OS Instance bean with the correct OS Instance instance in the DB and save this instance
     *
     * @param osInstance bean UI is working on
     */
    public void update(OSInstance osInstance) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            osInstance = em.find(osInstance.getClass(), osInstance.getId()).setNameR(osInstance.getName()).setAdminGateURIR(osInstance.getAdminGateURI()).
                                                                            setDescriptionR(osInstance.getDescription());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OS Instance updated successfully !",
                                                       "OS Instanc name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating OS Instance " + osInstance.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected OS Instances
     */
    public void delete() {
        log.debug("Remove selected Subnet !");
        for (OSInstance osInstance: selectedOSInstanceList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                if (osInstance.getOsType()!=null)
                    osInstance.getOsType().getOsInstances().remove(osInstance);
                for (Environment environment : osInstance.getEnvironments())
                    environment.getOsInstances().remove(osInstance);
                for (Subnet subnet : osInstance.getNetworkSubnets())
                    subnet.getOsInstances().remove(osInstance);
                for (IPAddress ipAddress : osInstance.getIpAddress())
                    ipAddress.getOsInstances().remove(osInstance);
                if (osInstance.getEmbeddingOSInstance()!=null)
                    osInstance.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(osInstance);
                for (OSInstance osi: osInstance.getEmbeddedOSInstances())
                    osi.setEmbeddingOSInstance(null);
                for (Application application: osInstance.getApplications())
                    application.getOsInstances().remove(osInstance);
                for (Team team: osInstance.getTeams())
                    team.getOsInstances().remove(osInstance);
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
            } finally {
                em.close();
            }
        }
        selectedOSInstanceList=null;
    }


    /**
     * Get all OS Instances from the db
     *
     * @return all OS Instances from the db
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<OSInstance> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
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
        em.close();
        return ret;
    }

    /**
     * Get all OS Instances from the db + select string
     *
     * @return all OS Instances from the db + select string
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<OSInstance> getAllForSelector() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
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
        list.add(0, new OSInstance().setNameR("None"));
        em.close();
        return list;
    }
}
