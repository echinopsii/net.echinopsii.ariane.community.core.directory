/**
 * Directory wat
 * Directories Location RUD Controller
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.location;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Location;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.routingArea.RoutingAreasListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
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
 * This class provide stuff to display a locations list in a PrimeFaces data table, display locations, update a location and remove locations
 */
public class LocationsListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LocationsListController.class);

    private LazyDataModel<Location> lazyModel = new LocationLazyModel();
    private Location[] selectedLOCList;

    private HashMap<Long, String> addedSubnet = new HashMap<Long, String>();
    private HashMap<Long, List<Subnet>> removedSubnets = new HashMap<Long, List<Subnet>>();

    private HashMap<Long, String> addedRArea = new HashMap<Long, String>();
    private HashMap<Long, List<RoutingArea>> removedRareas = new HashMap<Long, List<RoutingArea>>();

    public LazyDataModel<Location> getLazyModel() {
        return lazyModel;
    }

    public Location[] getSelectedLOCList() {
        return selectedLOCList;
    }

    public void setSelectedLOCList(Location[] selectedLOCList) {
        this.selectedLOCList = selectedLOCList;
    }

    public HashMap<Long, String> getAddedSubnet() {
        return addedSubnet;
    }

    public void setAddedSubnet(HashMap<Long, String> addedSubnet) {
        this.addedSubnet = addedSubnet;
    }

    /**
     * Synchronize added subnet into a location to database
     *
     * @param loc bean UI is working on
     */
    public void syncAddedSubnet(Location loc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (Subnet subnet : SubnetsListController.getAll()) {
                if (subnet.getName().equals(this.addedSubnet.get(loc.getId()))) {
                    em.getTransaction().begin();
                    loc = em.find(loc.getClass(), loc.getId());
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    loc.getSubnets().add(subnet);
                    subnet.getLocations().remove(loc);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Location updated successfully !",
                            "Location name : " + loc.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating location " + loc.getName() + " !",
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
     * Synchronize removed subnet from a location to database
     *
     * @param loc bean UI is working on
     */
    public void syncRemovedSubnets(Location loc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            loc = em.find(loc.getClass(), loc.getId());
            List<Subnet> subnets2beRM = this.removedSubnets.get(loc.getId());
            for (Subnet subnet2beRM : subnets2beRM) {
                subnet2beRM = em.find(subnet2beRM.getClass(), subnet2beRM.getId());
                loc.getSubnets().remove(subnet2beRM);
                subnet2beRM.getLocations().remove(loc);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Location updated successfully !",
                    "Location name : " + loc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating location " + loc.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, String> getAddedRArea() {
        return addedRArea;
    }

    public void setAddedRArea(HashMap<Long, String> addedRArea) {
        this.addedRArea = addedRArea;
    }

    /**
     * Synchronize added routing area into a location to database
     *
     * @param loc bean UI is working on
     */
    public void syncAddedRArea(Location loc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            for (RoutingArea rarea : RoutingAreasListController.getAll()) {
                if (rarea.getName().equals(this.addedRArea.get(loc.getId()))) {
                    em.getTransaction().begin();
                    loc = em.find(loc.getClass(), loc.getId());
                    rarea = em.find(rarea.getClass(), rarea.getId());
                    loc.getRoutingAreas().add(rarea);
                    rarea.getLocations().add(loc);
                    em.flush();
                    em.getTransaction().commit();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Location updated successfully !",
                            "Location name : " + loc.getName());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    break;
                }
            }
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating location " + loc.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public HashMap<Long, List<RoutingArea>> getRemovedRareas() {
        return removedRareas;
    }

    public void setRemovedRareas(HashMap<Long, List<RoutingArea>> removedRareas) {
        this.removedRareas = removedRareas;
    }

    /**
     * Synchronize removed routing area from a location to database
     *
     * @param loc bean UI is working on
     */
    public void syncRemovedRAreas(Location loc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            loc = em.find(loc.getClass(), loc.getId());
            List<RoutingArea> rareas2beRM = this.removedRareas.get(loc.getId());
            for (RoutingArea rarea2beRM : rareas2beRM) {
                rarea2beRM = em.find(rarea2beRM.getClass(), rarea2beRM.getId());
                loc.getRoutingAreas().remove(rarea2beRM);
                rarea2beRM.getLocations().remove(loc);
            }
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Location updated successfully !",
                    "Location name : " + loc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating location " + loc.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * When a PrimeFaces data table row is toogled init reference into the addedSubnet, removedSubnets, addedRArea, removedRareas lists
     * with the correct location id <br/>
     * When a PrimeFaces data table row is untoogled remove reference from the addedSubnet, removedSubnets, addedRArea, removedRareas lists
     * with the correct location id <br/>
     *
     * @param event provided by the UI through PrimeFaces on a row toggle
     */
    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        Location eventDc = ((Location) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            addedSubnet.remove(eventDc.getId());
            removedSubnets.remove(eventDc.getId());
            addedRArea.remove(eventDc.getId());
            removedRareas.remove(eventDc.getId());
        } else {
            addedSubnet.put(eventDc.getId(), "");
            removedSubnets.put(eventDc.getId(), new ArrayList<Subnet>());
            addedRArea.put(eventDc.getId(), "");
            removedRareas.put(eventDc.getId(), new ArrayList<RoutingArea>());
        }
    }

    /**
     * When UI actions an update merge the corresponding location bean with the correct location instance in the DB and save this instance
     *
     * @param loc bean UI is working on
     */
    public void update(Location loc) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            loc = em.find(loc.getClass(), loc.getId()).setNameR(loc.getName()).setDescriptionR(loc.getDescription()).
                    setAddressR(loc.getAddress()).setCountryR(loc.getCountry()).
                    setTownR(loc.getTown()).setGpsLatitudeR(loc.getGpsLatitude()).
                    setGpsLongitudeR(loc.getGpsLongitude()).setZipCodeR(loc.getZipCode()).
                    setTypeR(loc.getType());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Location updated successfully !",
                    "Location name : " + loc.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating location " + loc.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected locations
     */
    public void delete() {
        log.debug("Remove selected LOC !");
        for (Location loc2BeRemoved : selectedLOCList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                loc2BeRemoved = em.find(loc2BeRemoved.getClass(), loc2BeRemoved.getId());
                for (Subnet subnet : loc2BeRemoved.getSubnets())
                    subnet.getLocations().remove(loc2BeRemoved);
                for (RoutingArea rarea : loc2BeRemoved.getRoutingAreas())
                    rarea.getLocations().remove(loc2BeRemoved);
                em.remove(loc2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Location deleted successfully !",
                        "Location name : " + loc2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while deleting location " + loc2BeRemoved.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        selectedLOCList = null;
    }

    /**
     * Get all locations from the db
     *
     * @return all locations from the db
     */
    public static List<Location> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all locations from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                new Object[]{
                        (Thread.currentThread().getStackTrace().length > 0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length > 6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                });

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
        Root<Location> root = criteria.from(Location.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<Location> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    public static List<String> getAllLocationTypesForSelector() {
        List<String> locationTypes = Location.getTypeList();
        locationTypes.add(0, "Select Location Type");
        return locationTypes;
    }
}