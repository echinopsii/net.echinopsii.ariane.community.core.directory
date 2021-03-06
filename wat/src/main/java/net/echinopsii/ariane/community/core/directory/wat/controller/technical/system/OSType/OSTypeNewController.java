/**
 * Directory wat
 * Directories OSType Create Controller
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSType;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.company.CompanysListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;

/**
 * This class provide stuff to create and save a new OS Type from the UI form
 */
public class OSTypeNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSTypeNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String  name;
    private String  architecture;

    private String  osiCompany;
    private Company company;

    public EntityManager getEm() {
        return em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getOsiCompany() {
        return osiCompany;
    }

    public void setOsiCompany(String osiCompany) {
        this.osiCompany = osiCompany;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * synchronize this.company from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncCompany() throws NotSupportedException, SystemException {
        for (Company company: CompanysListController.getAll()) {
            if (company.getName().equals(this.osiCompany)) {
                company = em.find(company.getClass(), company.getId());
                this.company = company;
                log.debug("Synced embedding os instance : {} {}", new Object[]{this.company.getId(), this.company.getName()});
                break;
            }
        }
    }

    /**
     * save a new OS Type thanks data provided through UI form
     */
    public void save() {
        try {
            syncCompany();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating OS Instance " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (OSType.findOSTypeByNameAndArc(em, name, architecture) == null) {
            OSType osType = new OSType().setNameR(name).setArchitectureR(architecture).setCompanyR(company);
            try {
                em.getTransaction().begin();
                if (this.company != null) {
                    this.company.getOsTypes().add(osType);
                }
                em.persist(osType);
                em.flush();
                em.getTransaction().commit();
                log.debug("Save new OSType {} !", new Object[]{name});
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "OS type created successfully !",
                        "OS type name : " + osType.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Throwable t) {
                log.debug("Throwable catched !");
                t.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Throwable raised while creating OS type " + osType.getName() + " !",
                        "Throwable message : " + t.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "OS type already exists !",
                    "OS type (" + name + ", " + architecture + ") already exists!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
}