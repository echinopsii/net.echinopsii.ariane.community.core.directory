/**
 * Directory base
 * Model organisational/application
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
package com.spectral.cc.core.directory.base.model.organisational;

import com.spectral.cc.core.directory.base.model.technical.system.OSInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="application",uniqueConstraints = @UniqueConstraint(columnNames = {"applicationName","applicationCC"}))
public class Application implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="applicationName",unique=true)
    @NotNull
    private String name;

    @Column
    @NotNull
    private String shortName;

    @Column(name="applicationCC",unique=true)
    @NotNull
    private String colorCode;

    @Column
    private String description;

    @ManyToMany
    private Set<OSInstance> osInstances = new HashSet<OSInstance>();

    @ManyToOne
    private Team team;

    @ManyToOne
    private Company company;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Application setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Application setVersionR(final int version) {
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
            return id.equals(((Application) that).id);
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

    public Application setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public Application setShortNameR(final String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getColorCode() {
        return this.colorCode;
    }

    public void setColorCode(final String colorCode) {
        this.colorCode = colorCode;
    }

    public Application setColorCodeR(final String colorCode) {
        this.colorCode = colorCode;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Application setDescriptionR(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (shortName != null && !shortName.trim().isEmpty())
            result += ", shortName: " + shortName;
        if (colorCode != null && !colorCode.trim().isEmpty())
            result += ", colorCode: " + colorCode;
        if (description != null && !description.trim().isEmpty())
            result += ", description: " + description;
        return result;
    }

    public Set<OSInstance> getOsInstances() {
        return this.osInstances;
    }

    public void setOsInstances(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
    }

    public Application setOsInstancesR(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(final Team team) {
        this.team = team;
    }

    public Application setTeamR(final Team team) {
        this.team = team;
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(final Company company) {
        this.company = company;
    }

    public Application setCompanyR(final Company company) {
        this.company = company;
        return this;
    }

    public Application clone() {
        return new Application().setIdR(id).setVersionR(version).setNameR(name).setDescriptionR(description).setCompanyR(company).
                                 setTeamR(team).setColorCodeR(colorCode).setOsInstancesR(new HashSet<OSInstance>(osInstances)).setShortNameR(shortName);
    }

    public final static String APP_PRIMARY_MAPPING_PROPERTIES   = "primaryApplication";
    public final static String APP_SECONDARY_MAPPING_PROPERTIES = "secondaryApplication";
    private final static String APP_NAME_MAPPING_FIELD = "name";
    private final static String APP_COLR_MAPPING_FIELD = "color";

    public HashMap<String,Object> toMappingProperties() {
        HashMap<String,Object> ret = new HashMap<String,Object>();
        ret.put(APP_NAME_MAPPING_FIELD,name);
        ret.put(APP_COLR_MAPPING_FIELD,colorCode);
        return ret;
    }
}