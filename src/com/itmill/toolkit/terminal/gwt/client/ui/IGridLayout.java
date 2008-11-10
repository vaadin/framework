/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    private HashMap<Widget, ChildComponentContainer> nonRenderedWidgets;

    public IGridLayout() {
        super();
        getElement().appendChild(margin);
        setStyleName(CLASSNAME);
        setWidget(canvas);
    }

    @Override
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

        boolean mightToggleVScrollBar = "".equals(height) && !"".equals(width);
        boolean mightToggleHScrollBar = "".equals(width) && !"".equals(height);
        int wBeforeRender = 0;
        int hBeforeRender = 0;
        if (mightToggleHScrollBar || mightToggleVScrollBar) {
            wBeforeRender = canvas.getOffsetWidth();
            hBeforeRender = getOffsetHeight();
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

        nonRenderedWidgets = (HashMap<Widget, ChildComponentContainer>) widgetToComponentContainer
                .clone();

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        LinkedList<Cell> pendingCells = new LinkedList<Cell>();

        LinkedList<Cell> relativeHeighted = new LinkedList<Cell>();

        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                for (final Iterator j = r.getChildIterator(); j.hasNext();) {
                    final UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {
                        Cell cell = getCell(c);
                        if (cell.hasContent()) {
                            boolean rendered = cell.renderIfNoRelativeWidth();
                            cell.alignment = alignments[alignmentIndex++];
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
            }
        }

        distributeColSpanWidths();
        colExpandRatioArray = uidl.getIntArrayAttribute("colExpand");
        rowExpandRatioArray = uidl.getIntArrayAttribute("rowExpand");

        minColumnWidths = cloneArray(columnWidths);
        expandColumns();

        renderRemainingComponentsWithNoRelativeHeight(pendingCells);

        detectRowHeights();

        expandRows();

        renderRemainingComponents(pendingCells);

        for (Cell cell : relativeHeighted) {
            Widget widget2 = cell.cc.getWidget();
            client.handleComponentRelativeSize(widget2);
        }

        layoutCells();

        // clean non rendered components
        for (Widget w : nonRenderedWidgets.keySet()) {
            ChildComponentContainer childComponentContainer = widgetToComponentContainer
                    .get(w);
            paintableToCell.remove(w);
            widgetToComponentContainer.remove(w);
            childComponentContainer.removeFromParent();
            client.unregisterPaintable((Paintable) w);
        }
        nonRenderedWidgets = null;

        rendering = false;
        boolean needsRelativeSizeCheck = false;

        if (mightToggleHScrollBar && wBeforeRender != canvas.getOffsetWidth()) {
            needsRelativeSizeCheck = true;
        }
        if (mightToggleVScrollBar && hBeforeRender != getOffsetHeight()) {
            needsRelativeSizeCheck = true;
        }
        if (needsRelativeSizeCheck) {
            client.handleComponentRelativeSize(this);
        }
    }

    private static int[] cloneArray(int[] toBeCloned) {
        int[] clone = new int[toBeCloned.length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = toBeCloned[i] * 1;
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
        if (!height.equals(this.height)) {
            this.height = height;
            if (!rendering) {
                expandRows();
                layoutCells();
                for (Paintable c : paintableToCell.keySet()) {
                    client.handleComponentRelativeSize((Widget) c);
                }
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (!width.equals(this.width)) {
            this.width = width;
            if (!rendering) {
                expandColumns();
                layoutCells();
                for (Paintable c : paintableToCell.keySet()) {
                    client.handleComponentRelativeSize((Widget) c);
                }
            }
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
        ChildComponentContainer componentContainer = widgetToComponentContainer
                .remove(oldComponent);
        if (componentContainer == null) {
            return;
        }

        componentContainer.setWidget(newComponent);
        client.unregisterPaintable((Paintable) oldComponent);
        widgetToComponentContainer.put(newComponent, componentContainer);

        paintableToCell.put((Paintable) newComponent, paintableToCell
                .get(oldComponent));
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        ChildComponentContainer cc = widgetToComponentContainer.get(component);
        if (cc != null) {
            cc.updateCaption(uidl, client);
        }
        if (!rendering) {
            // ensure rel size details are updated
            paintableToCell.get(component).updateRelSizeStatus(uidl);
        }
    }

    public boolean requestLayout(final Set<Paintable> changedChildren) {
        ApplicationConnection.getConsole().log("IGridLayout.requestLayout()");
        boolean needsLayout = false;
        int offsetHeight = canvas.getOffsetHeight();
        int offsetWidth = canvas.getOffsetWidth();
        if ("".equals(width) || "".equals(height)) {
            needsLayout = true;
        }
        ArrayList<Integer> dirtyColumns = new ArrayList<Integer>();
        ArrayList<Integer> dirtyRows = new ArrayList<Integer>();
        for (Paintable paintable : changedChildren) {

            Cell cell = paintableToCell.get(paintable);
            if (!cell.hasRelativeHeight() || !cell.hasRelativeWidth()) {
                // cell sizes will only stay still if only relatively
                // sized
                // components
                // check if changed child affects min col widths
                cell.cc.setWidth("");
                cell.cc.setHeight("");

                cell.cc.updateWidgetSize();
                int width = cell.cc.getWidgetSize().getWidth()
                        + cell.cc.getCaptionWidthAfterComponent();
                int allocated = columnWidths[cell.col];
                for (int i = 1; i < cell.colspan; i++) {
                    allocated += spacingPixels + columnWidths[cell.col + i];
                }
                if (allocated < width) {
                    needsLayout = true;
                    // columnWidths needs to be expanded due colspanned
                    // cell
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
                } else if (allocated != width) {
                    // size is smaller thant allocated, column might
                    // shrink
                    dirtyColumns.add(cell.col);
                }

                int height = cell.cc.getWidgetSize().getHeight()
                        + cell.cc.getCaptionHeightAboveComponent();

                allocated = rowHeights[cell.row];
                for (int i = 1; i < cell.rowspan; i++) {
                    allocated += spacingPixels + rowHeights[cell.row + i];
                }
                if (allocated < height) {
                    needsLayout = true;
                    // columnWidths needs to be expanded due colspanned
                    // cell
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
                            && !cell.hasRelativeWidth()) {
                        int width = cell.cc.getWidgetSize().getWidth()
                                + cell.cc.getCaptionWidthAfterComponent();
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
                            && !cell.hasRelativeHeight()) {
                        int h = cell.cc.getWidgetSize().getHeight()
                                + cell.cc.getCaptionHeightAboveComponent();
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
        Cell cell = paintableToCell.get(child);
        assert cell != null;
        return cell.getAllocatedSpace();
    }

    private Cell[][] cells;

    /**
     * Private helper class.
     */
    private class Cell {
        private boolean relHeight = false;
        private boolean relWidth = false;

        public Cell(UIDL c) {
            row = c.getIntAttribute("y");
            col = c.getIntAttribute("x");
            setUidl(c);
        }

        public boolean hasRelativeHeight() {
            return relHeight;
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
                cc.updateAlignments(getAvailableWidth(), getAvailableHeight());
            }
        }

        public int getWidth() {
            if (cc != null) {
                int w = cc.getElement().getScrollWidth();
                return w;
            } else {
                return 0;
            }
        }

        public int getHeight() {
            if (cc != null) {
                return cc.getElement().getScrollHeight();
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

            Paintable paintable = client.getPaintable(childUidl);
            assert paintable != null;
            if (cc == null || cc.getWidget() != paintable) {
                if (widgetToComponentContainer.containsKey(paintable)) {
                    cc = widgetToComponentContainer.get(paintable);
                    cc.setWidth("");
                    cc.setHeight("");
                } else {
                    cc = new ChildComponentContainer((Widget) paintable,
                            CellBasedLayout.ORIENTATION_VERTICAL);
                    widgetToComponentContainer.put((Widget) paintable, cc);
                    paintableToCell.put(paintable, this);
                    cc.setWidth("");
                    canvas.add(cc, 0, 0);
                }
            }
            cc.renderChild(childUidl, client);
            cc.updateWidgetSize();
            nonRenderedWidgets.remove(paintable);
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
                    // canvas
                    // later durin render phase
                    cc = null;
                } else if (cc != null
                        && cc.getWidget() != client.getPaintable(c)) {
                    // content has changed
                    cc = null;
                    if (widgetToComponentContainer.containsKey(client
                            .getPaintable(c))) {
                        // cc exist for this component (moved) use that for this
                        // cell
                        cc = widgetToComponentContainer.get(client
                                .getPaintable(c));
                        cc.setWidth("");
                        cc.setHeight("");
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
                if (uidl.hasAttribute("width")
                        && uidl.getStringAttribute("width").contains("%")) {
                    relWidth = true;
                } else {
                    relWidth = false;
                }
            }
        }
    }

    private Cell getCell(UIDL c) {
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
}
