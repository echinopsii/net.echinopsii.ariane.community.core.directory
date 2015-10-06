/**
 * Directory wat
 * Directories RoutingArea Create Controller
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

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Location;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.location.LocationsListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provide stuff to create and save a new routing area from the UI form
 */
public class RoutingAreaNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RoutingAreaNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String name;
    private String description;
    private String multicast;
    private String type;

    private List<String> locationsToBind = new ArrayList<String>();
    private Set<Location> locations = new HashSet<Location>();

    public EntityManager getEm() {
        return em;
    }

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

    public String getMulticast() {
        return multicast;
    }

    public void setMulticast(String multicast) {
        this.multicast = multicast;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocationsToBind() {
        return locationsToBind;
    }

    public void setLocationsToBind(List<String> locationsToBind) {
        this.locationsToBind = locationsToBind;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    /**
     * populate locations list through locationsToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedLocations() throws NotSupportedException, SystemException {
        for (Location loc: LocationsListController.getAll()) {
            for (String locToBind : locationsToBind)
                if (loc.getName().equals(locToBind)) {
                    loc = em.find(loc.getClass(), loc.getId());
                    this.locations.add(loc);
                    log.debug("Synced location : {} {}", new Object[]{loc.getId(), loc.getName()});
                    break;
                }
        }
    }

    /**
     * save a new routing area thanks data provided through UI form
     */
    public void save() {
        try {
            bindSelectedLocations();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating routing area " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        RoutingArea routingArea = new RoutingArea();
        routingArea.setName(name);
        routingArea.setDescription(description);
        routingArea.setLocations(locations);
        routingArea.setMulticast(multicast);
        routingArea.setType(type);

        try {
            em.getTransaction().begin();
            em.persist(routingArea);
            for (Location loc : routingArea.getLocations()) {
                loc.getRoutingAreas().add(routingArea); em.merge(loc);
            }
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new RoutingArea {} !", new Object[]{name});
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Routing area created successfully !",
                                                       "Routing area name : " + routingArea.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating routing area " + routingArea.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}