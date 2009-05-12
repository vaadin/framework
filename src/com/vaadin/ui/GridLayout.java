/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <p>
 * A container that consists of components with certain coordinates (cell
 * position) on a grid. It also maintains cursor for adding component in left to
 * right, top to bottom order.
 * </p>
 * 
 * <p>
 * Each component in a <code>GridLayout</code> uses a certain
 * {@link GridLayout.Area area} (column1,row1,column2,row2) from the grid. One
 * should not add components that would overlap with the existing components
 * because in such case an {@link OverlapsException} is thrown. Adding component
 * with cursor automatically extends the grid by increasing the grid height.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class GridLayout extends AbstractLayout implements
        Layout.AlignmentHandler, Layout.SpacingHandler {

    /**
     * Initial grid columns.
     */
    private int cols = 0;

    /**
     * Initial grid rows.
     */
    private int rows = 0;

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
    private final LinkedList areas = new LinkedList();

    /**
     * Mapping from components to their respective areas.
     */
    private final LinkedList components = new LinkedList();

    /**
     * Mapping from components to alignments (horizontal + vertical).
     */
    private Map<Component, Alignment> componentToAlignment = new HashMap<Component, Alignment>();

    /**
     * Is spacing between contained components enabled. Defaults to false.
     */
    private boolean spacing = false;

    private static final Alignment ALIGNMENT_DEFAULT = Alignment.TOP_LEFT;

    /**
     * Has there been rows inserted or deleted in the middle of the layout since
     * the last paint operation.
     */
    private boolean structuralChange = false;

    private Map<Integer, Float> columnExpandRatio = new HashMap<Integer, Float>();
    private Map<Integer, Float> rowExpandRatio = new HashMap<Integer, Float>();

    /**
     * Constructor for grid of given size (number of cells). Note that grid's
     * final size depends on the items that are added into the grid. Grid grows
     * if you add components outside the grid's area.
     * 
     * @param columns
     *            Number of columns in the grid.
     * @param rows
     *            Number of rows in the grid.
     */
    public GridLayout(int columns, int rows) {
        setColumns(columns);
        setRows(rows);
    }

    /**
     * Constructs an empty grid layout that is extended as needed.
     */
    public GridLayout() {
        this(1, 1);
    }

    /**
     * <p>
     * Adds a component with a specified area to the grid. The area the new
     * component should take is defined by specifying the upper left corner
     * (column1, row1) and the lower right corner (column2, row2) of the area.
     * </p>
     * 
     * <p>
     * If the new component overlaps with any of the existing components already
     * present in the grid the operation will fail and an
     * {@link OverlapsException} is thrown.
     * </p>
     * 
     * @param c
     *            the component to be added.
     * @param column1
     *            the column of the upper left corner of the area <code>c</code>
     *            is supposed to occupy.
     * @param row1
     *            the row of the upper left corner of the area <code>c</code> is
     *            supposed to occupy.
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
        if (column1 < 0 || row1 < 0 || column2 >= cols || row2 >= rows) {
            throw new OutOfBoundsException(area);
        }

        // Checks that newItem does not overlap with existing items
        checkExistingOverlaps(area);

        // first attemt to add to super
        super.addComponent(component);

        // Inserts the component to right place at the list
        // Respect top-down, left-right ordering
        // component.setParent(this);
        final Iterator i = areas.iterator();
        int index = 0;
        boolean done = false;
        while (!done && i.hasNext()) {
            final Area existingArea = (Area) i.next();
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

        // update cursor position, if it's within this area; use first position
        // outside this area, even if it's occupied
        if (cursorX >= column1 && cursorX <= column2 && cursorY >= row1
                && cursorY <= row2) {
            // cursor within area
            cursorX = column2 + 1; // one right of area
            if (cursorX >= cols) {
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
        for (final Iterator i = areas.iterator(); i.hasNext();) {
            final Area existingArea = (Area) i.next();
            if (existingArea.overlaps(area)) {
                // Component not added, overlaps with existing component
                throw new OverlapsException(existingArea);
            }
        }
    }

    /**
     * Adds the component into this container to cells column1,row1 (NortWest
     * corner of the area.) End coordinates (SouthEast corner of the area) are
     * the same as column1,row1. Component width and height is 1.
     * 
     * @param c
     *            the component to be added.
     * @param column
     *            the column index.
     * @param row
     *            the row index.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cell is outside the grid area.
     */
    public void addComponent(Component c, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        this.addComponent(c, column, row, column, row);
    }

    /**
     * Force the next component to be added to the beginning of the next line.
     * By calling this function user can ensure that no more components are
     * added to the right of the previous component.
     * 
     * @see #space()
     */
    public void newLine() {
        cursorX = 0;
        cursorY++;
    }

    /**
     * Moves the cursor forwards by one. If the cursor goes out of the right
     * grid border, move it to next line.
     * 
     * @see #newLine()
     */
    public void space() {
        cursorX++;
        if (cursorX >= cols) {
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
     * @param c
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
        cols = cursorX >= cols ? cursorX + 1 : cols;
        rows = cursorY >= rows ? cursorY + 1 : rows;

        addComponent(component, cursorX, cursorY);
    }

    /**
     * Removes the given component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component component) {

        // Check that the component is contained in the container
        if (component == null || !components.contains(component)) {
            return;
        }

        super.removeComponent(component);

        Area area = null;
        for (final Iterator i = areas.iterator(); area == null && i.hasNext();) {
            final Area a = (Area) i.next();
            if (a.getComponent() == component) {
                area = a;
            }
        }

        components.remove(component);
        if (area != null) {
            areas.remove(area);
        }

        componentToAlignment.remove(component);

        requestRepaint();
    }

    /**
     * Removes the component specified with it's cell index.
     * 
     * @param column
     *            the Component's column.
     * @param row
     *            the Component's row.
     */
    public void removeComponent(int column, int row) {

        // Finds the area
        for (final Iterator i = areas.iterator(); i.hasNext();) {
            final Area area = (Area) i.next();
            if (area.getColumn1() == column && area.getRow1() == row) {
                removeComponent(area.getComponent());
                return;
            }
        }
    }

    /**
     * Gets an Iterator to the component container contents. Using the Iterator
     * it's possible to step through the contents of the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return Collections.unmodifiableCollection(components).iterator();
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
        target.addAttribute("h", rows);
        target.addAttribute("w", cols);

        target.addAttribute("structuralChange", structuralChange);
        structuralChange = false;

        if (spacing) {
            target.addAttribute("spacing", spacing);
        }

        // Area iterator
        final Iterator areaiterator = areas.iterator();

        // Current item to be processed (fetch first item)
        Area area = areaiterator.hasNext() ? (Area) areaiterator.next() : null;

        // Collects rowspan related information here
        final HashMap cellUsed = new HashMap();

        // Empty cell collector
        int emptyCells = 0;

        final String[] alignmentsArray = new String[components.size()];
        final Integer[] columnExpandRatioArray = new Integer[cols];
        final Integer[] rowExpandRatioArray = new Integer[rows];

        int realColExpandRatioSum = 0;
        float colSum = getExpandRatioSum(columnExpandRatio);
        if (colSum == 0) {
            // no columns has been expanded, all cols have same expand
            // rate
            float equalSize = 1 / (float) cols;
            int myRatio = Math.round(equalSize * 1000);
            for (int i = 0; i < cols; i++) {
                columnExpandRatioArray[i] = myRatio;
            }
            realColExpandRatioSum = myRatio * cols;
        } else {
            for (int i = 0; i < cols; i++) {
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
            float equalSize = 1 / (float) rows;
            int myRatio = Math.round(equalSize * 1000);
            for (int i = 0; i < rows; i++) {
                rowExpandRatioArray[i] = myRatio;
            }
            realRowExpandRatioSum = myRatio * rows;
        }

        int index = 0;

        // Iterates every applicable row
        for (int cury = 0; cury < rows; cury++) {
            target.startTag("gr");

            if (!equallyDividedRows) {
                int myRatio = Math
                        .round((getRowExpandRatio(cury) / rowSum) * 1000);
                rowExpandRatioArray[cury] = myRatio;
                realRowExpandRatioSum += myRatio;

            }
            // Iterates every applicable column
            for (int curx = 0; curx < cols; curx++) {

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
                        area = (Area) areaiterator.next();
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
                        final int rowspanDepth = ((Integer) cellUsed
                                .get(new Integer(curx))).intValue();

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
                            cellUsed.remove(new Integer(curx));
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
                target.addAttribute("x", cols - emptyCells);
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
     * Gets the components UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     * @see com.vaadin.ui.AbstractComponent#getTag()
     */
    @Override
    public String getTag() {
        return "gridlayout";
    }

    /**
     * This class defines an area on a grid. An Area is defined by the cells of
     * its upper left corner (column1,row1) and lower right corner
     * (column2,row2).
     * 
     * @author IT Mill Ltd.
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
         * Component painted on the area.
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
         *            The column of the upper left corner cell of the area
         *            <code>c</code> is supposed to occupy.
         * @param row1
         *            The row of the upper left corner cell of the area
         *            <code>c</code> is supposed to occupy.
         * @param column2
         *            The column of the lower right corner cell of the area
         *            <code>c</code> is supposed to occupy.
         * @param row2
         *            The row of the lower right corner cell of the area
         *            <code>c</code> is supposed to occupy.
         * @throws OverlapsException
         *             if the new component overlaps with any of the components
         *             already in the grid
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
         * Tests if the given Area overlaps with an another Area.
         * 
         * @param other
         *            the Another Area that's to be tested for overlap with this
         *            area.
         * @return <code>true</code> if <code>other</code> overlaps with this
         *         area, <code>false</code> if it doesn't.
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
         * This function only sets the value in the datastructure and does not
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
         * @deprecated Use getColumn1() instead.
         * 
         * @see com.vaadin.ui.GridLayout#getColumn1()
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
         * @deprecated Use getColumn2() instead.
         * 
         * @see com.vaadin.ui.GridLayout#getColumn2()
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
         * @deprecated Use getRow1() instead.
         * 
         * @see com.vaadin.ui.GridLayout#getRow1()
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
         * @deprecated Use getRow2() instead.
         * 
         * @see com.vaadin.ui.GridLayout#getRow2()
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
     * An <code>Exception</code> object which is thrown when two Items occupy
     * the same space on a grid.
     * 
     * @author IT Mill Ltd.
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
     * @author IT Mill Ltd.
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
        if (cols == columns) {
            return;
        }

        // Checks for overlaps
        if (cols > columns) {
            for (final Iterator i = areas.iterator(); i.hasNext();) {
                final Area area = (Area) i.next();
                if (area.column2 >= columns) {
                    throw new OutOfBoundsException(area);
                }
            }
        }

        cols = columns;

        requestRepaint();
    }

    /**
     * Get the number of columns in the grid.
     * 
     * @return the number of columns in the grid.
     */
    public final int getColumns() {
        return cols;
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
        if (this.rows == rows) {
            return;
        }

        // Checks for overlaps
        if (this.rows > rows) {
            for (final Iterator i = areas.iterator(); i.hasNext();) {
                final Area area = (Area) i.next();
                if (area.row2 >= rows) {
                    throw new OutOfBoundsException(area);
                }
            }
        }

        this.rows = rows;

        requestRepaint();
    }

    /**
     * Get the number of rows in the grid.
     * 
     * @return the number of rows in the grid.
     */
    public final int getRows() {
        return rows;
    }

    /**
     * Gets the current cursor x-position. The cursor position points the
     * position for the next component that is added without specifying its
     * coordinates (grid cell). When the cursor position is occupied, the next
     * component will be added to first free position after the cursor.
     * 
     * @return the grid column the Cursor is on.
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
     * Gets the current cursor y-position. The cursor position points the
     * position for the next component that is added without specifying its
     * coordinates (grid cell). When the cursor position is occupied, the next
     * component will be added to first free position after the cursor.
     * 
     * @return the grid row the Cursor is on.
     */
    public int getCursorY() {
        return cursorY;
    }

    /**
     * Sets the current cursor y-position. This is usually handled automatically
     * by GridLayout.
     * 
     * @param cursorY
     */
    public void setCursorY(int cursorY) {
        this.cursorY = cursorY;
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        Area oldLocation = null;
        Area newLocation = null;
        for (final Iterator i = areas.iterator(); i.hasNext();) {
            final Area location = (Area) i.next();
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
            addComponent(newComponent, oldLocation.getColumn1(), oldLocation
                    .getRow1(), oldLocation.getColumn2(), oldLocation.getRow2());
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
        componentToAlignment = new HashMap();
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
    public void setSpacing(boolean enabled) {
        spacing = enabled;
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#isSpacing()
     */
    @Deprecated
    public boolean isSpacingEnabled() {
        return spacing;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#isSpacing()
     */
    public boolean isSpacing() {
        return spacing;
    }

    /**
     * Inserts an empty row at the chosen position in the grid.
     * 
     * @param row
     *            Number of the row the new row will be inserted before
     */
    public void insertRow(int row) {
        if (row > rows) {
            throw new IllegalArgumentException("Cannot insert row at " + row
                    + " in a gridlayout with height " + rows);
        }

        for (Iterator i = areas.iterator(); i.hasNext();) {
            Area existingArea = (Area) i.next();
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

        setRows(rows + 1);
        structuralChange = true;
        requestRepaint();
    }

    /**
     * Removes row and all components in the row. Components which span over
     * several rows are removed if the selected row is the component's first
     * row.
     * 
     * @param row
     *            The row number to remove
     */
    public void removeRow(int row) {
        if (row >= rows) {
            throw new IllegalArgumentException("Cannot delete row " + row
                    + " from a gridlayout with height " + rows);
        }

        // Remove all components in row
        for (int col = 0; col < getColumns(); col++) {
            removeComponent(col, row);
        }

        // Shrink or remove areas in the selected row
        for (Iterator i = areas.iterator(); i.hasNext();) {
            Area existingArea = (Area) i.next();
            if (existingArea.row2 >= row) {
                existingArea.row2--;

                if (existingArea.row1 > row) {
                    existingArea.row1--;
                }
            }
        }

        setRows(rows - 1);
        if (cursorY > row) {
            cursorY--;
        }

        structuralChange = true;
        requestRepaint();

    }

    /**
     * Sets the expand ratio of given column. Expand ratio defines how excess
     * space is distributed among columns. Excess space means the space not
     * consumed by non relatively sized components.
     * 
     * <p>
     * By default excess space is distributed evenly.
     * 
     * <p>
     * Note, that width needs to be defined for this method to have any effect.
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
     * Sets the expand ratio of given row. Expand ratio defines how excess space
     * is distributed among rows. Excess space means the space not consumed by
     * non relatively sized components.
     * 
     * <p>
     * By default excess space is distributed evenly.
     * 
     * <p>
     * Note, that height needs to be defined for this method to have any effect.
     * 
     * @see #setHeight(float, int)
     * 
     * @param rowIndex
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
     *            x-index
     * @param y
     *            y-index
     * @return Component in given cell or null if empty
     */
    public Component getComponent(int x, int y) {
        for (final Iterator iterator = areas.iterator(); iterator.hasNext();) {
            final Area area = (Area) iterator.next();
            if (area.getColumn1() <= x && x <= area.getColumn2()
                    && area.getRow1() <= y && y <= area.getRow2()) {
                return area.getComponent();
            }
        }
        return null;
    }

    /**
     * Returns information about the area where given component is layed in the
     * GridLayout.
     * 
     * @param component
     *            the component whose area information is requested.
     * @return an Area object that contains information how component is layed
     *         in the grid
     */
    public Area getComponentArea(Component component) {
        for (final Iterator iterator = areas.iterator(); iterator.hasNext();) {
            final Area area = (Area) iterator.next();
            if (area.getComponent() == component) {
                return area;
            }
        }
        return null;
    }

    public void setComponentAlignment(Component component, String alignment) {
        AlignmentUtils.setComponentAlignment(this, component, alignment);
    }

}
