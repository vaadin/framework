/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.data.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ObjectProperty;

/** SQL query container.
 * Implementation of container interface for SQL tables accessed through
 * JDBC connection.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 4.0
 */
public class QueryContainer implements Container, Container.Ordered, Container.Indexed {

    String queryStatement;

    Connection connection;

    ResultSet result;

    Collection propertyIds;

    HashMap propertyTypes = new HashMap();

    int size = -1;

    Statement statement;

    /**
     * Constructor for Query.
     */
    public QueryContainer(String queryStatement, Connection connection)
            throws SQLException {
        this.connection = connection;
        this.queryStatement = queryStatement;
        refresh();
        ResultSetMetaData metadata;
        metadata = result.getMetaData();
        int count = metadata.getColumnCount();
        ArrayList list = new ArrayList(count);
        for (int i = 1; i <= count; i++) {
            String columnName = metadata.getColumnName(i);
            list.add(columnName);
            Property p = getContainerProperty(new Integer(1), columnName);
            propertyTypes.put(columnName, p == null ? Object.class : p
                    .getType());
        }
        propertyIds = Collections.unmodifiableCollection(list);
    }

    public void refresh() throws SQLException {
        close();
        statement = connection.createStatement();
        result = statement.executeQuery(queryStatement);
        result.last();
        size = result.getRow();
    }

    public void close() throws SQLException {
        if (statement != null)
            statement.close();
        statement = null;
    }

    public Item getItem(Object id) {
        return new Row(id);
    }

    public Collection getContainerPropertyIds() {
        return propertyIds;
    }

    public Collection getItemIds() {
        Collection c = new ArrayList(size);
        for (int i = 1; i <= size; i++)
            c.add(new Integer(i));
        return c;
    }

    public synchronized Property getContainerProperty(Object itemId,
            Object propertyId) {
        if (!(itemId instanceof Integer && propertyId instanceof String))
            return null;
        Object value;
        try {
            result.absolute(((Integer) itemId).intValue());
            value = result.getObject((String) propertyId);
        } catch (Exception e) {
            return null;
        }

        // Also deal with null values from the DB
        return new ObjectProperty(value != null ? value : new String(""));
    }

    public Class getType(Object id) {
        return (Class) propertyTypes.get(id);
    }

    public int size() {
        return size;
    }

    public boolean containsId(Object id) {
        if (!(id instanceof Integer))
            return false;
        int i = ((Integer) id).intValue();
        if (i < 1)
            return false;
        if (i > size)
            return false;
        return true;
    }

    public Item addItem(Object arg0) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean removeItem(Object arg0) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean addContainerProperty(Object arg0, Class arg1, Object arg2)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean removeContainerProperty(Object arg0)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItemAfter(Object arg0, Object arg1)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object addItemAfter(Object arg0)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object firstItemId() {
        if (size < 1)
            return null;
        return new Integer(1);
    }

    public boolean isFirstId(Object id) {
        return size > 0 && (id instanceof Integer)
                && ((Integer) id).intValue() == 1;
    }

    public boolean isLastId(Object id) {
        return size > 0 && (id instanceof Integer)
                && ((Integer) id).intValue() == size;
    }

    public Object lastItemId() {
        if (size < 1)
            return null;
        return new Integer(size);
    }

    public Object nextItemId(Object id) {
        if (size < 1 || !(id instanceof Integer))
            return null;
        int i = ((Integer) id).intValue();
        if (i >= size)
            return null;
        return new Integer(i + 1);
    }

    public Object prevItemId(Object id) {
        if (size < 1 || !(id instanceof Integer))
            return null;
        int i = ((Integer) id).intValue();
        if (i <= 1)
            return null;
        return new Integer(i - 1);
    }

    /** Query result row */
    class Row implements Item {

        Object id;

        private Row(Object rowId) {
            id = rowId;
        }

        public boolean addItemProperty(Object arg0, Property arg1)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Property getItemProperty(Object propertyId) {
            return getContainerProperty(id, propertyId);
        }

        public Collection getItemPropertyIds() {
            return propertyIds;
        }

        public boolean removeItemProperty(Object arg0)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

    }

    public void finalize() {
        try {
            close();
        } catch (SQLException ignored) {

        }
    }

    public Item addItemAt(int arg0, Object arg1)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object addItemAt(int arg0) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object getIdByIndex(int index) {
        if (size < 1 || index < 0 || index >= size)
            return null;
        return new Integer(index + 1);
    }

    public int indexOfId(Object id) {
        if (size < 1 || !(id instanceof Integer))
            return -1;
        int i = ((Integer) id).intValue();
        if (i >= size || i < 1)
            return -1;
        return i - 1;
    }

}
