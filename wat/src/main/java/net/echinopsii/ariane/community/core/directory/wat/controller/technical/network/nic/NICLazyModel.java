/**
 * Directory wat
 * Directories NIC PrimeFaces Lazy Model
 * Copyright (C) 2015 Echinopsii
 * Author : Sagar Ghuge
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

package net.echinopsii.ariane.community.core.directory.wat.controller.technical.network.nic;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
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

/**
 * This class provide lazy loading stuff for our NIC PrimeFaces datatable implementation
 */
public class NICLazyModel extends LazyDataModel<NIC> {

    private static final Logger log = LoggerFactory.getLogger(NICLazyModel.class);

    private int          rowCount ;
    private List<NIC> pageItems ;

    /**`
     * Add search predicate to the JPA query
     *
     * @param entityManager the current JPA entity manager in use
     * @param root the current JPA root of the query
     * @param filters the provided filters
     * @return the generated JPA predicate
     */
    private Predicate[] getSearchPredicates(EntityManager entityManager, Root<NIC> root, Map<String,String> filters) {
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

    /**
     * Generate a JPA query and push the result into pageItems
     *
     * @param first first result of the query (the NIC id)
     * @param sortField the sort field of the query
     * @param sortOrder the sort order of the query
     * @param filters the provided filters
     */
    private void paginate(int first, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        EntityManager entityManager;
        entityManager = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // Populate this.count
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<NIC> root = countCriteria.from(NIC.class);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(entityManager, root,filters));
        this.rowCount = (int) (long) entityManager.createQuery(countCriteria).getSingleResult();

        // Populate this.pageItems
        CriteriaQuery<NIC> criteria = builder.createQuery(NIC.class);
        root = criteria.from(NIC.class);
        criteria.select(root).where(getSearchPredicates(entityManager, root,filters));
        if (sortOrder!=null && sortField!=null)
            criteria.orderBy(sortOrder.toString().equals("DESCENDING") ? builder.desc(root.get(sortField)) : builder.asc(root.get(sortField)));
        TypedQuery<NIC> query = entityManager.createQuery(criteria);
        query.setFirstResult(first).setMaxResults(getPageSize());
        log.debug("Query: {}", new Object[]{query.toString()});
        this.pageItems = query.getResultList();

        entityManager.close();
    }

    /**
     * Return the NIC assigned to a table row
     *
     * @param rowKey the row ipAddress id
     *
     * @return NIC object according to provided NIC id
     */
    @Override
    public NIC getRowData(String rowKey) {
        for(NIC nic : pageItems) {
            if(nic.getId().equals(rowKey))
                return nic;
        }
        return null;
    }

    /**
     * Return the NIC id assigned to a table row
     *
     * @param nic the row NIC
     *
     * @return the NIC id
     */
    @Override
    public Object getRowKey(NIC nic) {
        return nic.getId();
    }

    /**
     * Return the NIC list for the PrimeFaces table
     *
     * @param first first result of the query (the NIC id)
     * @param pageSize the page size
     * @param sortField the sort field of the query
     * @param sortOrder the sort order of the query
     * @param filters the provided filters
     *
     * @return queried NIC list
     */
    @Override
    public List<NIC> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        this.setPageSize(pageSize);
        paginate(first,sortField,sortOrder,filters);
        this.setRowCount(rowCount);
        return pageItems;
    }
}