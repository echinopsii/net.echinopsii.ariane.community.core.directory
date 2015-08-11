/**
 * Directory wat
 * Directories NICard RUD Controller
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.niCard;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a ipAddresses list in a PrimeFaces data table, display ipAddresses, update a ipAddresses and remove ipAddresses
 */
public class NICardsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(NICardsListController.class);

    private LazyDataModel<NICard> lazyModel = new NICardLazyModel();

    private NICard[] selectedNICardList;

    public NICard[] getSelectedNICardList() {
        return selectedNICardList;
    }

    public void setSelectedNICardList(NICard[] selectedNICardList) {
        this.selectedNICardList = selectedNICardList;
    }

    public LazyDataModel<NICard> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<NICard> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        IPAddress eventIPAddress = ((IPAddress) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedSubnet.remove(eventIPAddress.getId());
            changedOSInstance.remove(eventIPAddress.getId());
        } else {
            changedSubnet.put(eventIPAddress.getId(), "");
            changedOSInstance.put(eventIPAddress.getId(), "");
        }
    }

    /**
     * check entered subnet and IPAddress are compatiable or not
     * before update
     *
     * @param ipAddress bean UI is working on
     */
    public void update(IPAddress ipAddress) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

        Boolean isValid = ipAddress.isValid();
        Boolean isBindToSubnet = ipAddress.isBindToSubnet();

        if(isValid && !isBindToSubnet) {
            em.getTransaction().begin();
            IPAddress ipAddressToUpdate = em.find(ipAddress.getClass(), ipAddress.getId());
            ipAddressToUpdate.setFqdnR(ipAddress.getFqdn()).
                    setIpAddressR(ipAddress.getIpAddress()).setNetworkSubnetR(ipAddress.getNetworkSubnet()).
                    setOsInstancesR(ipAddress.getOsInstance());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "IP Address updated successfully !",
                    "IP Address: " + ipAddress.getIpAddress());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (!isValid) {
                log.debug("Bad IP Address definition !");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while updating IP Address " + ipAddress.getIpAddress() + " !",
                        "Throwable message : Bad IP Address " + ipAddress.getIpAddress() + " - " + ipAddress.getNetworkSubnet().getSubnetIP());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                log.debug("IP Address is already bind to subnet !");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating IP Address " + ipAddress.getIpAddress() + " !",
                        "Throwable message : IP Address is already bind to subnet " + ipAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        em.close();
    }

    /**
     * Remove selected IPAddress
     */
    public void delete() {
        log.debug("Remove selected IP Address !");
        for (IPAddress ipAddress2BeRemoved : selectedIPAddressList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                ipAddress2BeRemoved = em.find(ipAddress2BeRemoved.getClass(), ipAddress2BeRemoved.getId());
                if (ipAddress2BeRemoved.getNetworkSubnet()!=null) ipAddress2BeRemoved.getNetworkSubnet().getIpAddresses().remove(ipAddress2BeRemoved);
                if (ipAddress2BeRemoved.getOsInstance()!=null) ipAddress2BeRemoved.getOsInstance().getIpAddresses().remove(ipAddress2BeRemoved);
                em.remove(ipAddress2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "IP Address deleted successfully !",
                        "IP Address name : " + ipAddress2BeRemoved.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while deleting IP Address " + ipAddress2BeRemoved.getIpAddress() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                em.close();
            }
        }
        selectedIPAddressList =null;
    }

    /**
     * Get All IPAddresses from DB
     * @return List of IPAddress
     */
    public static List<IPAddress> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all IP Addresses from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        criteria.select(root).where(builder.isNull(root.get("osInstance"))).orderBy(builder.asc(root.get("ipAddress")));

        List<IPAddress> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    public static List<IPAddress> getAllFromSubnet(Subnet subnet){
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all IP Addresses from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        criteria.select(root).where(builder.isNull(root.get("osInstance"))).where(builder.equal(root.get("networkSubnet"), subnet)).orderBy(builder.asc(root.get("ipAddress")));

        List<IPAddress> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}
