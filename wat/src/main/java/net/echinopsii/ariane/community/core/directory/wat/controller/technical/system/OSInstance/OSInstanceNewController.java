/**
 * Directory wat
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
package net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSInstance;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.ipAddress.IPAddressListController;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.application.ApplicationsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.environment.EnvironmentsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.organisational.team.TeamsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.subnet.SubnetsListController;
import net.echinopsii.ariane.community.core.directory.wat.controller.technical.system.OSType.OSTypesListController;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provide stuff to create and save a new OS Instance from the UI form
 */
public class OSInstanceNewController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(OSInstanceNewController.class);

    private EntityManager em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();

    @PreDestroy
    public void clean() {
        log.debug("Close entity manager");
        em.close();
    }

    private String name;
    private String description;
    private String adminGateURI;

    private String osType;
    private OSType type;

    private String     embeddingOSI;
    private OSInstance embingOSI;

    private List<String> subnetsToBind = new ArrayList<String>();
    private Set<Subnet>  subnets       = new HashSet<Subnet>();

    private List<String> ipAddressesToBind = new ArrayList<String>();
    private Set<IPAddress>  ipAddresses       = new HashSet<IPAddress>();

    private List<String>     envsToBind = new ArrayList<String>();
    private Set<Environment> envs       = new HashSet<Environment>();

    private List<String> teamsToBind = new ArrayList<String>();
    private Set<Team>    teams       = new HashSet<Team>();

    private List<String>     appsToBind = new ArrayList<String>();
    private Set<Application> apps       = new HashSet<Application>();

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

    /**
     * synchronize this.type from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncOSType() throws NotSupportedException, SystemException {
        OSType type = null;
        for (OSType osType: OSTypesListController.getAll()) {
            osType = em.find(osType.getClass(), osType.getId());
            String fullName = osType.getName() + " - " + osType.getArchitecture();
            log.debug("fullName:{};osType:{}",fullName, this.osType);
            if (fullName.equals(this.osType)) {
                type = osType;
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

    /**
     * synchronize this.embingOSI from DB
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void syncEmbingOSI() throws NotSupportedException, SystemException {
        OSInstance osInstance = null;
        for (OSInstance osInstance1: OSInstancesListController.getAll()) {
            if (osInstance1.getName().equals(this.embeddingOSI)) {
                osInstance1 = em.find(osInstance1.getClass(), osInstance1.getId());
                osInstance = osInstance1;
                break;
            }
        }
        if (osInstance != null) {
            this.embingOSI  = osInstance;
            log.debug("Synced embedding os instance : {} {}", new Object[]{this.embingOSI.getId(), this.embingOSI.getName()});
        }
    }

    public List<String> getSubnetsToBind() {
        return subnetsToBind;
    }

    public void setSubnetsToBind(List<String> subnetsToBind) {
        this.subnetsToBind = subnetsToBind;
    }

    public Set<Subnet> getSubnets() {
        return subnets;
    }

    public void setSubnets(Set<Subnet> subnets) {
        this.subnets = subnets;
    }

    /**
     * populate subnets list through subnetsToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedSubnets() throws NotSupportedException, SystemException {
        for (Subnet subnet : SubnetsListController.getAll()) {
            for (String subnetToBind : subnetsToBind)
                if (subnet.getName().equals(subnetToBind)) {
                    subnet = em.find(subnet.getClass(), subnet.getId());
                    this.subnets.add(subnet);
                    log.debug("Synced subnet : {} {}", new Object[]{subnet.getId(), subnet.getName()});
                    break;
                }
        }
    }

    public Set<IPAddress> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(Set<IPAddress> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public List<String> getIpAddressesToBind() {
        return ipAddressesToBind;
    }

    public void setIpAddressesToBind(List<String> ipAddressesToBind) {
        this.ipAddressesToBind = ipAddressesToBind;
    }

    /**
     * populate ipAddresses list through ipAddressesToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedIPAddresses() throws NotSupportedException, SystemException {
        for (IPAddress ipAddress : IPAddressListController.getAll()) {
            for (String ipAddressToBind : ipAddressesToBind)
                if (ipAddress.getIpAddress().equals(ipAddressToBind)) {
                    ipAddress = em.find(ipAddress.getClass(), ipAddress.getId());
                    this.ipAddresses.add(ipAddress);
                    log.debug("Synced IPAddress : {} {}", new Object[]{ipAddress.getId(), ipAddress.getIpAddress()});
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

    /**
     * populate envs list through envsToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedEnvs() throws NotSupportedException, SystemException {
        for (Environment environment: EnvironmentsListController.getAll()) {
            for (String envToBind : envsToBind)
                if (environment.getName().equals(envToBind)) {
                    environment = em.find(environment.getClass(), environment.getId());
                    this.envs.add(environment);
                    log.debug("Synced environment : {} {}", new Object[]{environment.getId(), environment.getName()});
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

    /**
     * populate teams list through teamsToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedTeams() throws NotSupportedException, SystemException {
        for (Team team: TeamsListController.getAll()) {
            for (String teamToBind : teamsToBind)
                if (team.getName().equals(teamToBind)) {
                    team = em.find(team.getClass(), team.getId());
                    this.teams.add(team);
                    log.debug("Synced team : {} {}", new Object[]{team.getId(), team.getName()});
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

    /**
     * populate applications list through appssToBind list provided through UI form
     *
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void bindSelectedApps() throws NotSupportedException, SystemException {
        for (Application application: ApplicationsListController.getAll()) {
            for (String appToBind : appsToBind)
                if (application.getName().equals(appToBind)) {
                    application = em.find(application.getClass(), application.getId());
                    this.apps.add(application);
                    log.debug("Synced app : {} {}", new Object[]{application.getId(), application.getName()});
                }
        }
    }

    /**
     * save a new OS Instance thanks data provided through UI form
     */
    public void save() {
        try {
            syncOSType();
            syncEmbingOSI();
            bindSelectedSubnets();
            bindSelectedEnvs();
            bindSelectedTeams();
            bindSelectedApps();
            bindSelectedIPAddresses();
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
        osInstance.setNetworkSubnets(subnets);
        osInstance.setIpAddress(ipAddresses);
        osInstance.setEnvironments(envs);
        osInstance.setTeams(teams);
        osInstance.setApplications(apps);

        try {
            em.getTransaction().begin();
            em.persist(osInstance);
            if (type!=null)  {type.getOsInstances().add(osInstance);  em.merge(type);}
            for(Application application : this.apps) {
                application.getOsInstances().add(osInstance);
                em.merge(application);
            }
            for(Team team : this.teams) {
                team.getOsInstances().add(osInstance);
                em.merge(team);
            }
            for(Environment env : this.envs) {
                env.getOsInstances().add(osInstance);
                em.merge(env);
            }
            for (Subnet subnet : this.subnets) {
                subnet.getOsInstances().add(osInstance);
                em.merge(subnet);
            }
            for (IPAddress ipAddress : this.ipAddresses) {
                ipAddress.getOsInstances().add(osInstance);
                em.merge(ipAddress);
            }

            if (embingOSI!=null) {embingOSI.getEmbeddedOSInstances().add(osInstance); em.merge(embingOSI);}
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new OSInstance {} !", new Object[]{name});
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
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}
