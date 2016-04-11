/**
 * Directory base
 * Model technical/system/OSType
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

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="osType", uniqueConstraints = @UniqueConstraint(columnNames = {"osTypeName"}))
public class OSType implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="osTypeName",nullable=false)
    @NotNull
    private String name;

    @Column(nullable=false)
    @NotNull
    private String architecture;

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @OneToMany(mappedBy = "osType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<OSInstance> osInstances = new HashSet<OSInstance>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public OSType setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public OSType setVersionR(final int version) {
        this.version = version ;
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
            return id.equals(((OSType) that).id);
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

    public OSType setNameR(final String name) {
        this.name = name ;
        return this;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    public OSType setArchitectureR(final String architecture) {
        this.architecture = architecture;
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(final Company company) {
        this.company = company;
    }

    public OSType setCompanyR(final Company company) {
        this.company = company;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (architecture != null && !architecture.trim().isEmpty())
            result += ", architecture: " + architecture;
        return result;
    }

    public Set<OSInstance> getOsInstances() {
        return this.osInstances;
    }

    public void setOsInstances(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
    }

    public OSType setOsInstancesR(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public OSType clone() {
        return new OSType().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setArchitectureR(this.architecture).setCompanyR(this.company).
                            setOsInstancesR(new HashSet<OSInstance>(this.osInstances));
    }

    public static OSType findOSTypeById(EntityManager em, long id) {
        TypedQuery<OSType> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.id = :entityId ORDER BY o.id", OSType.class);
        findByIdQuery.setParameter("entityId", id);
        OSType entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static OSType findOSTypeByNameAndArc(EntityManager em, String name, String arc) {
        TypedQuery<OSType> findByNameAndArcQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.name = :entityName AND o.architecture = :entityArc ORDER BY o.name", OSType.class);
        findByNameAndArcQuery.setParameter("entityName", name);
        findByNameAndArcQuery.setParameter("entityArc", arc);
        OSType entity;
        try {
            entity = findByNameAndArcQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }
}