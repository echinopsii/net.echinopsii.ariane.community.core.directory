/**
 * Directory Main bundle
 * Directories Lan Create Controller
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

package com.spectral.cc.core.directory.main.controller.technical.network.lan;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.main.controller.technical.network.datacenter.DatacentersListController;
import com.spectral.cc.core.directory.main.controller.technical.network.multicastArea.MulticastAreasListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.commons.model.technical.network.Lan;
import com.spectral.cc.core.directory.commons.model.technical.network.LanType;
import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@RequestScoped
public class LanNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LanNewController.class);

    private String name;
    private String description;
    private String subnetIP;
    private String subnetMask;

    private String lanType;
    private LanType type;

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

    public LanType getType() {
        return type;
    }

    public void setType(LanType type) {
        this.type = type;
    }

    public String getLanType() {
        return lanType;
    }

    public void setLanType(String lanType) {
        this.lanType = lanType;
    }

    private void syncLanType() throws NotSupportedException, SystemException {
        LanType type = null;
        for (LanType ltype: LansListController.getAllLanTypes()) {
            if (ltype.getName().equals(this.lanType)) {
                type = ltype;
                break;
            }
        }
        if (type != null) {
            this.type  = type;
            log.debug("Synced LanType : {} {}", new Object[]{this.type.getId(), this.type.getName()});
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
        log.debug("Save new Lan {} !", new Object[]{name});
        try {
            syncLanType();
            syncMulticastArea();
            bindSelectedDatacenters();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating lan " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Lan newLan = new Lan();
        newLan.setName(name);
        newLan.setDescription(description);
        newLan.setSubnetIP(subnetIP);
        newLan.setSubnetMask(subnetMask);
        newLan.setType(type);
        newLan.setMarea(marea);
        newLan.setDatacenters(this.datacenters);
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(newLan);
            if (type!=null)  {type.getLans().add(newLan);  JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(type);}
            if (this.datacenters.size()!=0)
                for (Datacenter dc: this.datacenters) {
                    dc.getLans().add(newLan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                    if (marea!=null) {
                        if (!marea.getDatacenters().contains(dc)) {
                            marea.getDatacenters().add(dc);
                        }
                    }
                }
            if (marea!=null) {marea.getLans().add(newLan); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(marea);}
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Lan created successfully !",
                                                       "Lan name : " + newLan.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating lan " + newLan.getName() + " !",
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
                                                       "Operation : lan " + newLan.getName() + " creation.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                        "Operation rollbacked !",
                                                                        "Operation : lan " + newLan.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                        "Error while rollbacking operation !",
                                                                        "Operation : lan " + newLan.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : lan " + newLan.getName() + " creation.");
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