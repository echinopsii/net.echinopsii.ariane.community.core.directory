/**
 * Directory wat
 * Directories Location Create Controller
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
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import java.io.Serializable;
import javax.faces.component.UIComponent;
/**
 * This class provide stuff to create and save a new datacenter from the UI form
 */
public class LocationNewController implements Serializable {


    private UIComponent warningMsg;
    public LocationNewController(){
        FacesContext.getCurrentInstance().addMessage("warningMsg", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning! Unable to reach google maps services", ""));
    }
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LocationsListController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String name;
    private String description;
    private String address;
    private long zipCode;
    private String town;
    private String country;
    private double gpsLatitude;
    private double gpsLongitude;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getZipCode() {
        return zipCode;
    }

    public void setZipCode(long zipCode) {
        this.zipCode = zipCode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public double getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }
    /**
     * save a new datacenter thanks data provided through UI form
     */
    public void save() {
        Location newLocation = new Location();
        newLocation.setName(name);
        newLocation.setDescription(description);
        newLocation.setAddress(address);
        newLocation.setZipCode(zipCode);
        newLocation.setTown(town);
        newLocation.setCountry(country);
        newLocation.setGpsLatitudeR(gpsLatitude);
        newLocation.setGpsLongitude(gpsLongitude);
        newLocation.setType(type);

        try {
            em.getTransaction().begin();
            em.persist(newLocation);
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new datacenter {} !", new Object[]{name});
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Location created successfully !",
                                                       "Location name : " + newLocation.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating datacenter " + newLocation.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}