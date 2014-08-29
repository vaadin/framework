/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.gridlayout.GridLayoutConnector;
import com.vaadin.client.ui.layout.ComponentConnectorLayoutSlot;
import com.vaadin.client.ui.layout.VLayoutSlot;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.gridlayout.GridLayoutState.ChildComponentData;

public class VGridLayout extends ComplexPanel {

    public static final String CLASSNAME = "v-gridlayout";

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public HashMap<Widget, Cell> widgetToCell = new HashMap<Widget, Cell>();

    /** For internal use only. May be removed or replaced in the future. */
    public int[] columnWidths;

    /** For internal use only. May be removed or replaced in the future. */
    public int[] rowHeights;

    /** For internal use only. May be removed or replaced in the future. */
    public int[] colExpandRatioArray;

    /** For internal use only. May be removed or replaced in the future. */
    public int[] rowExpandRatioArray;

    int[] minColumnWidths;

    private int[] minRowHeights;

    /** For internal use only. May be removed or replaced in the future. */
    public DivElement spacingMeasureElement;
    public Set<Integer> explicitRowRatios;
    public Set<Integer> explicitColRatios;

    public boolean hideEmptyRowsAndColumns = false;

    public VGridLayout() {
        super();
        setElement(Document.get().createDivElement());

        spacingMeasureElement = Document.get().createDivElement();
        Style spacingStyle = spacingMeasureElement.getStyle();
        spacingStyle.setPosition(Position.ABSOLUTE);
        getElement().appendChild(spacingMeasureElement);

        setStyleName(CLASSNAME);
        addStyleName(StyleConstants.UI_LAYOUT);
    }

    private GridLayoutConnector getConnector() {
        return (GridLayoutConnector) ConnectorMap.get(client)
                .getConnector(this);
    }

    /**
     * Returns the column widths measured in pixels
     * 
     * @return
     */
    protected int[] getColumnWidths() {
        return columnWidths;
    }

    /**
     * Returns the row heights measured in pixels
     * 
     * @return
     */
    protected int[] getRowHeights() {
        return rowHeights;
    }

    /**
     * Returns the spacing between the cells horizontally in pixels
     * 
     * @return
     */
    protected int getHorizontalSpacing() {
        return LayoutManager.get(client).getOuterWidth(spacingMeasureElement);
    }

    /**
     * Returns the spacing between the cells vertically in pixels
     * 
     * @return
     */
    protected int getVerticalSpacing() {
        return LayoutManager.get(client).getOuterHeight(spacingMeasureElement);
    }

    static int[] cloneArray(int[] toBeCloned) {
        int[] clone = new int[toBeCloned.length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = toBeCloned[i] * 1;
        }
        return clone;
    }

    void expandRows() {
        if (!isUndefinedHeight()) {
            int usedSpace = calcRowUsedSpace();
            int[] actualExpandRatio = calcRowExpandRatio();
            int availableSpace = LayoutManager.get(client).getInnerHeight(
                    getElement());
            int excessSpace = availableSpace - usedSpace;
            int distributed = 0;
            if (excessSpace > 0) {
                int expandRatioSum = 0;
                for (int i = 0; i < rowHeights.length; i++) {
                    expandRatioSum += actualExpandRatio[i];
                }
                for (int i = 0; i < rowHeights.length; i++) {
                    int ew = excessSpace * actualExpandRatio[i]
                            / expandRatioSum;
                    rowHeights[i] = minRowHeights[i] + ew;
                    distributed += ew;
                }
                excessSpace -= distributed;
                int c = 0;
                while (excessSpace > 0) {
                    rowHeights[c % rowHeights.length]++;
                    excessSpace--;
                    c++;
                }
            }
        }
    }

    private int[] calcRowExpandRatio() {
        int[] actualExpandRatio = new int[minRowHeights.length];
        for (int i = 0; i < minRowHeights.length; i++) {
            if (hiddenEmptyRow(i)) {
                actualExpandRatio[i] = 0;
            } else {
                actualExpandRatio[i] = rowExpandRatioArray[i];
            }
        }
        return actualExpandRatio;
    }

    /**
     * Checks if it is ok to hide (or ignore) the given row.
     * 
     * @param rowIndex
     *            the row to check
     * @return true, if the row should be interpreted as non-existant (hides
     *         extra spacing)
     */
    private boolean hiddenEmptyRow(int rowIndex) {
        return hideEmptyRowsAndColumns && !rowHasComponentsOrRowSpan(rowIndex)
                && !explicitRowRatios.contains(rowIndex);
    }

