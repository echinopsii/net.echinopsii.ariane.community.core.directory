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
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
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
     * save a new subnet thanks data provided through UI form
     */
    public void save() {
        try {
            syncSubnet();
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
        newIPAddress.setOsInstances(this.rosinstance);

        Boolean isExist = newIPAddress.isExist();
        Boolean isValidate = false;

        if(isExist){
            log.debug("IP Address is already bind to subnet !");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                    "Throwable message : IP Address is already bind to subnet " + newIPAddress.getIpAddress());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        try {
            isValidate = newIPAddress.checkIP();
            if (isValidate) {
                log.debug("Bad IP Address !");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                        "Throwable message : Bad IP Address " + newIPAddress.getIpAddress());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }catch (Exception e){
            log.debug("Exception catched !");
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Exception raised while creating IP Address " + newIPAddress.getIpAddress() + " !",
                    "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        if(!isExist && !isValidate) {
            try {
                em.getTransaction().begin();
                em.persist(newIPAddress);
                if (rsubnet != null) {
                    rsubnet.getIpAddress().add(newIPAddress);
                    em.merge(rsubnet);
                }
                if (rosinstance != null) {
                    rosinstance.getIpAddress().add(newIPAddress);
                    em.merge(rosinstance);
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
        }
    }
}