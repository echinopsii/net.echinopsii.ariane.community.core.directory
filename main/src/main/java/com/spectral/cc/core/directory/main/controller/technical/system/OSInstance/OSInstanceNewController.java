/**
 * Directory Main bundle
 * Directories OSInstance Create Controller
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
package com.spectral.cc.core.directory.main.controller.technical.system.OSInstance;

import com.spectral.cc.core.directory.commons.consumer.JPAProviderConsumer;
import com.spectral.cc.core.directory.main.controller.organisational.application.ApplicationsListController;
import com.spectral.cc.core.directory.main.controller.organisational.environment.EnvironmentsListController;
import com.spectral.cc.core.directory.main.controller.organisational.team.TeamsListController;
import com.spectral.cc.core.directory.main.controller.technical.network.lan.LansListController;
import com.spectral.cc.core.directory.main.controller.technical.system.OSType.OSTypesListController;
import com.spectral.cc.core.directory.commons.model.organisational.Application;
import com.spectral.cc.core.directory.commons.model.organisational.Environment;
import com.spectral.cc.core.directory.commons.model.organisational.Team;
import com.spectral.cc.core.directory.commons.model.technical.network.Lan;
import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;
import com.spectral.cc.core.directory.commons.model.technical.system.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean(name="OSInstanceNewController")
@RequestScoped
public class OSInstanceNewController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSInstanceNewController.class);

    private String name;
    private String description;
    private String adminGateURI;

    private String osType;
    private OSType type;

    private String     embeddingOSI;
    private OSInstance embingOSI;

    private List<String> lansToBind = new ArrayList<String>();
    private Set<Lan>     lans       = new HashSet<Lan>();

    private List<String>     envsToBind = new ArrayList<String>();
    private Set<Environment> envs       = new HashSet<Environment>();

    private List<String> teamsToBind = new ArrayList<String>();
    private Set<Team>    teams       = new HashSet<Team>();

    private List<String>     appsToBind = new ArrayList<String>();
    private Set<Application> apps       = new HashSet<Application>();

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

    public String getAdminGateURI() {
        return adminGateURI;
    }

    public void setAdminGateURI(String adminGateURI) {
        this.adminGateURI = adminGateURI;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public OSType getType() {
        return type;
    }

    public void setType(OSType type) {
        this.type = type;
    }

    private void syncOSType() throws NotSupportedException, SystemException {
        OSType type = null;
        for (OSType osType1: OSTypesListController.getAll()) {
            String fullName = osType1.getName() + " - " + osType1.getArchitecture();
            log.debug("fullName:{};osType:{}",fullName,osType);
            if (fullName.equals(this.osType)) {
                type = osType1;
                break;
            }
        }
        if (type != null) {
            this.type  = type;
            log.debug("Synced OSType : {} {}", new Object[]{this.type.getId(), this.type.getName()});
        }
    }

    public String getEmbeddingOSI() {
        return embeddingOSI;
    }

    public void setEmbeddingOSI(String embeddingOSI) {
        this.embeddingOSI = embeddingOSI;
    }

    public OSInstance getEmbingOSI() {
        return embingOSI;
    }

    public void setEmbingOSI(OSInstance embingOSI) {
        this.embingOSI = embingOSI;
    }

    private void syncEmbingOSI() throws NotSupportedException, SystemException {
        OSInstance osInstance = null;
        for (OSInstance osInstance1: OSInstancesListController.getAll()) {
            if (osInstance1.getName().equals(this.embeddingOSI)) {
                osInstance = osInstance1;
                break;
            }
        }
        if (osInstance != null) {
            this.embingOSI  = osInstance;
            log.debug("Synced embedding os instance : {} {}", new Object[]{this.embingOSI.getId(), this.embingOSI.getName()});
        }
    }

    public List<String> getLansToBind() {
        return lansToBind;
    }

    public void setLansToBind(List<String> lansToBind) {
        this.lansToBind = lansToBind;
    }

    public Set<Lan> getLans() {
        return lans;
    }

    public void setLans(Set<Lan> lans) {
        this.lans = lans;
    }

    private void bindSelectedLans() throws NotSupportedException, SystemException {
        for (Lan lan: LansListController.getAll()) {
            for (String lanToBind : lansToBind)
                if (lan.getName().equals(lanToBind)) {
                    this.lans.add(lan);
                    log.debug("Synced lan : {} {}", new Object[]{lan.getId(), lan.getName()});
                    break;
                }
        }
    }

    public List<String> getEnvsToBind() {
        return envsToBind;
    }

    public void setEnvsToBind(List<String> envsToBind) {
        this.envsToBind = envsToBind;
    }

    public Set<Environment> getEnvs() {
        return envs;
    }

    public void setEnvs(Set<Environment> envs) {
        this.envs = envs;
    }

    private void bindSelectedEnvs() throws NotSupportedException, SystemException {
        for (Environment environment: EnvironmentsListController.getAll()) {
            for (String envToBind : envsToBind)
                if (environment.getName().equals(envToBind)) {
                    this.envs.add(environment);
                    log.debug("Synced environment : {} {}", new Object[]{environment.getId(), environment.getName()});
                    break;
                }
        }
    }

    public List<String> getTeamsToBind() {
        return teamsToBind;
    }

    public void setTeamsToBind(List<String> teamsToBind) {
        this.teamsToBind = teamsToBind;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    private void bindSelectedTeams() throws NotSupportedException, SystemException {
        for (Team team: TeamsListController.getAll()) {
            for (String teamToBind : teamsToBind)
                if (team.getName().equals(teamToBind)) {
                    this.teams.add(team);
                    log.debug("Synced team : {} {}", new Object[]{team.getId(), team.getName()});
                    break;
                }
        }
    }

    public List<String> getAppsToBind() {
        return appsToBind;
    }

    public void setAppsToBind(List<String> appsToBind) {
        this.appsToBind = appsToBind;
    }

    public Set<Application> getApps() {
        return apps;
    }

    public void setApps(Set<Application> apps) {
        this.apps = apps;
    }

    private void bindSelectedApps() throws NotSupportedException, SystemException {
        for (Application application: ApplicationsListController.getAll()) {
            for (String envToBind : appsToBind)
                if (application.getName().equals(envToBind)) {
                    this.apps.add(application);
                    log.debug("Synced app : {} {}", new Object[]{application.getId(), application.getName()});
                    break;
                }
        }
    }

    public void save() {
        log.debug("Save new OSInstance {} !", new Object[]{name});
        try {
            syncOSType();
            syncEmbingOSI();
            bindSelectedLans();
            bindSelectedEnvs();
            bindSelectedTeams();
            bindSelectedApps();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating OS Instance " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        OSInstance osInstance = new OSInstance();
        osInstance.setName(name);
        osInstance.setDescription(description);
        osInstance.setOsType(type);
        osInstance.setAdminGateURIR(adminGateURI);
        osInstance.setEmbeddingOSInstance(embingOSI);
        osInstance.setNetworkLans(lans);
        osInstance.setEnvironments(envs);
        osInstance.setTeams(teams);
        osInstance.setApplications(apps);
        try {
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().begin();
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().joinTransaction();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().begin();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().persist(osInstance);
            if (type!=null)  {type.getOsInstances().add(osInstance);  JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(type);}
            for(Application application : this.apps) {
                application.getOsInstances().add(osInstance);
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(application);
            }
            for(Team team : this.teams) {
                team.getOsInstances().add(osInstance);
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(team);
            }
            for(Environment env : this.envs) {
                env.getOsInstances().add(osInstance);
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(env);
            }
            for (Lan lan : this.lans) {
                lan.getOsInstances().add(osInstance);
                JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(lan);
            }
            if (embingOSI!=null) {embingOSI.getEmbeddedOSInstances().add(osInstance); JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().merge(embingOSI);}
            //JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().commit();
            JPAProviderConsumer.getInstance().getJpaProvider().getSharedEM().getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "OSInstance created successfully !",
                                                       "OSInstance name : " + osInstance.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating OS Instance " + osInstance.getName() + " !",
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
                                                       "Operation : OS Instance " + osInstance.getName() + " creation.");
                        break;
                    case Status.STATUS_MARKED_ROLLBACK:
                        try {
                            log.debug("Rollback operation !");
                            JPAProviderConsumer.getInstance().getJpaProvider().getSharedUX().rollback();
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                           "Operation rollbacked !",
                                                           "Operation : OS Instance " + osInstance.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        } catch (SystemException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                           "Error while rollbacking operation !",
                                                           "Operation : OS Instance " + osInstance.getName() + " creation.");
                            FacesContext.getCurrentInstance().addMessage(null, msg2);
                        }
                        break;
                    default:
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation canceled ! ("+txStatus+")",
                                                       "Operation : OS Instance " + osInstance.getName() + " creation.");
                        break;
                }
                FacesContext.getCurrentInstance().addMessage(null, msg2);
            } catch (SystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
*/
        }
    }
}
