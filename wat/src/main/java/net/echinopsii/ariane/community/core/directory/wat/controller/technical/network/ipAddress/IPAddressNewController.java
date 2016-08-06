/**
 * Directory wat
 * Directories IPAddress Create Controller
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

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.nic.NICsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
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
 * This class provide stuff to create and save a new subnet from the UI form
 */
public class IPAddressNewController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(IPAddressNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String ipAddress;
    private String fqdn;

    private String rSubnet = "";
    private Subnet rsubnet;

    private String rOsInstance = "";
    private OSInstance rosinstance;

    private String NIC = "";
    private NIC nic;

    private List<OSInstance> osiList = new ArrayList<OSInstance>();
    private List<NIC> nicList = new ArrayList<NIC>();

    public List<NIC> getNicList() {
        return nicList;
    }

    public void setNicList(List<NIC> nicList) {
        this.nicList = nicList;
    }

    public NIC getNic() {
        return nic;
    }

    public void setNic(NIC nic) {
        this.nic = nic;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Subnet getRsubnet() {
        return rsubnet;
    }

    public void setRsubnet(Subnet rsubnet) {
        this.rsubnet = rsubnet;
    }

    public String getrSubnet() {
        return rSubnet;
    }

    public void setrSubnet(String rSubnet) {
        this.rSubnet = rSubnet;
    }

    public String getrOsInstance() {
        return rOsInstance;
    }

    public void setrOsInstance(String rOsInstance) {
        this.rOsInstance = rOsInstance;
    }

    public OSInstance getRosinstance() {
        return rosinstance;
    }

    public void setRosinstance(OSInstance rosinstance) {
        this.rosinstance = rosinstance;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public List<OSInstance> getOsiList() {
        return osiList;
    }

    public void setOsiList(List<OSInstance> osiList) {
        this.osiList = osiList;
    }

    /**
     * synchronize this.rsubnet from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncSubnet() throws NotSupportedException, SystemException {
        Subnet rsubnet = null;
        for (Subnet subnet: SubnetsListController.getAll()) {
            if (subnet.getName().equals(this.rSubnet)) {
                subnet = em.find(subnet.getClass(), subnet.getId());
                rsubnet = subnet;
                break;
            }
        }

        if (rsubnet!=null) {
            this.rsubnet = rsubnet;
            log.debug("Synced Subnet : {} {}", new Object[]{this.rsubnet.getId(), this.rsubnet.getName()});
        }
    }

    /**
     * synchronize this.rosinstance from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncOSInstance() throws NotSupportedException, SystemException {
        OSInstance rosInstance = null;
        for (OSInstance osInstance: OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.rOsInstance)) {
                osInstance = em.find(osInstance.getClass(), osInstance.getId());
                rosInstance = osInstance;
                break;
            }
        }

        if (rosInstance!=null) {
            this.rosinstance= rosInstance;
            log.debug("Synced OS Instance : {} {}", new Object[]{this.rosinstance.getId(), this.rosinstance.getName()});
        }
    }

    /**
     * synchronize this.nic from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncNIC() throws NotSupportedException, SystemException {
        NIC nic = null;
        for (NIC nicLoop: NICsListController.getAll()) {
            if (nicLoop.getMacAddress().equals(this.NIC)) {
                nicLoop = em.find(nicLoop.getClass(), nicLoop.getId());
                nic = nicLoop;
                break;
            }
        }

        if (nic!=null) {
            this.nic = nic;
            log.debug("Synced NIC : {} {}", new Object[]{this.nic.getId(), this.nic.getName()});
        }
    }

    public void handleSelectedSubnets() {
        Subnet subnetObj = null;
        for (Subnet subnet : SubnetsListController.getAll()) {
            if (subnet.getName().equals(rSubnet)) {
                subnetObj = subnet;
                break;
            }
        }
        this.osiList.clear();
        if(subnetObj!=null) {
            this.osiList = OSInstancesListController.getAllOSIFromSubnet(subnetObj);
            handleSelectedOSInstance();
        }
    }

    public void handleSelectedOSInstance() {
        OSInstance osInstanceObj = null;
        if(this.rOsInstance != "") {
            for (OSInstance osInstance : OSInstancesListController.getAll()) {
                if (osInstance.getName().equals(rOsInstance)) {
                    osInstanceObj = osInstance;
                    break;
                }
            }
            this.nicList.clear();
            if (osInstanceObj != null) {
                this.nicList = OSInstancesListController.getAllNICs(osInstanceObj);
            }
        }
    }

    /**
     * save a new subnet thanks data provided through UI form
     */
    public void save() {
        try {
            syncSubnet();
            syncOSInstance();
            syncNIC();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Exception raise while creating IPAddress " + ipAddress + " !",
                    "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        IPAddress newIPAddress = new IPAddress();
        newIPAddress.setIpAddress(ipAddress);
        newIPAddress.setFqdn(this.fqdn);
        newIPAddress.setNetworkSubnet(this.rsubnet);
        newIPAddress.setOsInstance(this.rosinstance);
        newIPAddress.setNic(this.nic);

        Boolean isBindToSubnet = newIPAddress.isBindToSubnet();
        Boolean isValid = newIPAddress.isValid();

        if(!isBindToSubnet && isValid) {
            try {
                em.getTransaction().begin();
                em.persist(newIPAddress);
                if (rsubnet != null) {
                    rsubnet.getIpAddresses().add(newIPAddress);
                    em.merge(rsubnet);
                }
                if (rosinstance != null) {
                    rosinstance.getIpAddresses().add(newIPAddress);
                    em.merge(rosinstance);
                }
                if (nic != null) {
                    nic.setIpAddress(newIPAddress);
                    em.merge(nic);
                }

                em.flush();
                em.getTransaction().commit();
                log.debug("Save new IPAddress {} !", new Object[]{ipAddress});
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "IPAddress created successfully !",
                        "IPAddress : " + newIPAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        } else {
            if (!isValid) {
                log.debug("Bad IP Address !");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                        "Throwable message : Bad IP Address " + newIPAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else{
                log.debug("IP Address is already bind to subnet !");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                        "Throwable message : IP Address is already bind to subnet " + newIPAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }
}