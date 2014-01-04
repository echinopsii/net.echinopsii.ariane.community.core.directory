/**
 * Directory JSF Commons
 * Directories Subnet RUD Controller
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
import com.spectral.cc.core.directory.commons.controller.technical.system.OSInstance.OSInstancesListController;
import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.commons.model.technical.network.Subnet;
import com.spectral.cc.core.directory.commons.model.technical.network.SubnetType;
import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubnetsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SubnetsListController.class);

    private HashMap<Long, Subnet> rollback = new HashMap<Long, Subnet>();

    private LazyDataModel<Subnet> lazyModel ;
    private Subnet[] selectedSubnetList;

    private HashMap<Long, String> changedSubnetType = new HashMap<Long, String>();
    private HashMap<Long, String> changedMarea      = new HashMap<Long, String>();

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>           addedOSI    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSIs = new HashMap<Long, List<OSInstance>>();

    public SubnetsListController() {
        lazyModel = new SubnetLazyModel().setEntityManager(JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM());
    }

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<Subnet> getLazyModel() {
        return lazyModel;
    }

    public Subnet[] getSelectedSubnetList() {
        return selectedSubnetList;
    }

    public void setSelectedSubnetList(Subnet[] selectedSubnetList) {
        this.selectedSubnetList = selectedSubnetList;
    }

    /*
     * Subnet update tools
     */
    public HashMap<Long, String> getChangedSubnetType() {
        return changedSubnetType;
    }

    public void setChangedSubnetType(HashMap<Long, String> changedSubnetType) {
        this.changedSubnetType = changedSubnetType;
    }

    public void syncSubnetType(Subnet subnet) throws NotSupportedException, SystemException {
        SubnetType type = null;
        for(SubnetType ltype: getAllSubnetTypes()) {
            if (ltype.getName().equals(changedSubnetType.get(subnet.getId()))) {
                type = ltype;
                break;
            }
        }
        if (type != null) {
            subnet.setType(type);
        }
    }

    public HashMap<Long, String> getChangedMarea() {
        return changedMarea;
    }

    public void setChangedMarea(HashMap<Long, String> changedMarea) {
        this.changedMarea = changedMarea;
    }

    public void syncMarea(Subnet subnet) throws NotSupportedException, SystemException {
        MulticastArea mArea = null;
        for (MulticastArea marea : MulticastAreasListController.getAll()) {
            if (marea.getName().equals(changedMarea.get(subnet.getId()))) {
                mArea = marea;
                break;
            }
        }
        subnet.setMarea(mArea);
    }

    public HashMap<Long, String> getAddedDC() {
        return addedDC;
    }

    public void setAddedDC(HashMap<Long, String> addedDC) {
        this.addedDC = addedDC;
    }

    public void syncAddedDC(Subnet subnet) throws NotSupportedException, SystemException {
        for (Datacenter dc: DatacentersListController.getAll()) {
            if (dc.getName().equals(this.addedDC.get(subnet.getId()))) {
                subnet.getDatacenters().add(dc);
            }
        }
    }

    public HashMap<Long, List<Datacenter>> getRemovedDCs() {
        return removedDCs;
    }

    public void setRemovedDCs(HashMap<Long, List<Datacenter>> removedDCs) {
        this.removedDCs = removedDCs;
    }

    public void syncRemovedDCs(Subnet subnet) throws NotSupportedException, SystemException {
        List<Datacenter> dcs2beRM = this.removedDCs.get(subnet.getId());
        log.debug("syncRemovedDCs:{} ", new Object[]{dcs2beRM});
        for (Datacenter dc2beRM : dcs2beRM) {
            subnet.getDatacenters().remove(dc2beRM);
        }
    }

    public HashMap<Long, String> getAddedOSI() {
        return addedOSI;
    }

    public void setAddedOSI(HashMap<Long, String> addedOSI) {
        this.addedOSI = addedOSI;
    }

    public void syncAddedOSI(Subnet subnet) throws NotSupportedException, SystemException {
        for (OSInstance osInstance: OSInstancesListController.getAll()) {
            if (osInstance.getName().equals(this.addedOSI.get(subnet.getId()))) {
                subnet.getOsInstances().add(osInstance);
            }
        }
    }

    public HashMap<Long, List<OSInstance>> getRemovedOSIs() {
        return removedOSIs;
    }

    public void setRemovedOSIs(HashMap<Long, List<OSInstance>> removedOSIs) {
        this.removedOSIs = removedOSIs;
    }

    public void syncRemovedOSIs(Subnet subnet) throws NotSupportedException, SystemException {
        List<OSInstance> osInstances = this.removedOSIs.get(subnet.getId());
        for (OSInstance osInstance : osInstances) {
            subnet.getOsInstances().remove(osInstance);
        }
    }

    public void onRowToggle(ToggleEvent event) throws CloneNotSupportedException {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Subnet eventSubnet = ((Subnet) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            log.debug("EDITION MODE CLOSED: remove eventSubnet {} clone from rollback map...", eventSubnet.getId());
            rollback.remove(eventSubnet.getId());
            changedSubnetType.remove(eventSubnet.getId());
            changedMarea.remove(eventSubnet.getId());
            addedDC.remove(eventSubnet.getId());
            removedDCs.remove(eventSubnet.getId());
            addedOSI.remove(eventSubnet.getId());
            removedOSIs.remove(eventSubnet.getId());
        } else {
            log.debug("EDITION MODE OPEN: store current eventSubnet {} clone into rollback map...", eventSubnet.getId());
            rollback.put(eventSubnet.getId(), eventSubnet.clone());
            changedSubnetType.put(eventSubnet.getId(), "");
            changedMarea.put(eventSubnet.getId(), "");
            addedDC.put(eventSubnet.getId(), "");
            removedDCs.put(eventSubnet.getId(), new ArrayList<Datacenter>());
            addedOSI.put(eventSubnet.getId(), "");
            removedOSIs.put(eventSubnet.getId(),new ArrayList<OSInstance>());
        }
    }

    public void update(Subnet subnet) {
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            if (subnet.getType()!=rollback.get(subnet.getId()).getType()) {
                if (rollback.get(subnet.getId()).getType()!=null) {
                    rollback.get(subnet.getId()).getType().getSubnets().remove(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(rollback.get(subnet.getId()).getType());
                }
                if (subnet.getType()!=null) {
                    subnet.getType().getSubnets().add(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(subnet.getType());
                }
            }
            if (subnet.getMarea()!=rollback.get(subnet.getId()).getMarea()) {
                if (rollback.get(subnet.getId()).getMarea()!=null)
                    rollback.get(subnet.getId()).getMarea().getSubnets().remove(subnet);
                if (subnet.getMarea()!=null) {
                    subnet.getMarea().getSubnets().add(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(subnet.getMarea());

                }
            }
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(subnet);
            for (Datacenter dc: subnet.getDatacenters()) {
                if (!rollback.get(subnet.getId()).getDatacenters().contains(dc)) {
                    dc.getSubnets().add(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                }
            }
            for (Datacenter dc: rollback.get(subnet.getId()).getDatacenters()) {
                if (!subnet.getDatacenters().contains(dc)) {
                    dc.getSubnets().remove(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                }
            }
            for (OSInstance osInstance: subnet.getOsInstances()) {
                if (!rollback.get(subnet.getId()).getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkSubnets().add(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            for (OSInstance osInstance: rollback.get(subnet.getId()).getOsInstances()) {
                if (!subnet.getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkSubnets().remove(subnet);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(osInstance);
                }
            }
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            rollback.put(subnet.getId(), subnet);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Subnet updated successfully !",
                                                       "Subnet name : " + subnet.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating subnet " + rollback.get(subnet.getId()).getName() + " !",
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
                                                       "Operation : subnet " + rollback.get(subnet.getId()).getName() + " update.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                        "Operation rollbacked !",
                                                                        "Operation : subnet " + rollback.get(subnet.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                        "Error while rollbacking operation !",
                                                                        "Operation : subnet " + rollback.get(subnet.getId()).getName() + " update.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : subnet " + rollback.get(subnet.getId()).getName() + " update.");
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
     * Subnet delete tool
     */
    public void delete() {
        log.debug("Remove selected Subnet !");
        for (Subnet subnet2BeRemoved : selectedSubnetList) {
            try {
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
                if (subnet2BeRemoved.getType()!=null) {
                    subnet2BeRemoved.getType().getSubnets().remove(subnet2BeRemoved);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(subnet2BeRemoved.getType());
                }
                if (subnet2BeRemoved.getMarea()!=null) {
                    subnet2BeRemoved.getMarea().getSubnets().remove(subnet2BeRemoved);
                    JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(subnet2BeRemoved.getMarea());
                }
                if (subnet2BeRemoved.getDatacenters().size()!=0) {
                    for (Datacenter dc : subnet2BeRemoved.getDatacenters()) {
                        dc.getSubnets().remove(subnet2BeRemoved);
                        JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(dc);
                    }
                }
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().remove(subnet2BeRemoved);
                //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Subnet deleted successfully !",
                                                           "Subnet name : " + subnet2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while deleting subnet " + subnet2BeRemoved.getName() + " !",
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
                                                           "Operation : subnet " + subnet2BeRemoved.getName() + " deletion.");
                            break;
                        case Status.STATUS_MARKED_ROLLBACK:
                            try {
                                log.debug("Rollback operation !");
                                JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                            "Operation rollbacked !",
                                                                            "Operation : subnet " + subnet2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            } catch (SystemException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                            "Error while rollbacking operation !",
                                                                            "Operation : subnet " + subnet2BeRemoved.getName() + " deletion.");
                                FacesContext.getCurrentInstance().addMessage(null, msg2);
                            }
                            break;
                        default:
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation canceled ! ("+txStatus+")",
                                                           "Operation : subnet " + subnet2BeRemoved.getName() + " deletion.");
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg2);
                } catch (SystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
*/
            }
        }
        selectedSubnetList =null;
    }

    /*
     * Subnet join tools
     */
    public static List<SubnetType> getAllSubnetTypes() throws SystemException, NotSupportedException {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<SubnetType> criteria = builder.createQuery(SubnetType.class);
        Root<SubnetType>       root     = criteria.from(SubnetType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }

    public static List<SubnetType> getAllSubnetTypesForSelector() throws SystemException, NotSupportedException {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<SubnetType> criteria = builder.createQuery(SubnetType.class);
        Root<SubnetType>       root     = criteria.from(SubnetType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        List<SubnetType> list = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
        list.add(0,new SubnetType().setNameR("Select the subnet type"));
        return list;
    }

    public static List<Subnet> getAll() {
        CriteriaBuilder        builder  = JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getCriteriaBuilder();
        CriteriaQuery<Subnet> criteria = builder.createQuery(Subnet.class);
        Root<Subnet>       root     = criteria.from(Subnet.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        return JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().createQuery(criteria).getResultList();
    }
}