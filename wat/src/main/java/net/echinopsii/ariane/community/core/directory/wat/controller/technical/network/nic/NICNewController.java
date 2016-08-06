/**
 * Directory wat
 * Directories NIC Create Controller
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.nic;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
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
 * This class provide stuff to create and save a new NIC from the UI form
 */
public class NICNewController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(NICNewController.class);

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

    private String ipAddress = "";
    private IPAddress ipaddress;

    private String osInstance = "";
    private OSInstance osi;

    private List<IPAddress> ipaList = new ArrayList<IPAddress>();

    public List<IPAddress> getIpaList() {
        return ipaList;
    }

    public void setIpaList(List<IPAddress> ipaList) {
        this.ipaList = ipaList;
    }

    public OSInstance getOsi() {
        return osi;
    }

    public void setOsi(OSInstance osi) {
        this.osi = osi;
    }

    public String getOsInstance() {
        return osInstance;
    }

    public void setOsInstance(String osInstance) {
        this.osInstance = osInstance;
    }

    public IPAddress getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(IPAddress ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        for (IPAddress ipAddress : IPAddressListController.getAllIPAddress()) {
            if (ipAddress.getIpAddress().equals(this.ipAddress)) {
                ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
                ripAddress = ipAddress;
                break;
            }
        }

        if (ripAddress!=null) {
            this.ipaddress = ripAddress;
            log.debug("Synced IPAddress : {} {}", new Object[]{this.ipaddress.getId(), this.ipaddress.getIpAddress()});
        }
    }

    private void syncOSInstance() throws NotSupportedException, SystemException {
        OSInstance rOsInstance = null;
        for (OSInstance osInstance : OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.osInstance)) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                rOsInstance = osInstance;
                break;
            }
        }

        if (rOsInstance != null) {
            this.osi = rOsInstance;
            log.debug("Synced OS Instances : {} {}", new Object[]{this.osi.getId(), this.osi.getName()});
        }
    }

    public void handleSelectedOSInstance() {
        OSInstance osInstanceObj = null;
        for (OSInstance osInstance : OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.osInstance)) {
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
     * save a new NIC data provided through UI form
     */
    public void save() {
        try {
            syncIPAddress();
            syncOSInstance();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Exception raise while creating NIC " + name + " !",
                    "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        NIC nic = new NIC();
        nic.setName(name);
        nic.setDuplex(duplex);
        nic.setIpAddress(ipaddress);
        nic.setOsInstance(osi);
        nic.setMacAddress(macAddress);
        nic.setMtu(mtu);
        nic.setSpeed(speed);

        try {
            em.getTransaction().begin();
            em.persist(nic);
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new NIC {} !", new Object[]{name});
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "NIC created successfully !",
                    "NIC name : " + nic.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while creating network interface card" + nic.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}
