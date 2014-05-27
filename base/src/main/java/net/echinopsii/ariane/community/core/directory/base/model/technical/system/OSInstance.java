/**
 * Directory base
 * Model technical/system/OSInstance
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
package net.echinopsii.ariane.community.core.directory.base.model.technical.system;

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="osInstance", uniqueConstraints = @UniqueConstraint(columnNames = {"osName"}))
public class OSInstance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="osName",unique=true)
    @NotNull
    private String name;

    @Column
    @NotNull
    private String adminGateURI;

    @Column
    private String description;

    @ManyToMany(mappedBy = "osInstances", fetch = FetchType.LAZY)
    private Set<Subnet> networkSubnets;

    @ManyToOne(fetch = FetchType.EAGER)
    private OSInstance embeddingOSInstance;
    @OneToMany(mappedBy = "embeddingOSInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<OSInstance> embeddedOSInstances = new HashSet<OSInstance>();

    @ManyToOne(fetch = FetchType.EAGER)
    private OSType osType;

    @ManyToMany(mappedBy = "osInstances", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Application> applications = new HashSet<Application>();

    @ManyToMany(mappedBy = "osInstances", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Team> teams = new HashSet<Team>();

    @ManyToMany(mappedBy = "osInstances", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Environment> environments = new HashSet<Environment>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public OSInstance setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public OSInstance setVersionR(final int version) {
        this.version = version;
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (id != null) {
            return id.equals(((OSInstance) that).id);
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public OSInstance setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getAdminGateURI() {
        return this.adminGateURI;
    }

    public void setAdminGateURI(final String adminGateURI) {
        this.adminGateURI = adminGateURI;
    }

    public OSInstance setAdminGateURIR(final String adminGateURI) {
        this.adminGateURI = adminGateURI;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public OSInstance setDescriptionR(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (adminGateURI != null && !adminGateURI.trim().isEmpty())
            result += ", adminGateURI: " + adminGateURI;
        if (description != null && !description.trim().isEmpty())
            result += ", description: " + description;
        return result;
    }

    public Set<Subnet> getNetworkSubnets() {
        return this.networkSubnets;
    }

    public void setNetworkSubnets(final Set<Subnet> networkSubnets) {
        this.networkSubnets = networkSubnets;
    }

    public OSInstance setNetworkSubnetsR(final Set<Subnet> networkSubnets) {
        this.networkSubnets = networkSubnets;
        return this;
    }

    public OSInstance getEmbeddingOSInstance() {
        return this.embeddingOSInstance;
    }

    public void setEmbeddingOSInstance(final OSInstance embeddingOSInstance) {
        this.embeddingOSInstance = embeddingOSInstance;
    }

    public OSInstance setEmbeddingOSInstanceR(final OSInstance embeddingOSInstance) {
        this.embeddingOSInstance = embeddingOSInstance;
        return this;
    }

    public Set<OSInstance> getEmbeddedOSInstances() {
        return this.embeddedOSInstances;
    }

    public void setEmbeddedOSInstances(final Set<OSInstance> embeddedOSInstances) {
        this.embeddedOSInstances = embeddedOSInstances;
    }

    public OSInstance setEmbeddedOSInstancesR(final Set<OSInstance> embeddedOSInstances) {
        this.embeddedOSInstances = embeddedOSInstances;
        return this;
    }

    public OSType getOsType() {
        return this.osType;
    }

    public void setOsType(final OSType osType) {
        this.osType = osType;
    }

    public OSInstance setOsTypeR(final OSType osType) {
        this.osType = osType;
        return this;
    }

    public Set<Application> getApplications() {
        return this.applications;
    }

    public void setApplications(final Set<Application> applications) {
        this.applications = applications;
    }

    public OSInstance setApplicationsR(final Set<Application> applications) {
        this.applications = applications;
        return this;
    }

    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(final Set<Team> teams) {
        this.teams = teams;
    }

    public OSInstance setTeamsR(final Set<Team> teams) {
        this.teams = teams;
        return this;
    }

    public Set<Environment> getEnvironments() {
        return this.environments;
    }

    public void setEnvironments(final Set<Environment> environments) {
        this.environments = environments;
    }

    public OSInstance setEnvironementsR(final Set<Environment> environments) {
        this.environments = environments;
        return this;
    }

    public OSInstance clone() {
        return new OSInstance().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setAdminGateURIR(this.adminGateURI).setOsTypeR(this.osType).
                   setApplicationsR(new HashSet<Application>(this.applications)).setEmbeddedOSInstancesR(new HashSet<OSInstance>(this.embeddedOSInstances)).setEmbeddingOSInstanceR(this.embeddingOSInstance).
                   setEnvironementsR(new HashSet<Environment>(this.environments)).setNetworkSubnetsR(new HashSet<Subnet>(networkSubnets)).setTeamsR(new HashSet<Team>(this.teams));
    }

    public final static String OSI_MAPPING_PROPERTIES = "Server";
    private final static String OSI_NAME_MAPPING_FIELD = "hostname";
    private final static String OSI_TYPE_MAPPING_FIELD = "os";

    public HashMap<String,Object> toMappingProperties() {
        HashMap<String,Object> ret = new HashMap<String,Object>();
        ret.put(OSI_NAME_MAPPING_FIELD,name);
        ret.put(OSI_TYPE_MAPPING_FIELD,osType.getName()+" - "+osType.getArchitecture());
        return ret;
    }
}