/**
 * Directory Main bundle
 * Directories Lan RUD Controller
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
import com.spectral.cc.core.directory.main.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.commons.model.technical.network.Lan;
import com.spectral.cc.core.directory.commons.model.technical.network.LanType;
import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
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
public class LansListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LansListController.class);

    private HashMap<Long, Lan> rollback = new HashMap<Long, Lan>();

    private LazyDataModel<Lan> lazyModel ;
    private Lan[]              selectedLanList ;

    private HashMap<Long, String> changedLanType = new HashMap<Long, String>();
    private HashMap<Long, String> changedMarea   = new HashMap<Long, String>();

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>           addedOSI    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSIs = new HashMap<Long, List<OSInstance>>();

    @PostConstruct
    private void init() {
        lazyModel = new LanLazyModel().setEntityManager(JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM());
    }

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<Lan> getLazyModel() {
        return lazyModel;
    }

    public Lan[] getSelectedLanList() {
        return selectedLanList;
    }

    public void setSelectedLanList(Lan[] selectedLanList) {
        this.selectedLanList = selectedLanList;
    }

    /*
     * Lan update tools
     */
    public HashMap<Long, String> getChangedLanType() {
        return changedLanType;
    }

    public void setChangedLanType(HashMap<Long, String> changedLanType) {
        this.changedLanType = changedLanType;
    }

    public void syncLanType(Lan lan) throws NotSupportedException, SystemException {
        LanType type = null;
        for(LanType ltype: getAllLanTypes()) {
            if (ltype.getName().equals(changedLanType.get(lan.getId()))) {
                type = ltype;
                break;
            }
        }
        if (type != null) {
            lan.setType(type);
        }
    }

    public HashMap<Long, String> getChangedMarea() {
        return changedMarea;
    }

    public void setChangedMarea(HashMap<Long, String> changedMarea) {
        this.changedMarea = changedMarea;
    }

    public void syncMarea(Lan lan) throws NotSupportedException, SystemException {
        MulticastArea mArea = null;
        for (MulticastArea marea : MulticastAreasListController.getAll()) {
            if (marea.getName().equals(changedMarea.get(lan.getId()))) {
                mArea = marea;
                break;
            }
        }
        lan.setMarea(mArea);
    }

    public HashMap<Long, String> getAddedDC() {
        return addedDC;
    }

    public void setAddedDC(HashMap<Long, String> addedDC) {
        this.addedDC = addedDC;
    }

    public void syncAddedDC(Lan lan) throws NotSupportedException, SystemException {
        for (Datacenter dc: DatacentersListController.getAll()) {
            if (dc.getName().equals(this.addedDC.get(lan.getId()))) {
                lan.getDatacenters().add(dc);
            }
        }
    }

    public HashMap<Long, List<Datacenter>> getRemovedDCs() {
        return removedDCs;
    }

    public void setRemovedDCs(HashMap<Long, List<Datacenter>> removedDCs) {
        this.removedDCs = removedDCs;
    }

    public void syncRemovedDCs(Lan lan) throws NotSupportedException, SystemException {
        List<Datacenter> dcs2beRM = this.removedDCs.get(lan.getId());
        log.debug("syncRemovedDCs:{} ", new Object[]{dcs2beRM});
        for (Datacenter dc2beRM : dcs2beRM) {
            lan.getDatacenters().remove(dc2beRM);
        }
    }

    public HashMap<Long, String> getAddedOSI() {
        return addedOSI;
    }

    public void setAddedOSI(HashMap<Long, String> addedOSI) {
        this.addedOSI = addedOSI;
    }

    public void syncAddedOSI(Lan lan) throws NotSupportedException, SystemException {
        for (OSInstance osInstance: OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.addedOSI.get(lan.getId()))) {
                lan.getOsInstances().add(osInstance);
            }
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSIs() {
        return removedOSIs;
    }

    public void setRemovedOSIs(HashMap<Long, List<OSInstance>> removedOSIs) {
        this.removedOSIs = removedOSIs;
    }

    public void syncRemovedOSIs(Lan lan) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSIs.get(lan.getId());
        for (OSInstance osInstance : osInstances) {
            lan.getOsInstances().remove(osInstance);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Lan eventLan = ((Lan) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventLan {} clone from rollback map...", eventLan.getId());
            rollback.remove(eventLan.getId());
            changedLanType.remove(eventLan.getId());
            changedMarea.remove(eventLan.getId());
            addedDC.remove(eventLan.getId());
            removedDCs.remove(eventLan.getId());
            addedOSI.remove(eventLan.getId());
            removedOSIs.remove(eventLan.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventLan {} clone into rollback map...", eventLan.getId());
            rollback.put(eventLan.getId(), eventLan.clone());
            changedLanType.put(eventLan.getId(), "");
            changedMarea.put(eventLan.getId(), "");
            addedDC.put(eventLan.getId(), "");
            removedDCs.put(eventLan.getId(), new ArrayList<Datacenter>());
            addedOSI.put(eventLan.getId(), "");
            removedOSIs.put(eventLan.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(Lan lan) {
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            if (lan.getType()!=rollback.get(lan.getId()).getType()) {
                if (rollback.get(lan.getId()).getType()!=null) {
                    rollback.get(lan.getId()).getType().getLans().remove(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(rollback.get(lan.getId()).getType());
                }
                if (lan.getType()!=null) {
                    lan.getType().getLans().add(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan.getType());
                }
            }
            if (lan.getMarea()!=rollback.get(lan.getId()).getMarea()) {
                if (rollback.get(lan.getId()).getMarea()!=null)
                    rollback.get(lan.getId()).getMarea().getLans().remove(lan);
                if (lan.getMarea()!=null) {
                    lan.getMarea().getLans().add(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan.getMarea());

                }
            }
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan);
            for (Datacenter dc: lan.getDatacenters()) {
                if (!rollback.get(lan.getId()).getDatacenters().contains(dc)) {
                    dc.getLans().add(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                }
            }
            for (Datacenter dc: rollback.get(lan.getId()).getDatacenters()) {
                if (!lan.getDatacenters().contains(dc)) {
                    dc.getLans().remove(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                }
            }
            for (OSInstance osInstance: lan.getOsInstances()) {
                if (!rollback.get(lan.getId()).getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkLans().add(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            for (OSInstance osInstance: rollback.get(lan.getId()).getOsInstances()) {
                if (!lan.getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkLans().remove(lan);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            rollback.put(lan.getId(), lan);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Lan updated successfully !",
                                                       "Lan name : " + lan.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating lan " + rollback.get(lan.getId()).getName() + " !",
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
                                                       "Operation : lan " + rollback.get(lan.getId()).getName() + " update.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                        "Operation rollbacked !",
                                                                        "Operation : lan " + rollback.get(lan.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                        "Error while rollbacking operation !",
                                                                        "Operation : lan " + rollback.get(lan.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : lan " + rollback.get(lan.getId()).getName() + " update.");
                        break;
                }
                FacesContext.getCurrentInstance().addMessage(null, msg2);
            } catch (SystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
*/
        }
    }

    /*
     * Lan delete tool
     */
    public void delete() {
        log.debug("Remove selected Lan !");
        for (Lan lan2BeRemoved: selectedLanList) {
            try {
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
                if (lan2BeRemoved.getType()!=null) {
                    lan2BeRemoved.getType().getLans().remove(lan2BeRemoved);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan2BeRemoved.getType());
                }
                if (lan2BeRemoved.getMarea()!=null) {
                    lan2BeRemoved.getMarea().getLans().remove(lan2BeRemoved);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan2BeRemoved.getMarea());
                }
                if (lan2BeRemoved.getDatacenters().size()!=0) {
                    for (Datacenter dc : lan2BeRemoved.getDatacenters()) {
                        dc.getLans().remove(lan2BeRemoved);
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                    }
                }
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().remove(lan2BeRemoved);
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Lan deleted successfully !",
                                                           "Lan name : " + lan2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while deleting lan " + lan2BeRemoved.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
/*
                try {
                    FacesMessage msg2;
                    int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
                    switch(txStatus) {
                        case Status.STATUS_NO_TRANSACTION:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled !",
                                                           "Operation : lan " + lan2BeRemoved.getName() + " deletion.");
                            break;
                        case Status.STATUS_MARKED_ROLLBACK:
                            try {
                                log.debug("Rollback operation !");
                                JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                            "Operation rollbacked !",
                                                                            "Operation : lan " + lan2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            } catch (SystemException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                            "Error while rollbacking operation !",
                                                                            "Operation : lan " + lan2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            }
                            break;
                        default:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled ! ("+txStatus+")",
                                                           "Operation : lan " + lan2BeRemoved.getName() + " deletion.");
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg2);
                } catch (SystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
*/
            }
        }
        selectedLanList=null;
    }

    /*
     * Lan join tools
     */
    public static List<LanType> getAllLanTypes() throws SystemException, NotSupportedException {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<LanType> criteria = builder.createQuery(LanType.class);
        Root<LanType>       root     = criteria.from(LanType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }

    public static List<LanType> getAllLanTypesForSelector() throws SystemException, NotSupportedException {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<LanType> criteria = builder.createQuery(LanType.class);
        Root<LanType>       root     = criteria.from(LanType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<LanType> list = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
        list.add(0,new LanType().setNameR("Select the lan type"));
        return list;
    }

    public static List<Lan> getAll() {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<Lan> criteria = builder.createQuery(Lan.class);
        Root<Lan>       root     = criteria.from(Lan.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }
}