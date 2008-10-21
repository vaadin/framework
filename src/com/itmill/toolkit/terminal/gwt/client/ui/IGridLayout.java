/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.layout.CellBasedLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class IGridLayout extends SimplePanel implements Paintable, Container {

    public static final String CLASSNAME = "i-gridlayout";

    private boolean needsLayout = false;

    private Element margin = DOM.createDiv();

    private final AbsolutePanel canvas = new AbsolutePanel();

    private ApplicationConnection client;

    protected HashMap<Widget, ChildComponentContainer> widgetToComponentContainer = new HashMap<Widget, ChildComponentContainer>();

    private HashMap<Paintable, Cell> paintableToCell = new HashMap<Paintable, Cell>();

    private int spacingPixels;

    private int[] columnWidths;
    private int[] rowHeights;

    private String height;

    private String width;

    private int[] colExpandRatioArray;

    private int[] rowExpandRatioArray;

    private int[] minColumnWidths;

    private int[] minRowHeights;

    private boolean rendering;

    public IGridLayout() {
        super();
        getElement().getStyle().setProperty("overflow", "hidden");
        getElement().appendChild(margin);
        setStyleName(CLASSNAME);
        setWidget(canvas);
    }

    protected Element getContainerElement() {
        return margin;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        this.client = client;

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }

        handleMargins(uidl);
        detectSpacing(uidl);

        int cols = uidl.getIntAttribute("w");
        int rows = uidl.getIntAttribute("h");

        columnWidths = new int[cols];
        rowHeights = new int[rows];

        if (cells == null) {
            cells = new Cell[cols][rows];
        } else if (cells.length != cols || cells[0].length != rows) {
            LinkedList<Cell> orphaned = new LinkedList<Cell>();
            Cell[][] newCells = new Cell[cols][rows];
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (i < cols && j < rows) {
                        newCells[i][j] = cells[i][j];
                    }
                }
            }
            cells = newCells;
            // TODO clean orphaned list
            for (Iterator iterator = orphaned.iterator(); iterator.hasNext();) {
                Cell cell = (Cell) iterator.next();
            }
        }

        HashMap<Widget, ChildComponentContainer> nonRenderedWidgets = (HashMap<Widget, ChildComponentContainer>) widgetToComponentContainer
                .clone();

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;
        int column;
        int row = 0;

        LinkedList<Cell> pendingCells = new LinkedList<Cell>();

        LinkedList<Cell> relativeHeighted = new LinkedList<Cell>();

        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                column = 0;
                for (final Iterator j = r.getChildIterator(); j.hasNext();) {
                    final UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {
                        Cell cell = getCell(c);
                        if (cell.hasContent()) {
                            boolean rendered = cell.renderIfNoRelativeWidth();
                            cell.alignment = alignments[alignmentIndex++];
                            column += cell.colspan;
                            if (!rendered) {
                                pendingCells.add(cell);
                            }

                            if (cell.colspan > 1) {
                                storeColSpannedCell(cell);
                            } else if (rendered) {
                                // strore non-colspanned widths to columnWidth
                                // array
                                if (columnWidths[cell.col] < cell.getWidth()) {
                                    columnWidths[cell.col] = cell.getWidth();
                                }
                            }
                            if (cell.hasRelativeHeight()) {
                                relativeHeighted.add(cell);
                            }
                        }
                    }
                }
                row++;
            }
        }

        distributeColSpanWidths();
        colExpandRatioArray = uidl.getIntArrayAttribute("colExpand");
        rowExpandRatioArray = uidl.getIntArrayAttribute("rowExpand");

        minColumnWidths = cloneArray(columnWidths);
        expandColumns();

        renderRemainingComponentsWithNoRelativeHeight(pendingCells);

        detectRowHeights();

        minRowHeights = cloneArray(rowHeights);
        expandRows();

        renderRemainingComponents(pendingCells);

        for (Cell cell : relativeHeighted) {
            Widget widget2 = cell.cc.getWidget();
            client.handleComponentRelativeSize(widget2);
        }

        layoutCells();

        for (Entry cc : nonRenderedWidgets.entrySet()) {
            // TODO remove components
        }

        rendering = false;

        // canvas.add(uidl.print_r());
    }

    private static int[] cloneArray(int[] toBeCloned) {
        int[] clone = new int[toBeCloned.length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = toBeCloned[i];
        }
        return clone;
    }

    private void expandRows() {
        if (!"".equals(height)) {
            int usedSpace = minRowHeights[0];
            for (int i = 1; i < minRowHeights.length; i++) {
                usedSpace += spacingPixels + minRowHeights[i];
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
        this.height = height;
        if (!rendering) {
            expandRows();
            layoutCells();
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        this.width = width;
        if (!rendering) {
            expandColumns();
            layoutCells();
        }
    }

    private void expandColumns() {
        if (!"".equals(width)) {
            int usedSpace = minColumnWidths[0];
            for (int i = 1; i < minColumnWidths.length; i++) {
                usedSpace += spacingPixels + minColumnWidths[i];
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

    private void layoutCells() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < cells.length; i++) {
            y = 0;
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    cell.layout(x, y);
                }
                y += rowHeights[j] + spacingPixels;
            }
            x += columnWidths[i] + spacingPixels;
        }
        // ensure canvas is right size
        canvas.setPixelSize(x - spacingPixels, y - spacingPixels);
    }

    private void renderRemainingComponents(LinkedList<Cell> pendingCells) {
        for (Cell cell : pendingCells) {
            cell.render();
        }
    }

    private void detectRowHeights() {

        // collect min rowheight from non-rowspanned cells
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell != null) {
                    if (cell.rowspan == 1) {
                        if (rowHeights[j] < cell.getHeight()) {
                            rowHeights[j] = cell.getHeight();
                        }
                    } else {
                        storeRowSpannedCell(cell);
                    }
                }
            }
        }
        distributeRowSpanHeights();

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

    private void renderRemainingComponentsWithNoRelativeHeight(
            LinkedList<Cell> pendingCells) {

        for (Iterator iterator = pendingCells.iterator(); iterator.hasNext();) {
            Cell cell = (Cell) iterator.next();
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
    private void distributeColSpanWidths() {
        for (SpanList list : colSpans) {
            for (Cell cell : list.cells) {
                int width = cell.getWidth();
                int allocated = columnWidths[cell.col];
                for (int i = 1; i < cell.colspan; i++) {
                    allocated += spacingPixels + columnWidths[cell.col + i];
                }
                if (allocated < width) {
                    // columnWidths needs to be expanded due colspanned cell
                    int neededExtraSpace = width - allocated;
                    int spaceForColunms = neededExtraSpace / cell.colspan;
                    for (int i = 0; i < cell.colspan; i++) {
                        int col = cell.col + i;
                        columnWidths[col] += spaceForColunms;
                        neededExtraSpace -= spaceForColunms;
                    }
                    if (neededExtraSpace > 0) {
                        for (int i = 0; i < cell.colspan; i++) {
                            int col = cell.col + i;
                            columnWidths[col] += 1;
                            neededExtraSpace -= 1;
                            if (neededExtraSpace == 0) {
                                break;
                            }
                        }
                    }
                }
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
                int height = cell.getHeight();
                int allocated = rowHeights[cell.row];
                for (int i = 1; i < cell.rowspan; i++) {
                    allocated += spacingPixels + rowHeights[cell.row + i];
                }
                if (allocated < height) {
                    // columnWidths needs to be expanded due colspanned cell
                    int neededExtraSpace = height - allocated;
                    int spaceForColunms = neededExtraSpace / cell.rowspan;
                    for (int i = 0; i < cell.rowspan; i++) {
                        int row = cell.row + i;
                        rowHeights[row] += spaceForColunms;
                        neededExtraSpace -= spaceForColunms;
                    }
                    if (neededExtraSpace > 0) {
                        for (int i = 0; i < cell.rowspan; i++) {
                            int row = cell.row + i;
                            rowHeights[row] += 1;
                            neededExtraSpace -= 1;
                            if (neededExtraSpace == 0) {
                                break;
                            }
                        }
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

    private void storeColSpannedCell(Cell cell) {
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

    private void detectSpacing(UIDL uidl) {
        if (uidl.getBooleanAttribute("spacing")) {
            Element spacingmeter = DOM.createDiv();
            spacingmeter.setClassName(CLASSNAME + "-" + "spacing-element");
            spacingmeter.getStyle().setProperty("width", "0");
            canvas.getElement().appendChild(spacingmeter);
            spacingPixels = spacingmeter.getOffsetWidth();
            canvas.getElement().removeChild(spacingmeter);
        } else {
            spacingPixels = 0;
        }
    }

    private void handleMargins(UIDL uidl) {
        final MarginInfo margins = new MarginInfo(uidl
                .getIntAttribute("margins"));

        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                margins.hasTop());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                margins.hasRight());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                margins.hasBottom());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                margins.hasLeft());

        marginTopAndBottom = margin.getOffsetHeight()
                - canvas.getOffsetHeight();
    }

    public boolean hasChildComponent(Widget component) {
        return paintableToCell.containsKey(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        ChildComponentContainer cc = widgetToComponentContainer.get(component);
        if (cc != null) {
            cc.updateCaption(uidl, client);
        }
    }

    public boolean requestLayout(Set<Paintable> child) {
        boolean mayNeedLayout = false;
        int offsetHeight = canvas.getOffsetHeight();
        int offsetWidth = canvas.getOffsetWidth();
        if ("".equals(width) || "".equals(height)) {
            mayNeedLayout = true;
        } else {
            for (Paintable paintable : child) {
                Cell cell = paintableToCell.get(paintable);
                if (!cell.hasRelativeHeight() || !cell.hasRelativeWidth()) {
                    // cell sizes will only stay still if only relatively sized
                    // components
                    mayNeedLayout = true;
                }
            }
        }
        if (mayNeedLayout) {
            expandColumns();
            expandRows();
            layoutCells();
            for (Paintable paintable : child) {
                Cell cell = paintableToCell.get(paintable);
                if (cell.hasRelativeHeight() || cell.hasRelativeWidth()) {
                    client.handleComponentRelativeSize((Widget) paintable);
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
        Cell cell = paintableToCell.get(child);
        assert cell != null;
        return cell.getAllocatedSpace();
    }

    private Cell[][] cells;

    /**
     * Private helper class.
     */
    private class Cell {
        public Cell(UIDL c) {
            // Set cell width
            colspan = c.hasAttribute("w") ? c.getIntAttribute("w") : 1;
            // Set cell height
            rowspan = c.hasAttribute("h") ? c.getIntAttribute("h") : 1;
            row = c.getIntAttribute("y");
            col = c.getIntAttribute("x");
            childUidl = c.getChildUIDL(0);
        }

        public boolean hasRelativeHeight() {
            if (childUidl.hasAttribute("height")) {
                String w = childUidl.getStringAttribute("height");
                if (w.contains("%")) {
                    return true;
                }
            }
            return false;
        }

        public RenderSpace getAllocatedSpace() {
            return new RenderSpace(getAvailableWidth(), getAvailableHeight());
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
                width += spacingPixels + columnWidths[col + i];
            }
            return width;
        }

        /**
         * @return total of spanned rows
         */
        private int getAvailableHeight() {
            int height = rowHeights[row];
            for (int i = 1; i < rowspan; i++) {
                height += spacingPixels + rowHeights[row + i];
            }
            return height;
        }

        public void layout(int x, int y) {
            if (cc != null && cc.isAttached()) {
                canvas.setWidgetPosition(cc, x, y);
                cc.setContainerSize(getAvailableWidth(), getAvailableHeight());
                cc.setAlignment(new AlignmentInfo(alignment));
            }
        }

        public int getWidth() {
            if (cc != null) {
                return cc.getOffsetWidth();
            } else {
                return 0;
            }
        }

        public int getHeight() {
            if (cc != null) {
                return cc.getOffsetHeight();
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

        private boolean hasRelativeWidth() {
            if (childUidl.hasAttribute("width")) {
                String w = childUidl.getStringAttribute("width");
                if (w.contains("%")) {
                    return true;
                }
            }
            return false;
        }

        protected void render() {
            Paintable paintable = client.getPaintable(childUidl);
            if (cc == null) {
                cc = new ChildComponentContainer((Widget) paintable,
                        CellBasedLayout.ORIENTATION_HORIZONTAL);
                cc.setHeight("");
                canvas.add(cc);
            }
            widgetToComponentContainer.put((Widget) paintable, cc);
            paintableToCell.put(paintable, this);
            cc.renderChild(childUidl, client);
        }

        public UIDL getChildUIDL() {
            return childUidl;
        }

        int row;
        int col;
        int colspan = 1;
        int rowspan = 1;
        UIDL childUidl;
        int alignment;
        ChildComponentContainer cc;
    }

    private Cell getCell(UIDL c) {
        int row = c.getIntAttribute("y");
        int col = c.getIntAttribute("x");
        Cell cell = cells[col][row];
        if (cell == null) {
            cell = new Cell(c);
            cells[col][row] = cell;
        }
        return cell;
    }
}
