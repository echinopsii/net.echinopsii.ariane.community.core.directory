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

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.datacenter.DatacentersListController;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
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

    private List<String> datacentersToBind = new ArrayList<String>();
    private Set<Datacenter> datacenters    = new HashSet<Datacenter>();

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

    public List<String> getDatacentersToBind() {
        return datacentersToBind;
    }

    public void setDatacentersToBind(List<String> datacentersToBind) {
        this.datacentersToBind = datacentersToBind;
    }

    public Set<Datacenter> getDatacenters() {
        return datacenters;
    }

    public void setDatacenters(Set<Datacenter> datacenters) {
        this.datacenters = datacenters;
    }

    /**
     * populate datacenters list through datacentersToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedDatacenters() throws NotSupportedException, SystemException {
        for (Datacenter dc: DatacentersListController.getAll()) {
            for (String dcToBind : datacentersToBind)
                if (dc.getName().equals(dcToBind)) {
                    dc = em.find(dc.getClass(), dc.getId());
                    this.datacenters.add(dc);
                    log.debug("Synced datacenter : {} {}", new Object[]{dc.getId(), dc.getName()});
                    break;
                }
        }
    }

    /**
     * save a new routing area thanks data provided through UI form
     */
    public void save() {
        try {
            bindSelectedDatacenters();
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
        routingArea.setDatacenters(datacenters);

        try {
            em.getTransaction().begin();
            em.persist(routingArea);
            for (Datacenter dc : routingArea.getDatacenters()) {
                dc.getRoutingAreas().add(routingArea); em.merge(dc);
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