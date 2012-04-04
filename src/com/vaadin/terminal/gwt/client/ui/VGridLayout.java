/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ui.layout.ComponentConnectorLayoutSlot;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;

public class VGridLayout extends ComplexPanel {

    public static final String CLASSNAME = "v-gridlayout";

    ApplicationConnection client;

    HashMap<Widget, Cell> widgetToCell = new HashMap<Widget, Cell>();

    int[] columnWidths;
    int[] rowHeights;

    int[] colExpandRatioArray;

    int[] rowExpandRatioArray;

    int[] minColumnWidths;

    private int[] minRowHeights;

    DivElement spacingMeasureElement;

    public VGridLayout() {
        super();
        setElement(Document.get().createDivElement());

        spacingMeasureElement = Document.get().createDivElement();
        Style spacingStyle = spacingMeasureElement.getStyle();
        spacingStyle.setPosition(Position.ABSOLUTE);
        getElement().appendChild(spacingMeasureElement);

        setStyleName(CLASSNAME);
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
            int usedSpace = minRowHeights[0];
            int verticalSpacing = getVerticalSpacing();
            for (int i = 1; i < minRowHeights.length; i++) {
                usedSpace += verticalSpacing + minRowHeights[i];
            }
            int availableSpace = LayoutManager.get(client).getInnerHeight(
                    getElement());
            int excessSpace = availableSpace - usedSpace;
            int distributed = 0;
            if (excessSpace > 0) {
                for (int i = 0; i < rowHeights.length; i++) {
                    int ew = excessSpace * rowExpandRatioArray[i] / 1000;
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

    void updateHeight() {
        // Detect minimum heights & calculate spans
        detectRowHeights();

        // Expand
        expandRows();

        // Position
        layoutCellsVertically();
    }

    void updateWidth() {
        // Detect widths & calculate spans
        detectColWidths();
        // Expand
        expandColumns();
        // Position
        layoutCellsHorizontally();

    }

    void expandColumns() {
        if (!isUndefinedWidth()) {
            int usedSpace = minColumnWidths[0];
            int horizontalSpacing = getHorizontalSpacing();
            for (int i = 1; i < minColumnWidths.length; i++) {
                usedSpace += horizontalSpacing + minColumnWidths[i];
            }

            int availableSpace = LayoutManager.get(client).getInnerWidth(
                    getElement());
            int excessSpace = availableSpace - usedSpace;
            int distributed = 0;
            if (excessSpace > 0) {
                for (int i = 0; i < columnWidths.length; i++) {
                    int ew = excessSpace * colExpandRatioArray[i] / 1000;
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

    void layoutCellsVertically() {
        int verticalSpacing = getVerticalSpacing();
        LayoutManager layoutManager = LayoutManager.get(client);
        Element element = getElement();
        int paddingTop = layoutManager.getPaddingTop(element);
        int y = paddingTop;

        for (int i = 0; i < cells.length; i++) {
            y = paddingTop;
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    cell.layoutVertically(y);
                }
                y += rowHeights[j] + verticalSpacing;
            }
        }

        if (isUndefinedHeight()) {
            int outerHeight = y - verticalSpacing
                    + layoutManager.getPaddingBottom(element)
                    + layoutManager.getBorderHeight(element);
            element.getStyle().setHeight(outerHeight, Unit.PX);
        }
    }

    void layoutCellsHorizontally() {
        LayoutManager layoutManager = LayoutManager.get(client);
        Element element = getElement();
        int x = layoutManager.getPaddingLeft(element);
        int horizontalSpacing = getHorizontalSpacing();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    cell.layoutHorizontally(x);
                }
            }
            x += columnWidths[i] + horizontalSpacing;
        }

        if (isUndefinedWidth()) {
            int outerWidth = x - horizontalSpacing
                    + layoutManager.getPaddingRight(element)
                    + layoutManager.getBorderWidth(element);
            element.getStyle().setWidth(outerWidth, Unit.PX);
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
    class Cell {
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean hasContent() {
            return hasContent;
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

        public void layoutHorizontally(int x) {
            if (slot != null) {
                slot.positionHorizontally(x, getAvailableWidth());
            }
        }

        public void layoutVertically(int y) {
            if (slot != null) {
                slot.positionVertically(y, getAvailableHeight());
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

        final int row;
        final int col;
        int colspan = 1;
        int rowspan = 1;

        private boolean hasContent;

        private AlignmentInfo alignment;

        ComponentConnectorLayoutSlot slot;

        public void updateFromUidl(UIDL cellUidl) {
            // Set cell width
            colspan = cellUidl.hasAttribute("w") ? cellUidl
                    .getIntAttribute("w") : 1;
            // Set cell height
            rowspan = cellUidl.hasAttribute("h") ? cellUidl
                    .getIntAttribute("h") : 1;
            // ensure we will lose reference to old cells, now overlapped by
            // this cell
            for (int i = 0; i < colspan; i++) {
                for (int j = 0; j < rowspan; j++) {
                    if (i > 0 || j > 0) {
                        cells[col + i][row + j] = null;
                    }
                }
            }

            UIDL childUidl = cellUidl.getChildUIDL(0); // we are interested
                                                       // about childUidl
            hasContent = childUidl != null;
            if (hasContent) {
                ComponentConnector childConnector = client
                        .getPaintable(childUidl);

                if (slot == null || slot.getChild() != childConnector) {
                    slot = new ComponentConnectorLayoutSlot(CLASSNAME,
                            childConnector, getConnector());
                    Element slotWrapper = slot.getWrapperElement();
                    getElement().appendChild(slotWrapper);

                    Widget widget = childConnector.getWidget();
                    insert(widget, slotWrapper, getWidgetCount(), false);
                    Cell oldCell = widgetToCell.put(widget, this);
                    if (oldCell != null) {
                        oldCell.slot.getWrapperElement().removeFromParent();
                        oldCell.slot = null;
                    }
                }

            }
        }

        public void setAlignment(AlignmentInfo alignmentInfo) {
            slot.setAlignment(alignmentInfo);
        }
    }

    /**
     * Returns the Cell with the given coordinates. Creates a new Cell if an
     * existing was not found and also updates the Cell based on the given UIDL.
     * 
     * @param row
     * @param col
     * @return
     */
    Cell getCell(int row, int col, UIDL c) {
        Cell cell = cells[col][row];
        if (cell == null) {
            cell = new Cell(row, col);
            cells[col][row] = cell;
        }
        cell.updateFromUidl(c);
        return cell;
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     */
    ComponentConnector getComponent(Element element) {
        return Util.getConnectorForElement(client, this, element);
    }

    void setCaption(Widget widget, VCaption caption) {
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

    void updateMarginStyleNames(VMarginInfo marginInfo) {
        togglePrefixedStyleName("margin-top", marginInfo.hasTop());
        togglePrefixedStyleName("margin-right", marginInfo.hasRight());
        togglePrefixedStyleName("margin-bottom", marginInfo.hasBottom());
        togglePrefixedStyleName("margin-left", marginInfo.hasLeft());
    }

    void updateSpacingStyleName(boolean spacingEnabled) {
        String styleName = getStylePrimaryName();
        if (spacingEnabled) {
            spacingMeasureElement.addClassName(styleName + "-spacing-on");
            spacingMeasureElement.removeClassName(styleName + "-spacing-off");
        } else {
            spacingMeasureElement.removeClassName(styleName + "-spacing-on");
            spacingMeasureElement.addClassName(styleName + "-spacing-off");
        }
    }

}
