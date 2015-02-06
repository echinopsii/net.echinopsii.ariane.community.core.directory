/**
 * Directory wat
 * Directories RoutingArea RUD Controller
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.routingArea;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.datacenter.DatacentersListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * This class provide stuff to display a routing areas list in a PrimeFaces data table, display routing areas, update a routing area and remove routing areas
 */
public class RoutingAreasListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RoutingAreasListController.class);

    private LazyDataModel<RoutingArea> lazyModel = new RoutingAreaLazyModel();
    private RoutingArea[] selectedRareaList;

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>       addedSubnet    = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<RoutingArea> getLazyModel() {
        return lazyModel;
    }

    public RoutingArea[] getSelectedRareaList() {
        return selectedRareaList;
    }

    public void setSelectedRareaList(RoutingArea[] selectedRareaList) {
        this.selectedRareaList = selectedRareaList;
    }

    /*
     * Multicast Area update tools
     */
    public HashMap<Long, String> getAddedSubnet() {
        return addedSubnet;
    }

    public void setAddedSubnet(HashMap<Long, String> addedSubnet) {
        this.addedSubnet = addedSubnet;
    }

    /**
     * Synchronize added subnet into a routing area to database
     *
     * @param rarea bean UI is working on
     */
    public void syncAddedSubnet(RoutingArea rarea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Subnet subnet : SubnetsListController.getAll()) {
                if (subnet.getName().equals(this.addedSubnet.get(rarea.getId()))) {
                    em.getTransaction().begin();
                    rarea = em.find(rarea.getClass(), rarea.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    rarea.getSubnets().add(subnet);
                    if (subnet.getRarea()!=null)
                        subnet.getRarea().getSubnets().remove(subnet);
                    subnet.setRarea(rarea);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Routing area updated successfully !",
                                                               "Routing area name : " + rarea.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating routing area " + rarea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Subnet>> getRemovedSubnets() {
        return removedSubnets;
    }

    public void setRemovedSubnets(HashMap<Long, List<Subnet>> removedSubnets) {
        this.removedSubnets = removedSubnets;
    }

    /**
     * Synchronize removed subnets from a routing area to database
     *
     * @param rarea bean UI is working on
     */
    public void syncRemovedSubnets(RoutingArea rarea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            rarea = em.find(rarea.getClass(), rarea.getId());
            List<Subnet> subnets2beRM = this.removedSubnets.get(rarea.getId());
            for (Subnet subnet2beRM : subnets2beRM) {
                subnet2beRM = em.find(subnet2beRM.getClass(), subnet2beRM.getId());
                rarea.getSubnets().remove(subnet2beRM);
                subnet2beRM.setRarea(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Routing area updated successfully !",
                                                       "Routing area name : " + rarea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating routing area " + rarea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedDC() {
        return addedDC;
    }

    public void setAddedDC(HashMap<Long, String> addedDC) {
        this.addedDC = addedDC;
    }

    /**
     * Synchronize added datacenter into a routing area to database
     *
     * @param rarea bean UI is working on
     */
    public void syncAddedDC(RoutingArea rarea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Datacenter dc: DatacentersListController.getAll()) {
                if (dc.getName().equals(this.addedDC.get(rarea.getId()))) {
                    em.getTransaction().begin();
                    rarea = em.find(rarea.getClass(), rarea.getId());
                    dc = em.find(dc.getClass(), dc.getId());
                    rarea.getDatacenters().add(dc);
                    dc.getRoutingAreas().add(rarea);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Routing area updated successfully !",
                                                               "Routing area name : " + rarea.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating routing area " + rarea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<Datacenter>> getRemovedDCs() {
        return removedDCs;
    }

    public void setRemovedDCs(HashMap<Long, List<Datacenter>> removedDCs) {
        this.removedDCs = removedDCs;
    }

    /**
     * Synchronize removed datacenters from a routing area to database
     *
     * @param rarea bean UI is working on
     */
    public void syncRemovedDCs(RoutingArea rarea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            rarea = em.find(rarea.getClass(), rarea.getId());
            List<Datacenter> dcs2beRM = this.removedDCs.get(rarea.getId());
            for (Datacenter dc2beRM : dcs2beRM) {
                dc2beRM = em.find(dc2beRM.getClass(), dc2beRM.getId());
                rarea.getDatacenters().remove(dc2beRM);
                dc2beRM.getRoutingAreas().remove(rarea);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Routing area updated successfully !",
                                                       "Routing area name : " + rarea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating routing area " + rarea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedDC, removedDCs, addedSubnet, removedSubnets lists
     * with the correct routing area id<br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedDC, removedDCs, addedSubnet, removedSubnets lists
     * with the correct routing area id<br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        RoutingArea eventRarea = ((RoutingArea) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedDC.remove(eventRarea.getId());
            removedDCs.remove(eventRarea.getId());
            addedSubnet.remove(eventRarea.getId());
            removedSubnets.remove(eventRarea.getId());
        } else {
            addedDC.put(eventRarea.getId(), "");
            removedDCs.put(eventRarea.getId(), new ArrayList<Datacenter>());
            addedSubnet.put(eventRarea.getId(), "");
            removedSubnets.put(eventRarea.getId(), new ArrayList<Subnet>());
        }
    }

    /**
     * When UI actions an update merge the corresponding routing area bean with the correct routing area instance in the DB and save this instance
     *
     * @param routingArea bean UI is working on
     */
    public void update(RoutingArea routingArea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            routingArea = em.find(routingArea.getClass(), routingArea.getId()).setNameR(routingArea.getName()).setDescriptionR(routingArea.getDescription());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Routing area updated successfully !",
                                                       "Routing area name : " + routingArea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating routing area " + routingArea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected routing areas
     */
    public void delete() {
        log.debug("Remove selected Routing Area !");
        for (RoutingArea rarea2BeRemoved: selectedRareaList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            rarea2BeRemoved = em.find(rarea2BeRemoved.getClass(), rarea2BeRemoved.getId());
            try {
                em.getTransaction().begin();
                for (Subnet subnet : rarea2BeRemoved.getSubnets())
                    subnet.setRarea(null);
                for (Datacenter dc : rarea2BeRemoved.getDatacenters())
                    dc.getRoutingAreas().remove(rarea2BeRemoved);
                em.remove(rarea2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Routing area deleted successfully !",
                                                           "Routing area name : " + rarea2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating routing area " + rarea2BeRemoved.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedRareaList =null;
    }

    /**
     * Get all routing areas from the db
     *
     * @return all routing areas from the db
     */
    public static List<RoutingArea> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all routing areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RoutingArea> criteria = builder.createQuery(RoutingArea.class);
        Root<RoutingArea> root = criteria.from(RoutingArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<RoutingArea> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret ;
    }

    /**
     * Get all routing areas from the db + no multicast area choice + selection string
     *
     * @return all routing areas from the db + no multicast area choice + selection string
     */
    public static List<RoutingArea> getAllForSelector() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all routing areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder  = em.getCriteriaBuilder();
        CriteriaQuery<RoutingArea> criteria = builder.createQuery(RoutingArea.class);
        Root<RoutingArea> root = criteria.from(RoutingArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<RoutingArea> ret =  em.createQuery(criteria).getResultList();
        ret.add(0, new RoutingArea().setNameR("No routing area"));
        ret.add(0, new RoutingArea().setNameR("Select routing area for this subnet"));
        em.close();
        return ret;
    }

    /**
     * Get all routing areas from the db + no multicast area choice
     *
     * @return all routing areas from the db + no multicast area choice
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<RoutingArea> getAllForInplace() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all routing areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });
        CriteriaBuilder builder  = em.getCriteriaBuilder();
        CriteriaQuery<RoutingArea> criteria = builder.createQuery(RoutingArea.class);
        Root<RoutingArea> root = criteria.from(RoutingArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<RoutingArea> ret =  em.createQuery(criteria).getResultList();
        ret.add(0, new RoutingArea().setNameR("No routing area"));
        em.close();
        return ret;
    }
}