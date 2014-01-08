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

import com.spectral.cc.core.directory.commons.consumer.DirectoryJPAProviderConsumer;
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

public class SubnetsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SubnetsListController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    private HashMap<Long, Subnet> rollback = new HashMap<Long, Subnet>();

    private LazyDataModel<Subnet> lazyModel = new SubnetLazyModel().setEntityManager(em);
    private Subnet[] selectedSubnetList;

    private HashMap<Long, String> changedSubnetType = new HashMap<Long, String>();
    private HashMap<Long, String> changedMarea      = new HashMap<Long, String>();

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>           addedOSI    = new HashMap<Long, String>();
    private HashMap<Long,List<OSInstance>> removedOSIs = new HashMap<Long, List<OSInstance>>();

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
        for(SubnetType ltype: getAllSubnetTypes(em)) {
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
        for (MulticastArea marea : MulticastAreasListController.getAll(em)) {
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
        for (Datacenter dc: DatacentersListController.getAll(em)) {
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
        for (OSInstance osInstance: OSInstancesListController.getAll(em)) {
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
            em.getTransaction().begin();
            if (subnet.getType()!=rollback.get(subnet.getId()).getType()) {
                if (rollback.get(subnet.getId()).getType()!=null) {
                    rollback.get(subnet.getId()).getType().getSubnets().remove(subnet);
                    em.merge(rollback.get(subnet.getId()).getType());
                }
                if (subnet.getType()!=null) {
                    subnet.getType().getSubnets().add(subnet);
                    em.merge(subnet.getType());
                }
            }
            if (subnet.getMarea()!=rollback.get(subnet.getId()).getMarea()) {
                if (rollback.get(subnet.getId()).getMarea()!=null)
                    rollback.get(subnet.getId()).getMarea().getSubnets().remove(subnet);
                if (subnet.getMarea()!=null) {
                    subnet.getMarea().getSubnets().add(subnet);
                    em.merge(subnet.getMarea());

                }
            }
            em.merge(subnet);
            for (Datacenter dc: subnet.getDatacenters()) {
                if (!rollback.get(subnet.getId()).getDatacenters().contains(dc)) {
                    dc.getSubnets().add(subnet);
                    em.merge(dc);
                }
            }
            for (Datacenter dc: rollback.get(subnet.getId()).getDatacenters()) {
                if (!subnet.getDatacenters().contains(dc)) {
                    dc.getSubnets().remove(subnet);
                    em.merge(dc);
                }
            }
            for (OSInstance osInstance: subnet.getOsInstances()) {
                if (!rollback.get(subnet.getId()).getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkSubnets().add(subnet);
                    em.merge(osInstance);
                }
            }
            for (OSInstance osInstance: rollback.get(subnet.getId()).getOsInstances()) {
                if (!subnet.getOsInstances().contains(osInstance)) {
                    osInstance.getNetworkSubnets().remove(subnet);
                    em.merge(osInstance);
                }
            }

            em.flush();
            em.getTransaction().commit();
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
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    /*
     * Subnet delete tool
     */
    public void delete() {
        log.debug("Remove selected Subnet !");
        for (Subnet subnet2BeRemoved : selectedSubnetList) {
            try {
                em.getTransaction().begin();
                if (subnet2BeRemoved.getType()!=null) {
                    subnet2BeRemoved.getType().getSubnets().remove(subnet2BeRemoved);
                    em.merge(subnet2BeRemoved.getType());
                }
                if (subnet2BeRemoved.getMarea()!=null) {
                    subnet2BeRemoved.getMarea().getSubnets().remove(subnet2BeRemoved);
                    em.merge(subnet2BeRemoved.getMarea());
                }
                if (subnet2BeRemoved.getDatacenters().size()!=0) {
                    for (Datacenter dc : subnet2BeRemoved.getDatacenters()) {
                        dc.getSubnets().remove(subnet2BeRemoved);
                        em.merge(dc);
                    }
                }
                em.remove(subnet2BeRemoved);

                em.flush();
                em.getTransaction().commit();
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
            }
        }
        selectedSubnetList =null;
    }

    /*
     * Subnet join tools
     */
    public static List<SubnetType> getAllSubnetTypes(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all subnets from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             Thread.currentThread().getStackTrace()[1].getClassName(),
                                             Thread.currentThread().getStackTrace()[2].getClassName(),
                                             Thread.currentThread().getStackTrace()[3].getClassName(),
                                             Thread.currentThread().getStackTrace()[4].getClassName(),
                                             Thread.currentThread().getStackTrace()[5].getClassName()
                         });
        CriteriaBuilder        builder  = em.getCriteriaBuilder();
        CriteriaQuery<SubnetType> criteria = builder.createQuery(SubnetType.class);
        Root<SubnetType>       root     = criteria.from(SubnetType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<SubnetType> ret = em.createQuery(criteria).getResultList();
        for (SubnetType stype : ret) {
            em.refresh(stype);
        }
        return ret;
    }

    public static List<SubnetType> getAllSubnetTypesForSelector(EntityManager em) throws SystemException, NotSupportedException {
        log.debug("Get all subnets from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder        builder  = em.getCriteriaBuilder();
        CriteriaQuery<SubnetType> criteria = builder.createQuery(SubnetType.class);
        Root<SubnetType>       root     = criteria.from(SubnetType.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<SubnetType> list = em.createQuery(criteria).getResultList();
        for (SubnetType stype : list) {
            em.refresh(stype);
        }
        list.add(0, new SubnetType().setNameR("Select the subnet type"));
        return list;
    }

    public static List<Subnet> getAll(EntityManager em) {
        log.debug("Get all subnets from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder        builder  = em.getCriteriaBuilder();
        CriteriaQuery<Subnet> criteria = builder.createQuery(Subnet.class);
        Root<Subnet>       root     = criteria.from(Subnet.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Subnet> ret = em.createQuery(criteria).getResultList();
        for (Subnet sub : ret) {
            em.refresh(sub);
        }
        return ret;
    }
}