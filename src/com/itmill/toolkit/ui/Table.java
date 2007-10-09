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

package com.itmill.toolkit.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ContainerOrderedWrapper;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.terminal.KeyMapper;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Sizeable;

/**
 * <code>TableComponent</code> is used for representing data or components in
 * pageable and selectable table.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Table extends Select implements Action.Container,
		Container.Ordered, Container.Sortable, Sizeable {

	private static final int CELL_KEY = 0;

	private static final int CELL_HEADER = 1;

	private static final int CELL_ICON = 2;

	private static final int CELL_ITEMID = 3;

	private static final int CELL_FIRSTCOL = 4;

	/**
	 * Width of the table or -1 if unspecified.
	 */
	private int width = -1;

	/**
	 * Height of the table or -1 if unspecified.
	 */
	private int height = -1;

	/**
	 * Width unit.
	 */
	private int widthUnit = Sizeable.UNITS_PIXELS;

	/**
	 * Height unit.
	 */
	private int heightUnit = Sizeable.UNITS_PIXELS;

	/**
	 * Left column alignment. <b>This is the default behaviour. </b>
	 */
	public static final String ALIGN_LEFT = "b";

	/**
	 * Center column alignment.
	 */
	public static final String ALIGN_CENTER = "c";

	/**
	 * Right column alignment.
	 */
	public static final String ALIGN_RIGHT = "e";

	/**
	 * Column header mode: Column headers are hidden. <b>This is the default
	 * behaviour. </b>
	 */
	public static final int COLUMN_HEADER_MODE_HIDDEN = -1;

	/**
	 * Column header mode: Property ID:s are used as column headers.
	 */
	public static final int COLUMN_HEADER_MODE_ID = 0;

	/**
	 * Column header mode: Column headers are explicitly specified with
	 * <code>setColumnHeaders</code>.
	 */
	public static final int COLUMN_HEADER_MODE_EXPLICIT = 1;

	/**
	 * Column header mode: Column headers are explicitly specified with
	 * <code>setColumnHeaders</code>
	 */
	public static final int COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID = 2;

	/**
	 * Row caption mode: The row headers are hidden. <b>This is the default
	 * mode. </b>
	 */
	public static final int ROW_HEADER_MODE_HIDDEN = -1;

	/**
	 * Row caption mode: Items Id-objects toString is used as row caption.
	 */
	public static final int ROW_HEADER_MODE_ID = Select.ITEM_CAPTION_MODE_ID;

	/**
	 * Row caption mode: Item-objects toString is used as row caption.
	 */
	public static final int ROW_HEADER_MODE_ITEM = Select.ITEM_CAPTION_MODE_ITEM;

	/**
	 * Row caption mode: Index of the item is used as item caption. The index
	 * mode can only be used with the containers implementing Container.Indexed
	 * interface.
	 */
	public static final int ROW_HEADER_MODE_INDEX = Select.ITEM_CAPTION_MODE_INDEX;

	/**
	 * Row caption mode: Item captions are explicitly specified.
	 */
	public static final int ROW_HEADER_MODE_EXPLICIT = Select.ITEM_CAPTION_MODE_EXPLICIT;

	/**
	 * Row caption mode: Item captions are read from property specified with
	 * <code>setItemCaptionPropertyId</code>.
	 */
	public static final int ROW_HEADER_MODE_PROPERTY = Select.ITEM_CAPTION_MODE_PROPERTY;

	/**
	 * Row caption mode: Only icons are shown, the captions are hidden.
	 */
	public static final int ROW_HEADER_MODE_ICON_ONLY = Select.ITEM_CAPTION_MODE_ICON_ONLY;

	/**
	 * Row caption mode: Item captions are explicitly specified, but if the
	 * caption is missing, the item id objects <code>toString()</code> is used
	 * instead.
	 */
	public static final int ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID = Select.ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID;

	/* Private table extensions to Select *********************************** */

	/**
	 * True if column collapsing is allowed.
	 */
	private boolean columnCollapsingAllowed = false;

	/**
	 * True if reordering of columns is allowed on the client side.
	 */
	private boolean columnReorderingAllowed = false;

	/**
	 * Keymapper for column ids.
	 */
	private KeyMapper columnIdMap = new KeyMapper();

	/**
	 * Holds visible column propertyIds - in order.
	 */
	private LinkedList visibleColumns = new LinkedList();

	/**
	 * Holds propertyIds of currently collapsed columns.
	 */
	private HashSet collapsedColumns = new HashSet();

	/**
	 * Holds headers for visible columns (by propertyId).
	 */
	private HashMap columnHeaders = new HashMap();

	/**
	 * Holds icons for visible columns (by propertyId).
	 */
	private HashMap columnIcons = new HashMap();

	/**
	 * Holds alignments for visible columns (by propertyId).
	 */
	private HashMap columnAlignments = new HashMap();

	/**
	 * Holds column widths in pixels for visible columns (by propertyId).
	 */
	private HashMap columnWidths = new HashMap();

	/**
	 * Holds value of property pageLength. 0 disables paging.
	 */
	private int pageLength = 15;

	/**
	 * Id the first item on the current page.
	 */
	private Object currentPageFirstItemId = null;

	/**
	 * Index of the first item on the current page.
	 */
	private int currentPageFirstItemIndex = 0;

	/**
	 * Holds value of property pageBuffering.
	 */
	private boolean pageBuffering = false;

	/**
	 * Holds value of property selectable.
	 */
	private boolean selectable = false;

	/**
	 * Holds value of property columnHeaderMode.
	 */
	private int columnHeaderMode = COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID;

	/**
	 * True iff the row captions are hidden.
	 */
	private boolean rowCaptionsAreHidden = true;

	/**
	 * Page contents buffer used in buffered mode.
	 */
	private Object[][] pageBuffer = null;

	/**
	 * List of properties listened - the list is kept to release the listeners
	 * later.
	 */
	private LinkedList listenedProperties = null;

	/**
	 * List of visible components - the is used for needsRepaint calculation.
	 */
	private LinkedList visibleComponents = null;

	/**
	 * List of action handlers.
	 */
	private LinkedList actionHandlers = null;

	/**
	 * Action mapper.
	 */
	private KeyMapper actionMapper = null;

	/**
	 * Table cell editor factory.
	 */
	private FieldFactory fieldFactory = new BaseFieldFactory();

	/**
	 * Is table editable.
	 */
	private boolean editable = false;

	/**
	 * Current sorting direction.
	 */
	private boolean sortAscending = true;

	/**
	 * Currently table is sorted on this propertyId.
	 */
	private Object sortContainerPropertyId = null;

	/**
	 * Is table sorting disabled alltogether; even if some of the properties
	 * would be sortable.
	 */
	private boolean sortDisabled = false;

	/**
	 * Number of rows explicitly requested by the client to be painted on next
	 * paint. This is -1 if no request by the client is made. Painting the
	 * component will automatically reset this to -1.
	 */
	private int reqRowsToPaint = -1;

	/**
	 * Index of the first rows explicitly requested by the client to be painted.
	 * This is -1 if no request by the client is made. Painting the component
	 * will automatically reset this to -1.
	 */
	private int reqFirstRowToPaint = -1;

	/* Table constructors *************************************************** */

	/**
	 * Creates a new empty table.
	 */
	public Table() {
		setRowHeaderMode(ROW_HEADER_MODE_HIDDEN);
	}

	/**
	 * Creates a new empty table with caption.
	 * 
	 * @param caption
	 */
	public Table(String caption) {
		this();
		setCaption(caption);
	}

	/**
	 * Creates a new table with caption and connect it to a Container.
	 * 
	 * @param caption
	 * @param dataSource
	 */
	public Table(String caption, Container dataSource) {
		this();
		setCaption(caption);
		setContainerDataSource(dataSource);
	}

	/* Table functionality ************************************************** */

	/**
	 * Gets the array of visible column property id:s.
	 * 
	 * <p>
	 * The columns are show in the order of their appearance in this array.
	 * </p>
	 * 
	 * @return the Value of property availableColumns.
	 */
	public Object[] getVisibleColumns() {
		if (this.visibleColumns == null) {
			return null;
		}
		return this.visibleColumns.toArray();
	}

	/**
	 * Sets the array of visible column property id:s.
	 * 
	 * <p>
	 * The columns are show in the order of their appearance in this array.
	 * </p>
	 * 
	 * @param visibleColumns
	 *            the Array of shown property id:s.
	 */
	public void setVisibleColumns(Object[] visibleColumns) {

		// Visible columns must exist
		if (visibleColumns == null)
			throw new NullPointerException(
					"Can not set visible columns to null value");

		// Checks that the new visible columns contains no nulls and properties
		// exist
		Collection properties = getContainerPropertyIds();
		for (int i = 0; i < visibleColumns.length; i++) {
			if (visibleColumns[i] == null)
				throw new NullPointerException("Properties must be non-nulls");
			else if (!properties.contains(visibleColumns[i]))
				throw new IllegalArgumentException(
						"Properties must exist in the Container, missing property: "
								+ visibleColumns[i]);
		}

		// If this is called befor the constructor is finished, it might be
		// uninitialized
		LinkedList newVC = new LinkedList();
		for (int i = 0; i < visibleColumns.length; i++) {
			newVC.add(visibleColumns[i]);
		}

		// Removes alignments, icons and headers from hidden columns
		if (this.visibleColumns != null)
			for (Iterator i = this.visibleColumns.iterator(); i.hasNext();) {
				Object col = i.next();
				if (!newVC.contains(col)) {
					setColumnHeader(col, null);
					setColumnAlignment(col, null);
					setColumnIcon(col, null);
				}
			}

		this.visibleColumns = newVC;

		// Assures visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the headers of the columns.
	 * 
	 * <p>
	 * The headers match the property id:s given my the set visible column
	 * headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode to show the
	 * headers. In the defaults mode any nulls in the headers array are replaced
	 * with id.toString() outputs when rendering.
	 * </p>
	 * 
	 * @return the Array of column headers.
	 */
	public String[] getColumnHeaders() {
		if (this.columnHeaders == null) {
			return null;
		}
		String[] headers = new String[this.visibleColumns.size()];
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext(); i++) {
			headers[i] = (String) this.columnHeaders.get(it.next());
		}
		return headers;
	}

	/**
	 * Sets the headers of the columns.
	 * 
	 * <p>
	 * The headers match the property id:s given my the set visible column
	 * headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode to show the
	 * headers. In the defaults mode any nulls in the headers array are replaced
	 * with id.toString() outputs when rendering.
	 * </p>
	 * 
	 * @param columnHeaders
	 *            the Array of column headers that match the
	 *            <code>getVisibleColumns</code> method.
	 */
	public void setColumnHeaders(String[] columnHeaders) {

		if (columnHeaders.length != this.visibleColumns.size())
			throw new IllegalArgumentException(
					"The length of the headers array must match the number of visible columns");

		this.columnHeaders.clear();
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
				&& i < columnHeaders.length; i++) {
			this.columnHeaders.put(it.next(), columnHeaders[i]);
		}

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the icons of the columns.
	 * 
	 * <p>
	 * The icons in headers match the property id:s given my the set visible
	 * column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode to show the
	 * headers with icons.
	 * </p>
	 * 
	 * @return the Array of icons that match the <code>getVisibleColumns</code>.
	 */
	public Resource[] getColumnIcons() {
		if (this.columnIcons == null) {
			return null;
		}
		Resource[] icons = new Resource[this.visibleColumns.size()];
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext(); i++) {
			icons[i] = (Resource) this.columnIcons.get(it.next());
		}

		return icons;
	}

	/**
	 * Sets the icons of the columns.
	 * 
	 * <p>
	 * The icons in headers match the property id:s given my the set visible
	 * column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode to show the
	 * headers with icons.
	 * </p>
	 * 
	 * @param columnIcons
	 *            the Array of icons that match the
	 *            <code>getVisibleColumns</code>.
	 */
	public void setColumnIcons(Resource[] columnIcons) {

		if (columnIcons.length != this.visibleColumns.size())
			throw new IllegalArgumentException(
					"The length of the icons array must match the number of visible columns");

		this.columnIcons.clear();
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
				&& i < columnIcons.length; i++) {
			this.columnIcons.put(it.next(), columnIcons[i]);
		}

		// Assure visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the array of column alignments.
	 * 
	 * <p>
	 * The items in the array must match the properties identified by
	 * <code>getVisibleColumns()</code>. The possible values for the
	 * alignments include:
	 * <ul>
	 * <li><code>ALIGN_LEFT</code>: Left alignment</li>
	 * <li><code>ALIGN_CENTER</code>: Centered</li>
	 * <li><code>ALIGN_RIGHT</code>: Right alignment</li>
	 * </ul>
	 * The alignments default to <code>ALIGN_LEFT</code>: any null values are
	 * rendered as align lefts.
	 * </p>
	 * 
	 * @return the Column alignments array.
	 */
	public String[] getColumnAlignments() {
		if (this.columnAlignments == null) {
			return null;
		}
		String[] alignments = new String[this.visibleColumns.size()];
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext(); i++) {
			alignments[i++] = getColumnAlignment(it.next());
		}

		return alignments;
	}

	/**
	 * Sets the column alignments.
	 * 
	 * <p>
	 * The items in the array must match the properties identified by
	 * <code>getVisibleColumns()</code>. The possible values for the
	 * alignments include:
	 * <ul>
	 * <li><code>ALIGN_LEFT</code>: Left alignment</li>
	 * <li><code>ALIGN_CENTER</code>: Centered</li>
	 * <li><code>ALIGN_RIGHT</code>: Right alignment</li>
	 * </ul>
	 * The alignments default to <code>ALIGN_LEFT</code>
	 * </p>
	 * 
	 * @param columnAlignments
	 *            the Column alignments array.
	 */
	public void setColumnAlignments(String[] columnAlignments) {

		if (columnAlignments.length != this.visibleColumns.size())
			throw new IllegalArgumentException(
					"The length of the alignments array must match the number of visible columns");

		// Checks all alignments
		for (int i = 0; i < columnAlignments.length; i++) {
			String a = columnAlignments[i];
			if (a != null && !a.equals(ALIGN_LEFT) && !a.equals(ALIGN_CENTER)
					&& !a.equals(ALIGN_RIGHT))
				throw new IllegalArgumentException("Column " + i
						+ " aligment '" + a + "' is invalid");
		}

		// Resets the alignments
		HashMap newCA = new HashMap();
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
				&& i < columnAlignments.length; i++) {
			newCA.put(it.next(), columnAlignments[i]);
		}
		this.columnAlignments = newCA;

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Sets columns width (in pixels). Theme may not necessary respect very
	 * small or very big values. Setting width to -1 (default) means that theme
	 * will make decision of width.
	 * 
	 * @param columnId
	 *            colunmns property id
	 * @param width
	 *            width to be reserved for colunmns content
	 * @since 4.0.3
	 */
	public void setColumnWidth(Object columnId, int width) {
		columnWidths.put(columnId, new Integer(width));
	}

	/**
	 * Gets the width of column
	 * 
	 * @param propertyId
	 * @return width of colun or -1 when value not set
	 */
	public int getColumnWidth(Object propertyId) {
		Integer value = (Integer) columnWidths.get(propertyId);
		if (value == null)
			return -1;
		return value.intValue();
	}

	/**
	 * Gets the page length.
	 * 
	 * <p>
	 * Setting page length 0 disables paging.
	 * </p>
	 * 
	 * @return the Length of one page.
	 */
	public int getPageLength() {
		return this.pageLength;
	}

	/**
	 * Sets the page length.
	 * 
	 * <p>
	 * Setting page length 0 disables paging. The page length defaults to 15.
	 * </p>
	 * 
	 * @param pageLength
	 *            the Length of one page.
	 */
	public void setPageLength(int pageLength) {
		if (pageLength >= 0 && this.pageLength != pageLength) {
			this.pageLength = pageLength;
			// "scroll" to first row
			this.setCurrentPageFirstItemIndex(0);
			// Assures the visual refresh
			refreshCurrentPage();
		}
	}

	/**
	 * Getter for property currentPageFirstItem.
	 * 
	 * @return the Value of property currentPageFirstItem.
	 */
	public Object getCurrentPageFirstItemId() {

		// Priorise index over id if indexes are supported
		if (items instanceof Container.Indexed) {
			int index = getCurrentPageFirstItemIndex();
			Object id = null;
			if (index >= 0 && index < size())
				id = ((Container.Indexed) items).getIdByIndex(index);
			if (id != null && !id.equals(currentPageFirstItemId))
				currentPageFirstItemId = id;
		}

		// If there is no item id at all, use the first one
		if (currentPageFirstItemId == null)
			currentPageFirstItemId = ((Container.Ordered) items).firstItemId();

		return currentPageFirstItemId;
	}

	/**
	 * Setter for property currentPageFirstItemId.
	 * 
	 * @param currentPageFirstItemId
	 *            the New value of property currentPageFirstItemId.
	 */
	public void setCurrentPageFirstItemId(Object currentPageFirstItemId) {

		// Gets the corresponding index
		int index = -1;
		try {
			index = ((Container.Indexed) items)
					.indexOfId(currentPageFirstItemId);
		} catch (ClassCastException e) {

			// If the table item container does not have index, we have to
			// calculates the index by hand
			Object id = ((Container.Ordered) items).firstItemId();
			while (id != null && !id.equals(currentPageFirstItemId)) {
				index++;
				id = ((Container.Ordered) items).nextItemId(id);
			}
			if (id == null)
				index = -1;
		}

		// If the search for item index was successfull
		if (index >= 0) {
			this.currentPageFirstItemId = currentPageFirstItemId;
			this.currentPageFirstItemIndex = index;
		}

		// Assures the visual refresh
		refreshCurrentPage();

	}

	/**
	 * Gets the icon Resource for the specified column.
	 * 
	 * @param propertyId
	 *            the propertyId indentifying the column.
	 * @return the icon for the specified column; null if the column has no icon
	 *         set, or if the column is not visible.
	 */
	public Resource getColumnIcon(Object propertyId) {
		return (Resource) this.columnIcons.get(propertyId);
	}

	/**
	 * Sets the icon Resource for the specified column.
	 * <p>
	 * Throws IllegalArgumentException if the specified column is not visible.
	 * </p>
	 * 
	 * @param propertyId
	 *            the propertyId identifying the column.
	 * @param icon
	 *            the icon Resource to set.
	 */
	public void setColumnIcon(Object propertyId, Resource icon) {

		if (icon == null)
			this.columnIcons.remove(propertyId);
		else
			this.columnIcons.put(propertyId, icon);

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the header for the specified column.
	 * 
	 * @param propertyId
	 *            the propertyId indentifying the column.
	 * @return the header for the specifed column if it has one.
	 */
	public String getColumnHeader(Object propertyId) {
		if (getColumnHeaderMode() == COLUMN_HEADER_MODE_HIDDEN)
			return null;

		String header = (String) this.columnHeaders.get(propertyId);
		if ((header == null && this.getColumnHeaderMode() == COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID)
				|| this.getColumnHeaderMode() == COLUMN_HEADER_MODE_ID) {
			header = propertyId.toString();
		}

		return header;
	}

	/**
	 * Sets the column header for the specified column;
	 * 
	 * @param propertyId
	 *            the propertyId indentifying the column.
	 * @param header
	 *            the header to set.
	 */
	public void setColumnHeader(Object propertyId, String header) {

		if (header == null) {
			this.columnHeaders.remove(propertyId);
			return;
		}
		this.columnHeaders.put(propertyId, header);

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the specified column's alignment.
	 * 
	 * @param propertyId
	 *            the propertyID identifying the column.
	 * @return the specified column's alignment if it as one; null otherwise.
	 */
	public String getColumnAlignment(Object propertyId) {
		String a = (String) this.columnAlignments.get(propertyId);
		return a == null ? ALIGN_LEFT : a;
	}

	/**
	 * Sets the specified column's alignment.
	 * 
	 * <p>
	 * Throws IllegalArgumentException if the alignment is not one of the
	 * following: ALIGN_LEFT, ALIGN_CENTER or ALIGN_RIGHT
	 * </p>
	 * 
	 * @param propertyId
	 *            the propertyID identifying the column.
	 * @param alignment
	 *            the desired alignment.
	 */
	public void setColumnAlignment(Object propertyId, String alignment) {

		// Checks for valid alignments
		if (alignment != null && !alignment.equals(ALIGN_LEFT)
				&& !alignment.equals(ALIGN_CENTER)
				&& !alignment.equals(ALIGN_RIGHT))
			throw new IllegalArgumentException("Column alignment '" + alignment
					+ "' is not supported.");

		if (alignment == null || alignment.equals(ALIGN_LEFT)) {
			this.columnAlignments.remove(propertyId);
			return;
		}

		this.columnAlignments.put(propertyId, alignment);

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Checks if the specified column is collapsed.
	 * 
	 * @param propertyId
	 *            the propertyID identifying the column.
	 * @return true if the column is collapsed; false otherwise;
	 */
	public boolean isColumnCollapsed(Object propertyId) {
		return collapsedColumns != null
				&& collapsedColumns.contains(propertyId);
	}

	/**
	 * Sets whether the specified column is collapsed or not.
	 * 
	 * 
	 * @param propertyId
	 *            the propertyID identifying the column.
	 * @param collapsed
	 *            the desired collapsedness.
	 * @throws IllegalAccessException
	 */
	public void setColumnCollapsed(Object propertyId, boolean collapsed)
			throws IllegalAccessException {
		if (!this.isColumnCollapsingAllowed()) {
			throw new IllegalAccessException("Column collapsing not allowed!");
		}

		if (collapsed)
			this.collapsedColumns.add(propertyId);
		else
			this.collapsedColumns.remove(propertyId);

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Checks if column collapsing is allowed.
	 * 
	 * @return true if columns can be collapsed; false otherwise.
	 */
	public boolean isColumnCollapsingAllowed() {
		return this.columnCollapsingAllowed;
	}

	/**
	 * Sets whether column collapsing is allowed or not.
	 * 
	 * @param collapsingAllowed
	 *            specifies whether column collapsing is allowed.
	 */
	public void setColumnCollapsingAllowed(boolean collapsingAllowed) {
		this.columnCollapsingAllowed = collapsingAllowed;

		if (!collapsingAllowed)
			collapsedColumns.clear();

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Checks if column reordering is allowed.
	 * 
	 * @return true if columns can be reordered; false otherwise.
	 */
	public boolean isColumnReorderingAllowed() {
		return this.columnReorderingAllowed;
	}

	/**
	 * Sets whether column reordering is allowed or not.
	 * 
	 * @param reorderingAllowed
	 *            specifies whether column reordering is allowed.
	 */
	public void setColumnReorderingAllowed(boolean reorderingAllowed) {
		this.columnReorderingAllowed = reorderingAllowed;

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/*
	 * Arranges visible columns according to given columnOrder. Silently ignores
	 * colimnId:s that are not visible columns, and keeps the internal order of
	 * visible columns left out of the ordering (trailing). Silently does
	 * nothing if columnReordering is not allowed.
	 */
	private void setColumnOrder(Object[] columnOrder) {
		if (columnOrder == null || !this.isColumnReorderingAllowed()) {
			return;
		}
		LinkedList newOrder = new LinkedList();
		for (int i = 0; i < columnOrder.length; i++) {
			if (columnOrder[i] != null
					&& this.visibleColumns.contains(columnOrder[i])) {
				this.visibleColumns.remove(columnOrder[i]);
				newOrder.add(columnOrder[i]);
			}
		}
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext();) {
			Object columnId = it.next();
			if (!newOrder.contains(columnId))
				newOrder.add(columnId);
		}
		this.visibleColumns = newOrder;

		// Assure visual refresh
		refreshCurrentPage();
	}

	/**
	 * Getter for property currentPageFirstItem.
	 * 
	 * @return the Value of property currentPageFirstItem.
	 */
	public int getCurrentPageFirstItemIndex() {
		return this.currentPageFirstItemIndex;
	}

	/**
	 * Setter for property currentPageFirstItem.
	 * 
	 * @param newIndex
	 *            the New value of property currentPageFirstItem.
	 */
	public void setCurrentPageFirstItemIndex(int newIndex) {

		// Ensures that the new value is valid
		if (newIndex < 0)
			newIndex = 0;
		if (newIndex >= size())
			newIndex = size() - 1;

		// Refresh first item id
		if (items instanceof Container.Indexed) {
			try {
				currentPageFirstItemId = ((Container.Indexed) items)
						.getIdByIndex(newIndex);
			} catch (IndexOutOfBoundsException e) {
				currentPageFirstItemId = null;
			}
			this.currentPageFirstItemIndex = newIndex;
		} else {

			// For containers not supporting indexes, we must iterate the
			// container forwards / backwards
			// next available item forward or backward

			this.currentPageFirstItemId = ((Container.Ordered) items)
					.firstItemId();

			// Go forwards in the middle of the list (respect borders)
			while (this.currentPageFirstItemIndex < newIndex
					&& !((Container.Ordered) items)
							.isLastId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex++;
				currentPageFirstItemId = ((Container.Ordered) items)
						.nextItemId(currentPageFirstItemId);
			}

			// If we did hit the border
			if (((Container.Ordered) items).isLastId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex = size() - 1;
			}

			// Go backwards in the middle of the list (respect borders)
			while (this.currentPageFirstItemIndex > newIndex
					&& !((Container.Ordered) items)
							.isFirstId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex--;
				currentPageFirstItemId = ((Container.Ordered) items)
						.prevItemId(currentPageFirstItemId);
			}

			// If we did hit the border
			if (((Container.Ordered) items).isFirstId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex = 0;
			}

			// Go forwards once more
			while (this.currentPageFirstItemIndex < newIndex
					&& !((Container.Ordered) items)
							.isLastId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex++;
				currentPageFirstItemId = ((Container.Ordered) items)
						.nextItemId(currentPageFirstItemId);
			}

			// If for some reason we do hit border again, override
			// the user index request
			if (((Container.Ordered) items).isLastId(currentPageFirstItemId)) {
				newIndex = this.currentPageFirstItemIndex = size() - 1;
			}
		}

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Getter for property pageBuffering.
	 * 
	 * @return the Value of property pageBuffering.
	 */
	public boolean isPageBufferingEnabled() {
		return this.pageBuffering;
	}

	/**
	 * Setter for property pageBuffering.
	 * 
	 * @param pageBuffering
	 *            the New value of property pageBuffering.
	 */
	public void setPageBufferingEnabled(boolean pageBuffering) {

		this.pageBuffering = pageBuffering;

		// If page buffering is disabled, clear the buffer
		if (!pageBuffering)
			pageBuffer = null;
	}

	/**
	 * Getter for property selectable.
	 * 
	 * <p>
	 * The table is not selectable by default.
	 * </p>
	 * 
	 * @return the Value of property selectable.
	 */
	public boolean isSelectable() {
		return this.selectable;
	}

	/**
	 * Setter for property selectable.
	 * 
	 * <p>
	 * The table is not selectable by default.
	 * </p>
	 * 
	 * @param selectable
	 *            the New value of property selectable.
	 */
	public void setSelectable(boolean selectable) {
		if (this.selectable != selectable) {
			this.selectable = selectable;
			requestRepaint();
		}
	}

	/**
	 * Getter for property columnHeaderMode.
	 * 
	 * @return the Value of property columnHeaderMode.
	 */
	public int getColumnHeaderMode() {
		return this.columnHeaderMode;
	}

	/**
	 * Setter for property columnHeaderMode.
	 * 
	 * @param columnHeaderMode
	 *            the New value of property columnHeaderMode.
	 */
	public void setColumnHeaderMode(int columnHeaderMode) {
		if (columnHeaderMode >= COLUMN_HEADER_MODE_HIDDEN
				&& columnHeaderMode <= COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID)
			this.columnHeaderMode = columnHeaderMode;

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Refreshes the current page contents. If the page buffering is turned off,
	 * it is not necessary to call this explicitely.
	 */
	public void refreshCurrentPage() {

		// Clear page buffer and notify about the change
		pageBuffer = null;
		requestRepaint();
	}

	/**
	 * Sets the row header mode.
	 * <p>
	 * The mode can be one of the following ones:
	 * <ul>
	 * <li><code>ROW_HEADER_MODE_HIDDEN</code>: The row captions are hidden.
	 * </li>
	 * <li><code>ROW_HEADER_MODE_ID</code>: Items Id-objects
	 * <code>toString()</code> is used as row caption.
	 * <li><code>ROW_HEADER_MODE_ITEM</code>: Item-objects
	 * <code>toString()</code> is used as row caption.
	 * <li><code>ROW_HEADER_MODE_PROPERTY</code>: Property set with
	 * <code>setItemCaptionPropertyId()</code> is used as row header.
	 * <li><code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code>: Items
	 * Id-objects <code>toString()</code> is used as row header. If caption is
	 * explicitly specified, it overrides the id-caption.
	 * <li><code>ROW_HEADER_MODE_EXPLICIT</code>: The row headers must be
	 * explicitly specified.</li>
	 * <li><code>ROW_HEADER_MODE_INDEX</code>: The index of the item is used
	 * as row caption. The index mode can only be used with the containers
	 * implementing <code>Container.Indexed</code> interface.</li>
	 * </ul>
	 * The default value is <code>ROW_HEADER_MODE_HIDDEN</code>
	 * </p>
	 * 
	 * @param mode
	 *            the One of the modes listed above.
	 */
	public void setRowHeaderMode(int mode) {
		if (ROW_HEADER_MODE_HIDDEN == mode)
			rowCaptionsAreHidden = true;
		else {
			rowCaptionsAreHidden = false;
			setItemCaptionMode(mode);
		}

		// Assure visual refresh
		refreshCurrentPage();
	}

	/**
	 * Gets the row header mode.
	 * 
	 * @return the Row header mode.
	 * @see #setRowHeaderMode(int)
	 */
	public int getRowHeaderMode() {
		return rowCaptionsAreHidden ? ROW_HEADER_MODE_HIDDEN
				: getItemCaptionMode();
	}

	/**
	 * Adds the new row to table and fill the visible cells with given values.
	 * 
	 * @param cells
	 *            the Object array that is used for filling the visible cells
	 *            new row. The types must be settable to visible column property
	 *            types.
	 * @param itemId
	 *            the Id the new row. If null, a new id is automatically
	 *            assigned. If given, the table cant already have a item with
	 *            given id.
	 * @return Returns item id for the new row. Returns null if operation fails.
	 */
	public Object addItem(Object[] cells, Object itemId)
			throws UnsupportedOperationException {

		Object[] cols = getVisibleColumns();

		// Checks that a correct number of cells are given
		if (cells.length != cols.length)
			return null;

		// Creates new item
		Item item;
		if (itemId == null) {
			itemId = items.addItem();
			if (itemId == null)
				return null;
			item = items.getItem(itemId);
		} else
			item = items.addItem(itemId);
		if (item == null)
			return null;

		// Fills the item properties
		for (int i = 0; i < cols.length; i++)
			item.getItemProperty(cols[i]).setValue(cells[i]);

		return itemId;
	}

	/* Overriding select behavior******************************************** */

	/**
	 * Sets the Container that serves as the data source of the viewer.
	 * 
	 * @see com.itmill.toolkit.data.Container.Viewer#setContainerDataSource(Container)
	 */
	public void setContainerDataSource(Container newDataSource) {

		if (newDataSource == null)
			newDataSource = new IndexedContainer();

		// Assures that the data source is ordered by making unordered
		// containers ordered by wrapping them
		if (newDataSource instanceof Container.Ordered)
			super.setContainerDataSource(newDataSource);
		else
			super.setContainerDataSource(new ContainerOrderedWrapper(
					newDataSource));

		// Resets page position
		currentPageFirstItemId = null;
		currentPageFirstItemIndex = 0;

		// Resets column properties
		if (this.collapsedColumns != null)
			this.collapsedColumns.clear();
		setVisibleColumns(getContainerPropertyIds().toArray());

		// Assure visual refresh
		refreshCurrentPage();
	}

	/* Component basics ***************************************************** */

	/**
	 * Invoked when the value of a variable has changed.
	 * 
	 * @see com.itmill.toolkit.ui.Select#changeVariables(java.lang.Object,
	 *      java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {

		super.changeVariables(source, variables);

		// Page start index
		if (variables.containsKey("firstvisible")) {
			Integer value = (Integer) variables.get("firstvisible");
			if (value != null)
				setCurrentPageFirstItemIndex(value.intValue());
		}

		// Sets requested firstrow and rows for the next paint
		if (variables.containsKey("reqfirstrow")
				|| variables.containsKey("reqrows")) {
			Integer value = (Integer) variables.get("reqfirstrow");
			if (value != null)
				reqFirstRowToPaint = value.intValue();
			value = (Integer) variables.get("reqrows");
			if (value != null)
				reqRowsToPaint = value.intValue();
			pageBuffer = null;
			requestRepaint();
		}

		// Actions
		if (variables.containsKey("action")) {
			StringTokenizer st = new StringTokenizer((String) variables
					.get("action"), ",");
			if (st.countTokens() == 2) {
				Object itemId = itemIdMapper.get(st.nextToken());
				Action action = (Action) actionMapper.get(st.nextToken());
				if (action != null && containsId(itemId)
						&& actionHandlers != null)
					for (Iterator i = actionHandlers.iterator(); i.hasNext();)
						((Action.Handler) i.next()).handleAction(action, this,
								itemId);
			}
		}

		// Sorting
		boolean doSort = false;
		if (!this.sortDisabled) {
			if (variables.containsKey("sortcolumn")) {
				String colId = (String) variables.get("sortcolumn");
				if (colId != null && !"".equals(colId) && !"null".equals(colId)) {
					Object id = this.columnIdMap.get(colId);
					setSortContainerPropertyId(id);
					doSort = true;
				}
			}
			if (variables.containsKey("sortascending")) {
				boolean state = ((Boolean) variables.get("sortascending"))
						.booleanValue();
				if (state != this.sortAscending) {
					setSortAscending(state);
					doSort = true;
				}
			}
		}
		if (doSort)
			this.sort();

		// Dynamic column hide/show and order
		// Update visible columns
		if (this.isColumnCollapsingAllowed()) {
			if (variables.containsKey("collapsedcolumns")) {
				try {
					Object[] ids = (Object[]) variables.get("collapsedcolumns");
					for (Iterator it = this.visibleColumns.iterator(); it
							.hasNext();) {
						this.setColumnCollapsed(it.next(), false);
					}
					for (int i = 0; i < ids.length; i++) {
						this.setColumnCollapsed(columnIdMap.get(ids[i]
								.toString()), true);
					}
				} catch (Exception ignored) {
				}
			}
		}
		if (this.isColumnReorderingAllowed()) {
			if (variables.containsKey("columnorder")) {
				try {
					Object[] ids = (Object[]) variables.get("columnorder");
					for (int i = 0; i < ids.length; i++) {
						ids[i] = columnIdMap.get(ids[i].toString());
					}
					this.setColumnOrder(ids);
				} catch (Exception ignored) {
				}
			}
		}
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param target
	 *            the Paint target.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Focus control id
		if (this.getFocusableId() > 0) {
			target.addAttribute("focusid", this.getFocusableId());
		}

		// The tab ordering number
		if (this.getTabIndex() > 0)
			target.addAttribute("tabindex", this.getTabIndex());

		// Size
		if (getHeight() >= 0)
			target.addAttribute("height", "" + getHeight()
					+ Sizeable.UNIT_SYMBOLS[getHeightUnits()]);
		if (getWidth() >= 0)
			target.addAttribute("width", "" + getWidth()
					+ Sizeable.UNIT_SYMBOLS[getWidthUnits()]);

		// Initialize temps
		Object[] colids = getVisibleColumns();
		int cols = colids.length;
		int first = getCurrentPageFirstItemIndex();
		int total = size();
		int pagelen = getPageLength();
		int colHeadMode = getColumnHeaderMode();
		boolean colheads = colHeadMode != COLUMN_HEADER_MODE_HIDDEN;
		boolean rowheads = getRowHeaderMode() != ROW_HEADER_MODE_HIDDEN;
		Object[][] cells = getVisibleCells();
		boolean iseditable = this.isEditable();

		// selection support
		String[] selectedKeys;
		if (isMultiSelect())
			selectedKeys = new String[((Set) getValue()).size()];
		else
			selectedKeys = new String[(getValue() == null
					&& getNullSelectionItemId() == null ? 0 : 1)];
		int keyIndex = 0;

		// Table attributes
		if (isSelectable())
			target.addAttribute("selectmode", (isMultiSelect() ? "multi"
					: "single"));
		else
			target.addAttribute("selectmode", "none");
		target.addAttribute("cols", cols);
		target.addAttribute("rows", cells[0].length);
		target.addAttribute("firstrow",
				(reqFirstRowToPaint >= 0 ? reqFirstRowToPaint : first));
		target.addAttribute("totalrows", total);
		if (pagelen != 0)
			target.addAttribute("pagelength", pagelen);
		if (colheads)
			target.addAttribute("colheaders", true);
		if (rowheads)
			target.addAttribute("rowheaders", true);

		// Visible column order
		Collection sortables = getSortableContainerPropertyIds();
		ArrayList visibleColOrder = new ArrayList();
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext();) {
			Object columnId = it.next();
			if (!isColumnCollapsed(columnId)) {
				visibleColOrder.add(this.columnIdMap.key(columnId));
			}
		}
		target.addAttribute("vcolorder", visibleColOrder.toArray());

		// Rows
		Set actionSet = new LinkedHashSet();
		boolean selectable = isSelectable();
		boolean[] iscomponent = new boolean[this.visibleColumns.size()];
		int iscomponentIndex = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
				&& iscomponentIndex < iscomponent.length;) {
			Object columnId = it.next();
			Class colType = getType(columnId);
			iscomponent[iscomponentIndex++] = colType != null
					&& Component.class.isAssignableFrom(colType);
		}
		target.startTag("rows");
		for (int i = 0; i < cells[0].length; i++) {
			target.startTag("tr");
			Object itemId = cells[CELL_ITEMID][i];

			// tr attributes
			if (rowheads) {
				if (cells[CELL_ICON][i] != null)
					target.addAttribute("icon", (Resource) cells[CELL_ICON][i]);
				if (cells[CELL_HEADER][i] != null)
					target.addAttribute("caption",
							(String) cells[CELL_HEADER][i]);
			}
			target.addAttribute("key", Integer.parseInt(cells[CELL_KEY][i]
					.toString()));
			if (actionHandlers != null || isSelectable()) {
				if (isSelected(itemId) && keyIndex < selectedKeys.length) {
					target.addAttribute("selected", true);
					selectedKeys[keyIndex++] = (String) cells[CELL_KEY][i];
				}
			}

			// Actions
			if (actionHandlers != null) {
				ArrayList keys = new ArrayList();
				for (Iterator ahi = actionHandlers.iterator(); ahi.hasNext();) {
					Action[] aa = ((Action.Handler) ahi.next()).getActions(
							itemId, this);
					if (aa != null)
						for (int ai = 0; ai < aa.length; ai++) {
							String key = actionMapper.key(aa[ai]);
							actionSet.add(aa[ai]);
							keys.add(key);
						}
				}
				target.addAttribute("al", keys.toArray());
			}

			// cells
			int currentColumn = 0;
			for (Iterator it = this.visibleColumns.iterator(); it.hasNext(); currentColumn++) {
				Object columnId = it.next();
				if (columnId == null || this.isColumnCollapsed(columnId))
					continue;
				if ((iscomponent[currentColumn] || iseditable)
						&& Component.class.isInstance(cells[CELL_FIRSTCOL
								+ currentColumn][i])) {
					Component c = (Component) cells[CELL_FIRSTCOL
							+ currentColumn][i];
					if (c == null)
						target.addText("");
					else
						c.paint(target);
				} else
					target
							.addText((String) cells[CELL_FIRSTCOL
									+ currentColumn][i]);
			}

			target.endTag("tr");
		}
		target.endTag("rows");

		// The select variable is only enabled if selectable
		if (selectable)
			target.addVariable(this, "selected", selectedKeys);

		// The cursors are only shown on pageable table
		if (first != 0 || getPageLength() > 0)
			target.addVariable(this, "firstvisible", first);

		// Sorting
		if (getContainerDataSource() instanceof Container.Sortable) {
			target.addVariable(this, "sortcolumn", this.columnIdMap
					.key(this.sortContainerPropertyId));
			target.addVariable(this, "sortascending", this.sortAscending);
		}

		// Resets and paints "to be painted next" variables. Also reset
		// pageBuffer
		reqFirstRowToPaint = -1;
		reqRowsToPaint = -1;
		pageBuffer = null;
		target.addVariable(this, "reqrows", reqRowsToPaint);
		target.addVariable(this, "reqfirstrow", reqFirstRowToPaint);

		// Actions
		if (!actionSet.isEmpty()) {
			target.addVariable(this, "action", "");
			target.startTag("actions");
			for (Iterator it = actionSet.iterator(); it.hasNext();) {
				Action a = (Action) it.next();
				target.startTag("action");
				if (a.getCaption() != null)
					target.addAttribute("caption", a.getCaption());
				if (a.getIcon() != null)
					target.addAttribute("icon", a.getIcon());
				target.addAttribute("key", actionMapper.key(a));
				target.endTag("action");
			}
			target.endTag("actions");
		}
		if (this.columnReorderingAllowed) {
			String[] colorder = new String[this.visibleColumns.size()];
			int i = 0;
			for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
					&& i < colorder.length;) {
				colorder[i++] = this.columnIdMap.key(it.next());
			}
			target.addVariable(this, "columnorder", colorder);
		}
		// Available columns
		if (this.columnCollapsingAllowed) {
			HashSet ccs = new HashSet();
			for (Iterator i = visibleColumns.iterator(); i.hasNext();) {
				Object o = i.next();
				if (isColumnCollapsed(o))
					ccs.add(o);
			}
			String[] collapsedkeys = new String[ccs.size()];
			int nextColumn = 0;
			for (Iterator it = this.visibleColumns.iterator(); it.hasNext()
					&& nextColumn < collapsedkeys.length;) {
				Object columnId = it.next();
				if (this.isColumnCollapsed(columnId)) {
					collapsedkeys[nextColumn++] = this.columnIdMap
							.key(columnId);
				}
			}
			target.addVariable(this, "collapsedcolumns", collapsedkeys);
		}
		target.startTag("visiblecolumns");
		int i = 0;
		for (Iterator it = this.visibleColumns.iterator(); it.hasNext(); i++) {
			Object columnId = it.next();
			if (columnId != null) {
				target.startTag("column");
				target.addAttribute("cid", this.columnIdMap.key(columnId));
				String head = getColumnHeader(columnId);
				target.addAttribute("caption", (head != null ? head : ""));
				if (this.isColumnCollapsed(columnId)) {
					target.addAttribute("collapsed", true);
				}
				if (colheads) {
					if (this.getColumnIcon(columnId) != null)
						target.addAttribute("icon", this
								.getColumnIcon(columnId));
					if (sortables.contains(columnId))
						target.addAttribute("sortable", true);
				}
				if (!ALIGN_LEFT.equals(this.getColumnAlignment(columnId)))
					target.addAttribute("align", this
							.getColumnAlignment(columnId));
				if (getColumnWidth(columnId) > -1)
					target.addAttribute("width", String
							.valueOf(getColumnWidth(columnId)));

				target.endTag("column");
			}
		}
		target.endTag("visiblecolumns");
	}

	/**
	 * Gets the UIDL tag corresponding to component.
	 * 
	 * @return the UIDL tag as string.
	 */
	public String getTag() {
		return "table";
	}

	/**
	 * Gets the cached visible table contents.
	 * 
	 * @return the cahced visible table conetents.
	 */
	private Object[][] getVisibleCells() {

		// Returns a buffered value if possible
		if (pageBuffer != null && isPageBufferingEnabled())
			return pageBuffer;

		// Stops listening the old properties and initialise the list
		if (listenedProperties == null)
			listenedProperties = new LinkedList();
		else
			for (Iterator i = listenedProperties.iterator(); i.hasNext();) {
				((Property.ValueChangeNotifier) i.next()).removeListener(this);
			}

		// Detach old visible component from the table
		if (visibleComponents == null)
			visibleComponents = new LinkedList();
		else {
			for (Iterator i = visibleComponents.iterator(); i.hasNext();) {
				((Component) i.next()).setParent(null);
			}
			visibleComponents.clear();
		}

		// Collects the basic facts about the table page
		Object[] colids = getVisibleColumns();
		int cols = colids.length;
		int pagelen = getPageLength();
		int firstIndex = getCurrentPageFirstItemIndex();
		int rows = size();
		if (rows > 0 && firstIndex >= 0)
			rows -= firstIndex;
		if (pagelen > 0 && pagelen < rows)
			rows = pagelen;

		// If "to be painted next" variables are set, use them
		if (reqRowsToPaint >= 0)
			rows = reqRowsToPaint;
		Object id;
		if (reqFirstRowToPaint >= 0 && reqFirstRowToPaint < size())
			firstIndex = reqFirstRowToPaint;
		if (size() > 0) {
			if (rows + firstIndex > size())
				rows = size() - firstIndex;
		} else {
			rows = 0;
		}

		Object[][] cells = new Object[cols + CELL_FIRSTCOL][rows];
		if (rows == 0)
			return cells;

		// Gets the first item id
		if (items instanceof Container.Indexed)
			id = ((Container.Indexed) items).getIdByIndex(firstIndex);
		else {
			id = ((Container.Ordered) items).firstItemId();
			for (int i = 0; i < firstIndex; i++)
				id = ((Container.Ordered) items).nextItemId(id);
		}

		int headmode = getRowHeaderMode();
		boolean[] iscomponent = new boolean[cols];
		for (int i = 0; i < cols; i++)
			iscomponent[i] = Component.class
					.isAssignableFrom(getType(colids[i]));

		// Creates the page contents
		int filledRows = 0;
		for (int i = 0; i < rows && id != null; i++) {
			cells[CELL_ITEMID][i] = id;
			cells[CELL_KEY][i] = itemIdMapper.key(id);
			if (headmode != ROW_HEADER_MODE_HIDDEN) {
				switch (headmode) {
				case ROW_HEADER_MODE_INDEX:
					cells[CELL_HEADER][i] = String.valueOf(i + firstIndex + 1);
					break;
				default:
					cells[CELL_HEADER][i] = getItemCaption(id);
				}
				cells[CELL_ICON][i] = getItemIcon(id);
			}
			if (cols > 0) {
				for (int j = 0; j < cols; j++) {
					Object value = null;
					Property p = getContainerProperty(id, colids[j]);
					if (p != null) {
						if (p instanceof Property.ValueChangeNotifier) {
							((Property.ValueChangeNotifier) p)
									.addListener(this);
							listenedProperties.add(p);
						}
						if (iscomponent[j]) {
							value = p.getValue();
						} else if (p != null) {
							value = getPropertyValue(id, colids[j], p);
						} else {
							value = getPropertyValue(id, colids[j], null);
						}
					} else {
						value = "";
					}

					if (value instanceof Component) {
						((Component) value).setParent(this);
						visibleComponents.add((Component) value);
					}
					cells[CELL_FIRSTCOL + j][i] = value;

				}
			}
			id = ((Container.Ordered) items).nextItemId(id);

			filledRows++;
		}

		// Assures that all the rows of the cell-buffer are valid
		if (filledRows != cells[0].length) {
			Object[][] temp = new Object[cells.length][filledRows];
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < filledRows; j++)
					temp[i][j] = cells[i][j];
			cells = temp;
		}

		// Saves the results to internal buffer iff in buffering mode
		// to possible conserve memory from large non-buffered pages
		if (isPageBufferingEnabled())
			pageBuffer = cells;

		return cells;
	}

	/**
	 * Gets the value of property.
	 * 
	 * By default if the table is editable the fieldFactory is used to create
	 * editors for table cells. Otherwise formatPropertyValue is used to format
	 * the value representation.
	 * 
	 * @param rowId
	 *            the Id of the row (same as item Id).
	 * @param colId
	 *            the Id of the column.
	 * @param property
	 *            the Property to be presented.
	 * @return Object Either formatted value or Component for field.
	 * @see #setFieldFactory(FieldFactory)
	 */
	protected Object getPropertyValue(Object rowId, Object colId,
			Property property) {
		if (this.isEditable() && this.fieldFactory != null) {
			Field f = this.fieldFactory.createField(getContainerDataSource(),
					rowId, colId, this);
			if (f != null) {
				f.setPropertyDataSource(property);
				return f;
			}
		}

		return formatPropertyValue(rowId, colId, property);
	}

	/**
	 * Formats table cell property values. By default the property.toString()
	 * and return a empty string for null properties.
	 * 
	 * @param rowId
	 *            the Id of the row (same as item Id).
	 * @param colId
	 *            the Id of the column.
	 * @param property
	 *            the Property to be formatted.
	 * @return the String representation of property and its value.
	 * @since 3.1
	 */
	protected String formatPropertyValue(Object rowId, Object colId,
			Property property) {
		if (property == null) {
			return "";
		}
		return property.toString();
	}

	/* Action container *************************************************** */

	/**
	 * Registers a new action handler for this container
	 * 
	 * @see com.itmill.toolkit.event.Action.Container#addActionHandler(Action.Handler)
	 */
	public void addActionHandler(Action.Handler actionHandler) {

		if (actionHandler != null) {

			if (actionHandlers == null) {
				actionHandlers = new LinkedList();
				actionMapper = new KeyMapper();
			}

			if (!actionHandlers.contains(actionHandler)) {
				actionHandlers.add(actionHandler);
				requestRepaint();
			}

		}
	}

	/**
	 * Removes a previously registered action handler for the contents of this
	 * container.
	 * 
	 * @see com.itmill.toolkit.event.Action.Container#removeActionHandler(Action.Handler)
	 */
	public void removeActionHandler(Action.Handler actionHandler) {

		if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

			actionHandlers.remove(actionHandler);

			if (actionHandlers.isEmpty()) {
				actionHandlers = null;
				actionMapper = null;
			}

			requestRepaint();
		}
	}

	/* Property value change listening support **************************** */

	/**
	 * Notifies this listener that the Property's value has changed.
	 * 
	 * @see com.itmill.toolkit.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
	 */
	public void valueChange(Property.ValueChangeEvent event) {
		super.valueChange(event);
		requestRepaint();
	}

	/**
	 * Notifies the component that it is connected to an application.
	 * 
	 * @see com.itmill.toolkit.ui.Component#attach()
	 */
	public void attach() {
		super.attach();

		if (visibleComponents != null)
			for (Iterator i = visibleComponents.iterator(); i.hasNext();)
				((Component) i.next()).attach();
	}

	/**
	 * Notifies the component that it is detached from the application
	 * 
	 * @see com.itmill.toolkit.ui.Component#detach()
	 */
	public void detach() {
		super.detach();

		if (visibleComponents != null)
			for (Iterator i = visibleComponents.iterator(); i.hasNext();)
				((Component) i.next()).detach();
	}

	/**
	 * Removes all Items from the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container#removeAllItems()
	 */
	public boolean removeAllItems() {
		this.currentPageFirstItemId = null;
		this.currentPageFirstItemIndex = 0;
		return super.removeAllItems();
	}

	/**
	 * Removes the Item identified by <code>ItemId</code> from the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container#removeItem(Object)
	 */
	public boolean removeItem(Object itemId) {
		Object nextItemId = ((Container.Ordered) items).nextItemId(itemId);
		boolean ret = super.removeItem(itemId);
		if (ret && (itemId != null)
				&& (itemId.equals(this.currentPageFirstItemId))) {
			this.currentPageFirstItemId = nextItemId;
		}
		return ret;
	}

	/**
	 * Removes a Property specified by the given Property ID from the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container#removeContainerProperty(Object)
	 */
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {

		// If a visible property is removed, remove the corresponding column
		this.visibleColumns.remove(propertyId);
		this.columnAlignments.remove(propertyId);
		this.columnIcons.remove(propertyId);
		this.columnHeaders.remove(propertyId);

		return super.removeContainerProperty(propertyId);
	}

	/**
	 * Adds a new property to the table and show it as a visible column.
	 * 
	 * @param propertyId
	 *            the Id of the proprty.
	 * @param type
	 *            the class of the property.
	 * @param defaultValue
	 *            the default value given for all existing items.
	 * @see com.itmill.toolkit.data.Container#addContainerProperty(Object,
	 *      Class, Object)
	 */
	public boolean addContainerProperty(Object propertyId, Class type,
			Object defaultValue) throws UnsupportedOperationException {
		if (!super.addContainerProperty(propertyId, type, defaultValue))
			return false;
		if (!this.visibleColumns.contains(propertyId))
			this.visibleColumns.add(propertyId);
		return true;
	}

	/**
	 * Adds a new property to the table and show it as a visible column.
	 * 
	 * @param propertyId
	 *            the Id of the proprty
	 * @param type
	 *            the class of the property
	 * @param defaultValue
	 *            the default value given for all existing items
	 * @param columnHeader
	 *            the Explicit header of the column. If explicit header is not
	 *            needed, this should be set null.
	 * @param columnIcon
	 *            the Icon of the column. If icon is not needed, this should be
	 *            set null.
	 * @param columnAlignment
	 *            the Alignment of the column. Null implies align left.
	 * @throws UnsupportedOperationException
	 *             if the operation is not supported.
	 * @see com.itmill.toolkit.data.Container#addContainerProperty(Object,
	 *      Class, Object)
	 */
	public boolean addContainerProperty(Object propertyId, Class type,
			Object defaultValue, String columnHeader, Resource columnIcon,
			String columnAlignment) throws UnsupportedOperationException {
		if (!this.addContainerProperty(propertyId, type, defaultValue))
			return false;
		this.setColumnAlignment(propertyId, columnAlignment);
		this.setColumnHeader(propertyId, columnHeader);
		this.setColumnIcon(propertyId, columnIcon);
		return true;
	}

	/**
	 * Returns the list of items on the current page
	 * 
	 * @see com.itmill.toolkit.ui.Select#getVisibleItemIds()
	 */
	public Collection getVisibleItemIds() {

		LinkedList visible = new LinkedList();

		Object[][] cells = getVisibleCells();
		for (int i = 0; i < cells[CELL_ITEMID].length; i++)
			visible.add(cells[CELL_ITEMID][i]);

		return visible;
	}

	/**
	 * Container datasource item set change. Table must flush its buffers on
	 * change.
	 * 
	 * @see com.itmill.toolkit.data.Container.ItemSetChangeListener#containerItemSetChange(com.itmill.toolkit.data.Container.ItemSetChangeEvent)
	 */
	public void containerItemSetChange(Container.ItemSetChangeEvent event) {
		pageBuffer = null;
		super.containerItemSetChange(event);
		setCurrentPageFirstItemIndex(this.getCurrentPageFirstItemIndex());
	}

	/**
	 * Container datasource property set change. Table must flush its buffers on
	 * change.
	 * 
	 * @see com.itmill.toolkit.data.Container.PropertySetChangeListener#containerPropertySetChange(com.itmill.toolkit.data.Container.PropertySetChangeEvent)
	 */
	public void containerPropertySetChange(
			Container.PropertySetChangeEvent event) {
		pageBuffer = null;
		super.containerPropertySetChange(event);
	}

	/**
	 * Adding new items is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 *             if set to true.
	 * @see com.itmill.toolkit.ui.Select#setNewItemsAllowed(boolean)
	 */
	public void setNewItemsAllowed(boolean allowNewOptions)
			throws UnsupportedOperationException {
		if (allowNewOptions)
			throw new UnsupportedOperationException();
	}

	/**
	 * Focusing to this component is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 *             if invoked.
	 * @see com.itmill.toolkit.ui.AbstractField#focus()
	 */
	public void focus() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the ID of the Item following the Item that corresponds to itemId.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#nextItemId(java.lang.Object)
	 */
	public Object nextItemId(Object itemId) {
		return ((Container.Ordered) items).nextItemId(itemId);
	}

	/**
	 * Gets the ID of the Item preceding the Item that corresponds to the
	 * itemId.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#prevItemId(java.lang.Object)
	 */
	public Object prevItemId(Object itemId) {
		return ((Container.Ordered) items).prevItemId(itemId);
	}

	/**
	 * Gets the ID of the first Item in the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#firstItemId()
	 */
	public Object firstItemId() {
		return ((Container.Ordered) items).firstItemId();
	}

	/**
	 * Gets the ID of the last Item in the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#lastItemId()
	 */
	public Object lastItemId() {
		return ((Container.Ordered) items).lastItemId();
	}

	/**
	 * Tests if the Item corresponding to the given Item ID is the first Item in
	 * the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#isFirstId(java.lang.Object)
	 */
	public boolean isFirstId(Object itemId) {
		return ((Container.Ordered) items).isFirstId(itemId);
	}

	/**
	 * Tests if the Item corresponding to the given Item ID is the last Item in
	 * the Container.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#isLastId(java.lang.Object)
	 */
	public boolean isLastId(Object itemId) {
		return ((Container.Ordered) items).isLastId(itemId);
	}

	/**
	 * Adds new item after the given item.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(java.lang.Object)
	 */
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		return ((Container.Ordered) items).addItemAfter(previousItemId);
	}

	/**
	 * Adds new item after the given item.
	 * 
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		return ((Container.Ordered) items).addItemAfter(previousItemId,
				newItemId);
	}

	/**
	 * Gets the FieldFactory that is used to create editor for table cells.
	 * 
	 * The FieldFactory is only used if the Table is editable.
	 * 
	 * @return FieldFactory used to create the Field instances.
	 * @see #isEditable
	 */
	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	/**
	 * Sets the FieldFactory that is used to create editor for table cells.
	 * 
	 * The FieldFactory is only used if the Table is editable. By default the
	 * BaseFieldFactory is used.
	 * 
	 * @param fieldFactory
	 *            the field factory to set.
	 * @see #isEditable
	 * @see BaseFieldFactory
	 * 
	 */
	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;

		// Assure visual refresh
		refreshCurrentPage();
	}

	/**
	 * Is table editable.
	 * 
	 * If table is editable a editor of type Field is created for each table
	 * cell. The assigned FieldFactory is used to create the instances.
	 * 
	 * To provide custom editors for table cells create a class implementins the
	 * FieldFactory interface, and assign it to table, and set the editable
	 * property to true.
	 * 
	 * @return true if table is editable, false oterwise.
	 * @see Field
	 * @see FieldFactory
	 * 
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets the editable property.
	 * 
	 * If table is editable a editor of type Field is created for each table
	 * cell. The assigned FieldFactory is used to create the instances.
	 * 
	 * To provide custom editors for table cells create a class implementins the
	 * FieldFactory interface, and assign it to table, and set the editable
	 * property to true.
	 * 
	 * @param editable
	 *            true if table should be editable by user.
	 * @see Field
	 * @see FieldFactory
	 * 
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;

		// Assure visual refresh
		refreshCurrentPage();
	}

	/**
	 * Sorts the table.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the container data source does not implement
	 *             Container.Sortable
	 * @see com.itmill.toolkit.data.Container.Sortable#sort(java.lang.Object[],
	 *      boolean[])
	 * 
	 */
	public void sort(Object[] propertyId, boolean[] ascending)
			throws UnsupportedOperationException {
		Container c = getContainerDataSource();
		if (c instanceof Container.Sortable) {
			int pageIndex = this.getCurrentPageFirstItemIndex();
			((Container.Sortable) c).sort(propertyId, ascending);
			setCurrentPageFirstItemIndex(pageIndex);
		} else if (c != null) {
			throw new UnsupportedOperationException(
					"Underlying Data does not allow sorting");
		}
	}

	/**
	 * Sorts the table by currently selected sorting column.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the container data source does not implement
	 *             Container.Sortable
	 */
	public void sort() {
		if (getSortContainerPropertyId() == null)
			return;
		sort(new Object[] { this.sortContainerPropertyId },
				new boolean[] { this.sortAscending });
	}

	/**
	 * Gets the container property IDs, which can be used to sort the item.
	 * 
	 * @see com.itmill.toolkit.data.Container.Sortable#getSortableContainerPropertyIds()
	 */
	public Collection getSortableContainerPropertyIds() {
		Container c = getContainerDataSource();
		if (c instanceof Container.Sortable && !isSortDisabled()) {
			return ((Container.Sortable) c).getSortableContainerPropertyIds();
		} else {
			return new LinkedList();
		}
	}

	/**
	 * Gets the currently sorted column property ID.
	 * 
	 * @return the Container property id of the currently sorted column.
	 */
	public Object getSortContainerPropertyId() {
		return this.sortContainerPropertyId;
	}

	/**
	 * Sets the currently sorted column property id.
	 * 
	 * @param propertyId
	 *            the Container property id of the currently sorted column.
	 */
	public void setSortContainerPropertyId(Object propertyId) {
		if ((this.sortContainerPropertyId != null && !this.sortContainerPropertyId
				.equals(propertyId))
				|| (this.sortContainerPropertyId == null && propertyId != null)) {
			this.sortContainerPropertyId = propertyId;
			sort();
		}

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Is the table currently sorted in ascending order.
	 * 
	 * @return <code>true</code> if ascending, <code>false</code> if
	 *         descending.
	 */
	public boolean isSortAscending() {
		return this.sortAscending;
	}

	/**
	 * Sets the table in ascending order.
	 * 
	 * @param ascending
	 *            <code>true</code> if ascending, <code>false</code> if
	 *            descending.
	 */
	public void setSortAscending(boolean ascending) {
		if (this.sortAscending != ascending) {
			this.sortAscending = ascending;
			sort();
		}

		// Assures the visual refresh
		refreshCurrentPage();
	}

	/**
	 * Is sorting disabled alltogether.
	 * 
	 * True iff no sortable columns are given even in the case where datasource
	 * would support this.
	 * 
	 * @return True iff sorting is disabled.
	 */
	public boolean isSortDisabled() {
		return sortDisabled;
	}

	/**
	 * Disables the sorting alltogether.
	 * 
	 * To disable sorting alltogether, set to true. In this case no sortable
	 * columns are given even in the case where datasource would support this.
	 * 
	 * @param sortDisabled
	 *            True iff sorting is disabled.
	 */
	public void setSortDisabled(boolean sortDisabled) {
		if (this.sortDisabled != sortDisabled) {
			this.sortDisabled = sortDisabled;
			refreshCurrentPage();
		}
	}

	/**
	 * Gets the height property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return heightUnit;
	}

	/**
	 * Gets the width property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return widthUnit;
	}

	/**
	 * Sets the height units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		this.heightUnit = units;
	}

	/**
	 * Sets the width units. Tabel supports only Sizeable.UNITS_PIXELS and
	 * Sizeable.UNITS_PERCENTAGE. Setting to any other throws
	 * IllegalArgumentException.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		if (units != Sizeable.UNITS_PIXELS
				&& units != Sizeable.UNITS_PERCENTAGE)
			throw new IllegalArgumentException();
		this.widthUnit = units;
	}

	/**
	 * Gets the height in pixels.
	 * 
	 * @return the height in pixels or negative value if not assigned.
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the width in pixels.
	 * 
	 * @return the width in pixels or negative value if not assigned.
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height in pixels. Use negative value to let the client decide
	 * the height.
	 * 
	 * @param height
	 *            the height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
		requestRepaint();
	}

	/**
	 * Sets the width in pixels. Use negative value to allow the client decide
	 * the width.
	 * 
	 * @param width
	 *            the width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
		requestRepaint();
	}

	/**
	 * Table does not support lazy options loading mode. Setting this true will
	 * throw UnsupportedOperationException.
	 * 
	 * @see com.itmill.toolkit.ui.Select#setLazyLoading(boolean)
	 */
	public void setLazyLoading(boolean useLazyLoading) {
		if (useLazyLoading)
			throw new UnsupportedOperationException(
					"Lazy options loading is not supported by Table.");
	}

}