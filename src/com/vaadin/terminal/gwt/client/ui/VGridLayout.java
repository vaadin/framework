/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class VGridLayout extends SimplePanel implements Container {

    public static final String CLASSNAME = "v-gridlayout";

    private DivElement margin = Document.get().createDivElement();

    final AbsolutePanel canvas = new AbsolutePanel();

    ApplicationConnection client;

    protected HashMap<Widget, ChildComponentContainer> widgetToComponentContainer = new HashMap<Widget, ChildComponentContainer>();

    HashMap<Widget, Cell> widgetToCell = new HashMap<Widget, Cell>();

    private int spacingPixelsHorizontal;
    private int spacingPixelsVertical;

    int[] columnWidths;
    int[] rowHeights;

    private String height;

    private String width;

    int[] colExpandRatioArray;

    int[] rowExpandRatioArray;

    int[] minColumnWidths;

    private int[] minRowHeights;

    boolean rendering;

    HashMap<Widget, ChildComponentContainer> nonRenderedWidgets;

    boolean sizeChangedDuringRendering = false;

    public VGridLayout() {
        super();
        getElement().appendChild(margin);
        setStyleName(CLASSNAME);
        setWidget(canvas);
    }

    @Override
    protected Element getContainerElement() {
        return margin.cast();
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
        return spacingPixelsHorizontal;
    }

    /**
     * Returns the spacing between the cells vertically in pixels
     * 
     * @return
     */
    protected int getVerticalSpacing() {
        return spacingPixelsVertical;
    }

    static int[] cloneArray(int[] toBeCloned) {
        int[] clone = new int[toBeCloned.length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = toBeCloned[i] * 1;
        }
        return clone;
    }

    void expandRows() {
        if (!"".equals(height)) {
            int usedSpace = minRowHeights[0];
            for (int i = 1; i < minRowHeights.length; i++) {
                usedSpace += spacingPixelsVertical + minRowHeights[i];
            }
            int availableSpace = getOffsetHeight() - marginTopAndBottom;
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

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (!height.equals(this.height)) {
            this.height = height;
            if (rendering) {
                sizeChangedDuringRendering = true;
            } else {
                expandRows();
                layoutCells();
                for (Widget w : widgetToCell.keySet()) {
                    client.handleComponentRelativeSize(w);
                }
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (!width.equals(this.width)) {
            this.width = width;
            if (rendering) {
                sizeChangedDuringRendering = true;
            } else {
                int[] oldWidths = cloneArray(columnWidths);
                expandColumns();
                boolean heightChanged = false;
                HashSet<Integer> dirtyRows = null;
                for (int i = 0; i < oldWidths.length; i++) {
                    if (columnWidths[i] != oldWidths[i]) {
                        Cell[] column = cells[i];
                        for (int j = 0; j < column.length; j++) {
                            Cell c = column[j];
                            if (c != null && c.cc != null
                                    && c.widthCanAffectHeight()) {
                                c.cc.setContainerSize(c.getAvailableWidth(),
                                        c.getAvailableHeight());
                                client.handleComponentRelativeSize(c.cc
                                        .getWidget());
                                c.cc.updateWidgetSize();
                                int newHeight = c.getHeight();
                                if (columnWidths[i] < oldWidths[i]
                                        && newHeight > minRowHeights[j]
                                        && c.rowspan == 1) {
                                    /*
                                     * The width of this column was reduced and
                                     * this affected the height. The height is
                                     * now greater than the previously
                                     * calculated minHeight for the row.
                                     */
                                    minRowHeights[j] = newHeight;
                                    if (newHeight > rowHeights[j]) {
                                        /*
                                         * The new height is greater than the
                                         * previously calculated rowHeight -> we
                                         * need to recalculate heights later on
                                         */
                                        rowHeights[j] = newHeight;
                                        heightChanged = true;
                                    }
                                } else if (newHeight < minRowHeights[j]) {
                                    /*
                                     * The new height of the component is less
                                     * than the previously calculated min row
                                     * height. The min row height may be
                                     * affected and must thus be recalculated
                                     */
                                    if (dirtyRows == null) {
                                        dirtyRows = new HashSet<Integer>();
                                    }
                                    dirtyRows.add(j);
                                }
                            }
                        }
                    }
                }
                if (dirtyRows != null) {
                    /* flag indicating that there is a potential row shrinking */
                    boolean rowMayShrink = false;
                    for (Integer rowIndex : dirtyRows) {
                        int oldMinimum = minRowHeights[rowIndex];
                        int newMinimum = 0;
                        for (int colIndex = 0; colIndex < columnWidths.length; colIndex++) {
                            Cell cell = cells[colIndex][rowIndex];
                            if (cell != null && !cell.hasRelativeHeight()
                                    && cell.getHeight() > newMinimum) {
                                newMinimum = cell.getHeight();
                            }
                        }
                        if (newMinimum < oldMinimum) {
                            minRowHeights[rowIndex] = rowHeights[rowIndex] = newMinimum;
                            rowMayShrink = true;
                        }
                    }
                    if (rowMayShrink) {
                        distributeRowSpanHeights();
                        minRowHeights = cloneArray(rowHeights);
                        heightChanged = true;
                    }

                }
                layoutCells();
                for (Widget w : widgetToCell.keySet()) {
                    client.handleComponentRelativeSize(w);
                }
                if (heightChanged && "".equals(height)) {
                    Util.notifyParentOfSizeChange(this, false);
                }
            }
        }
    }

    void expandColumns() {
        if (!"".equals(width)) {
            int usedSpace = minColumnWidths[0];
            for (int i = 1; i < minColumnWidths.length; i++) {
                usedSpace += spacingPixelsHorizontal + minColumnWidths[i];
            }
            canvas.setWidth("");
            int availableSpace = canvas.getOffsetWidth();
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

    void layoutCells() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < cells.length; i++) {
            y = 0;
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    cell.layout(x, y);
                }
                y += rowHeights[j] + spacingPixelsVertical;
            }
            x += columnWidths[i] + spacingPixelsHorizontal;
        }

        if (isUndefinedWidth()) {
            canvas.setWidth((x - spacingPixelsHorizontal) + "px");
        } else {
            // main element defines width
            canvas.setWidth("");
        }

        int canvasHeight;
        if (isUndefinedHeight()) {
            canvasHeight = y - spacingPixelsVertical;
        } else {
            canvasHeight = getOffsetHeight() - marginTopAndBottom;
            if (canvasHeight < 0) {
                canvasHeight = 0;
            }
        }
        canvas.setHeight(canvasHeight + "px");
    }

    private boolean isUndefinedHeight() {
        return "".equals(height);
    }

    private boolean isUndefinedWidth() {
        return "".equals(width);
    }

    void renderRemainingComponents(LinkedList<Cell> pendingCells) {
        for (Cell cell : pendingCells) {
            cell.render();
        }
    }

    void detectRowHeights() {

        // collect min rowheight from non-rowspanned cells
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    /*
                     * Setting fixing container width may in some situations
                     * affect height. Example: Label with wrapping text without
                     * or with relative width.
                     */
                    if (cell.cc != null && cell.widthCanAffectHeight()) {
                        cell.cc.setWidth(cell.getAvailableWidth() + "px");
                        cell.cc.updateWidgetSize();
                    }
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

    void renderRemainingComponentsWithNoRelativeHeight(
            LinkedList<Cell> pendingCells) {

        for (Iterator<Cell> iterator = pendingCells.iterator(); iterator
                .hasNext();) {
            Cell cell = iterator.next();
            if (!cell.hasRelativeHeight()) {
                cell.render();
                iterator.remove();
            }
        }

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
                        spacingPixelsHorizontal, width, colExpandRatioArray);
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
                        spacingPixelsVertical, height, rowExpandRatioArray);
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

    private int marginTopAndBottom;

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

    void detectSpacing(UIDL uidl) {
        DivElement spacingmeter = Document.get().createDivElement();
        spacingmeter.setClassName(CLASSNAME + "-" + "spacing-"
                + (uidl.getBooleanAttribute("spacing") ? "on" : "off"));
        spacingmeter.getStyle().setProperty("width", "0");
        spacingmeter.getStyle().setProperty("height", "0");
        canvas.getElement().appendChild(spacingmeter);
        spacingPixelsHorizontal = spacingmeter.getOffsetWidth();
        spacingPixelsVertical = spacingmeter.getOffsetHeight();
        canvas.getElement().removeChild(spacingmeter);
    }

    void handleMargins(UIDL uidl) {
        final VMarginInfo margins = new VMarginInfo(
                uidl.getIntAttribute("margins"));

        String styles = CLASSNAME + "-margin";
        if (margins.hasTop()) {
            styles += " " + CLASSNAME + "-" + StyleConstants.MARGIN_TOP;
        }
        if (margins.hasRight()) {
            styles += " " + CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT;
        }
        if (margins.hasBottom()) {
            styles += " " + CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM;
        }
        if (margins.hasLeft()) {
            styles += " " + CLASSNAME + "-" + StyleConstants.MARGIN_LEFT;
        }
        margin.setClassName(styles);

        marginTopAndBottom = margin.getOffsetHeight()
                - canvas.getOffsetHeight();
    }

    public boolean hasChildComponent(Widget component) {
        return widgetToCell.containsKey(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        ChildComponentContainer componentContainer = widgetToComponentContainer
                .remove(oldComponent);
        if (componentContainer == null) {
            return;
        }

        componentContainer.setPaintable(VPaintableMap.get(client).getPaintable(
                newComponent));
        widgetToComponentContainer.put(newComponent, componentContainer);

        widgetToCell.put(newComponent, widgetToCell.get(oldComponent));
    }

    public boolean requestLayout(final Set<Widget> changedChildren) {
        boolean needsLayout = false;
        boolean reDistributeColSpanWidths = false;
        boolean reDistributeRowSpanHeights = false;
        int offsetHeight = canvas.getOffsetHeight();
        int offsetWidth = canvas.getOffsetWidth();
        if ("".equals(width) || "".equals(height)) {
            needsLayout = true;
        }
        ArrayList<Integer> dirtyColumns = new ArrayList<Integer>();
        ArrayList<Integer> dirtyRows = new ArrayList<Integer>();
        for (Widget widget : changedChildren) {

            Cell cell = widgetToCell.get(widget);
            if (!cell.hasRelativeHeight() || !cell.hasRelativeWidth()) {
                // cell sizes will only stay still if only relatively
                // sized components
                // check if changed child affects min col widths
                assert cell.cc != null;
                cell.cc.setWidth("");
                cell.cc.setHeight("");

                cell.cc.updateWidgetSize();

                /*
                 * If this is the result of an caption icon onload event the
                 * caption size may have changed
                 */
                cell.cc.updateCaptionSize();

                int width = cell.getWidth();
                int allocated = columnWidths[cell.col];
                for (int i = 1; i < cell.colspan; i++) {
                    allocated += spacingPixelsHorizontal
                            + columnWidths[cell.col + i];
                }
                if (allocated < width) {
                    needsLayout = true;
                    if (cell.colspan == 1) {
                        // do simple column width expansion
                        columnWidths[cell.col] = minColumnWidths[cell.col] = width;
                    } else {
                        // mark that col span expansion is needed
                        reDistributeColSpanWidths = true;
                    }
                } else if (allocated != width) {
                    // size is smaller thant allocated, column might
                    // shrink
                    dirtyColumns.add(cell.col);
                }

                int height = cell.getHeight();

                allocated = rowHeights[cell.row];
                for (int i = 1; i < cell.rowspan; i++) {
                    allocated += spacingPixelsVertical
                            + rowHeights[cell.row + i];
                }
                if (allocated < height) {
                    needsLayout = true;
                    if (cell.rowspan == 1) {
                        // do simple row expansion
                        rowHeights[cell.row] = minRowHeights[cell.row] = height;
                    } else {
                        // mark that row span expansion is needed
                        reDistributeRowSpanHeights = true;
                    }
                } else if (allocated != height) {
                    // size is smaller than allocated, row might shrink
                    dirtyRows.add(cell.row);
                }
            }
        }

        if (dirtyColumns.size() > 0) {
            for (Integer colIndex : dirtyColumns) {
                int colW = 0;
                for (int i = 0; i < rowHeights.length; i++) {
                    Cell cell = cells[colIndex][i];
                    if (cell != null && cell.getChildUIDL() != null
                            && !cell.hasRelativeWidth() && cell.colspan == 1) {
                        int width = cell.getWidth();
                        if (width > colW) {
                            colW = width;
                        }
                    }
                }
                minColumnWidths[colIndex] = colW;
            }
            needsLayout = true;
            // ensure colspanned columns have enough space
            columnWidths = cloneArray(minColumnWidths);
            distributeColSpanWidths();
            reDistributeColSpanWidths = false;
        }

        if (reDistributeColSpanWidths) {
            distributeColSpanWidths();
        }

        if (dirtyRows.size() > 0) {
            needsLayout = true;
            for (Integer rowIndex : dirtyRows) {
                // recalculate min row height
                int rowH = minRowHeights[rowIndex] = 0;
                // loop all columns on row rowIndex
                for (int i = 0; i < columnWidths.length; i++) {
                    Cell cell = cells[i][rowIndex];
                    if (cell != null && cell.getChildUIDL() != null
                            && !cell.hasRelativeHeight() && cell.rowspan == 1) {
                        int h = cell.getHeight();
                        if (h > rowH) {
                            rowH = h;
                        }
                    }
                }
                minRowHeights[rowIndex] = rowH;
            }
            // TODO could check only some row spans
            rowHeights = cloneArray(minRowHeights);
            distributeRowSpanHeights();
            reDistributeRowSpanHeights = false;
        }

        if (reDistributeRowSpanHeights) {
            distributeRowSpanHeights();
        }

        if (needsLayout) {
            expandColumns();
            expandRows();
            layoutCells();
            // loop all relative sized components and update their size
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    if (cell != null
                            && cell.cc != null
                            && (cell.hasRelativeHeight() || cell
                                    .hasRelativeWidth())) {
                        client.handleComponentRelativeSize(cell.cc.getWidget());
                    }
                }
            }
        }
        if (canvas.getOffsetHeight() != offsetHeight
                || canvas.getOffsetWidth() != offsetWidth) {
            return false;
        } else {
            return true;
        }
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        Cell cell = widgetToCell.get(child);
        assert cell != null;
        return cell.getAllocatedSpace();
    }

    Cell[][] cells;

    /**
     * Private helper class.
     */
    class Cell {
        private boolean relHeight = false;
        private boolean relWidth = false;
        private boolean widthCanAffectHeight = false;

        public Cell(UIDL c) {
            row = c.getIntAttribute("y");
            col = c.getIntAttribute("x");
            setUidl(c);
        }

        public boolean widthCanAffectHeight() {
            return widthCanAffectHeight;
        }

        public boolean hasRelativeHeight() {
            return relHeight;
        }

        public RenderSpace getAllocatedSpace() {
            return new RenderSpace(getAvailableWidth()
                    - cc.getCaptionWidthAfterComponent(), getAvailableHeight()
                    - cc.getCaptionHeightAboveComponent());
        }

        public boolean hasContent() {
            return childUidl != null;
        }

        /**
         * @return total of spanned cols
         */
        private int getAvailableWidth() {
            int width = columnWidths[col];
            for (int i = 1; i < colspan; i++) {
                width += spacingPixelsHorizontal + columnWidths[col + i];
            }
            return width;
        }

        /**
         * @return total of spanned rows
         */
        private int getAvailableHeight() {
            int height = rowHeights[row];
            for (int i = 1; i < rowspan; i++) {
                height += spacingPixelsVertical + rowHeights[row + i];
            }
            return height;
        }

        public void layout(int x, int y) {
            if (cc != null && cc.isAttached()) {
                canvas.setWidgetPosition(cc, x, y);
                cc.setContainerSize(getAvailableWidth(), getAvailableHeight());
                cc.setAlignment(new AlignmentInfo(alignment));
                cc.updateAlignments(getAvailableWidth(), getAvailableHeight());
            }
        }

        public int getWidth() {
            if (cc != null) {
                int w = cc.getWidgetSize().getWidth()
                        + cc.getCaptionWidthAfterComponent();
                return w;
            } else {
                return 0;
            }
        }

        public int getHeight() {
            if (cc != null) {
                return cc.getWidgetSize().getHeight()
                        + cc.getCaptionHeightAboveComponent();
            } else {
                return 0;
            }
        }

        public boolean renderIfNoRelativeWidth() {
            if (childUidl == null) {
                return false;
            }
            if (!hasRelativeWidth()) {
                render();
                return true;
            } else {
                return false;
            }
        }

        protected boolean hasRelativeWidth() {
            return relWidth;
        }

        protected void render() {
            assert childUidl != null;

            VPaintableWidget paintable = client.getPaintable(childUidl);
            Widget w = paintable.getWidgetForPaintable();
            assert paintable != null;
            if (cc == null || cc.getWidget() != w) {
                if (widgetToComponentContainer.containsKey(w)) {
                    // Component moving from one place to another
                    cc = widgetToComponentContainer.get(w);
                    cc.setWidth("");
                    cc.setHeight("");
                    /*
                     * Widget might not be set if moving from another component
                     * and this layout has been hidden when moving out, see
                     * #5372
                     */
                    cc.setPaintable(paintable);
                } else {
                    // A new component
                    cc = new ChildComponentContainer(paintable,
                            ChildComponentContainer.ORIENTATION_VERTICAL);
                    widgetToComponentContainer.put(w, cc);
                    cc.setWidth("");
                    canvas.add(cc, 0, 0);
                }
                widgetToCell.put(w, this);
            }
            cc.renderChild(childUidl, client, -1);
            if (sizeChangedDuringRendering && Util.isCached(childUidl)) {
                client.handleComponentRelativeSize(cc.getWidget());
            }
            cc.updateWidgetSize();
            nonRenderedWidgets.remove(w);
        }

        public UIDL getChildUIDL() {
            return childUidl;
        }

        final int row;
        final int col;
        int colspan = 1;
        int rowspan = 1;
        UIDL childUidl;
        int alignment;
        // may be null after setUidl() if content has vanished or changed, set
        // in render()
        ChildComponentContainer cc;

        public void setUidl(UIDL c) {
            // Set cell width
            colspan = c.hasAttribute("w") ? c.getIntAttribute("w") : 1;
            // Set cell height
            rowspan = c.hasAttribute("h") ? c.getIntAttribute("h") : 1;
            // ensure we will lose reference to old cells, now overlapped by
            // this cell
            for (int i = 0; i < colspan; i++) {
                for (int j = 0; j < rowspan; j++) {
                    if (i > 0 || j > 0) {
                        cells[col + i][row + j] = null;
                    }
                }
            }

            c = c.getChildUIDL(0); // we are interested about childUidl
            if (childUidl != null) {
                if (c == null) {
                    // content has vanished, old content will be removed from
                    // canvas later during the render phase
                    cc = null;
                } else if (cc != null
                        && cc.getWidget() != client.getPaintable(c)
                                .getWidgetForPaintable()) {
                    // content has changed
                    cc = null;
                    VPaintableWidget paintable = client.getPaintable(c);
                    Widget w = paintable.getWidgetForPaintable();
                    if (widgetToComponentContainer.containsKey(w)) {
                        // cc exist for this component (moved) use that for this
                        // cell
                        cc = widgetToComponentContainer.get(w);
                        cc.setWidth("");
                        cc.setHeight("");
                        widgetToCell.put(w, this);
                    }
                }
            }
            childUidl = c;
            updateRelSizeStatus(c);
        }

        protected void updateRelSizeStatus(UIDL uidl) {
            if (uidl != null && !uidl.getBooleanAttribute("cached")) {
                if (uidl.hasAttribute("height")
                        && uidl.getStringAttribute("height").contains("%")) {
                    relHeight = true;
                } else {
                    relHeight = false;
                }
                if (uidl.hasAttribute("width")) {
                    widthCanAffectHeight = relWidth = uidl.getStringAttribute(
                            "width").contains("%");
                    if (uidl.hasAttribute("height")) {
                        widthCanAffectHeight = false;
                    }
                } else {
                    widthCanAffectHeight = !uidl.hasAttribute("height");
                    relWidth = false;
                }
            }
        }
    }

    Cell getCell(UIDL c) {
        int row = c.getIntAttribute("y");
        int col = c.getIntAttribute("x");
        Cell cell = cells[col][row];
        if (cell == null) {
            cell = new Cell(c);
            cells[col][row] = cell;
        } else {
            cell.setUidl(c);
        }
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
    VPaintableWidget getComponent(Element element) {
        return Util.getPaintableForElement(client, this, element);
    }

}
