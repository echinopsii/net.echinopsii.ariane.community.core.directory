/**
 * Directory JSF Commons
 * Directories Datacenter PrimeFaces Lazy Model
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

package com.spectral.cc.core.directory.wat.controller.technical.network.datacenter;

import com.spectral.cc.core.directory.base.model.technical.network.Datacenter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

public class DatacenterLazyModel extends LazyDataModel<Datacenter> {

    private static final Logger log = LoggerFactory.getLogger(DatacenterLazyModel.class);

    private int              rowCount      ;
    private EntityManager    entityManager ;
    private List<Datacenter> pageItems ;

    private Predicate[] getSearchPredicates(Root<Datacenter> root, Map<String,String> filters) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
            String filterProperty = it.next();
            String filterValue = filters.get(filterProperty);
            log.debug("Filter : { {}, {} }", new Object[]{filterProperty, filterValue});
            predicatesList.add(builder.like(root.<String> get(filterProperty), '%' + filterValue + '%'));
        }
        Predicate[] ret = predicatesList.toArray(new Predicate[predicatesList.size()]);
        log.debug("Return predicates list: {}", new Object[]{ret.toString()});
        return ret;
    }

    private void paginate(int first, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Datacenter> root = countCriteria.from(Datacenter.class);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root,filters));
        this.rowCount = (int) (long) this.entityManager.createQuery(countCriteria).getSingleResult();

        // Populate this.pageItems
        CriteriaQuery<Datacenter> criteria = builder.createQuery(Datacenter.class);
        root = criteria.from(Datacenter.class);
        criteria.select(root).where(getSearchPredicates(root,filters));
        if (sortOrder!=null && sortField!=null)
            criteria.orderBy(sortOrder.toString().equals("DESCENDING") ? builder.desc(root.get(sortField)) : builder.asc(root.get(sortField)));
        TypedQuery<Datacenter> query = this.entityManager.createQuery(criteria);
        query.setFirstResult(first).setMaxResults(getPageSize());
        log.debug("Query: {}", new Object[]{query.toString()});
        this.pageItems = query.getResultList();

        // Refresh page items as operations can occurs on them from != em
        for(Datacenter dc : this.pageItems) {
            this.entityManager.refresh(dc);
        }
    }

    @Override
    public Datacenter getRowData(String rowKey) {
        for(Datacenter datacenter : pageItems) {
            if(datacenter.getId().equals(rowKey))
                return datacenter;
        }
        return null;
    }

    @Override
    public Object getRowKey(Datacenter datacenter) {
        return datacenter.getId();
    }

    public DatacenterLazyModel setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return this;
    }

    @Override
    public List<Datacenter> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        this.setPageSize(pageSize);
        paginate(first,sortField,sortOrder,filters);
        this.setRowCount(rowCount);
        return pageItems;
    }
}