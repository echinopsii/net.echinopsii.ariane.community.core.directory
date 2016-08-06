/**
 * Directory wat
 * Directories NIC RUD Controller
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.nic;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
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
public class NICsListController implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(NICsListController.class);

    private LazyDataModel<NIC> lazyModel = new NICLazyModel();

    private NIC[] selectedNICList;

    private HashMap<Long, String> changedIPAddress = new HashMap<Long, String>();

    private HashMap<Long, String> changedOSInstance = new HashMap<Long, String>();


    public HashMap<Long, String> getChangedOSInstance() {
        return changedOSInstance;
    }

    public void setChangedOSInstance(HashMap<Long, String> changedOSInstance) {
        this.changedOSInstance = changedOSInstance;
    }

    public NIC[] getSelectedNICList() {
        return selectedNICList;
    }

    public void setSelectedNICList(NIC[] selectedNICList) {
        this.selectedNICList = selectedNICList;
    }

    public LazyDataModel<NIC> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<NIC> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public HashMap<Long, String> getChangedIPAddress() {
        return changedIPAddress;
    }

    public void setChangedIPAddress(HashMap<Long, String> changedIPAddress) {
        this.changedIPAddress = changedIPAddress;
    }

    public void syncIPAddress(NIC nic) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        nic = em.find(nic.getClass(), nic.getId());
        IPAddress ipAddress = null;
        for (IPAddress ipAddressLoop : IPAddressListController.getAll()) {
            if (ipAddressLoop.getIpAddress().equals(changedIPAddress.get(nic.getId()))) {
                ipAddress = ipAddressLoop;
                break;
            }
        }
        if (ipAddress != null) {
            try {
                em.getTransaction().begin();
                nic.setIpAddressR(ipAddress);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "NIC updated successfully !",
                        "NiCard : " + nic.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while updating NIC " + ipAddress.getIpAddress() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
    }

    public String getNICIPAddress(NIC nic) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        nic = em.find(nic.getClass(), nic.getId());
        String mipAddress = (nic.getIpAddress()!=null) ? nic.getIpAddress().getIpAddress() : "None";
        em.close();
        return mipAddress;
    }

    public void syncOSInstance(NIC nic) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        nic = em.find(nic.getClass(), nic.getId());
        OSInstance osInstance = null;
        for (OSInstance osInstanceLoop : OSInstancesListController.getAll()) {
            if (osInstanceLoop.getName().equals(changedOSInstance.get(nic.getId()))) {
                osInstance = osInstanceLoop;
                break;
            }
        }
        if (osInstance != null) {
            try {
                em.getTransaction().begin();
                nic.setOsInstanceR(osInstance);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "NIC updated successfully !",
                        "NiCard : " + nic.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while updating NIC " + osInstance.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
    }

    public String getNICOSInstance(NIC nic) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        nic = em.find(nic.getClass(), nic.getId());
        String mOsInstance = (nic.getOsInstance()!=null) ? nic.getOsInstance().getName() : "None";
        em.close();
        return mOsInstance;
    }

    public void onRowToggle(ToggleEvent event) {
        log.debug("Row Toogled : {}", new Object[]{event.getVisibility().toString()});
        NIC eventNIC = ((NIC) event.getData());
        if (event.getVisibility().toString().equals("HIDDEN")) {
            changedIPAddress.remove(eventNIC.getId());
            changedOSInstance.remove(eventNIC.getId());
        } else {
            changedIPAddress.put(eventNIC.getId(), "");
            changedOSInstance.put(eventNIC.getId(), "");
        }
    }

    public void update(NIC nic) {
        EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        try {
            em.getTransaction().begin();
            nic = em.find(nic.getClass(), nic.getId()).setDuplexR(nic.getDuplex()).setMacAddressR(nic.getMacAddress())
                    .setMtuR(nic.getMtu()).setNameR(nic.getName()).setSpeedR(nic.getSpeed()).setIpAddressR(nic.getIpAddress())
                    .setOsInstanceR(nic.getOsInstance());
            em.flush();
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Network Interface card updated successfully !",
                    "NIC name : " + nic.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while updating network interface card " + nic.getName() + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Remove selected NIC
     */
    public void delete() {
        log.debug("Remove selected NIC !");
        for (NIC entity : selectedNICList) {
            EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            try {
                em.getTransaction().begin();
                entity = em.find(entity.getClass(), entity.getId());
                if (entity.getOsInstance() != null) {
                    entity.getOsInstance().getNics().remove(entity);
                    if (entity.getIpAddress()!=null) {
                        entity.getOsInstance().getIpAddresses().remove(entity.getIpAddress());
                        entity.getIpAddress().setOsInstance(null);
                    }
                    entity.setOsInstance(null);
                }
                if (entity.getIpAddress() != null) {
                    entity.getIpAddress().setNic(null);
                    entity.setIpAddressR(null);
                }
                em.remove(entity);
                em.flush();
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Network interface card deleted successfully !",
                        "NIC name : " + entity.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while deleting NIC " + entity.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } finally {
                em.close();
            }
        }
        selectedNICList = null;
    }

    /**
     * Get All NIC from DB
     * @return List of NICards
     */
    public static List<NIC> getAll() {
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
        CriteriaQuery<NIC> criteria = builder.createQuery(NIC.class);
        Root<NIC>       root     = criteria.from(NIC.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));

        List<NIC> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }

    /**
     * Get All NIC from DB
     * @return List of NICards
     */
    public static List<NIC> getAllUnlinked() {
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
        CriteriaQuery<NIC> criteria = builder.createQuery(NIC.class);
        Root<NIC>       root     = criteria.from(NIC.class);
        criteria.select(root).where(builder.isNull(root.get("osInstance"))).orderBy(builder.asc(root.get("name")));

        List<NIC> ret = em.createQuery(criteria).getResultList();
        em.close();
        return ret;
    }
}
