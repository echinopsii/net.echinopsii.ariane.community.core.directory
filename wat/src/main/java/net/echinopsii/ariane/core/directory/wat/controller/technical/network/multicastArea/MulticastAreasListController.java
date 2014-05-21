/**
 * Directory wat
 * Directories MulticastArea RUD Controller
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

package net.echinopsii.ariane.core.directory.wat.controller.technical.network.multicastArea;

import net.echinopsii.ariane.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.core.directory.wat.controller.technical.network.datacenter.DatacentersListController;
import net.echinopsii.ariane.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.core.directory.base.model.technical.network.MulticastArea;
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
 * This class provide stuff to display a multicastareas list in a PrimeFaces data table, display multicastareas, update a multicastarea and remove multicastareas
 */
public class MulticastAreasListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MulticastAreasListController.class);

    private LazyDataModel<MulticastArea> lazyModel = new MulticastAreaLazyModel();
    private MulticastArea[]              selectedMareaList ;

    private HashMap<Long,String>           addedDC    = new HashMap<Long, String>();
    private HashMap<Long,List<Datacenter>> removedDCs = new HashMap<Long, List<Datacenter>>();

    private HashMap<Long,String>       addedSubnet    = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    /*
     * PrimeFaces table tools
     */
    public LazyDataModel<MulticastArea> getLazyModel() {
        return lazyModel;
    }

    public MulticastArea[] getSelectedMareaList() {
        return selectedMareaList;
    }

    public void setSelectedMareaList(MulticastArea[] selectedMareaList) {
        this.selectedMareaList = selectedMareaList;
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
     * Synchronize added subnet into a multicast area to database
     *
     * @param marea bean UI is working on
     */
    public void syncAddedSubnet(MulticastArea marea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Subnet subnet : SubnetsListController.getAll()) {
                if (subnet.getName().equals(this.addedSubnet.get(marea.getId()))) {
                    em.getTransaction().begin();
                    marea = em.find(marea.getClass(), marea.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    marea.getSubnets().add(subnet);
                    if (subnet.getMarea()!=null)
                        subnet.getMarea().getSubnets().remove(subnet);
                    subnet.setMarea(marea);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Multicast area updated successfully !",
                                                               "Multicast area name : " + marea.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating multicast area " + marea.getName() + " !",
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
     * Synchronize removed subnets from a multicast area to database
     *
     * @param marea bean UI is working on
     */
    public void syncRemovedSubnets(MulticastArea marea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            marea = em.find(marea.getClass(), marea.getId());
            List<Subnet> subnets2beRM = this.removedSubnets.get(marea.getId());
            for (Subnet subnet2beRM : subnets2beRM) {
                subnet2beRM = em.find(subnet2beRM.getClass(), subnet2beRM.getId());
                marea.getSubnets().remove(subnet2beRM);
                subnet2beRM.setMarea(null);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Multicast area updated successfully !",
                                                       "Multicast area name : " + marea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating multicast area " + marea.getName() + " !",
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
     * Synchronize added datacenter into a multicast area to database
     *
     * @param marea bean UI is working on
     */
    public void syncAddedDC(MulticastArea marea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Datacenter dc: DatacentersListController.getAll()) {
                if (dc.getName().equals(this.addedDC.get(marea.getId()))) {
                    em.getTransaction().begin();
                    marea = em.find(marea.getClass(), marea.getId());
                    dc = em.find(dc.getClass(), dc.getId());
                    marea.getDatacenters().add(dc);
                    dc.getMulticastAreas().add(marea);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Multicast area updated successfully !",
                                                               "Multicast area name : " + marea.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating multicast area " + marea.getName() + " !",
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
     * Synchronize removed datacenters from a multicast area to database
     *
     * @param marea bean UI is working on
     */
    public void syncRemovedDCs(MulticastArea marea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            marea = em.find(marea.getClass(), marea.getId());
            List<Datacenter> dcs2beRM = this.removedDCs.get(marea.getId());
            for (Datacenter dc2beRM : dcs2beRM) {
                dc2beRM = em.find(dc2beRM.getClass(), dc2beRM.getId());
                marea.getDatacenters().remove(dc2beRM);
                dc2beRM.getMulticastAreas().remove(marea);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Multicast area updated successfully !",
                                                       "Multicast area name : " + marea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating multicast area " + marea.getName() + " !",
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
     * with the correct multicastarea id<br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedDC, removedDCs, addedSubnet, removedSubnets lists
     * with the correct multicastarea id<br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        MulticastArea eventMarea = ((MulticastArea) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedDC.remove(eventMarea.getId());
            removedDCs.remove(eventMarea.getId());
            addedSubnet.remove(eventMarea.getId());
            removedSubnets.remove(eventMarea.getId());
        } else {
            addedDC.put(eventMarea.getId(), "");
            removedDCs.put(eventMarea.getId(), new ArrayList<Datacenter>());
            addedSubnet.put(eventMarea.getId(), "");
            removedSubnets.put(eventMarea.getId(), new ArrayList<Subnet>());
        }
    }

    /**
     * When UI actions an update merge the corresponding multicastarea bean with the correct multicastarea instance in the DB and save this instance
     *
     * @param multicastArea bean UI is working on
     */
    public void update(MulticastArea multicastArea) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            multicastArea = em.find(multicastArea.getClass(), multicastArea.getId()).setNameR(multicastArea.getName()).setDescriptionR(multicastArea.getDescription());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Multicast area updated successfully !",
                                                       "Multicast area name : " + multicastArea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating multicast area " + multicastArea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected multicastareas
     */
    public void delete() {
        log.debug("Remove selected Multicast Area !");
        for (MulticastArea marea2BeRemoved: selectedMareaList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            marea2BeRemoved = em.find(marea2BeRemoved.getClass(), marea2BeRemoved.getId());
            try {
                em.getTransaction().begin();
                for (Subnet subnet : marea2BeRemoved.getSubnets())
                    subnet.setMarea(null);
                for (Datacenter dc : marea2BeRemoved.getDatacenters())
                    dc.getMulticastAreas().remove(marea2BeRemoved);
                em.remove(marea2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Multicast area deleted successfully !",
                                                           "Multicast area name : " + marea2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Throwable raised while creating multicast area " + marea2BeRemoved.getName() + " !",
                                                           "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedMareaList=null;
    }

    /**
     * Get all multicastareas from the db
     *
     * @return all multicastareas from the db
     */
    public static List<MulticastArea> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all multicast areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<MulticastArea> criteria = builder.createQuery(MulticastArea.class);
        Root<MulticastArea> root = criteria.from(MulticastArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<MulticastArea> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret ;
    }

    /**
     * Get all multicastareas from the db + no multicast area choice + selection string
     *
     * @return all multicastareas from the db + no multicast area choice + selection string
     */
    public static List<MulticastArea> getAllForSelector() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all multicast areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<MulticastArea> criteria = builder.createQuery(MulticastArea.class);
        Root<MulticastArea> root = criteria.from(MulticastArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<MulticastArea> ret =  em.createQuery(criteria).getResultList();
        ret.add(0, new MulticastArea().setNameR("No multicast area"));
        ret.add(0, new MulticastArea().setNameR("Select multicast area for this subnet"));
        em.close();
        return ret;
    }

    /**
     * Get all multicastareas from the db + no multicast area choice
     *
     * @return all multicastareas from the db + no multicast area choice
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static List<MulticastArea> getAllForInplace() throws SystemException, NotSupportedException {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all multicast areas from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
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
        CriteriaQuery<MulticastArea> criteria = builder.createQuery(MulticastArea.class);
        Root<MulticastArea> root = criteria.from(MulticastArea.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<MulticastArea> ret =  em.createQuery(criteria).getResultList();
        ret.add(0, new MulticastArea().setNameR("No multicast area"));
        em.close();
        return ret;
    }
}