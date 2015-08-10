/**
 * Directory wat
 * Directories NICard Create Controller
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

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress.IPAddressListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provide stuff to create and save a new NICard from the UI form
 */
public class NICardNewController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(NICardNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String name;
    private String macAddress;
    private String duplex;
    private int mtu;
    private int speed;

    private String rIPAddress = "";
    private IPAddress ripAddress;

    private String rOsInstance = "";
    private OSInstance rosInstance;

    private List<IPAddress> ipaList = new ArrayList<IPAddress>();

    public List<IPAddress> getIpaList() {
        return ipaList;
    }

    public void setIpaList(List<IPAddress> ipaList) {
        this.ipaList = ipaList;
    }

    public OSInstance getRosInstance() {
        return rosInstance;
    }

    public void setRosInstance(OSInstance rosInstance) {
        this.rosInstance = rosInstance;
    }

    public String getrOsInstance() {
        return rOsInstance;
    }

    public void setrOsInstance(String rOsInstance) {
        this.rOsInstance = rOsInstance;
    }

    public IPAddress getRipAddress() {
        return ripAddress;
    }

    public void setRipAddress(IPAddress ripAddress) {
        this.ripAddress = ripAddress;
    }

    public String getrIPAddress() {
        return rIPAddress;
    }

    public void setrIPAddress(String rIPAddress) {
        this.rIPAddress = rIPAddress;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public String getDuplex() {
        return duplex;
    }

    public void setDuplex(String duplex) {
        this.duplex = duplex;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void syncIPAddress() throws NotSupportedException, SystemException {
        IPAddress ripAddress= null;
        for (IPAddress ipAddress : IPAddressListController.getAll()) {
            if (ipAddress.getIpAddress().equals(this.rIPAddress)) {
                ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
                ripAddress = ipAddress;
                break;
            }
        }

        if (ripAddress!=null) {
            this.ripAddress = ripAddress;
            log.debug("Synced IPAddress : {} {}", new Object[]{this.ripAddress.getId(), this.ripAddress.getIpAddress()});
        }
    }

    private void syncOSInstance() throws NotSupportedException, SystemException {
        OSInstance rOsInstance = null;
        for (OSInstance osInstance : OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.rOsInstance)) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                rOsInstance = osInstance;
                break;
            }
        }

        if (rOsInstance != null) {
            this.rosInstance = rOsInstance;
            log.debug("Synced OS Instances : {} {}", new Object[]{this.rosInstance.getId(), this.rosInstance.getName()});
        }
    }

    public void handleSelectedOSInstance() {
        OSInstance osInstanceObj = null;
        for (OSInstance osInstance : OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(rOsInstance)) {
                osInstanceObj = osInstance;
                break;
            }
        }
        this.ipaList.clear();
        if(osInstanceObj!=null) {
            this.ipaList = OSInstancesListController.getAllIPAddresses(osInstanceObj);
        }
    }

    /**
     * save a new nicard data provided through UI form
     */
    public void save() {
        try {
            syncIPAddress();
            syncOSInstance();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Exception raise while creating NICard " + name + " !",
                    "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        NICard niCard = new NICard();
        niCard.setName(name);
        niCard.setDuplex(duplex);
        niCard.setRipAddress(ripAddress);
        niCard.setRosInstance(rosInstance);
        niCard.setMacAddress(macAddress);
        niCard.setMtu(mtu);
        niCard.setSpeed(speed);

        try {
            em.getTransaction().begin();
            em.persist(niCard);
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new NICard {} !", new Object[]{name});
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "NICard created successfully !",
                    "NICard name : " + niCard.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while creating network interface card" + niCard.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}
