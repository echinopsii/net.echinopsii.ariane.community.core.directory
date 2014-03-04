/**
 * Directory JSF Commons
 * Directories Company PrimeFaces Lazy Model
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

package com.spectral.cc.core.directory.wat.controller.organisational.company;

import com.spectral.cc.core.directory.base.model.organisational.Company;
import com.spectral.cc.core.directory.wat.consumer.DirectoryJPAProviderConsumer;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CompanyLazyModel extends LazyDataModel<Company> {
    private static final Logger log = LoggerFactory.getLogger(CompanyLazyModel.class);

    private int           rowCount ;
    private List<Company> pageItems ;

    private Predicate[] getSearchPredicates(EntityManager entityManager, Root<Company> root, Map<String,String> filters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
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
        EntityManager entityManager = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // Populate this.count
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Company> root = countCriteria.from(Company.class);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(entityManager, root, filters));
        this.rowCount = (int) (long) entityManager.createQuery(countCriteria).getSingleResult();
        log.debug("Returned rowCount : {}", this.rowCount);

        // Populate this.pageItems
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        root = criteria.from(Company.class);
        criteria.select(root).where(getSearchPredicates(entityManager, root, filters));
        if (sortOrder!=null && sortField!=null)
            criteria.orderBy(sortOrder.toString().equals("DESCENDING") ? builder.desc(root.get(sortField)) : builder.asc(root.get(sortField)));
        TypedQuery<Company> query = entityManager.createQuery(criteria);
        query.setFirstResult(first).setMaxResults(getPageSize());
        log.debug("Query: {}", new Object[]{query.toString()});
        this.pageItems = query.getResultList();
        log.debug("Returned pageItems : {}", this.pageItems);
        entityManager.close();
    }

    @Override
    public Company getRowData(String rowKey) {
        for(Company company : pageItems) {
            if(company.getId().equals(rowKey))
                return company;
        }
        return null;
    }

    @Override
    public Object getRowKey(Company company) {
        return company.getId();
    }

    @Override
    public List<Company> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        this.setPageSize(pageSize);
        paginate(first,sortField,sortOrder,filters);
        this.setRowCount(rowCount);
        return pageItems;
    }
}