/**
 * Directory JSF Commons
 * Directories Subnet Create Controller
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

package com.spectral.cc.core.directory.commons.controller.technical.network.subnet;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.technical.network.datacenter.DatacentersListController;
import com.spectral.cc.core.directory.commons.controller.technical.network.multicastArea.MulticastAreasListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.commons.model.technical.network.Subnet;
import com.spectral.cc.core.directory.commons.model.technical.network.SubnetType;
import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubnetNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SubnetNewController.class);

    private String name;
    private String description;
    private String subnetIP;
    private String subnetMask;

    private String subnetType;
    private SubnetType type;

    private String mArea = "";
    private MulticastArea marea;

    private List<String>    datacentersToBind = new ArrayList<String>();
    private Set<Datacenter> datacenters       = new HashSet<Datacenter>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubnetIP() {
        return subnetIP;
    }

    public void setSubnetIP(String subnetIP) {
        this.subnetIP = subnetIP;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public SubnetType getType() {
        return type;
    }

    public void setType(SubnetType type) {
        this.type = type;
    }

    public String getSubnetType() {
        return subnetType;
    }

    public void setSubnetType(String subnetType) {
        this.subnetType = subnetType;
    }

    private void syncSubnetType() throws NotSupportedException, SystemException {
        SubnetType type = null;
        for (SubnetType ltype: SubnetsListController.getAllSubnetTypes()) {
            if (ltype.getName().equals(this.subnetType)) {
                type = ltype;
                break;
            }
        }
        if (type != null) {
            this.type  = type;
            log.debug("Synced SubnetType : {} {}", new Object[]{this.type.getId(), this.type.getName()});
        }
    }

    public MulticastArea getMarea() {
        return marea;
    }

    public void setMarea(MulticastArea marea) {
        this.marea = marea;
    }

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    private void syncMulticastArea() throws NotSupportedException, SystemException {
        MulticastArea marea = null;
        for (MulticastArea area: MulticastAreasListController.getAll()) {
            if (area.getName().equals(this.mArea)) {
                marea = area;
                break;
            }
        }

        if (marea!=null) {
            this.marea = marea;
            log.debug("Synced Multicast Area : {} {}", new Object[]{this.marea.getId(), this.marea.getName()});
        }
    }

    public List<String> getDatacentersToBind() {
        return datacentersToBind;
    }

    public void setDatacentersToBind(List<String> datacentersToBind) {
        this.datacentersToBind = datacentersToBind;
    }

    public Set<Datacenter> getDatacenters() {
        return datacenters;
    }

    public void setDatacenters(Set<Datacenter> datacenters) {
        this.datacenters = datacenters;
    }

    private void bindSelectedDatacenters() throws NotSupportedException, SystemException {
        for (Datacenter dc: DatacentersListController.getAll()) {
            for (String dcToBind : datacentersToBind)
                if (dc.getName().equals(dcToBind)) {
                    this.datacenters.add(dc);
                    log.debug("Synced datacenter : {} {}", new Object[]{dc.getId(), dc.getName()});
                    break;
                }
        }
    }

    public void save() {
        log.debug("Save new Subnet {} !", new Object[]{name});
        try {
            syncSubnetType();
            syncMulticastArea();
            bindSelectedDatacenters();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating subnet " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Subnet newSubnet = new Subnet();
        newSubnet.setName(name);
        newSubnet.setDescription(description);
        newSubnet.setSubnetIP(subnetIP);
        newSubnet.setSubnetMask(subnetMask);
        newSubnet.setType(type);
        newSubnet.setMarea(marea);
        newSubnet.setDatacenters(this.datacenters);
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(newSubnet);
            if (type!=null)  {type.getSubnets().add(newSubnet);  JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(type);}
            if (this.datacenters.size()!=0)
                for (Datacenter dc: this.datacenters) {
                    dc.getSubnets().add(newSubnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                    if (marea!=null) {
                        if (!marea.getDatacenters().contains(dc)) {
                            marea.getDatacenters().add(dc);
                        }
                    }
                }
            if (marea!=null) {marea.getSubnets().add(newSubnet); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(marea);}
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Subnet created successfully !",
                                                       "Subnet name : " + newSubnet.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating subnet " + newSubnet.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().isActive())
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().rollback();
/*
            try {
                FacesMessage msg2;
                int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
                switch(txStatus) {
                    case Status.STATUS_NO_TRANSACTION:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled !",
                                                       "Operation : subnet " + newSubnet.getName() + " creation.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                        "Operation rollbacked !",
                                                                        "Operation : subnet " + newSubnet.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                        "Error while rollbacking operation !",
                                                                        "Operation : subnet " + newSubnet.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : subnet " + newSubnet.getName() + " creation.");
                        break;
                }
                FacesContext.getCurrentInstance().addMessage(null, msg2);
            } catch (SystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
*/
        }
    }
}