
/**
 * Directory wat
 * Directories Subnet RUD Controller
 * Copyright (C) 2015 Echinopsii
 * Author : Sagar Ghuge
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress;

import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
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
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a ipAddresses list in a PrimeFaces data table, display ipAddresses, update a ipAddresses and remove ipAddresses
 */
public class IPAddressListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(IPAddressListController.class);

    private LazyDataModel<IPAddress> lazyModel = new IPAddressLazyModel();
    private IPAddress[] selectedIPAddressList;

    private HashMap<Long, String> changedSubnet = new HashMap<Long, String>();

    public LazyDataModel<IPAddress> getLazyModel() {
        return lazyModel;
    }

    public IPAddress[] getSelectedIPAddressList() {
        return selectedIPAddressList;
    }

    public void setSelectedIPAddressList(IPAddress[] selectedIPAddressList) {
        this.selectedIPAddressList = selectedIPAddressList;
    }

    public HashMap<Long, String> getChangedSubnet() {
        return changedSubnet;
    }

    public void setChangedSubnet(HashMap<Long, String> changedSubnet) {
        this.changedSubnet = changedSubnet;
    }

    /**
     * Synchronize changed subnet from a IPAddress to database
     *
     * @param ipAddress bean UI is working on
     */
    public void syncRSubnet(IPAddress ipAddress) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            boolean noRsubnet = true;
            ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
            for (Subnet subnet: SubnetsListController.getAll()) {
                if (subnet.getName().equals(changedSubnet.get(ipAddress.getId()))) {
                    em.getTransaction().begin();
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    ipAddress.setNetworkSubnetR(subnet);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "IPAddress updated successfully !",
                            "IPAddress : " + ipAddress.getIpAddress());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    noRsubnet = false;
                    break;
                }
            }
            if (noRsubnet) {
                em.getTransaction().begin();
                ipAddress.setNetworkSubnetR(null);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "IPAddress updated successfully !",
                        "IPAddress : " + ipAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating IPAddress " + ipAddress.getIpAddress() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public String getIPAddressSubnetName(IPAddress ipAddress) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
        String msubnetName = (ipAddress.getNetworkSubnet()!=null) ? ipAddress.getNetworkSubnet().getName() : "None";
        em.close();
        return msubnetName;
    }

    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        IPAddress eventIPAddress = ((IPAddress) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedSubnet.remove(eventIPAddress.getId());
        } else {
            changedSubnet.put(eventIPAddress.getId(), "");
        }
    }

    /**
     * When UI actions an update merge the corresponding subnet bean with the correct subnet instance in the DB and save this instance
     *
     * @param ipAddress bean UI is working on
     */
    public void update(IPAddress ipAddress) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            ipAddress = em.find(ipAddress.getClass(), ipAddress.getId()).setIpAddressR(ipAddress.getIpAddress()).setNetworkSubnetR(ipAddress.getNetworkSubnet());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "IPAddress updated successfully !",
                    "IPAddress: " + ipAddress.getIpAddress());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating IPAddress " + ipAddress.getIpAddress() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected IPAddress
     */
    public void delete() {
        log.debug("Remove selected IPAddress !");
        for (IPAddress ipAddress2BeRemoved : selectedIPAddressList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                ipAddress2BeRemoved = em.find(ipAddress2BeRemoved.getClass(), ipAddress2BeRemoved.getId());
                em.remove(ipAddress2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "IPAddress deleted successfully !",
                        "IPAddress name : " + ipAddress2BeRemoved.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while deleting ipAddress " + ipAddress2BeRemoved.getIpAddress() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                em.close();
            }
        }
        selectedIPAddressList =null;
    }

    /**
     * Get all ipAddress from the db
     *
     * @return all ipAddress from the db
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<IPAddress> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all ipAddress from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<IPAddress> criteria = builder.createQuery(IPAddress.class);
        Root<IPAddress>       root     = criteria.from(IPAddress.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<IPAddress> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}
