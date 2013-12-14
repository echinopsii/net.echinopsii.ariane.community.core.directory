/**
 * Directory Main bundle
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

package com.spectral.cc.core.directory.main.controller.technical.network.datacenter;

import com.spectral.cc.core.directory.main.controller.technical.network.lan.LansListController;
import com.spectral.cc.core.directory.main.controller.technical.network.multicastArea.MulticastAreasListController;
import com.spectral.cc.core.directory.main.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.main.model.technical.network.Lan;
import com.spectral.cc.core.directory.main.model.technical.network.MulticastArea;
import com.spectral.cc.core.directory.main.runtime.TXPersistenceConsumer;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@SessionScoped
public class DatacentersListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DatacentersListController.class);

    private HashMap<Long, Datacenter> rollback = new HashMap<Long, Datacenter>();

    private LazyDataModel<Datacenter> lazyModel ;
    private Datacenter[]              selectedDCList ;

    private HashMap<Long,String>    addedLAN    = new HashMap<Long, String>();
    private HashMap<Long,List<Lan>> removedLANs = new HashMap<Long, List<Lan>>();

    private HashMap<Long,String>              addedMArea    = new HashMap<Long, String>();
    private HashMap<Long,List<MulticastArea>> removedMareas = new HashMap<Long, List<MulticastArea>>();


    @PostConstruct
    private void init() {
        lazyModel = new DatacenterLazyModel().setEntityManager(TXPersistenceConsumer.getSharedEM());
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

    public HashMap<Long, String> getAddedLAN() {
        return addedLAN;
    }

    public void setAddedLAN(HashMap<Long, String> addedLAN) {
        this.addedLAN = addedLAN;
    }

    public void syncAddedLan(Datacenter dc) throws NotSupportedException, SystemException {
        for (Lan lan: LansListController.getAll()) {
            if (lan.getName().equals(this.addedLAN.get(dc.getId()))) {
                dc.getLans().add(lan);
            }
        }
    }

    public HashMap<Long, List<Lan>> getRemovedLANs() {
        return removedLANs;
    }

    public void setRemovedLANs(HashMap<Long, List<Lan>> removedLANs) {
        this.removedLANs = removedLANs;
    }

    public void syncRemovedLans(Datacenter dc) throws NotSupportedException, SystemException {
        List<Lan> lans2beRM = this.removedLANs.get(dc.getId());
        for (Lan lan2beRM : lans2beRM) {
            dc.getLans().remove(lan2beRM);
        }
    }

    public HashMap<Long, String> getAddedMArea() {
        return addedMArea;
    }

    public void setAddedMArea(HashMap<Long, String> addedMArea) {
        this.addedMArea = addedMArea;
    }

    public void syncAddedMArea(Datacenter dc) throws NotSupportedException, SystemException {
        for (MulticastArea marea: MulticastAreasListController.getAll()) {
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
            addedLAN.remove(eventDc.getId());
            removedLANs.remove(eventDc.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventDC {} clone into rollback map...", eventDc.getId());
            rollback.put(eventDc.getId(), eventDc.clone());
            addedLAN.put(eventDc.getId(), "");
            removedLANs.put(eventDc.getId(), new ArrayList<Lan>());
            addedMArea.put(eventDc.getId(), "");
            removedMareas.put(eventDc.getId(), new ArrayList<MulticastArea>());
        }
    }

    public void update(Datacenter dc) {

        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            TXPersistenceConsumer.getSharedEM().merge(dc);
            for (Lan lan : dc.getLans()) {
                if (!rollback.get(dc.getId()).getLans().contains(lan)){
                    lan.getDatacenters().add(dc);
                    TXPersistenceConsumer.getSharedEM().merge(lan);
                }
            }
            for (Lan lan : rollback.get(dc.getId()).getLans()) {
                if (!dc.getLans().contains(lan)) {
                    lan.getDatacenters().remove(dc);
                    TXPersistenceConsumer.getSharedEM().merge(lan);
                }
            }
            for (MulticastArea marea : dc.getMulticastAreas()) {
                if (!rollback.get(dc.getId()).getMulticastAreas().contains(marea)) {
                    marea.getDatacenters().add(dc);
                    TXPersistenceConsumer.getSharedEM().merge(marea);
                }
            }
            for (MulticastArea marea : rollback.get(dc.getId()).getMulticastAreas()) {
                if (!dc.getMulticastAreas().contains(marea)) {
                    marea.getDatacenters().remove(dc);
                    TXPersistenceConsumer.getSharedEM().merge(marea);
                }
            }
            TXPersistenceConsumer.getSharedUX().commit();
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

            try {
                FacesMessage msg2;
                int txStatus = TXPersistenceConsumer.getSharedUX().getStatus();
                switch(txStatus) {
                    case Status.STATUS_NO_TRANSACTION:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled !",
                                                       "Operation : datacenter " + rollback.get(dc.getId()).getName() + " update.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            TXPersistenceConsumer.getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                        "Operation rollbacked !",
                                                                        "Operation : datacenter " + rollback.get(dc.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                        "Error while rollbacking operation !",
                                                                        "Operation : datacenter " + rollback.get(dc.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : datacenter " + rollback.get(dc.getId()).getName() + " update.");
                        break;
                }
                FacesContext.getCurrentInstance().addMessage(null, msg2);
            } catch (SystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /*
     * Datacenter delete tool
     */
    public void delete() {
        log.debug("Remove selected DC !");
        for (Datacenter dc2BeRemoved: selectedDCList) {
            try {
                TXPersistenceConsumer.getSharedUX().begin();
                TXPersistenceConsumer.getSharedEM().joinTransaction();
                for (Lan lan : dc2BeRemoved.getLans()) {
                    lan.getDatacenters().remove(dc2BeRemoved);
                    TXPersistenceConsumer.getSharedEM().merge(lan);
                }
                for (MulticastArea marea : dc2BeRemoved.getMulticastAreas()) {
                    marea.getDatacenters().remove(dc2BeRemoved);
                    TXPersistenceConsumer.getSharedEM().merge(marea);
                }
                TXPersistenceConsumer.getSharedEM().remove(dc2BeRemoved);
                TXPersistenceConsumer.getSharedUX().commit();
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

                try {
                    FacesMessage msg2;
                    int txStatus = TXPersistenceConsumer.getSharedUX().getStatus();
                    switch(txStatus) {
                        case Status.STATUS_NO_TRANSACTION:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled !",
                                                           "Operation : datacenter " + dc2BeRemoved.getName() + " deletion.");
                            break;
                        case Status.STATUS_MARKED_ROLLBACK:
                            try {
                                log.debug("Rollback operation !");
                                TXPersistenceConsumer.getSharedUX().rollback();
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                            "Operation rollbacked !",
                                                                            "Operation : datacenter " + dc2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            } catch (SystemException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                            "Error while rollbacking operation !",
                                                                            "Operation : datacenter " + dc2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            }
                            break;
                        default:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled ! ("+txStatus+")",
                                                           "Operation : datacenter " + dc2BeRemoved.getName() + " deletion.");
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg2);
                } catch (SystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        selectedDCList=null;
    }

    public static List<Datacenter> getAll() throws SystemException, NotSupportedException {
        CriteriaBuilder           builder  = TXPersistenceConsumer.getSharedEM().getCriteriaBuilder();
        CriteriaQuery<Datacenter> criteria = builder.createQuery(Datacenter.class);
        Root<Datacenter>          root     = criteria.from(Datacenter.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        return TXPersistenceConsumer.getSharedEM().createQuery(criteria).getResultList();
    }
}