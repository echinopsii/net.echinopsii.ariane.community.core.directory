/**
 * Directory JSF Commons
 * Directories Datacenter RUD Controller
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

package com.spectral.cc.core.directory.commons.controller.technical.network.datacenter;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.technical.network.subnet.SubnetsListController;
import com.spectral.cc.core.directory.commons.controller.technical.network.multicastArea.MulticastAreasListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.commons.model.technical.network.Subnet;
import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
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

public class DatacentersListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DatacentersListController.class);

    private EntityManager em = JPAProviderConsumer.getInstance().getJpaProvider().createEM();

    private HashMap<Long, Datacenter> rollback = new HashMap<Long, Datacenter>();

    private LazyDataModel<Datacenter> lazyModel = new DatacenterLazyModel().setEntityManager(em);
    private Datacenter[]              selectedDCList ;

    private HashMap<Long,String>       addedSubnet = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    private HashMap<Long,String>              addedMArea    = new HashMap<Long, String>();
    private HashMap<Long,List<MulticastArea>> removedMareas = new HashMap<Long, List<MulticastArea>>();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    public EntityManager getEm() {
        return em;
    }

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<Datacenter> getLazyModel() {
        return lazyModel;
    }

    public Datacenter[] getSelectedDCList() {
        return selectedDCList;
    }

    public void setSelectedDCList(Datacenter[] selectedDCList) {
        this.selectedDCList = selectedDCList;
    }

    public HashMap<Long, String> getAddedSubnet() {
        return addedSubnet;
    }

    public void setAddedSubnet(HashMap<Long, String> addedSubnet) {
        this.addedSubnet = addedSubnet;
    }

    public void syncAddedSubnet(Datacenter dc) throws NotSupportedException, SystemException {
        for (Subnet subnet : SubnetsListController.getAll(em)) {
            if (subnet.getName().equals(this.addedSubnet.get(dc.getId()))) {
                dc.getSubnets().add(subnet);
            }
        }
    }

    public HashMap<Long, List<Subnet>> getRemovedSubnets() {
        return removedSubnets;
    }

    public void setRemovedSubnets(HashMap<Long, List<Subnet>> removedSubnets) {
        this.removedSubnets = removedSubnets;
    }

    public void syncRemovedSubnets(Datacenter dc) throws NotSupportedException, SystemException {
        List<Subnet> subnets2beRM = this.removedSubnets.get(dc.getId());
        for (Subnet subnet2beRM : subnets2beRM) {
            dc.getSubnets().remove(subnet2beRM);
        }
    }

    public HashMap<Long, String> getAddedMArea() {
        return addedMArea;
    }

    public void setAddedMArea(HashMap<Long, String> addedMArea) {
        this.addedMArea = addedMArea;
    }

    public void syncAddedMArea(Datacenter dc) throws NotSupportedException, SystemException {
        for (MulticastArea marea: MulticastAreasListController.getAll(em)) {
            if (marea.getName().equals(this.addedMArea.get(dc.getId()))) {
                dc.getMulticastAreas().add(marea);
            }
        }
    }

    public HashMap<Long, List<MulticastArea>> getRemovedMareas() {
        return removedMareas;
    }

    public void setRemovedMareas(HashMap<Long, List<MulticastArea>> removedMareas) {
        this.removedMareas = removedMareas;
    }

    public void syncRemovedMAreas(Datacenter dc) throws NotSupportedException, SystemException {
        List<MulticastArea> mareas2beRM = this.removedMareas.get(dc.getId());
        for (MulticastArea marea2beRM : mareas2beRM) {
            dc.getMulticastAreas().remove(marea2beRM);
        }
    }

    /*
     * Datacenter update tools
     */
    public HashMap<Long, Datacenter> getRollback() {
        return rollback;
    }

    public void setRollback(HashMap<Long, Datacenter> rollback) {
        this.rollback = rollback;
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Datacenter eventDc = ((Datacenter) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventDC {} clone from rollback map...", eventDc.getId());
            rollback.remove(eventDc.getId());
            addedSubnet.remove(eventDc.getId());
            removedSubnets.remove(eventDc.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventDC {} clone into rollback map...", eventDc.getId());
            rollback.put(eventDc.getId(), eventDc.clone());
            addedSubnet.put(eventDc.getId(), "");
            removedSubnets.put(eventDc.getId(), new ArrayList<Subnet>());
            addedMArea.put(eventDc.getId(), "");
            removedMareas.put(eventDc.getId(), new ArrayList<MulticastArea>());
        }
    }

    public void update(Datacenter dc) {

        try {
            em.getTransaction().begin();
            em.merge(dc);
            for (Subnet subnet : dc.getSubnets()) {
                if (!rollback.get(dc.getId()).getSubnets().contains(subnet)){
                    subnet.getDatacenters().add(dc);
                    em.merge(subnet);
                }
            }
            for (Subnet subnet : rollback.get(dc.getId()).getSubnets()) {
                if (!dc.getSubnets().contains(subnet)) {
                    subnet.getDatacenters().remove(dc);
                    em.merge(subnet);
                }
            }
            for (MulticastArea marea : dc.getMulticastAreas()) {
                if (!rollback.get(dc.getId()).getMulticastAreas().contains(marea)) {
                    marea.getDatacenters().add(dc);
                    em.merge(marea);
                }
            }
            for (MulticastArea marea : rollback.get(dc.getId()).getMulticastAreas()) {
                if (!dc.getMulticastAreas().contains(marea)) {
                    marea.getDatacenters().remove(dc);
                    em.merge(marea);
                }
            }
            em.flush();
            em.getTransaction().commit();
            rollback.put(dc.getId(), dc);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Datacenter updated successfully !",
                                                       "Datacenter name : " + dc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + rollback.get(dc.getId()).getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * Datacenter delete tool
     */
    public void delete() {
        log.debug("Remove selected DC !");
        for (Datacenter dc2BeRemoved: selectedDCList) {
            try {
                em.getTransaction().begin();
                for (Subnet subnet : dc2BeRemoved.getSubnets()) {
                    subnet.getDatacenters().remove(dc2BeRemoved);
                    em.merge(subnet);
                }
                for (MulticastArea marea : dc2BeRemoved.getMulticastAreas()) {
                    marea.getDatacenters().remove(dc2BeRemoved);
                    em.merge(marea);
                }
                em.remove(dc2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                    "Datacenter deleted successfully !",
                                                    "Datacenter name : " + dc2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while deleting datacenter " + dc2BeRemoved.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        }
        selectedDCList=null;
    }

    public static List<Datacenter> getAll(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all datacenters from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });

        CriteriaBuilder           builder  = em.getCriteriaBuilder();
        CriteriaQuery<Datacenter> criteria = builder.createQuery(Datacenter.class);
        Root<Datacenter>          root     = criteria.from(Datacenter.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Datacenter> ret = em.createQuery(criteria).getResultList();
        // Refresh return list entities as operations can occurs on them from != em
        for(Datacenter dc : ret) {
            em.refresh(dc);
        }
        return ret;
    }
}