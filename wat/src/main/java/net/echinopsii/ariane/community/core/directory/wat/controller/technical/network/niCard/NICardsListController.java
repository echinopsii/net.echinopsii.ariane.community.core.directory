/**
 * Directory wat
 * Directories NICard RUD Controller
 * Copyright (C) 2015 Echinopsii
 * Author : Sagar Ghuge
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.niCard;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress.IPAddressListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance.OSInstancesListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
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
import java.util.HashMap;
import java.util.List;

/**
 * This class provide stuff to display a ipAddresses list in a PrimeFaces data table, display ipAddresses, update a ipAddresses and remove ipAddresses
 */
public class NICardsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(NICardsListController.class);

    private LazyDataModel<NICard> lazyModel = new NICardLazyModel();

    private NICard[] selectedNICardList;

    private HashMap<Long, String> changedIPAddress = new HashMap<Long, String>();

    private HashMap<Long, String> changedOSInstance = new HashMap<Long, String>();


    public HashMap<Long, String> getChangedOSInstance() {
        return changedOSInstance;
    }

    public void setChangedOSInstance(HashMap<Long, String> changedOSInstance) {
        this.changedOSInstance = changedOSInstance;
    }

    public NICard[] getSelectedNICardList() {
        return selectedNICardList;
    }

    public void setSelectedNICardList(NICard[] selectedNICardList) {
        this.selectedNICardList = selectedNICardList;
    }

    public LazyDataModel<NICard> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<NICard> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public HashMap<Long, String> getChangedIPAddress() {
        return changedIPAddress;
    }

    public void setChangedIPAddress(HashMap<Long, String> changedIPAddress) {
        this.changedIPAddress = changedIPAddress;
    }

    public void syncRIPAddress(NICard niCard) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        niCard = em.find(niCard.getClass(), niCard.getId());
        IPAddress ipAddress = null;
        for (IPAddress ipAddressLoop : IPAddressListController.getAll()) {
            if (ipAddressLoop.getIpAddress().equals(changedIPAddress.get(niCard.getId()))) {
                ipAddress = ipAddressLoop;
                break;
            }
        }
        if (ipAddress != null) {
            try {
                em.getTransaction().begin();
                niCard.setIpAddressR(ipAddress);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "NICard updated successfully !",
                        "NiCard : " + niCard.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while updating NICard " + ipAddress.getIpAddress() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
    }

    public String getNICardIPAddress(NICard niCard) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        niCard = em.find(niCard.getClass(), niCard.getId());
        String mipAddress = (niCard.getRipAddress()!=null) ? niCard.getRipAddress().getIpAddress() : "None";
        em.close();
        return mipAddress;
    }

    public void syncROSInstance(NICard niCard) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        niCard = em.find(niCard.getClass(), niCard.getId());
        OSInstance osInstance = null;
        for (OSInstance osInstanceLoop : OSInstancesListController.getAll()) {
            if (osInstanceLoop.getName().equals(changedOSInstance.get(niCard.getId()))) {
                osInstance = osInstanceLoop;
                break;
            }
        }
        if (osInstance != null) {
            try {
                em.getTransaction().begin();
                niCard.setOsInstanceR(osInstance);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "NICard updated successfully !",
                        "NiCard : " + niCard.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while updating NICard " + osInstance.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
    }

    public String getNICardOSInstance(NICard niCard) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        niCard = em.find(niCard.getClass(), niCard.getId());
        String mOsInstance = (niCard.getRosInstance()!=null) ? niCard.getRosInstance().getName() : "None";
        em.close();
        return mOsInstance;
    }

    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        NICard  eventniCard = ((NICard) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedIPAddress.remove(eventniCard.getId());
            changedOSInstance.remove(eventniCard.getId());
        } else {
            changedIPAddress.put(eventniCard.getId(), "");
            changedOSInstance.put(eventniCard.getId(), "");
        }
    }

    public void update(NICard niCard) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            niCard = em.find(niCard.getClass(), niCard.getId()).setDuplexR(niCard.getDuplex()).setMacAddressR(niCard.getMacAddress())
                    .setMtuR(niCard.getMtu()).setNameR(niCard.getName()).setSpeedR(niCard.getSpeed()).setIpAddressR(niCard.getRipAddress())
                    .setOsInstanceR(niCard.getRosInstance());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Network Interface card updated successfully !",
                    "NICard name : " + niCard.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating network interface card " + niCard.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected NICard
     */
    public void delete() {
        log.debug("Remove selected NICard !");
        for (NICard niCard2BeRemoved : selectedNICardList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                niCard2BeRemoved = em.find(niCard2BeRemoved.getClass(), niCard2BeRemoved.getId());
                em.remove(niCard2BeRemoved);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Network interface card deleted successfully !",
                        "NICard name : " + niCard2BeRemoved.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while deleting NICard " + niCard2BeRemoved.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                em.close();
            }
        }
        selectedNICardList = null;
    }

    /**
     * Get All NICard from DB
     * @return List of NICards
     */
    public static List<NICard> getAll() {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        log.debug("Get all NICards from : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                new Object[]{
                        (Thread.currentThread().getStackTrace().length>0) ? Thread.currentThread().getStackTrace()[0].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>1) ? Thread.currentThread().getStackTrace()[1].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>2) ? Thread.currentThread().getStackTrace()[2].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>3) ? Thread.currentThread().getStackTrace()[3].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>4) ? Thread.currentThread().getStackTrace()[4].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>5) ? Thread.currentThread().getStackTrace()[5].getClassName() : "",
                        (Thread.currentThread().getStackTrace().length>6) ? Thread.currentThread().getStackTrace()[6].getClassName() : ""
                });
        CriteriaBuilder        builder  = em.getCriteriaBuilder();
        CriteriaQuery<NICard> criteria = builder.createQuery(NICard.class);
        Root<NICard>       root     = criteria.from(NICard.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<NICard> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}
