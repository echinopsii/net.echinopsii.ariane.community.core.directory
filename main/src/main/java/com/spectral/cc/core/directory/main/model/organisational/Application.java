package com.spectral.cc.core.directory.main.model.organisational;

import com.spectral.cc.core.directory.main.model.technical.system.OSInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="application",uniqueConstraints = @UniqueConstraint(columnNames = {"applicationName"}))
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

    @Column
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
}