    /**
     * Checks if it is ok to hide (or ignore) the given column.
     * 
     * @param columnIndex
     *            the column to check
     * @return true, if the column should be interpreted as non-existant (hides
     *         extra spacing)
     */
    private boolean hiddenEmptyColumn(int columnIndex) {
        return hideEmptyRowsAndColumns
                && !colHasComponentsOrColSpan(columnIndex)
                && !explicitColRatios.contains(columnIndex);
    }

    private int calcRowUsedSpace() {
        int usedSpace = minRowHeights[0];
        int verticalSpacing = getVerticalSpacing();
        for (int i = 1; i < minRowHeights.length; i++) {
            if (minRowHeights[i] > 0 || !hiddenEmptyRow(i)) {
                usedSpace += verticalSpacing + minRowHeights[i];
            }
        }
        return usedSpace;
    }

    void expandColumns() {
        if (!isUndefinedWidth()) {
            int usedSpace = calcColumnUsedSpace();
            int[] actualExpandRatio = calcColumnExpandRatio();
            int availableSpace = LayoutManager.get(client).getInnerWidth(
                    getElement());
            int excessSpace = availableSpace - usedSpace;
            int distributed = 0;
            if (excessSpace > 0) {
                int expandRatioSum = 0;
                for (int i = 0; i < columnWidths.length; i++) {
                    expandRatioSum += actualExpandRatio[i];
                }
                for (int i = 0; i < columnWidths.length; i++) {
                    int ew = excessSpace * actualExpandRatio[i]
                            / expandRatioSum;
                    columnWidths[i] = minColumnWidths[i] + ew;
                    distributed += ew;
                }
                excessSpace -= distributed;
                int c = 0;
                while (excessSpace > 0) {
                    columnWidths[c % columnWidths.length]++;
                    excessSpace--;
                    c++;
                }
            }
        }
    }

    /**
     * Calculates column expand ratio.
     */
    private int[] calcColumnExpandRatio() {
        int[] actualExpandRatio = new int[minColumnWidths.length];
        for (int i = 0; i < minColumnWidths.length; i++) {
            if (!hiddenEmptyColumn(i)) {
                actualExpandRatio[i] = colExpandRatioArray[i];
            } else {
                actualExpandRatio[i] = 0;
            }
        }
        return actualExpandRatio;
    }

    /**
     * Calculates column used space
     */
    private int calcColumnUsedSpace() {
        int usedSpace = minColumnWidths[0];
        int horizontalSpacing = getHorizontalSpacing();
        for (int i = 1; i < minColumnWidths.length; i++) {
            if (minColumnWidths[i] > 0 || !hiddenEmptyColumn(i)) {
                usedSpace += horizontalSpacing + minColumnWidths[i];
            }
        }
        return usedSpace;
    }

