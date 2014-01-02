/**
 * Directory Main bundle
 * Directories Application Create Controller
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

package com.spectral.cc.core.directory.main.controller.organisational.application;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.main.controller.organisational.company.CompanysListController;
import com.spectral.cc.core.directory.main.controller.organisational.team.TeamsListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.organisational.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;

@ManagedBean
@RequestScoped
public class ApplicationNewController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ApplicationNewController.class);

    private String  name;
    private String  shortName;
    private String  colorCode;
    private String  description;

    private String  appCompany;
    private Company company;

    private String appTeam;
    private Team   team;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppCompany() {
        return appCompany;
    }

    public void setAppCompany(String osiCompany) {
        this.appCompany = osiCompany;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    private void syncCompany() throws NotSupportedException, SystemException {
        for (Company company2: CompanysListController.getAll()) {
            if (company2.getName().equals(this.appCompany)) {
                this.company = company2;
                log.debug("Synced embedding os instance : {} {}", new Object[]{this.company.getId(), this.company.getName()});
                break;
            }
        }
    }

    public String getAppTeam() {
        return appTeam;
    }

    public void setAppTeam(String appTeam) {
        this.appTeam = appTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    private void syncTeam()  throws NotSupportedException, SystemException {
        for (Team team: TeamsListController.getAll()) {
            if (team.getName().equals(this.appTeam)) {
                this.team = team;
                break;
            }
        }
    }

    public void save() {
        log.debug("Save new Application {} !", new Object[]{name});
        try {
            syncCompany();
            syncTeam();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating OS Instance " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Application application = new Application().setNameR(name).setShortNameR(shortName).setColorCodeR(colorCode).setDescriptionR(description).
                                                    setCompanyR(company).setTeamR(team);
        try {
            //JPAProviderConsumer.getSharedUX().begin();
            //JPAProviderConsumer.getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(application);
            if (this.company!=null) {this.company.getApplications().add(application); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(this.company);}
            if (this.team!=null) {this.team.getApplications().add(application); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(this.team);}
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Application created successfully !",
                                                       "Application name : " + application.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating Application " + application.getName() + " !",
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
                                                       "Operation : Application " + application.getName() + " creation.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation rollbacked !",
                                                           "Operation : Application " + application.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Error while rollbacking operation !",
                                                           "Operation : Application " + application.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : Application " + application.getName() + " creation.");
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