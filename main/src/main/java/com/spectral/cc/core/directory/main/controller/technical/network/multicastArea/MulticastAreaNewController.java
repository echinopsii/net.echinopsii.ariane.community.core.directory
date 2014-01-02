/**
 * Directory Main bundle
 * Directories MulticastArea Create Controller
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

package com.spectral.cc.core.directory.main.controller.technical.network.multicastArea;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.main.controller.technical.network.datacenter.DatacentersListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
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
public class MulticastAreaNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MulticastAreaNewController.class);

    private String name;
    private String description;

    private List<String> datacentersToBind = new ArrayList<String>();
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

    public void save() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        log.debug("Save new MulticastArea {} !", new Object[]{name});
        try {
            bindSelectedDatacenters();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating lan " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        MulticastArea multicastArea = new MulticastArea();
        multicastArea.setName(name);
        multicastArea.setDescription(description);
        multicastArea.setDatacenters(datacenters);
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(multicastArea);
            for (Datacenter dc : multicastArea.getDatacenters()) {
                dc.getMulticastAreas().add(multicastArea); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
            }
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Multicast area created successfully !",
                                                       "Multicast area name : " + multicastArea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating multicast area " + multicastArea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);

            /*
            FacesMessage msg2;
            int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
            switch(txStatus) {
                case Status.STATUS_NO_TRANSACTION:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled !",
                                                   "Operation : multicast area " + multicastArea.getName() + " creation.");
                    break;
                case Status.STATUS_MARKED_ROLLBACK:
                    try {
                        log.debug("Rollback operation !");
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                    "Operation rollbacked !",
                                                                    "Operation : multicast area " + multicastArea.getName() + " creation.");
                        FacesContext.getCurrentInstance().addMessage(null, msg2);
                    } catch (SystemException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                    "Error while rollbacking operation !",
                                                                    "Operation : multicast area " + multicastArea.getName() + " creation.");
                        FacesContext.getCurrentInstance().addMessage(null, msg2);
                    }
                    break;
                default:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled ! ("+txStatus+")",
                                                   "Operation : multicast area " + multicastArea.getName() + " creation.");
                    break;
            }
            FacesContext.getCurrentInstance().addMessage(null, msg2);
            */
        }
    }
}