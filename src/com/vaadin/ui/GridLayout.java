/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.GridLayoutConnector.GridLayoutServerRPC;
import com.vaadin.terminal.gwt.client.ui.GridLayoutConnector.GridLayoutState;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;

/**
 * A layout where the components are laid out on a grid using cell coordinates.
 * 
 * <p>
 * The GridLayout also maintains a cursor for adding components in
 * left-to-right, top-to-bottom order.
 * </p>
 * 
 * <p>
 * Each component in a <code>GridLayout</code> uses a defined
 * {@link GridLayout.Area area} (column1,row1,column2,row2) from the grid. The
 * components may not overlap with the existing components - if you try to do so
 * you will get an {@link OverlapsException}. Adding a component with cursor
 * automatically extends the grid by increasing the grid height.
 * </p>
 * 
 * <p>
 * The grid coordinates, which are specified by a row and column index, always
 * start from 0 for the topmost row and the leftmost column.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class GridLayout extends AbstractLayout implements
        Layout.AlignmentHandler, Layout.SpacingHandler, LayoutClickNotifier {

    private GridLayoutServerRPC rpc = new GridLayoutServerRPC() {

        public void layoutClick(MouseEventDetails mouseDetails,
                Connector clickedConnector) {
            fireEvent(LayoutClickEvent.createEvent(GridLayout.this,
                    mouseDetails, clickedConnector));

        }
    };
    /**
     * Cursor X position: this is where the next component with unspecified x,y
     * is inserted
     */
    private int cursorX = 0;

    /**
     * Cursor Y position: this is where the next component with unspecified x,y
     * is inserted
     */
    private int cursorY = 0;

    /**
     * Contains all items that are placed on the grid. These are components with
     * grid area definition.
     */
    private final LinkedList<Area> areas = new LinkedList<Area>();

    /**
     * Mapping from components to their respective areas.
     */
    private final LinkedList<Component> components = new LinkedList<Component>();

    /**
     * Mapping from components to alignments (horizontal + vertical).
     */
    private Map<Component, Alignment> componentToAlignment = new HashMap<Component, Alignment>();

    private static final Alignment ALIGNMENT_DEFAULT = Alignment.TOP_LEFT;

    /**
     * Has there been rows inserted or deleted in the middle of the layout since
     * the last paint operation.
     */
    private boolean structuralChange = false;

    private Map<Integer, Float> columnExpandRatio = new HashMap<Integer, Float>();
    private Map<Integer, Float> rowExpandRatio = new HashMap<Integer, Float>();

    /**
     * Constructor for a grid of given size (number of columns and rows).
     * 
     * The grid may grow or shrink later. Grid grows automatically if you add
     * components outside its area.
     * 
     * @param columns
     *            Number of columns in the grid.
     * @param rows
     *            Number of rows in the grid.
     */
    public GridLayout(int columns, int rows) {
        setColumns(columns);
        setRows(rows);
        registerRpc(rpc);
    }

    /**
     * Constructs an empty (1x1) grid layout that is extended as needed.
     */
    public GridLayout() {
        this(1, 1);
    }

    @Override
    public GridLayoutState getState() {
        return (GridLayoutState) super.getState();
    }

    /**
     * <p>
     * Adds a component to the grid in the specified area. The area is defined
     * by specifying the upper left corner (column1, row1) and the lower right
     * corner (column2, row2) of the area. The coordinates are zero-based.
     * </p>
     * 
     * <p>
     * If the area overlaps with any of the existing components already present
     * in the grid, the operation will fail and an {@link OverlapsException} is
     * thrown.
     * </p>
     * 
     * @param component
     *            the component to be added.
     * @param column1
     *            the column of the upper left corner of the area <code>c</code>
     *            is supposed to occupy. The leftmost column has index 0.
     * @param row1
     *            the row of the upper left corner of the area <code>c</code> is
     *            supposed to occupy. The topmost row has index 0.
     * @param column2
     *            the column of the lower right corner of the area
     *            <code>c</code> is supposed to occupy.
     * @param row2
     *            the row of the lower right corner of the area <code>c</code>
     *            is supposed to occupy.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cells are outside the grid area.
     */
    public void addComponent(Component component, int column1, int row1,
            int column2, int row2) throws OverlapsException,
            OutOfBoundsException {

        if (component == null) {
            throw new NullPointerException("Component must not be null");
        }

        // Checks that the component does not already exist in the container
        if (components.contains(component)) {
            throw new IllegalArgumentException(
                    "Component is already in the container");
        }

        // Creates the area
        final Area area = new Area(component, column1, row1, column2, row2);

        // Checks the validity of the coordinates
        if (column2 < column1 || row2 < row1) {
            throw new IllegalArgumentException(
                    "Illegal coordinates for the component");
        }
        if (column1 < 0 || row1 < 0 || column2 >= getColumns()
                || row2 >= getRows()) {
            throw new OutOfBoundsException(area);
        }

        // Checks that newItem does not overlap with existing items
        checkExistingOverlaps(area);

        // Inserts the component to right place at the list
        // Respect top-down, left-right ordering
        // component.setParent(this);
        final Iterator<Area> i = areas.iterator();
        int index = 0;
        boolean done = false;
        while (!done && i.hasNext()) {
            final Area existingArea = i.next();
            if ((existingArea.row1 >= row1 && existingArea.column1 > column1)
                    || existingArea.row1 > row1) {
                areas.add(index, area);
                components.add(index, component);
                done = true;
            }
            index++;
        }
        if (!done) {
            areas.addLast(area);
            components.addLast(component);
        }

        // Attempt to add to super
        try {
            super.addComponent(component);
        } catch (IllegalArgumentException e) {
            areas.remove(area);
            components.remove(component);
            throw e;
        }

        // update cursor position, if it's within this area; use first position
        // outside this area, even if it's occupied
        if (cursorX >= column1 && cursorX <= column2 && cursorY >= row1
                && cursorY <= row2) {
            // cursor within area
            cursorX = column2 + 1; // one right of area
            if (cursorX >= getColumns()) {
                // overflowed columns
                cursorX = 0; // first col
                // move one row down, or one row under the area
                cursorY = (column1 == 0 ? row2 : row1) + 1;
            } else {
                cursorY = row1;
            }
        }

        requestRepaint();
    }

    /**
     * Tests if the given area overlaps with any of the items already on the
     * grid.
     * 
     * @param area
     *            the Area to be checked for overlapping.
     * @throws OverlapsException
     *             if <code>area</code> overlaps with any existing area.
     */
    private void checkExistingOverlaps(Area area) throws OverlapsException {
        for (final Iterator<Area> i = areas.iterator(); i.hasNext();) {
            final Area existingArea = i.next();
            if (existingArea.overlaps(area)) {
                // Component not added, overlaps with existing component
                throw new OverlapsException(existingArea);
            }
        }
    }

    /**
     * Adds the component to the grid in cells column1,row1 (NortWest corner of
     * the area.) End coordinates (SouthEast corner of the area) are the same as
     * column1,row1. The coordinates are zero-based. Component width and height
     * is 1.
     * 
     * @param component
     *            the component to be added.
     * @param column
     *            the column index, starting from 0.
     * @param row
     *            the row index, starting from 0.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cell is outside the grid area.
     */
    public void addComponent(Component component, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        this.addComponent(component, column, row, column, row);
    }

    /**
     * Forces the next component to be added at the beginning of the next line.
     * 
     * <p>
     * Sets the cursor column to 0 and increments the cursor row by one.
     * </p>
     * 
     * <p>
     * By calling this function you can ensure that no more components are added
     * right of the previous component.
     * </p>
     * 
     * @see #space()
     */
    public void newLine() {
        cursorX = 0;
        cursorY++;
    }

    /**
     * Moves the cursor forward by one. If the cursor goes out of the right grid
     * border, it is moved to the first column of the next row.
     * 
     * @see #newLine()
     */
    public void space() {
        cursorX++;
        if (cursorX >= getColumns()) {
            cursorX = 0;
            cursorY++;
        }
    }

    /**
     * Adds the component into this container to the cursor position. If the
     * cursor position is already occupied, the cursor is moved forwards to find
     * free position. If the cursor goes out from the bottom of the grid, the
     * grid is automatically extended.
     * 
     * @param component
     *            the component to be added.
     */
    @Override
    public void addComponent(Component component) {

        // Finds first available place from the grid
        Area area;
        boolean done = false;
        while (!done) {
            try {
                area = new Area(component, cursorX, cursorY, cursorX, cursorY);
                checkExistingOverlaps(area);
                done = true;
            } catch (final OverlapsException e) {
                space();
            }
        }

        // Extends the grid if needed
        if (cursorX >= getColumns()) {
            setColumns(cursorX + 1);
        }
        if (cursorY >= getRows()) {
            setRows(cursorY + 1);
        }

        addComponent(component, cursorX, cursorY);
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param component
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component component) {

        // Check that the component is contained in the container
        if (component == null || !components.contains(component)) {
            return;
        }

        Area area = null;
        for (final Iterator<Area> i = areas.iterator(); area == null
                && i.hasNext();) {
            final Area a = i.next();
            if (a.getComponent() == component) {
                area = a;
            }
        }

        components.remove(component);
        if (area != null) {
            areas.remove(area);
        }

        componentToAlignment.remove(component);

        super.removeComponent(component);

        requestRepaint();
    }

    /**
     * Removes the component specified by its cell coordinates.
     * 
     * @param column
     *            the component's column, starting from 0.
     * @param row
     *            the component's row, starting from 0.
     */
    public void removeComponent(int column, int row) {

        // Finds the area
        for (final Iterator<Area> i = areas.iterator(); i.hasNext();) {
            final Area area = i.next();
            if (area.getColumn1() == column && area.getRow1() == row) {
                removeComponent(area.getComponent());
                return;
            }
        }
    }

    /**
     * Gets an Iterator for the components contained in the layout. By using the
     * Iterator it is possible to step through the contents of the layout.
     * 
     * @return the Iterator of the components inside the layout.
     */
    public Iterator<Component> getComponentIterator() {
        return Collections.unmodifiableCollection(components).iterator();
    }

    /**
     * Gets the number of components contained in the layout. Consistent with
     * the iterator returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */
    public int getComponentCount() {
        return components.size();
    }

    /**
     * Paints the contents of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        super.paintContent(target);

        // TODO refactor attribute names in future release.
        target.addAttribute("structuralChange", structuralChange);
        structuralChange = false;

        // Area iterator
        final Iterator<Area> areaiterator = areas.iterator();

        // Current item to be processed (fetch first item)
        Area area = areaiterator.hasNext() ? (Area) areaiterator.next() : null;

        // Collects rowspan related information here
        final HashMap<Integer, Integer> cellUsed = new HashMap<Integer, Integer>();

        // Empty cell collector
        int emptyCells = 0;

        final String[] alignmentsArray = new String[components.size()];
        final Integer[] columnExpandRatioArray = new Integer[getColumns()];
        final Integer[] rowExpandRatioArray = new Integer[getRows()];

        int realColExpandRatioSum = 0;
        float colSum = getExpandRatioSum(columnExpandRatio);
        if (colSum == 0) {
            // no columns has been expanded, all cols have same expand
            // rate
            float equalSize = 1 / (float) getColumns();
            int myRatio = Math.round(equalSize * 1000);
            for (int i = 0; i < getColumns(); i++) {
                columnExpandRatioArray[i] = myRatio;
            }
            realColExpandRatioSum = myRatio * getColumns();
        } else {
            for (int i = 0; i < getColumns(); i++) {
                int myRatio = Math
                        .round((getColumnExpandRatio(i) / colSum) * 1000);
                columnExpandRatioArray[i] = myRatio;
                realColExpandRatioSum += myRatio;
            }
        }

        boolean equallyDividedRows = false;
        int realRowExpandRatioSum = 0;
        float rowSum = getExpandRatioSum(rowExpandRatio);
        if (rowSum == 0) {
            // no rows have been expanded
            equallyDividedRows = true;
            float equalSize = 1 / (float) getRows();
            int myRatio = Math.round(equalSize * 1000);
            for (int i = 0; i < getRows(); i++) {
                rowExpandRatioArray[i] = myRatio;
            }
            realRowExpandRatioSum = myRatio * getRows();
        }

        int index = 0;

        // Iterates every applicable row
        for (int cury = 0; cury < getRows(); cury++) {
            target.startTag("gr");

            if (!equallyDividedRows) {
                int myRatio = Math
                        .round((getRowExpandRatio(cury) / rowSum) * 1000);
                rowExpandRatioArray[cury] = myRatio;
                realRowExpandRatioSum += myRatio;

            }
            // Iterates every applicable column
            for (int curx = 0; curx < getColumns(); curx++) {

                // Checks if current item is located at curx,cury
                if (area != null && (area.row1 == cury)
                        && (area.column1 == curx)) {

                    // First check if empty cell needs to be rendered
                    if (emptyCells > 0) {
                        target.startTag("gc");
                        target.addAttribute("x", curx - emptyCells);
                        target.addAttribute("y", cury);
                        if (emptyCells > 1) {
                            target.addAttribute("w", emptyCells);
                        }
                        target.endTag("gc");
                        emptyCells = 0;
                    }

                    // Now proceed rendering current item
                    final int cols = (area.column2 - area.column1) + 1;
                    final int rows = (area.row2 - area.row1) + 1;
                    target.startTag("gc");

                    target.addAttribute("x", curx);
                    target.addAttribute("y", cury);

                    if (cols > 1) {
                        target.addAttribute("w", cols);
                    }
                    if (rows > 1) {
                        target.addAttribute("h", rows);
                    }
                    area.getComponent().paint(target);

                    alignmentsArray[index++] = String
                            .valueOf(getComponentAlignment(area.getComponent())
                                    .getBitMask());

                    target.endTag("gc");

                    // Fetch next item
                    if (areaiterator.hasNext()) {
                        area = areaiterator.next();
                    } else {
                        area = null;
                    }

                    // Updates the cellUsed if rowspan needed
                    if (rows > 1) {
                        int spannedx = curx;
                        for (int j = 1; j <= cols; j++) {
                            cellUsed.put(new Integer(spannedx), new Integer(
                                    cury + rows - 1));
                            spannedx++;
                        }
                    }

                    // Skips the current item's spanned columns
                    if (cols > 1) {
                        curx += cols - 1;
                    }

                } else {

                    // Checks against cellUsed, render space or ignore cell
                    if (cellUsed.containsKey(new Integer(curx))) {

                        // Current column contains already an item,
                        // check if rowspan affects at current x,y position
                        final int rowspanDepth = cellUsed
                                .get(new Integer(curx)).intValue();

                        if (rowspanDepth >= cury) {

                            // ignore cell
                            // Check if empty cell needs to be rendered
                            if (emptyCells > 0) {
                                target.startTag("gc");
                                target.addAttribute("x", curx - emptyCells);
                                target.addAttribute("y", cury);
                                if (emptyCells > 1) {
                                    target.addAttribute("w", emptyCells);
                                }
                                target.endTag("gc");

                                emptyCells = 0;
                            }
                        } else {

                            // empty cell is needed
                            emptyCells++;

                            // Removes the cellUsed key as it has become
                            // obsolete
                            cellUsed.remove(Integer.valueOf(curx));
                        }
                    } else {

                        // empty cell is needed
                        emptyCells++;
                    }
                }

            } // iterates every column

            // Last column handled of current row

            // Checks if empty cell needs to be rendered
            if (emptyCells > 0) {
                target.startTag("gc");
                target.addAttribute("x", getColumns() - emptyCells);
                target.addAttribute("y", cury);
                if (emptyCells > 1) {
                    target.addAttribute("w", emptyCells);
                }
                target.endTag("gc");

                emptyCells = 0;
            }

            target.endTag("gr");
        } // iterates every row

        // Last row handled

        // correct possible rounding error
        if (rowExpandRatioArray.length > 0) {
            rowExpandRatioArray[0] -= realRowExpandRatioSum - 1000;
        }
        if (columnExpandRatioArray.length > 0) {
            columnExpandRatioArray[0] -= realColExpandRatioSum - 1000;
        }

        target.addAttribute("colExpand", columnExpandRatioArray);
        target.addAttribute("rowExpand", rowExpandRatioArray);

        // Add child component alignment info to layout tag
        target.addAttribute("alignments", alignmentsArray);

    }

    private float getExpandRatioSum(Map<Integer, Float> ratioMap) {
        float sum = 0;
        for (Iterator<Entry<Integer, Float>> iterator = ratioMap.entrySet()
                .iterator(); iterator.hasNext();) {
            sum += iterator.next().getValue();
        }
        return sum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.AlignmentHandler#getComponentAlignment(com
     * .vaadin.ui.Component)
     */
    public Alignment getComponentAlignment(Component childComponent) {
        Alignment alignment = componentToAlignment.get(childComponent);
        if (alignment == null) {
            return ALIGNMENT_DEFAULT;
        } else {
            return alignment;
        }
    }

    /**
     * Defines a rectangular area of cells in a GridLayout.
     * 
     * <p>
     * Also maintains a reference to the component contained in the area.
     * </p>
     * 
     * <p>
     * The area is specified by the cell coordinates of its upper left corner
     * (column1,row1) and lower right corner (column2,row2). As otherwise with
     * GridLayout, the column and row coordinates start from zero.
     * </p>
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class Area implements Serializable {

        /**
         * The column of the upper left corner cell of the area.
         */
        private final int column1;

        /**
         * The row of the upper left corner cell of the area.
         */
        private int row1;

        /**
         * The column of the lower right corner cell of the area.
         */
        private final int column2;

        /**
         * The row of the lower right corner cell of the area.
         */
        private int row2;

        /**
         * Component painted in the area.
         */
        private Component component;

        /**
         * <p>
         * Construct a new area on a grid.
         * </p>
         * 
         * @param component
         *            the component connected to the area.
         * @param column1
         *            The column of the upper left corner cell of the area. The
         *            leftmost column has index 0.
         * @param row1
         *            The row of the upper left corner cell of the area. The
         *            topmost row has index 0.
         * @param column2
         *            The column of the lower right corner cell of the area. The
         *            leftmost column has index 0.
         * @param row2
         *            The row of the lower right corner cell of the area. The
         *            topmost row has index 0.
         */
        public Area(Component component, int column1, int row1, int column2,
                int row2) {
            this.column1 = column1;
            this.row1 = row1;
            this.column2 = column2;
            this.row2 = row2;
            this.component = component;
        }

        /**
         * Tests if this Area overlaps with another Area.
         * 
         * @param other
         *            the other Area that is to be tested for overlap with this
         *            area
         * @return <code>true</code> if <code>other</code> area overlaps with
         *         this on, <code>false</code> if it does not.
         */
        public boolean overlaps(Area other) {
            return column1 <= other.getColumn2() && row1 <= other.getRow2()
                    && column2 >= other.getColumn1() && row2 >= other.getRow1();

        }

        /**
         * Gets the component connected to the area.
         * 
         * @return the Component.
         */
        public Component getComponent() {
            return component;
        }

        /**
         * Sets the component connected to the area.
         * 
         * <p>
         * This function only sets the value in the data structure and does not
         * send any events or set parents.
         * </p>
         * 
         * @param newComponent
         *            the new connected overriding the existing one.
         */
        protected void setComponent(Component newComponent) {
            component = newComponent;
        }

        /**
         * @deprecated Use {@link #getColumn1()} instead.
         */
        @Deprecated
        public int getX1() {
            return getColumn1();
        }

        /**
         * Gets the column of the top-left corner cell.
         * 
         * @return the column of the top-left corner cell.
         */
        public int getColumn1() {
            return column1;
        }

        /**
         * @deprecated Use {@link #getColumn2()} instead.
         */
        @Deprecated
        public int getX2() {
            return getColumn2();
        }

        /**
         * Gets the column of the bottom-right corner cell.
         * 
         * @return the column of the bottom-right corner cell.
         */
        public int getColumn2() {
            return column2;
        }

        /**
         * @deprecated Use {@link #getRow1()} instead.
         */
        @Deprecated
        public int getY1() {
            return getRow1();
        }

        /**
         * Gets the row of the top-left corner cell.
         * 
         * @return the row of the top-left corner cell.
         */
        public int getRow1() {
            return row1;
        }

        /**
         * @deprecated Use {@link #getRow2()} instead.
         */
        @Deprecated
        public int getY2() {
            return getRow2();
        }

        /**
         * Gets the row of the bottom-right corner cell.
         * 
         * @return the row of the bottom-right corner cell.
         */
        public int getRow2() {
            return row2;
        }

    }

    /**
     * Gridlayout does not support laying components on top of each other. An
     * <code>OverlapsException</code> is thrown when a component already exists
     * (even partly) at the same space on a grid with the new component.
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class OverlapsException extends java.lang.RuntimeException {

        private final Area existingArea;

        /**
         * Constructs an <code>OverlapsException</code>.
         * 
         * @param existingArea
         */
        public OverlapsException(Area existingArea) {
            this.existingArea = existingArea;
        }

        @Override
        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            Component component = existingArea.getComponent();
            sb.append(component);
            sb.append("( type = ");
            sb.append(component.getClass().getName());
            if (component.getCaption() != null) {
                sb.append(", caption = \"");
                sb.append(component.getCaption());
                sb.append("\"");
            }
            sb.append(")");
            sb.append(" is already added to ");
            sb.append(existingArea.column1);
            sb.append(",");
            sb.append(existingArea.column1);
            sb.append(",");
            sb.append(existingArea.row1);
            sb.append(",");
            sb.append(existingArea.row2);
            sb.append("(column1, column2, row1, row2).");

            return sb.toString();
        }

        /**
         * Gets the area .
         * 
         * @return the existing area.
         */
        public Area getArea() {
            return existingArea;
        }
    }

    /**
     * An <code>Exception</code> object which is thrown when an area exceeds the
     * bounds of the grid.
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class OutOfBoundsException extends java.lang.RuntimeException {

        private final Area areaOutOfBounds;

        /**
         * Constructs an <code>OoutOfBoundsException</code> with the specified
         * detail message.
         * 
         * @param areaOutOfBounds
         */
        public OutOfBoundsException(Area areaOutOfBounds) {
            this.areaOutOfBounds = areaOutOfBounds;
        }

        /**
         * Gets the area that is out of bounds.
         * 
         * @return the area out of Bound.
         */
        public Area getArea() {
            return areaOutOfBounds;
        }
    }

    /**
     * Sets the number of columns in the grid. The column count can not be
     * reduced if there are any areas that would be outside of the shrunk grid.
     * 
     * @param columns
     *            the new number of columns in the grid.
     */
    public void setColumns(int columns) {

        // The the param
        if (columns < 1) {
            throw new IllegalArgumentException(
                    "The number of columns and rows in the grid must be at least 1");
        }

        // In case of no change
        if (getColumns() == columns) {
            return;
        }

        // Checks for overlaps
        if (getColumns() > columns) {
            for (final Iterator<Area> i = areas.iterator(); i.hasNext();) {
                final Area area = i.next();
                if (area.column2 >= columns) {
                    throw new OutOfBoundsException(area);
                }
            }
        }

        getState().setColumns(columns);

        requestRepaint();
    }

    /**
     * Get the number of columns in the grid.
     * 
     * @return the number of columns in the grid.
     */
    public int getColumns() {
        return getState().getColumns();
    }

    /**
     * Sets the number of rows in the grid. The number of rows can not be
     * reduced if there are any areas that would be outside of the shrunk grid.
     * 
     * @param rows
     *            the new number of rows in the grid.
     */
    public void setRows(int rows) {

        // The the param
        if (rows < 1) {
            throw new IllegalArgumentException(
                    "The number of columns and rows in the grid must be at least 1");
        }

        // In case of no change
        if (getRows() == rows) {
            return;
        }

        // Checks for overlaps
        if (getRows() > rows) {
            for (final Iterator<Area> i = areas.iterator(); i.hasNext();) {
                final Area area = i.next();
                if (area.row2 >= rows) {
                    throw new OutOfBoundsException(area);
                }
            }
        }

        getState().setRows(rows);

        requestRepaint();
    }

    /**
     * Get the number of rows in the grid.
     * 
     * @return the number of rows in the grid.
     */
    public int getRows() {
        return getState().getRows();
    }

    /**
     * Gets the current x-position (column) of the cursor.
     * 
     * <p>
     * The cursor position points the position for the next component that is
     * added without specifying its coordinates (grid cell). When the cursor
     * position is occupied, the next component will be added to first free
     * position after the cursor.
     * </p>
     * 
     * @return the grid column the cursor is on, starting from 0.
     */
    public int getCursorX() {
        return cursorX;
    }

    /**
     * Sets the current cursor x-position. This is usually handled automatically
     * by GridLayout.
     * 
     * @param cursorX
     */
    public void setCursorX(int cursorX) {
        this.cursorX = cursorX;
    }

    /**
     * Gets the current y-position (row) of the cursor.
     * 
     * <p>
     * The cursor position points the position for the next component that is
     * added without specifying its coordinates (grid cell). When the cursor
     * position is occupied, the next component will be added to the first free
     * position after the cursor.
     * </p>
     * 
     * @return the grid row the Cursor is on.
     */
    public int getCursorY() {
        return cursorY;
    }

    /**
     * Sets the current y-coordinate (row) of the cursor. This is usually
     * handled automatically by GridLayout.
     * 
     * @param cursorY
     *            the row number, starting from 0 for the topmost row.
     */
    public void setCursorY(int cursorY) {
        this.cursorY = cursorY;
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        Area oldLocation = null;
        Area newLocation = null;
        for (final Iterator<Area> i = areas.iterator(); i.hasNext();) {
            final Area location = i.next();
            final Component component = location.getComponent();
            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }
        }

        if (oldLocation == null) {
            addComponent(newComponent);
        } else if (newLocation == null) {
            removeComponent(oldComponent);
            addComponent(newComponent, oldLocation.getColumn1(),
                    oldLocation.getRow1(), oldLocation.getColumn2(),
                    oldLocation.getRow2());
        } else {
            oldLocation.setComponent(newComponent);
            newLocation.setComponent(oldComponent);
            requestRepaint();
        }
    }

    /*
     * Removes all components from this container.
     * 
     * @see com.vaadin.ui.ComponentContainer#removeAllComponents()
     */
    @Override
    public void removeAllComponents() {
        super.removeAllComponents();
        componentToAlignment = new HashMap<Component, Alignment>();
        cursorX = 0;
        cursorY = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.AlignmentHandler#setComponentAlignment(com
     * .vaadin.ui.Component, int, int)
     */
    public void setComponentAlignment(Component childComponent,
            int horizontalAlignment, int verticalAlignment) {
        componentToAlignment.put(childComponent, new Alignment(
                horizontalAlignment + verticalAlignment));
        requestRepaint();
    }

    public void setComponentAlignment(Component childComponent,
            Alignment alignment) {
        componentToAlignment.put(childComponent, alignment);
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#setSpacing(boolean)
     */
    public void setSpacing(boolean spacing) {
        getState().setSpacing(spacing);
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#isSpacing()
     */
    public boolean isSpacing() {
        return getState().isSpacing();
    }

    /**
     * Inserts an empty row at the specified position in the grid.
     * 
     * @param row
     *            Index of the row before which the new row will be inserted.
     *            The leftmost row has index 0.
     */
    public void insertRow(int row) {
        if (row > getRows()) {
            throw new IllegalArgumentException("Cannot insert row at " + row
                    + " in a gridlayout with height " + getRows());
        }

        for (Iterator<Area> i = areas.iterator(); i.hasNext();) {
            Area existingArea = i.next();
            // Areas ending below the row needs to be moved down or stretched
            if (existingArea.row2 >= row) {
                existingArea.row2++;

                // Stretch areas that span over the selected row
                if (existingArea.row1 >= row) {
                    existingArea.row1++;
                }

            }
        }

        if (cursorY >= row) {
            cursorY++;
        }

        setRows(getRows() + 1);
        structuralChange = true;
        requestRepaint();
    }

    /**
     * Removes a row and all the components in the row.
     * 
     * <p>
     * Components which span over several rows are removed if the selected row
     * is on the first row of such a component.
     * </p>
     * 
     * <p>
     * If the last row is removed then all remaining components will be removed
     * and the grid will be reduced to one row. The cursor will be moved to the
     * upper left cell of the grid.
     * </p>
     * 
     * @param row
     *            Index of the row to remove. The leftmost row has index 0.
     */
    public void removeRow(int row) {
        if (row >= getRows()) {
            throw new IllegalArgumentException("Cannot delete row " + row
                    + " from a gridlayout with height " + getRows());
        }

        // Remove all components in row
        for (int col = 0; col < getColumns(); col++) {
            removeComponent(col, row);
        }

        // Shrink or remove areas in the selected row
        for (Iterator<Area> i = areas.iterator(); i.hasNext();) {
            Area existingArea = i.next();
            if (existingArea.row2 >= row) {
                existingArea.row2--;

                if (existingArea.row1 > row) {
                    existingArea.row1--;
                }
            }
        }

        if (getRows() == 1) {
            /*
             * Removing the last row means that the dimensions of the Grid
             * layout will be truncated to 1 empty row and the cursor is moved
             * to the first cell
             */
            cursorX = 0;
            cursorY = 0;
        } else {
            setRows(getRows() - 1);
            if (cursorY > row) {
                cursorY--;
            }
        }

        structuralChange = true;
        requestRepaint();

    }

    /**
     * Sets the expand ratio of given column.
     * 
     * <p>
     * The expand ratio defines how excess space is distributed among columns.
     * Excess space means space that is left over from components that are not
     * sized relatively. By default, the excess space is distributed evenly.
     * </p>
     * 
     * <p>
     * Note that the component width of the GridLayout must be defined (fixed or
     * relative, as opposed to undefined) for this method to have any effect.
     * </p>
     * 
     * @see #setWidth(float, int)
     * 
     * @param columnIndex
     * @param ratio
     */
    public void setColumnExpandRatio(int columnIndex, float ratio) {
        columnExpandRatio.put(columnIndex, ratio);
        requestRepaint();
    }

    /**
     * Returns the expand ratio of given column
     * 
     * @see #setColumnExpandRatio(int, float)
     * 
     * @param columnIndex
     * @return the expand ratio, 0.0f by default
     */
    public float getColumnExpandRatio(int columnIndex) {
        Float r = columnExpandRatio.get(columnIndex);
        return r == null ? 0 : r.floatValue();
    }

    /**
     * Sets the expand ratio of given row.
     * 
     * <p>
     * Expand ratio defines how excess space is distributed among rows. Excess
     * space means the space left over from components that are not sized
     * relatively. By default, the excess space is distributed evenly.
     * </p>
     * 
     * <p>
     * Note, that height needs to be defined (fixed or relative, as opposed to
     * undefined height) for this method to have any effect.
     * </p>
     * 
     * @see #setHeight(float, int)
     * 
     * @param rowIndex
     *            The row index, starting from 0 for the topmost row.
     * @param ratio
     */
    public void setRowExpandRatio(int rowIndex, float ratio) {
        rowExpandRatio.put(rowIndex, ratio);
        requestRepaint();
    }

    /**
     * Returns the expand ratio of given row.
     * 
     * @see #setRowExpandRatio(int, float)
     * 
     * @param rowIndex
     *            The row index, starting from 0 for the topmost row.
     * @return the expand ratio, 0.0f by default
     */
    public float getRowExpandRatio(int rowIndex) {
        Float r = rowExpandRatio.get(rowIndex);
        return r == null ? 0 : r.floatValue();
    }

    /**
     * Gets the Component at given index.
     * 
     * @param x
     *            The column index, starting from 0 for the leftmost column.
     * @param y
     *            The row index, starting from 0 for the topmost row.
     * @return Component in given cell or null if empty
     */
    public Component getComponent(int x, int y) {
        for (final Iterator<Area> iterator = areas.iterator(); iterator
                .hasNext();) {
            final Area area = iterator.next();
            if (area.getColumn1() <= x && x <= area.getColumn2()
                    && area.getRow1() <= y && y <= area.getRow2()) {
                return area.getComponent();
            }
        }
        return null;
    }

    /**
     * Returns information about the area where given component is laid in the
     * GridLayout.
     * 
     * @param component
     *            the component whose area information is requested.
     * @return an Area object that contains information how component is laid in
     *         the grid
     */
    public Area getComponentArea(Component component) {
        for (final Iterator<Area> iterator = areas.iterator(); iterator
                .hasNext();) {
            final Area area = iterator.next();
            if (area.getComponent() == component) {
                return area;
            }
        }
        return null;
    }

    /**
     * Sets the component alignment using a shorthand string notation.
     * 
     * @deprecated Replaced by
     *             {@link #setComponentAlignment(Component, Alignment)}
     * 
     * @param component
     *            A child component in this layout
     * @param alignment
     *            A short hand notation described in {@link AlignmentUtils}
     */
    @Deprecated
    public void setComponentAlignment(Component component, String alignment) {
        AlignmentUtils.setComponentAlignment(this, component, alignment);
    }

    public void addListener(LayoutClickListener listener) {
        addListener(LayoutClickEventHandler.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    public void removeListener(LayoutClickListener listener) {
        removeListener(LayoutClickEventHandler.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener);
    }

}
