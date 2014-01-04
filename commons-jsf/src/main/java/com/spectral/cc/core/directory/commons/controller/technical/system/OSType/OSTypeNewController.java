/**
 * Directory JSF Commons
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

package com.spectral.cc.core.directory.commons.controller.technical.system.OSType;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.company.CompanysListController;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.technical.system.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;

public class OSTypeNewController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSTypeNewController.class);

    private String  name;
    private String  architecture;

    private String  osiCompany;
    private Company company;

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

    private void syncCompany() throws NotSupportedException, SystemException {
        for (Company company2: CompanysListController.getAll()) {
            if (company2.getName().equals(this.osiCompany)) {
                this.company = company2;
                log.debug("Synced embedding os instance : {} {}", new Object[]{this.company.getId(), this.company.getName()});
                break;
            }
        }
    }

    public void save() {
        log.debug("Save new OSType {} !", new Object[]{name});
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
        OSType osType = new OSType().setNameR(name).setArchitectureR(architecture).setCompanyR(company);
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            if (this.company!=null) {this.company.getOsTypes().add(osType);}
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(osType);
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
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
            if (JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().isActive())
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().rollback();
/*
            try {
                FacesMessage msg2;
                int txStatus = JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().getStatus();
                switch(txStatus) {
                    case Status.STATUS_NO_TRANSACTION:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled !",
                                                       "Operation : OS type " + osType.getName() + " creation.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation rollbacked !",
                                                           "Operation : OS type " + osType.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Error while rollbacking operation !",
                                                           "Operation : OS type " + osType.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : OS type " + osType.getName() + " creation.");
                        break;
                }
                FacesContext.getCurrentInstance().addMessage(null, msg2);
            } catch (SystemException e) {
                e.printStackTrace();
            }
*/
        }
    }
}