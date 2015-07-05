/**
 * Directory wat
 * Directories Subnet RUD Controller
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress.IPAddressListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.routingArea.RoutingAreasListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.datacenter.DatacentersListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
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

/**
 * This class provide stuff to display a subnets list in a PrimeFaces data table, display subnets, update a subnet and remove subnets
 */
public class SubnetsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SubnetsListController.class);

    private LazyDataModel<Subnet> lazyModel = new SubnetLazyModel();
    private Subnet[] selectedSubnetList;

    private HashMap<Long, String> changedSubnetType = new HashMap<Long, String>();
    private HashMap<Long, String> changedRarea = new HashMap<Long, String>();

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>           addedOSI    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSIs = new HashMap<Long, List<OSInstance>>();

    public LazyDataModel<Subnet> getLazyModel() {
        return lazyModel;
    }

    public Subnet[] getSelectedSubnetList() {
        return selectedSubnetList;
    }

    public void setSelectedSubnetList(Subnet[] selectedSubnetList) {
        this.selectedSubnetList = selectedSubnetList;
    }

    public HashMap<Long, String> getChangedRarea() {
        return changedRarea;
    }

    public void setChangedRarea(HashMap<Long, String> changedRarea) {
        this.changedRarea = changedRarea;
    }

    /**
     * Synchronize changed routing area from a subnet to database
     *
     * @param subnet bean UI is working on
     */
    public void syncRarea(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            boolean noRarea = true;
            subnet = em.find(subnet.getClass(), subnet.getId());
            for (RoutingArea rarea : RoutingAreasListController.getAll()) {
                if (rarea.getName().equals(changedRarea.get(subnet.getId()))) {
                    em.getTransaction().begin();
                    rarea = em.find(rarea.getClass(), rarea.getId());
                    subnet.setRarea(rarea);
                    rarea.getSubnets().add(subnet);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Subnet updated successfully !",
                                                               "Subnet name : " + subnet.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    noRarea = false;
                    break;
                }
            }
            if (noRarea) {
                em.getTransaction().begin();
                subnet.setRarea(null);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Subnet updated successfully !",
                                                           "Subnet name : " + subnet.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getSubnetRareaName(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        subnet = em.find(subnet.getClass(), subnet.getId());
        String mareaName = (subnet.getRarea()!=null) ? subnet.getRarea().getName() : "None";
        em.close();
        return mareaName;
    }


    public HashMap<Long, String> getAddedDC() {
        return addedDC;
    }

    public void setAddedDC(HashMap<Long, String> addedDC) {
        this.addedDC = addedDC;
    }

    /**
     * Synchronize added datacenter into a subnet to database
     *
     * @param subnet bean UI is working on
     */
    public void syncAddedDC(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Datacenter dc: DatacentersListController.getAll()) {
                if (dc.getName().equals(this.addedDC.get(subnet.getId()))) {
                    em.getTransaction().begin();
                    dc = em.find(dc.getClass(), dc.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    subnet.getDatacenters().add(dc);
                    dc.getSubnets().add(subnet);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                        "Subnet updated successfully !",
                                                        "Subnet name : " + subnet.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Datacenter>> getRemovedDCs() {
        return removedDCs;
    }

    public void setRemovedDCs(HashMap<Long, List<Datacenter>> removedDCs) {
        this.removedDCs = removedDCs;
    }

    /**
     * Synchronize removed datacenters from a subnet to database
     *
     * @param subnet bean UI is working on
     */
    public void syncRemovedDCs(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            subnet = em.find(subnet.getClass(), subnet.getId());
            List<Datacenter> dcs2beRM = this.removedDCs.get(subnet.getId());
            log.debug("syncRemovedDCs:{} ", new Object[]{dcs2beRM});
            for (Datacenter dc2beRM : dcs2beRM) {
                dc2beRM = em.find(dc2beRM.getClass(),dc2beRM.getId());
                subnet.getDatacenters().remove(dc2beRM);
                dc2beRM.getSubnets().remove(subnet);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Subnet updated successfully !",
                                                       "Subnet name : " + subnet.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedOSI() {
        return addedOSI;
    }

    public void setAddedOSI(HashMap<Long, String> addedOSI) {
        this.addedOSI = addedOSI;
    }

    /**
     * Synchronize added OS instance into a subnet to database
     *
     * @param subnet bean UI is working on
     */
    public void syncAddedOSI(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            for (OSInstance osInstance: OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(this.addedOSI.get(subnet.getId()))) {
                    subnet     = em.find(subnet.getClass(), subnet.getId());
                    osInstance = em.find(osInstance.getClass(), osInstance.getId());
                    subnet.getOsInstances().add(osInstance);
                    osInstance.getNetworkSubnets().add(subnet);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                        "Subnet updated successfully !",
                                                        "Subnet name : " + subnet.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSIs() {
        return removedOSIs;
    }

    public void setRemovedOSIs(HashMap<Long, List<OSInstance>> removedOSIs) {
        this.removedOSIs = removedOSIs;
    }

    /**
     * Synchronize removed OS instances from a subnet to database
     *
     * @param subnet bean UI is working on
     */
    public void syncRemovedOSIs(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            subnet = em.find(subnet.getClass(), subnet.getId());
            List<OSInstance> osInstances = this.removedOSIs.get(subnet.getId());
            for (OSInstance osInstance : osInstances) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                subnet.getOsInstances().remove(osInstance);
                osInstance.getNetworkSubnets().remove(subnet);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Subnet updated successfully !",
                                                       "Subnet name : " + subnet.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the changedSubnetType, changedRarea,
     * addedDC, removedDCs, addedOSI, removedOSIs lists with the correct multicastarea id<br/>
     * When a PrimeFaces data table row is untoogled remove reference from the changedSubnetType, changedRarea,
     * addedDC, removedDCs, addedOSI, removedOSIs lists with the correct multicastarea id<br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Subnet eventSubnet = ((Subnet) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedSubnetType.remove(eventSubnet.getId());
            changedRarea.remove(eventSubnet.getId());
            addedDC.remove(eventSubnet.getId());
            removedDCs.remove(eventSubnet.getId());
            addedOSI.remove(eventSubnet.getId());
            removedOSIs.remove(eventSubnet.getId());
        } else {
            changedSubnetType.put(eventSubnet.getId(), "");
            changedRarea.put(eventSubnet.getId(), "");
            addedDC.put(eventSubnet.getId(), "");
            removedDCs.put(eventSubnet.getId(), new ArrayList<Datacenter>());
            addedOSI.put(eventSubnet.getId(), "");
            removedOSIs.put(eventSubnet.getId(),new ArrayList<OSInstance>());
        }
    }

    /**
     * When UI actions an update merge the corresponding subnet bean with the correct subnet instance in the DB and save this instance
     *
     * @param subnet bean UI is working on
     */
    public void update(Subnet subnet) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            subnet = em.find(subnet.getClass(), subnet.getId()).setNameR(subnet.getName()).setDescriptionR(subnet.getDescription()).
                                                                setSubnetIPR(subnet.getSubnetIP()).setSubnetMaskR(subnet.getSubnetMask());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Subnet updated successfully !",
                                                       "Subnet name : " + subnet.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + subnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected subnets
     */
    public void delete() {
        log.debug("Remove selected Subnet !");
        for (Subnet subnet2BeRemoved : selectedSubnetList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                subnet2BeRemoved = em.find(subnet2BeRemoved.getClass(), subnet2BeRemoved.getId());
                if (subnet2BeRemoved.getRarea()!=null)
                    subnet2BeRemoved.getRarea().getSubnets().remove(subnet2BeRemoved);
                for (Datacenter dc : subnet2BeRemoved.getDatacenters())
                    dc.getSubnets().remove(subnet2BeRemoved);
                em.remove(subnet2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Subnet deleted successfully !",
                                                           "Subnet name : " + subnet2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while deleting subnet " + subnet2BeRemoved.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                em.close();
            }
        }
        selectedSubnetList =null;
    }

    /**
     * Get all subnets from the db
     *
     * @return all subnets from the db
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<Subnet> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all subnets from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<Subnet> criteria = builder.createQuery(Subnet.class);
        Root<Subnet>       root     = criteria.from(Subnet.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Subnet> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}