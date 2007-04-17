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
 * The <code>QueryContainer</code> is the specialized form of Container which
 * is Ordered and Indexed. This is used to represent the contents of relational
 * database tables accessed through the JDBC Connection in the Toolkit Table.
 * This creates Items based on the queryStatement provided to the container.
 * </p>
 * 
 * <p>
 * The <code>QueryContainer</code> can be visualized as a representation of a
 * relational database table.Each Item in the container represents the row
 * fetched by the query.All cells in a column have same data type and the data
 * type information is retrieved from the metadata of the resultset.
 * </p>
 * 
 * <p>
 * Note : If data in the tables gets modified, Container will not get reflected
 * with the updates, we have to explicity invoke QueryContainer.refresh method.
 * {@link com.itmill.toolkit.data.util.QueryContainer#refresh() refresh()}
 * </p>
 * 
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

	private String queryStatement;

	private Connection connection;

	private ResultSet result;

	private Collection propertyIds;

	private HashMap propertyTypes = new HashMap();

	private int size = -1;

	private Statement statement;

	/**
	 * Constructs new <code>QueryContainer</code> with the specified
	 * <code>queryStatement</code>.
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
	 * Constructs new <code>QueryContainer</code> with the specified
	 * queryStatement using the default resultset type and default resultset
	 * concurrency.
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
	 * Fills the Container with the items and properties. Invoked by the
	 * constructor.
	 * 
	 * @throws SQLException
	 *             when parameter initialization fails.
	 * @see QueryContainer#QueryContainer(String, Connection, int, int).
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
	 * Restores items in the container. This method will update the latest data
	 * to the container.
	 * </p>
	 * Note: This method should be used to update the container with the latest
	 * items.
	 * 
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
	 * Releases and nullifies the <code>statement</code>.
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
	 * Gets the Item with the given Item ID from the Container.
	 * 
	 * @param id
	 *            ID of the Item to retrieve
	 * @return Item Id.
	 */

	public Item getItem(Object id) {
		return new Row(id);
	}

	/**
	 * Gets the collection of propertyId from the Container.
	 * 
	 * @return Collection of Property ID.
	 */

	public Collection getContainerPropertyIds() {
		return propertyIds;
	}

	/**
	 * Gets an collection of all the item IDs in the container.
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
	 * Gets the property identified by the given itemId and propertyId from the
	 * container. If the container does not contain the property
	 * <code>null</code> is returned.
	 * 
	 * @param itemId
	 *            ID of the Item which contains the Property
	 * @param propertyId
	 *            ID of the Property to retrieve
	 * 
	 * @return Property with the given ID if exists; <code>null</code>
	 *         otherwise.
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
	 * Gets the data type of all properties identified by the given type ID.
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
	 * Gets the number of items in the container.
	 * 
	 * @return the number of items in the container.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if the list contains the specified Item.
	 * 
	 * @param id
	 *            ID the of Item to be tested.
	 * @return <code>true</code> if given id is in the container;
	 *         <code>false</code> otherwise.
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
	 * Creates new Item with the given ID into the Container.
	 * 
	 * @param itemId
	 *            ID of the Item to be created.
	 * 
	 * @return Created new Item, or <code>null</code> if it fails.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addItem method is not supported.
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a new Item into the Container, and assign it an ID.
	 * 
	 * @return ID of the newly created Item, or <code>null</code> if it fails.
	 * @throws UnsupportedOperationException
	 *             if the addItem method is not supported.
	 */
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the Item identified by ItemId from the Container.
	 * 
	 * @param itemId
	 *            ID of the Item to remove.
	 * @return <code>true</code> if the operation succeeded;
	 *         <code>false</code> otherwise.
	 * @throws UnsupportedOperationException
	 *             if the removeItem method is not supported.
	 */
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds new Property to all Items in the Container.
	 * 
	 * @param propertyId
	 *            ID of the Property
	 * @param type
	 *            Data type of the new Property
	 * @param defaultValue
	 *            The value all created Properties are initialized to.
	 * @return <code>true</code> if the operation succeeded;
	 *         <code>false</code> otherwise.
	 * @throws UnsupportedOperationException
	 *             if the addContainerProperty method is not supported.
	 */
	public boolean addContainerProperty(Object propertyId, Class type,
			Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes a Property specified by the given Property ID from the Container.
	 * 
	 * @param propertyId
	 *            ID of the Property to remove
	 * @return <code>true</code> if the operation succeeded;
	 *         <code>false</code> otherwise.
	 * @throws UnsupportedOperationException
	 *             if the removeContainerProperty method is not supported.
	 */
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes all Items from the Container.
	 * 
	 * @return <code>true</code> if the operation succeeded;
	 *         <code>false</code> otherwise.
	 * @throws UnsupportedOperationException
	 *             if the removeAllItems method is not supported.
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds new item after the given item.
	 * 
	 * @param previousItemId
	 *            Id of the previous item in ordered container.
	 * @param newItemId
	 *            Id of the new item to be added.
	 * @return Returns new item or <code>null</code> if the operation fails.
	 * @throws UnsupportedOperationException
	 *             if the addItemAfter method is not supported.
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds new item after the given item.
	 * 
	 * @param previousItemId
	 *            Id of the previous item in ordered container.
	 * @return Returns item id created new item or <code>null</code> if the
	 *         operation fails.
	 * @throws UnsupportedOperationException
	 *             if the addItemAfter method is not supported.
	 */
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns id of first item in the Container.
	 * 
	 * @return ID of the first Item in the list.
	 */
	public Object firstItemId() {
		if (size < 1)
			return null;
		return new Integer(1);
	}

	/**
	 * Returns <code>true</code> if given id is first id at first index.
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 */
	public boolean isFirstId(Object id) {
		return size > 0 && (id instanceof Integer)
				&& ((Integer) id).intValue() == 1;
	}

	/**
	 * Returns <code>true</code> if given id is last id at last index.
	 * 
	 * @param id
	 *            ID of an Item in the Container
	 * 
	 */
	public boolean isLastId(Object id) {
		return size > 0 && (id instanceof Integer)
				&& ((Integer) id).intValue() == size;
	}

	/**
	 * Returns id of last item in the Container.
	 * 
	 * @return ID of the last Item.
	 */
	public Object lastItemId() {
		if (size < 1)
			return null;
		return new Integer(size);
	}

	/**
	 * Returns id of next item in container at next index.
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 * @return ID of the next Item or null.
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
	 * Returns id of previous item in container at previous index.
	 * 
	 * @param id
	 *            ID of an Item in the Container.
	 * @return ID of the previous Item or null.
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
	 * The <code>Row</code> class implements methods of Item.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @since 4.0
	 */
	class Row implements Item {

		Object id;

		private Row(Object rowId) {
			id = rowId;
		}

		/**
		 * Adds the item property.
		 * 
		 * @param id
		 *            ID of the new Property.
		 * @param property
		 *            Property to be added and associated with ID.
		 * @return <code>true</code> if the operation succeeded;
		 *         <code>false</code> otherwise.
		 * @throws UnsupportedOperationException
		 *             if the addItemProperty method is not supported.
		 */
		public boolean addItemProperty(Object id, Property property)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		/**
		 * Gets the property corresponding to the given property ID stored in
		 * the Item.
		 * 
		 * @param propertyId
		 *            identifier of the Property to get
		 * @return the Property with the given ID or <code>null</code>
		 */
		public Property getItemProperty(Object propertyId) {
			return getContainerProperty(id, propertyId);
		}

		/**
		 * Gets the collection of property IDs stored in the Item.
		 * 
		 * @return unmodifiable collection containing IDs of the Properties
		 *         stored the Item.
		 */
		public Collection getItemPropertyIds() {
			return propertyIds;
		}

		/**
		 * Removes given item property.
		 * 
		 * @param id
		 *            ID of the Property to be removed.
		 * @return <code>true</code> if the item property is removed;
		 *         <code>false</code> otherwise.
		 * @throws UnsupportedOperationException
		 *             if the removeItemProperty is not supported.
		 */
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Closes the statement.
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
	 * Adds the given item at the position of given index.
	 * 
	 * @param index
	 *            Index to add the new item.
	 * @param newItemId
	 *            Id of the new item to be added.
	 * @return new item or <code>null</code> if the operation fails.
	 * @throws UnsupportedOperationException
	 *             if the addItemAt is not supported.
	 */
	public Item addItemAt(int index, Object newItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds item at the position of provided index in the container.
	 * 
	 * @param index
	 *            Index to add the new item.
	 * @return item id created new item or <code>null</code> if the operation
	 *         fails.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the addItemAt is not supported.
	 */

	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the Index id in the container.
	 * 
	 * @param index
	 *            Index Id.
	 * @return ID in the given index.
	 */
	public Object getIdByIndex(int index) {
		if (size < 1 || index < 0 || index >= size)
			return null;
		return new Integer(index + 1);
	}

	/**
	 * Gets the index of the Item corresponding to id in the container.
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
