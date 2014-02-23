/**
 * Directory JSF Commons
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

package com.spectral.cc.core.directory.commons.controller.organisational.application;

import com.spectral.cc.core.directory.commons.consumer.DirectoryJPAProviderConsumer;
import com.spectral.cc.core.directory.commons.controller.organisational.company.CompanysListController;
import com.spectral.cc.core.directory.commons.controller.organisational.team.TeamsListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Company;
import com.spectral.cc.core.directory.commons.model.organisational.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;

public class ApplicationNewController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ApplicationNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    private String  name;
    private String  shortName;
    private String  colorCode;
    private String  description;

    private String  appCompany;
    private Company company;

    private String appTeam;
    private Team   team;

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    public EntityManager getEm() {
        return em;
    }

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
        for (Company company2: CompanysListController.getAll(em)) {
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
        for (Team team: TeamsListController.getAll(em)) {
            if (team.getName().equals(this.appTeam)) {
                this.team = team;
                break;
            }
        }
    }

    public void save() {
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
            em.getTransaction().begin();
            em.persist(application);
            if (this.company!=null) {this.company.getApplications().add(application); em.merge(this.company);}
            if (this.team!=null) {this.team.getApplications().add(application); em.merge(this.team);}
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new Application {} !", new Object[]{name});
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
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}