    private boolean rowHasComponentsOrRowSpan(int i) {
        for (Cell cell : widgetToCell.values()) {
            if (cell.row == i) {
                return true;
            }
        }
        for (SpanList l : rowSpans) {
            for (Cell cell : l.cells) {
                if (cell.row <= i && i < cell.row + cell.rowspan) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean colHasComponentsOrColSpan(int i) {
        for (Cell cell : widgetToCell.values()) {
            if (cell.col == i) {
                return true;
            }
        }
        for (SpanList l : colSpans) {
            for (Cell cell : l.cells) {
                if (cell.col <= i && i < cell.col + cell.colspan) {
                    return true;
                }
            }
        }
        return false;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateHeight() {
        // Detect minimum heights & calculate spans
        detectRowHeights();

        // Expand
        expandRows();

        // Position
        layoutCellsVertically();
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateWidth() {
        // Detect widths & calculate spans
        detectColWidths();
        // Expand
        expandColumns();
        // Position
        layoutCellsHorizontally();

    }

    void layoutCellsVertically() {
        int verticalSpacing = getVerticalSpacing();
        LayoutManager layoutManager = LayoutManager.get(client);
        Element element = getElement();
        int paddingTop = layoutManager.getPaddingTop(element);
        int paddingBottom = layoutManager.getPaddingBottom(element);

        int y = paddingTop;
        for (int column = 0; column < cells.length; column++) {
            y = paddingTop + 1 - 1; // Ensure IE10 does not optimize this out by
                                    // adding something to evaluate on the RHS
                                    // #11303

            for (int row = 0; row < cells[column].length; row++) {
                Cell cell = cells[column][row];
                if (cell != null) {
                    int reservedMargin;
                    if (cell.rowspan + row >= cells[column].length) {
                        // Make room for layout padding for cells reaching the
                        // bottom of the layout
                        reservedMargin = paddingBottom;
                    } else {
                        reservedMargin = 0;
                    }

                    cell.layoutVertically(y, reservedMargin);
                }
                if (!hideEmptyRowsAndColumns || rowHasComponentsOrRowSpan(row)
                        || rowHeights[row] > 0) {
                    y += rowHeights[row] + verticalSpacing;
                }
            }
        }

        if (isUndefinedHeight()) {
            int outerHeight = y - verticalSpacing
                    + layoutManager.getPaddingBottom(element)
                    + layoutManager.getBorderHeight(element);
            element.getStyle().setHeight(outerHeight, Unit.PX);

            getConnector().getLayoutManager().reportOuterHeight(getConnector(),
                    outerHeight);
        }
    }

    void layoutCellsHorizontally() {
        LayoutManager layoutManager = LayoutManager.get(client);
        Element element = getElement();
        int x = layoutManager.getPaddingLeft(element);
        int paddingRight = layoutManager.getPaddingRight(element);
        int horizontalSpacing = getHorizontalSpacing();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    int reservedMargin;
                    // Make room for layout padding for cells reaching the
                    // right edge of the layout
                    if (i + cell.colspan >= cells.length) {
                        reservedMargin = paddingRight;
                    } else {
                        reservedMargin = 0;
                    }
                    cell.layoutHorizontally(x, reservedMargin);
                }
            }
            if (!hideEmptyRowsAndColumns || colHasComponentsOrColSpan(i)
                    || columnWidths[i] > 0) {
                x += columnWidths[i] + horizontalSpacing;
            }
        }

        if (isUndefinedWidth()) {
            int outerWidth = x - horizontalSpacing
                    + layoutManager.getPaddingRight(element)
                    + layoutManager.getBorderWidth(element);
            element.getStyle().setWidth(outerWidth, Unit.PX);
            getConnector().getLayoutManager().reportOuterWidth(getConnector(),
                    outerWidth);
        }
    }

    private boolean isUndefinedHeight() {
        return getConnector().isUndefinedHeight();
    }

    private boolean isUndefinedWidth() {
        return getConnector().isUndefinedWidth();
    }

    private void detectRowHeights() {
        for (int i = 0; i < rowHeights.length; i++) {
            rowHeights[i] = 0;
        }

        // collect min rowheight from non-rowspanned cells
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    if (cell.rowspan == 1) {
                        if (!cell.hasRelativeHeight()
                                && rowHeights[j] < cell.getHeight()) {
                            rowHeights[j] = cell.getHeight();
                        }
                    } else {
                        storeRowSpannedCell(cell);
                    }
                }
            }
        }

        distributeRowSpanHeights();

