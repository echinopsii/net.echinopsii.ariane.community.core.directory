/**
 * Directory wat
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.datacenter;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.multicastArea.MulticastAreasListController;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.MulticastArea;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a datacenters list in a PrimeFaces data table, display datacenters, update a datacenter and remove datacenters
 */
public class DatacentersListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DatacentersListController.class);

    private LazyDataModel<Datacenter> lazyModel = new DatacenterLazyModel();
    private Datacenter[]              selectedDCList ;

    private HashMap<Long,String>       addedSubnet    = new HashMap<Long, String>();
    private HashMap<Long,List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    private HashMap<Long,String>              addedMArea    = new HashMap<Long, String>();
    private HashMap<Long,List<MulticastArea>> removedMareas = new HashMap<Long, List<MulticastArea>>();

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

    /**
     * Synchronize added subnet into a datacenter to database
     *
     * @param dc bean UI is working on
     */
    public void syncAddedSubnet(Datacenter dc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Subnet subnet : SubnetsListController.getAll()) {
                if (subnet.getName().equals(this.addedSubnet.get(dc.getId()))) {
                    em.getTransaction().begin();
                    dc = em.find(dc.getClass(), dc.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    dc.getSubnets().add(subnet);
                    subnet.getDatacenters().remove(dc);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Datacenter updated successfully !",
                                                               "Datacenter name : " + dc.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + dc.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
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
     * Synchronize removed subnet from a datacenter to database
     *
     * @param dc bean UI is working on
     */
    public void syncRemovedSubnets(Datacenter dc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            dc = em.find(dc.getClass(), dc.getId());
            List<Subnet> subnets2beRM = this.removedSubnets.get(dc.getId());
            for (Subnet subnet2beRM : subnets2beRM) {
                subnet2beRM = em.find(subnet2beRM.getClass(), subnet2beRM.getId());
                dc.getSubnets().remove(subnet2beRM);
                subnet2beRM.getDatacenters().remove(dc);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Datacenter updated successfully !",
                                                       "Datacenter name : " + dc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + dc.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedMArea() {
        return addedMArea;
    }

    public void setAddedMArea(HashMap<Long, String> addedMArea) {
        this.addedMArea = addedMArea;
    }

    /**
     * Synchronize added multicast area into a datacenter to database
     *
     * @param dc bean UI is working on
     */
    public void syncAddedMArea(Datacenter dc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (MulticastArea marea: MulticastAreasListController.getAll()) {
                if (marea.getName().equals(this.addedMArea.get(dc.getId()))) {
                    em.getTransaction().begin();
                    dc = em.find(dc.getClass(), dc.getId());
                    marea = em.find(marea.getClass(), marea.getId());
                    dc.getMulticastAreas().add(marea);
                    marea.getDatacenters().add(dc);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                               "Datacenter updated successfully !",
                                                               "Datacenter name : " + dc.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + dc.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<MulticastArea>> getRemovedMareas() {
        return removedMareas;
    }

    public void setRemovedMareas(HashMap<Long, List<MulticastArea>> removedMareas) {
        this.removedMareas = removedMareas;
    }

    /**
     * Synchronize removed multicast area from a datacenter to database
     *
     * @param dc bean UI is working on
     */
    public void syncRemovedMAreas(Datacenter dc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            dc = em.find(dc.getClass(), dc.getId());
            List<MulticastArea> mareas2beRM = this.removedMareas.get(dc.getId());
            for (MulticastArea marea2beRM : mareas2beRM) {
                marea2beRM = em.find(marea2beRM.getClass(), marea2beRM.getId());
                dc.getMulticastAreas().remove(marea2beRM);
                marea2beRM.getDatacenters().remove(dc);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Datacenter updated successfully !",
                                                       "Datacenter name : " + dc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + dc.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedSubnet, removedSubnets, addedMArea, removedMareas lists
     * with the correct datacenter id <br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedSubnet, removedSubnets, addedMArea, removedMareas lists
     * with the correct datacenter id <br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Datacenter eventDc = ((Datacenter) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedSubnet.remove(eventDc.getId());
            removedSubnets.remove(eventDc.getId());
            addedMArea.remove(eventDc.getId());
            removedMareas.remove(eventDc.getId());
        } else {
            addedSubnet.put(eventDc.getId(), "");
            removedSubnets.put(eventDc.getId(), new ArrayList<Subnet>());
            addedMArea.put(eventDc.getId(), "");
            removedMareas.put(eventDc.getId(), new ArrayList<MulticastArea>());
        }
    }

    /**
     * When UI actions an update merge the corresponding datacenter bean with the correct datacenter instance in the DB and save this instance
     *
     * @param dc bean UI is working on
     */
    public void update(Datacenter dc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            dc = em.find(dc.getClass(), dc.getId()).setNameR(dc.getName()).setDescriptionR(dc.getDescription()).setAddressR(dc.getAddress()).setCountryR(dc.getCountry()).
                                                    setTownR(dc.getTown()).setGpsLatitudeR(dc.getGpsLatitude()).setGpsLongitudeR(dc.getGpsLongitude()).setZipCodeR(dc.getZipCode());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Datacenter updated successfully !",
                                                       "Datacenter name : " + dc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while updating datacenter " + dc.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected datacenters
     */
    public void delete() {
        log.debug("Remove selected DC !");
        for (Datacenter dc2BeRemoved: selectedDCList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                dc2BeRemoved = em.find(dc2BeRemoved.getClass(), dc2BeRemoved.getId());
                for (Subnet subnet : dc2BeRemoved.getSubnets())
                    subnet.getDatacenters().remove(dc2BeRemoved);
                for (MulticastArea marea : dc2BeRemoved.getMulticastAreas())
                    marea.getDatacenters().remove(dc2BeRemoved);
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
            } finally {
                em.close();
            }
        }
        selectedDCList=null;
    }

    /**
     * Get all datacenters from the db
     *
     * @return all datacenters from the db
     */
    public static List<Datacenter> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all datacenters from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                         new Object[]{
                                             (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                                             (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                         });

        CriteriaBuilder           builder  = em.getCriteriaBuilder();
        CriteriaQuery<Datacenter> criteria = builder.createQuery(Datacenter.class);
        Root<Datacenter>          root     = criteria.from(Datacenter.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Datacenter> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}