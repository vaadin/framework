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

/**
 * <p>
 * The <code>QueryContainer</code> is the specialized form of
 * Container which is Ordered and Indexed. This is used to represent the
 * contents of relational database tables accessed through the JDBC Connection
 * in the Toolkit Table. This creates Items based on the queryStatement provided
 * to the container.
 * </p>
 * 
 * <p>
 * The <code>QueryContainer</code> can be visualized as a representation of a
 * relational database table.Each Item in the container represents the
 * row fetched by the query.All cells in a column have same data type and the
 * data type information is retrieved from the metadata of the resultset.
 * </p>
 * 
 * <p>
 * Note : If data in the tables gets modified, Container will not get reflected
 * with the updates, we have to explicity invoke QueryContainer.refresh method.
 * {@link com.itmill.toolkit.data.util.QueryContainer#refresh() refresh()}
 * </p>
 * @see com.itmill.toolkit.data.Container
 * 
 * @author IT Mill Ltd.
 * @version
 * @since 4.0
 */

public class QueryContainer implements Container, Container.Ordered,
		Container.Indexed {

	// default ResultSet type
	public static final int DEFAULT_RESULTSET_TYPE = ResultSet.TYPE_SCROLL_INSENSITIVE;

	// default ResultSet concurrency
	public static final int DEFAULT_RESULTSET_CONCURRENCY = ResultSet.CONCUR_READ_ONLY;

	private int resultSetType = DEFAULT_RESULTSET_TYPE;

	private int resultSetConcurrency = DEFAULT_RESULTSET_CONCURRENCY;

	String queryStatement;

	Connection connection;

	ResultSet result;

	Collection propertyIds;

	HashMap propertyTypes = new HashMap();

	int size = -1;

	Statement statement;

	/**
	 * Constructs a new <code>QueryContainer</code> with the specified
	 * queryStatement
	 * 
	 * @param queryStatement
	 *            Database query
	 * @param connection
	 *            Connection object
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @throws SQLException
	 *             when database operation fails
	 */
	public QueryContainer(String queryStatement, Connection connection,
			int resultSetType, int resultSetConcurrency) throws SQLException {
		this.queryStatement = queryStatement;
		this.connection = connection;
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
		init();
	}

	/**
	 * Constructs a new <code>QueryContainer</code> with the specified  
	 * queryStatement using the default resultset type and default resultset concurrency 
	 * 
	 * @param queryStatement
	 *            Database query
	 * @param connection
	 *            Connection object
	 * @see QueryContainer#DEFAULT_RESULTSET_TYPE
	 * @see QueryContainer#DEFAULT_RESULTSET_CONCURRENCY
	 * @throws SQLException
	 *             when database operation fails
	 */
	public QueryContainer(String queryStatement, Connection connection)
			throws SQLException {
		this(queryStatement, connection, DEFAULT_RESULTSET_TYPE,
				DEFAULT_RESULTSET_CONCURRENCY);
	}
	
	/**
	 * <p>
	 * The <code>init</code> method s invoked by the constructor. This 
	 * method fills the container with the items and properties
	 * </p>
	 */
	private void init() throws SQLException {
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

	/**
	 * <p>
	 * The <code>refresh</code> method refreshes the items in the container.
	 * This method will update the latest data to the container. 
	 * </p>
	 * Note : This method should be used to update the container with the latest items.
	 * @throws SQLException
	 *             when database operation fails
	 * 
	 */

	public void refresh() throws SQLException {
		close();
		statement = connection.createStatement(resultSetType,
				resultSetConcurrency);
		result = statement.executeQuery(queryStatement);
		result.last();
		size = result.getRow();
	}

	/**
	 * The <code>close</code> method closes the statement and nullify the
	 * statement.
	 * 
	 * @throws SQLException
	 *             when database operation fails
	 */

	public void close() throws SQLException {
		if (statement != null)
			statement.close();
		statement = null;
	}

	/**
	 * The <code>getItem</code> method gets the Item with the given Item ID
	 * from the Container.
	 * 
	 * @param id
	 *            ID of the Item to retrieve
	 * @return Item Id.
	 */

	public Item getItem(Object id) {
		return new Row(id);
	}

	/**
	 * <p>
	 * The <code>getContainerPropertyIds</code> method gets the collection of
	 * propertyId from the Container.
	 * </p>
	 * 
	 * @return Collection of Property ID.
	 */

	public Collection getContainerPropertyIds() {
		return propertyIds;
	}

	/**
	 * <p>
	 * The <code>getItemIds</code> method gets the collection of all the item id in the container.
	 * </p>
	 * 
	 * @return collection of Item IDs
	 */
	public Collection getItemIds() {
		Collection c = new ArrayList(size);
		for (int i = 1; i <= size; i++)
			c.add(new Integer(i));
		return c;
	}

	/**
	 * <p>
	 * The <code>getContainerProperty</code> method gets the property
	 * identified by the given itemId and propertyId from the container.If the
	 * container does not contain the property <code>null</code> is returned.
	 * </p>
	 * 
	 * @param itemId
	 *            ID of the Item which contains the Property
	 * @param propertyId
	 *            ID of the Property to retrieve
	 * 
	 * @return Property with the given ID or <code>null</code>
	 */

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

		// Handle also null values from the database
		return new ObjectProperty(value != null ? value : new String(""));
	}

	/**
	 * <p>
	 * The <code>getType</code> gets the data type of all properties
	 * identified by the given type ID.
	 * </p>
	 * 
	 * @param id
	 *            ID identifying the Properties
	 * 
	 * @return data type of the Properties
	 */

	public Class getType(Object id) {
		return (Class) propertyTypes.get(id);
	}

	/**
	 * The <code>size</code> method gets the number of items in the container.
	 * 
	 * @return the number of items in the container.
	 */

	public int size() {
		return size;
	}

	/**
	 * <p>
	 * The <code>containsId</code> method returns <code>true</code> if given
	 * id is there in container else <code>false</code>.
	 * <p>
	 * 
	 * @param id
	 *            ID the of Item to be tested.
	 */
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

	/**
	 * The <code>addItem</code> method creates a new Item with the given ID
	 * into the Container.
	 * 
	 * @param arg0
	 *            ID of the Item to be created.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addItem method is not supported.
	 */
	public Item addItem(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addItem</code> method creates a new Item into the Container,
	 * and assign it an ID.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addItem method is not supported.
	 */
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addItem</code> method removes the Item identified by ItemId
	 * from the Container.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the removeItem method is not supported.
	 */
	public boolean removeItem(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addContainerProperty</code> method adds a new Property to all
	 * Items in the Container.
	 * 
	 * @param arg0
	 *            ID of the Property
	 * @param arg1
	 *            Data type of the new Property
	 * @param arg2
	 *            The value all created Properties are initialized to
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addContainerProperty method is not supported.
	 */
	public boolean addContainerProperty(Object arg0, Class arg1, Object arg2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>removeContainerProperty</code> method removes a Property
	 * specified by the given Property ID from the Container.
	 * 
	 * @param arg0
	 *            ID of the Property to remove
	 * 
	 * @throws UnsupportedOperationException
	 *             if the removeContainerProperty method is not supported.
	 */
	public boolean removeContainerProperty(Object arg0)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>removeAllItems</code> method removes all Items from the
	 * Container
	 * 
	 * @throws UnsupportedOperationException
	 *             if the removeAllItems method is not supported.
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addItemAfter</code> method add new item after the given item.
	 * 
	 * @param arg0
	 *            Id of the previous item in ordered container.
	 * @param arg1
	 *            Id of the new item to be added.
	 * @throws UnsupportedOperationException
	 *             if the addItemAfter method is not supported.
	 */
	public Item addItemAfter(Object arg0, Object arg1)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addItemAfter</code> method add new item after the given item.
	 * 
	 * @param arg0
	 *            Id of the previous item in ordered container.
	 */
	public Object addItemAfter(Object arg0)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * The <code>firstItemId</code> method returns id of first item in the
	 * Container.
	 * </p>
	 */
	public Object firstItemId() {
		if (size < 1)
			return null;
		return new Integer(1);
	}

	/**
	 * <p>
	 * The <code>isFirstId</code> method return <code>true</code> if given
	 * id is first id at first index.
	 * </p>
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 */
	public boolean isFirstId(Object id) {
		return size > 0 && (id instanceof Integer)
				&& ((Integer) id).intValue() == 1;
	}

	/**
	 * <p>
	 * The <code>isLastId</code> method return <code>true</code> if given id
	 * is last id at last index
	 * </p>
	 * 
	 * @param id
	 *            ID of an Item in the Container
	 */
	public boolean isLastId(Object id) {
		return size > 0 && (id instanceof Integer)
				&& ((Integer) id).intValue() == size;
	}

	/**
	 * <p>
	 * The <code>lastItemId</code> method returns id of last item in the
	 * Container.
	 * </p>
	 */
	public Object lastItemId() {
		if (size < 1)
			return null;
		return new Integer(size);
	}

	/**
	 * <p>
	 * The <code>nextItemId</code> method return id of next item in container
	 * at next index.
	 * </p>
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 */
	public Object nextItemId(Object id) {
		if (size < 1 || !(id instanceof Integer))
			return null;
		int i = ((Integer) id).intValue();
		if (i >= size)
			return null;
		return new Integer(i + 1);
	}

	/**
	 * <p>
	 * The <code>prevItemId</code> method return id of previous item in
	 * container at previous index.
	 * </p>
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 */
	public Object prevItemId(Object id) {
		if (size < 1 || !(id instanceof Integer))
			return null;
		int i = ((Integer) id).intValue();
		if (i <= 1)
			return null;
		return new Integer(i - 1);
	}

	/**
	 * <p>
	 * The <code>Row</code> class implements methods of Item.
	 * </p>
	 */
	/** Query result row */
	class Row implements Item {

		Object id;

		private Row(Object rowId) {
			id = rowId;
		}

		/**
		 * <p>
		 * The <code>addItemProperty</code> method adds the item property.
		 * </p>
		 * 
		 * @param arg0
		 *            ID of the new Property.
		 * @param arg1
		 *            Property to be added and associated with ID.
		 * @throws UnsupportedOperationException
		 *             if the addItemProperty method is not supported.
		 */
		public boolean addItemProperty(Object arg0, Property arg1)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		/**
		 * <p>
		 * The <code>getItemProperty</code> gets the property corresponding to
		 * the given property ID stored in the Item. If the Item does not
		 * contain the Property, <code>null</code> is returned.
		 * </p>
		 * 
		 * @param id
		 *            identifier of the Property to get
		 * @return the Property with the given ID or <code>null</code>
		 */
		public Property getItemProperty(Object propertyId) {
			return getContainerProperty(id, propertyId);
		}

		/**
		 * <p>
		 * The <code>getItemPropertyIds</code> method gets the collection of
		 * property IDs stored in the Item.
		 * </p>
		 * 
		 * @return unmodifiable collection containing IDs of the Properties
		 *         stored the Item.
		 */
		public Collection getItemPropertyIds() {
			return propertyIds;
		}

		/**
		 * <p>
		 * The <code>removeItemProperty</code> method removes given item
		 * property return <code>true</code> if the item property is removed
		 * <code>false</code> if not.
		 * </p>
		 * 
		 * @throws UnsupportedOperationException
		 *             if the removeItemProperty is not supported.
		 */
		public boolean removeItemProperty(Object arg0)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * The <code>finalize</code> method closes the statement.
	 * 
	 * @see #close()
	 */
	public void finalize() {
		try {
			close();
		} catch (SQLException ignored) {

		}
	}

	/**
	 * The <code>addItemAt</code> method adds the given item at the position
	 * of given index.
	 * 
	 * @param arg0
	 *            Index to add the new item.
	 * @param arg1
	 *            Id of the new item to be added.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public Item addItemAt(int arg0, Object arg1)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>addItemAt</code> method adds the item at the position of
	 * provided index in the container.
	 * 
	 * @param arg0
	 *            Index to add the new item.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addItemAt is not supported.
	 */

	public Object addItemAt(int arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The <code>getIdByIndex</code> method gets the Index id in the
	 * container.
	 * 
	 * @param index
	 *            Index Id.
	 */
	public Object getIdByIndex(int index) {
		if (size < 1 || index < 0 || index >= size)
			return null;
		return new Integer(index + 1);
	}

	/**
	 * The <code>indexOfId</code> gets the index of the Item corresponding to
	 * <code>id</code> in the container.
	 * 
	 * @param id
	 *            ID of an Item in the Container
	 * @return index of the Item, or -1 if the Container does not include the
	 *         Item
	 */

	public int indexOfId(Object id) {
		if (size < 1 || !(id instanceof Integer))
			return -1;
		int i = ((Integer) id).intValue();
		if (i >= size || i < 1)
			return -1;
		return i - 1;
	}

}