        minRowHeights = cloneArray(rowHeights);
    }

    private void detectColWidths() {
        // collect min colwidths from non-colspanned cells
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = 0;
        }

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    if (cell.colspan == 1) {
                        if (!cell.hasRelativeWidth()
                                && columnWidths[i] < cell.getWidth()) {
                            columnWidths[i] = cell.getWidth();
                        }
                    } else {
                        storeColSpannedCell(cell);
                    }
                }
            }
        }

        distributeColSpanWidths();

        minColumnWidths = cloneArray(columnWidths);
    }

    private void storeRowSpannedCell(Cell cell) {
        SpanList l = null;
        for (SpanList list : rowSpans) {
            if (list.span < cell.rowspan) {
                continue;
            } else {
                // insert before this
                l = list;
                break;
            }
        }
        if (l == null) {
            l = new SpanList(cell.rowspan);
            rowSpans.add(l);
        } else if (l.span != cell.rowspan) {
            SpanList newL = new SpanList(cell.rowspan);
            rowSpans.add(rowSpans.indexOf(l), newL);
            l = newL;
        }
        l.cells.add(cell);
    }

    /**
     * Iterates colspanned cells, ensures cols have enough space to accommodate
     * them
     */
    void distributeColSpanWidths() {
        for (SpanList list : colSpans) {
            for (Cell cell : list.cells) {
                // cells with relative content may return non 0 here if on
                // subsequent renders
                int width = cell.hasRelativeWidth() ? 0 : cell.getWidth();
                distributeSpanSize(columnWidths, cell.col, cell.colspan,
                        getHorizontalSpacing(), width, colExpandRatioArray);
            }
        }
    }

    /**
     * Iterates rowspanned cells, ensures rows have enough space to accommodate
     * them
     */
    private void distributeRowSpanHeights() {
        for (SpanList list : rowSpans) {
            for (Cell cell : list.cells) {
                // cells with relative content may return non 0 here if on
                // subsequent renders
                int height = cell.hasRelativeHeight() ? 0 : cell.getHeight();
                distributeSpanSize(rowHeights, cell.row, cell.rowspan,
                        getVerticalSpacing(), height, rowExpandRatioArray);
            }
        }
    }

    private static void distributeSpanSize(int[] dimensions,
            int spanStartIndex, int spanSize, int spacingSize, int size,
            int[] expansionRatios) {
        int allocated = dimensions[spanStartIndex];
        for (int i = 1; i < spanSize; i++) {
            allocated += spacingSize + dimensions[spanStartIndex + i];
        }
        if (allocated < size) {
            // dimensions needs to be expanded due spanned cell
            int neededExtraSpace = size - allocated;
            int allocatedExtraSpace = 0;

            // Divide space according to expansion ratios if any span has a
            // ratio
            int totalExpansion = 0;
            for (int i = 0; i < spanSize; i++) {
                int itemIndex = spanStartIndex + i;
                totalExpansion += expansionRatios[itemIndex];
            }

            for (int i = 0; i < spanSize; i++) {
                int itemIndex = spanStartIndex + i;
                int expansion;
                if (totalExpansion == 0) {
                    // Divide equally among all cells if there are no
                    // expansion ratios
                    expansion = neededExtraSpace / spanSize;
                } else {
                    expansion = neededExtraSpace * expansionRatios[itemIndex]
                            / totalExpansion;
                }
                dimensions[itemIndex] += expansion;
                allocatedExtraSpace += expansion;
            }

            // We might still miss a couple of pixels because of
            // rounding errors...
            if (neededExtraSpace > allocatedExtraSpace) {
                for (int i = 0; i < spanSize; i++) {
                    // Add one pixel to every cell until we have
                    // compensated for any rounding error
                    int itemIndex = spanStartIndex + i;
                    dimensions[itemIndex] += 1;
                    allocatedExtraSpace += 1;
                    if (neededExtraSpace == allocatedExtraSpace) {
                        break;
                    }
                }
            }
        }
    }

    private LinkedList<SpanList> colSpans = new LinkedList<SpanList>();
    private LinkedList<SpanList> rowSpans = new LinkedList<SpanList>();

    private class SpanList {
        final int span;
        List<Cell> cells = new LinkedList<Cell>();

        public SpanList(int span) {
            this.span = span;
        }
    }

    void storeColSpannedCell(Cell cell) {
        SpanList l = null;
        for (SpanList list : colSpans) {
            if (list.span < cell.colspan) {
                continue;
            } else {
                // insert before this
                l = list;
                break;
            }
        }
        if (l == null) {
            l = new SpanList(cell.colspan);
            colSpans.add(l);
        } else if (l.span != cell.colspan) {

            SpanList newL = new SpanList(cell.colspan);
            colSpans.add(colSpans.indexOf(l), newL);
            l = newL;
        }
        l.cells.add(cell);
    }

    Cell[][] cells;

    /**
     * Private helper class.
     */
    /** For internal use only. May be removed or replaced in the future. */
    public class Cell {
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean hasRelativeHeight() {
            if (slot != null) {
                return slot.getChild().isRelativeHeight();
            } else {
                return true;
            }
        }

        /**
         * @return total of spanned cols
         */
        private int getAvailableWidth() {
            int width = columnWidths[col];
            for (int i = 1; i < colspan; i++) {
                width += getHorizontalSpacing() + columnWidths[col + i];
            }
            return width;
        }

        /**
         * @return total of spanned rows
         */
        private int getAvailableHeight() {
            int height = rowHeights[row];
            for (int i = 1; i < rowspan; i++) {
                height += getVerticalSpacing() + rowHeights[row + i];
            }
            return height;
        }

        public void layoutHorizontally(int x, int marginRight) {
            if (slot != null) {
                slot.positionHorizontally(x, getAvailableWidth(), marginRight);
            }
        }

        public void layoutVertically(int y, int marginBottom) {
            if (slot != null) {
                slot.positionVertically(y, getAvailableHeight(), marginBottom);
            }
        }

        public int getWidth() {
            if (slot != null) {
                return slot.getUsedWidth();
            } else {
                return 0;
            }
        }

        public int getHeight() {
            if (slot != null) {
                return slot.getUsedHeight();
            } else {
                return 0;
            }
        }

        protected boolean hasRelativeWidth() {
            if (slot != null) {
                return slot.getChild().isRelativeWidth();
            } else {
                return true;
            }
        }

        private int row;
        private int col;
        int colspan = 1;
        int rowspan = 1;

        private AlignmentInfo alignment;

        /** For internal use only. May be removed or replaced in the future. */
        public ComponentConnectorLayoutSlot slot;

        public void updateCell(ChildComponentData childComponentData) {
            if (row != childComponentData.row1
                    || col != childComponentData.column1) {
                // cell has been moved, update matrix
                if (col < cells.length && cells.length != 0
                        && row < cells[0].length && cells[col][row] == this) {
                    // Remove old reference if still relevant
                    cells[col][row] = null;
                }

                row = childComponentData.row1;
                col = childComponentData.column1;

                cells[col][row] = this;
            }

            // Set cell width
            colspan = 1 + childComponentData.column2
                    - childComponentData.column1;
            // Set cell height
            rowspan = 1 + childComponentData.row2 - childComponentData.row1;
            setAlignment(new AlignmentInfo(childComponentData.alignment));
        }

        public void setComponent(ComponentConnector component) {
            if (slot == null || slot.getChild() != component) {
                slot = new ComponentConnectorLayoutSlot(CLASSNAME, component,
                        getConnector());
                slot.setAlignment(alignment);
                if (component.isRelativeWidth()) {
                    slot.getWrapperElement().getStyle().setWidth(100, Unit.PCT);
                }
                Element slotWrapper = slot.getWrapperElement();
                getElement().appendChild(slotWrapper);

                Widget widget = component.getWidget();
                insert(widget, slotWrapper, getWidgetCount(), false);
                Cell oldCell = widgetToCell.put(widget, this);
                if (oldCell != null) {
                    oldCell.slot.getWrapperElement().removeFromParent();
                    oldCell.slot = null;
                }
            }
        }

        public void setAlignment(AlignmentInfo alignmentInfo) {
            alignment = alignmentInfo;
            if (slot != null) {
                slot.setAlignment(alignmentInfo);
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public Cell getCell(int row, int col) {
        return cells[col][row];
    }

    /**
     * Creates a new Cell with the given coordinates.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param row
     * @param col
     * @return
     */
    public Cell createNewCell(int row, int col) {
        Cell cell = new Cell(row, col);
        cells[col][row] = cell;
        return cell;
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     * @deprecated As of 7.2, call or override {@link #getComponent(Element)}
     *             instead
     */
    @Deprecated
    public ComponentConnector getComponent(
            com.google.gwt.user.client.Element element) {
        return Util.getConnectorForElement(client, this, element);

    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     * 
     * @since 7.2
     */
    public ComponentConnector getComponent(Element element) {
        return getComponent(DOM.asOld(element));
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setCaption(Widget widget, VCaption caption) {
        VLayoutSlot slot = widgetToCell.get(widget).slot;

        if (caption != null) {
            // Logical attach.
            getChildren().add(caption);
        }

        // Physical attach if not null, also removes old caption
        slot.setCaption(caption);

        if (caption != null) {
            // Adopt.
            adopt(caption);
        }
    }

    private void togglePrefixedStyleName(String name, boolean enabled) {
        if (enabled) {
            addStyleDependentName(name);
        } else {
            removeStyleDependentName(name);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateMarginStyleNames(MarginInfo marginInfo) {
        togglePrefixedStyleName("margin-top", marginInfo.hasTop());
        togglePrefixedStyleName("margin-right", marginInfo.hasRight());
        togglePrefixedStyleName("margin-bottom", marginInfo.hasBottom());
        togglePrefixedStyleName("margin-left", marginInfo.hasLeft());
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateSpacingStyleName(boolean spacingEnabled) {
        String styleName = getStylePrimaryName();
        if (spacingEnabled) {
            spacingMeasureElement.addClassName(styleName + "-spacing-on");
            spacingMeasureElement.removeClassName(styleName + "-spacing-off");
        } else {
            spacingMeasureElement.removeClassName(styleName + "-spacing-on");
            spacingMeasureElement.addClassName(styleName + "-spacing-off");
        }
    }

    public void setSize(int rows, int cols) {
        if (cells == null) {
            cells = new Cell[cols][rows];
        } else if (cells.length != cols || cells[0].length != rows) {
            Cell[][] newCells = new Cell[cols][rows];
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (i < cols && j < rows) {
                        newCells[i][j] = cells[i][j];
                    }
                }
            }
            cells = newCells;
        }
    }

    @Override
    public boolean remove(Widget w) {
        boolean removed = super.remove(w);
        if (removed) {
            Cell cell = widgetToCell.remove(w);
            if (cell != null) {
                cell.slot.setCaption(null);
                cell.slot.getWrapperElement().removeFromParent();
                cell.slot = null;

                if (cells.length < cell.col && cells.length != 0
                        && cells[0].length < cell.row
                        && cells[cell.col][cell.row] == cell) {
                    cells[cell.col][cell.row] = null;
                }
            }
        }
        return removed;
    }

}
