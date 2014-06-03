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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.VTreeTable.VTreeTableScrollBody.VTreeTableRow;

public class VTreeTable extends VScrollTable {

    /** For internal use only. May be removed or replaced in the future. */
    public static class PendingNavigationEvent {
        public final int keycode;
        public final boolean ctrl;
        public final boolean shift;

        public PendingNavigationEvent(int keycode, boolean ctrl, boolean shift) {
            this.keycode = keycode;
            this.ctrl = ctrl;
            this.shift = shift;
        }

        @Override
        public String toString() {
            String string = "Keyboard event: " + keycode;
            if (ctrl) {
                string += " + ctrl";
            }
            if (shift) {
                string += " + shift";
            }
            return string;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public boolean collapseRequest;

    private boolean selectionPending;

    /** For internal use only. May be removed or replaced in the future. */
    public int colIndexOfHierarchy;

    /** For internal use only. May be removed or replaced in the future. */
    public String collapsedRowKey;

    /** For internal use only. May be removed or replaced in the future. */
    public VTreeTableScrollBody scrollBody;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean animationsEnabled;

    /** For internal use only. May be removed or replaced in the future. */
    public LinkedList<PendingNavigationEvent> pendingNavigationEvents = new LinkedList<VTreeTable.PendingNavigationEvent>();

    /** For internal use only. May be removed or replaced in the future. */
    public boolean focusParentResponsePending;

    @Override
    protected VScrollTableBody createScrollBody() {
        scrollBody = new VTreeTableScrollBody();
        return scrollBody;
    }

    /*
     * Overridden to allow animation of expands and collapses of nodes.
     */
    @Override
    public void addAndRemoveRows(UIDL partialRowAdditions) {
        if (partialRowAdditions == null) {
            return;
        }

        if (animationsEnabled) {
            if (partialRowAdditions.hasAttribute("hide")) {
                scrollBody.unlinkRowsAnimatedAndUpdateCacheWhenFinished(
                        partialRowAdditions.getIntAttribute("firstprowix"),
                        partialRowAdditions.getIntAttribute("numprows"));
            } else {
                scrollBody.insertRowsAnimated(partialRowAdditions,
                        partialRowAdditions.getIntAttribute("firstprowix"),
                        partialRowAdditions.getIntAttribute("numprows"));
                discardRowsOutsideCacheWindow();
            }
        } else {
            super.addAndRemoveRows(partialRowAdditions);
        }
    }

    @Override
    protected int getHierarchyColumnIndex() {
        return colIndexOfHierarchy + (showRowHeaders ? 1 : 0);
    }

    public class VTreeTableScrollBody extends VScrollTable.VScrollTableBody {
        private int indentWidth = -1;
        private int maxIndent = 0;

        protected VTreeTableScrollBody() {
            super();
        }

        @Override
        protected VScrollTableRow createRow(UIDL uidl, char[] aligns2) {
            if (uidl.hasAttribute("gen_html")) {
                // This is a generated row.
                return new VTreeTableGeneratedRow(uidl, aligns2);
            }
            return new VTreeTableRow(uidl, aligns2);
        }

        public class VTreeTableRow extends
                VScrollTable.VScrollTableBody.VScrollTableRow {

            private boolean isTreeCellAdded = false;
            private SpanElement treeSpacer;
            private boolean open;
            private int depth;
            private boolean canHaveChildren;
            protected Widget widgetInHierarchyColumn;

            public VTreeTableRow(UIDL uidl, char[] aligns2) {
                super(uidl, aligns2);
            }

            @Override
            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean isSorted,
                    String description) {
                super.addCell(rowUidl, text, align, style, textIsHTML,
                        isSorted, description);

                addTreeSpacer(rowUidl);
            }

            protected boolean addTreeSpacer(UIDL rowUidl) {
                if (cellShowsTreeHierarchy(getElement().getChildCount() - 1)) {
                    Element container = (Element) getElement().getLastChild()
                            .getFirstChild();

                    if (rowUidl.hasAttribute("icon")) {
                        // icons are in first content cell in TreeTable
                        ImageElement icon = Document.get().createImageElement();
                        icon.setClassName("v-icon");
                        icon.setAlt("icon");
                        icon.setSrc(client.translateVaadinUri(rowUidl
                                .getStringAttribute("icon")));
                        container.insertFirst(icon);
                    }

                    String classname = "v-treetable-treespacer";
                    if (rowUidl.getBooleanAttribute("ca")) {
                        canHaveChildren = true;
                        open = rowUidl.getBooleanAttribute("open");
                        classname += open ? " v-treetable-node-open"
                                : " v-treetable-node-closed";
                    }

                    treeSpacer = Document.get().createSpanElement();

                    treeSpacer.setClassName(classname);
                    container.insertFirst(treeSpacer);
                    depth = rowUidl.hasAttribute("depth") ? rowUidl
                            .getIntAttribute("depth") : 0;
                    setIndent();
                    isTreeCellAdded = true;
                    return true;
                }
                return false;
            }

            private boolean cellShowsTreeHierarchy(int curColIndex) {
                if (isTreeCellAdded) {
                    return false;
                }
                return curColIndex == getHierarchyColumnIndex();
            }

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getEventTarget().cast() == treeSpacer
                        && treeSpacer.getClassName().contains("node")) {
                    if (event.getTypeInt() == Event.ONMOUSEUP) {
                        sendToggleCollapsedUpdate(getKey());
                    }
                    return;
                }
                super.onBrowserEvent(event);
            }

            @Override
            public void addCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean isSorted, String description) {
                super.addCell(rowUidl, w, align, style, isSorted, description);
                if (addTreeSpacer(rowUidl)) {
                    widgetInHierarchyColumn = w;
                }

            }

            private void setIndent() {
                if (getIndentWidth() > 0) {
                    treeSpacer.getParentElement().getStyle()
                            .setPaddingLeft(getIndent(), Unit.PX);
                    treeSpacer.getStyle().setWidth(getIndent(), Unit.PX);
                    int colWidth = getColWidth(getHierarchyColumnIndex());
                    if (colWidth > 0 && getIndent() > colWidth) {
                        VTreeTable.this.setColWidth(getHierarchyColumnIndex(),
                                getIndent(), false);
                    }
                }
            }

            @Override
            protected void onAttach() {
                super.onAttach();
                if (getIndentWidth() < 0) {
                    detectIndent(this);
                    // If we detect indent here then the size of the hierarchy
                    // column is still wrong as it has been set when the indent
                    // was not known.
                    int w = getCellWidthFromDom(getHierarchyColumnIndex());
                    if (w >= 0) {
                        setColWidth(getHierarchyColumnIndex(), w);
                    }
                }
            }

            private int getCellWidthFromDom(int cellIndex) {
                final Element cell = DOM.getChild(getElement(), cellIndex);
                String w = cell.getStyle().getProperty("width");
                if (w == null || "".equals(w) || !w.endsWith("px")) {
                    return -1;
                } else {
                    return Integer.parseInt(w.substring(0, w.length() - 2));
                }
            }

            private int getHierarchyAndIconWidth() {
                int consumedSpace = treeSpacer.getOffsetWidth();
                if (treeSpacer.getParentElement().getChildCount() > 2) {
                    // icon next to tree spacer
                    consumedSpace += ((com.google.gwt.dom.client.Element) treeSpacer
                            .getNextSibling()).getOffsetWidth();
                }
                return consumedSpace;
            }

            @Override
            protected void setCellWidth(int cellIx, int width) {
                if (cellIx == getHierarchyColumnIndex()) {
                    // take indentation padding into account if this is the
                    // hierarchy column
                    int indent = getIndent();
                    if (indent != -1) {
                        width = Math.max(width - indent, 0);
                    }
                }
                super.setCellWidth(cellIx, width);
            }

            private int getIndent() {
                return (depth + 1) * getIndentWidth();
            }
        }

        protected class VTreeTableGeneratedRow extends VTreeTableRow {
            private boolean spanColumns;
            private boolean htmlContentAllowed;

            public VTreeTableGeneratedRow(UIDL uidl, char[] aligns) {
                super(uidl, aligns);
                addStyleName("v-table-generated-row");
            }

            public boolean isSpanColumns() {
                return spanColumns;
            }

            @Override
            protected void initCellWidths() {
                if (spanColumns) {
                    setSpannedColumnWidthAfterDOMFullyInited();
                } else {
                    super.initCellWidths();
                }
            }

            private void setSpannedColumnWidthAfterDOMFullyInited() {
                // Defer setting width on spanned columns to make sure that
                // they are added to the DOM before trying to calculate
                // widths.
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        if (showRowHeaders) {
                            setCellWidth(0, tHead.getHeaderCell(0)
                                    .getWidthWithIndent());
                            calcAndSetSpanWidthOnCell(1);
                        } else {
                            calcAndSetSpanWidthOnCell(0);
                        }
                    }
                });
            }

            @Override
            protected boolean isRenderHtmlInCells() {
                return htmlContentAllowed;
            }

            @Override
            protected void addCellsFromUIDL(UIDL uidl, char[] aligns, int col,
                    int visibleColumnIndex) {
                htmlContentAllowed = uidl.getBooleanAttribute("gen_html");
                spanColumns = uidl.getBooleanAttribute("gen_span");

                final Iterator<?> cells = uidl.getChildIterator();
                if (spanColumns) {
                    int colCount = uidl.getChildCount();
                    if (cells.hasNext()) {
                        final Object cell = cells.next();
                        if (cell instanceof String) {
                            addSpannedCell(uidl, cell.toString(), aligns[0],
                                    "", htmlContentAllowed, false, null,
                                    colCount);
                        } else {
                            addSpannedCell(uidl, (Widget) cell, aligns[0], "",
                                    false, colCount);
                        }
                    }
                } else {
                    super.addCellsFromUIDL(uidl, aligns, col,
                            visibleColumnIndex);
                }
            }

            private void addSpannedCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean sorted, int colCount) {
                TableCellElement td = DOM.createTD().cast();
                td.setColSpan(colCount);
                initCellWithWidget(w, align, style, sorted, td);
                td.getStyle().setHeight(getRowHeight(), Unit.PX);
                if (addTreeSpacer(rowUidl)) {
                    widgetInHierarchyColumn = w;
                }
            }

            private void addSpannedCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean sorted,
                    String description, int colCount) {
                // String only content is optimized by not using Label widget
                final TableCellElement td = DOM.createTD().cast();
                td.setColSpan(colCount);
                initCellWithText(text, align, style, textIsHTML, sorted,
                        description, td);
                td.getStyle().setHeight(getRowHeight(), Unit.PX);
                addTreeSpacer(rowUidl);
            }

            @Override
            protected void setCellWidth(int cellIx, int width) {
                if (isSpanColumns()) {
                    if (showRowHeaders) {
                        if (cellIx == 0) {
                            super.setCellWidth(0, width);
                        } else {
                            // We need to recalculate the spanning TDs width for
                            // every cellIx in order to support column resizing.
                            calcAndSetSpanWidthOnCell(1);
                        }
                    } else {
                        // Same as above.
                        calcAndSetSpanWidthOnCell(0);
                    }
                } else {
                    super.setCellWidth(cellIx, width);
                }
            }

            private void calcAndSetSpanWidthOnCell(final int cellIx) {
                int spanWidth = 0;
                for (int ix = (showRowHeaders ? 1 : 0); ix < tHead
                        .getVisibleCellCount(); ix++) {
                    spanWidth += tHead.getHeaderCell(ix).getOffsetWidth();
                }
                Util.setWidthExcludingPaddingAndBorder((Element) getElement()
                        .getChild(cellIx), spanWidth, 13, false);
            }
        }

        private int getIndentWidth() {
            return indentWidth;
        }

        @Override
        protected int getMaxIndent() {
            return maxIndent;
        }

        @Override
        protected void calculateMaxIndent() {
            int maxIndent = 0;
            Iterator<Widget> iterator = iterator();
            while (iterator.hasNext()) {
                VTreeTableRow next = (VTreeTableRow) iterator.next();
                maxIndent = Math.max(maxIndent, next.getIndent());
            }
            this.maxIndent = maxIndent;
        }

        private void detectIndent(VTreeTableRow vTreeTableRow) {
            indentWidth = vTreeTableRow.treeSpacer.getOffsetWidth();
            if (indentWidth == 0) {
                indentWidth = -1;
                return;
            }
            Iterator<Widget> iterator = iterator();
            while (iterator.hasNext()) {
                VTreeTableRow next = (VTreeTableRow) iterator.next();
                next.setIndent();
            }
            calculateMaxIndent();
        }

        protected void unlinkRowsAnimatedAndUpdateCacheWhenFinished(
                final int firstIndex, final int rows) {
            List<VScrollTableRow> rowsToDelete = new ArrayList<VScrollTableRow>();
            for (int ix = firstIndex; ix < firstIndex + rows; ix++) {
                VScrollTableRow row = getRowByRowIndex(ix);
                if (row != null) {
                    rowsToDelete.add(row);
                }
            }
            if (!rowsToDelete.isEmpty()) {
                // #8810 Only animate if there's something to animate
                RowCollapseAnimation anim = new RowCollapseAnimation(
                        rowsToDelete) {
                    @Override
                    protected void onComplete() {
                        super.onComplete();
                        // Actually unlink the rows and update the cache after
                        // the
                        // animation is done.
                        unlinkAndReindexRows(firstIndex, rows);
                        discardRowsOutsideCacheWindow();
                        ensureCacheFilled();
                    }
                };
                anim.run(150);
            }
        }

        protected List<VScrollTableRow> insertRowsAnimated(UIDL rowData,
                int firstIndex, int rows) {
            List<VScrollTableRow> insertedRows = insertAndReindexRows(rowData,
                    firstIndex, rows);
            if (!insertedRows.isEmpty()) {
                // Only animate if there's something to animate (#8810)
                RowExpandAnimation anim = new RowExpandAnimation(insertedRows);
                anim.run(150);
            }
            scrollBody.calculateMaxIndent();
            return insertedRows;
        }

        /**
         * Prepares the table for animation by copying the background colors of
         * all TR elements to their respective TD elements if the TD element is
         * transparent. This is needed, since if TDs have transparent
         * backgrounds, the rows sliding behind them are visible.
         */
        private class AnimationPreparator {
            private final int lastItemIx;

            public AnimationPreparator(int lastItemIx) {
                this.lastItemIx = lastItemIx;
            }

            public void prepareTableForAnimation() {
                int ix = lastItemIx;
                VScrollTableRow row = null;
                while ((row = getRowByRowIndex(ix)) != null) {
                    copyTRBackgroundsToTDs(row);
                    --ix;
                }
            }

            private void copyTRBackgroundsToTDs(VScrollTableRow row) {
                Element tr = row.getElement();
                ComputedStyle cs = new ComputedStyle(tr);
                String backgroundAttachment = cs
                        .getProperty("backgroundAttachment");
                String backgroundClip = cs.getProperty("backgroundClip");
                String backgroundColor = cs.getProperty("backgroundColor");
                String backgroundImage = cs.getProperty("backgroundImage");
                String backgroundOrigin = cs.getProperty("backgroundOrigin");
                for (int ix = 0; ix < tr.getChildCount(); ix++) {
                    Element td = tr.getChild(ix).cast();
                    if (!elementHasBackground(td)) {
                        td.getStyle().setProperty("backgroundAttachment",
                                backgroundAttachment);
                        td.getStyle().setProperty("backgroundClip",
                                backgroundClip);
                        td.getStyle().setProperty("backgroundColor",
                                backgroundColor);
                        td.getStyle().setProperty("backgroundImage",
                                backgroundImage);
                        td.getStyle().setProperty("backgroundOrigin",
                                backgroundOrigin);
                    }
                }
            }

            private boolean elementHasBackground(Element element) {
                ComputedStyle cs = new ComputedStyle(element);
                String clr = cs.getProperty("backgroundColor");
                String img = cs.getProperty("backgroundImage");
                return !("rgba(0, 0, 0, 0)".equals(clr.trim())
                        || "transparent".equals(clr.trim()) || img == null);
            }

            public void restoreTableAfterAnimation() {
                int ix = lastItemIx;
                VScrollTableRow row = null;
                while ((row = getRowByRowIndex(ix)) != null) {
                    restoreStyleForTDsInRow(row);

                    --ix;
                }
            }

            private void restoreStyleForTDsInRow(VScrollTableRow row) {
                Element tr = row.getElement();
                for (int ix = 0; ix < tr.getChildCount(); ix++) {
                    Element td = tr.getChild(ix).cast();
                    td.getStyle().clearProperty("backgroundAttachment");
                    td.getStyle().clearProperty("backgroundClip");
                    td.getStyle().clearProperty("backgroundColor");
                    td.getStyle().clearProperty("backgroundImage");
                    td.getStyle().clearProperty("backgroundOrigin");
                }
            }
        }

        /**
         * Animates row expansion using the GWT animation framework.
         * 
         * The idea is as follows:
         * 
         * 1. Insert all rows normally
         * 
         * 2. Insert a newly created DIV containing a new TABLE element below
         * the DIV containing the actual scroll table body.
         * 
         * 3. Clone the rows that were inserted in step 1 and attach the clones
         * to the new TABLE element created in step 2.
         * 
         * 4. The new DIV from step 2 is absolutely positioned so that the last
         * inserted row is just behind the row that was expanded.
         * 
         * 5. Hide the contents of the originally inserted rows by setting the
         * DIV.v-table-cell-wrapper to display:none;.
         * 
         * 6. Set the height of the originally inserted rows to 0.
         * 
         * 7. The animation loop slides the DIV from step 2 downwards, while at
         * the same pace growing the height of each of the inserted rows from 0
         * to full height. The first inserted row grows from 0 to full and after
         * this the second row grows from 0 to full, etc until all rows are full
         * height.
         * 
         * 8. Remove the DIV from step 2
         * 
         * 9. Restore display:block; to the DIV.v-table-cell-wrapper elements.
         * 
         * 10. DONE
         */
        private class RowExpandAnimation extends Animation {

            private final List<VScrollTableRow> rows;
            private Element cloneDiv;
            private Element cloneTable;
            private AnimationPreparator preparator;

            /**
             * @param rows
             *            List of rows to animate. Must not be empty.
             */
            public RowExpandAnimation(List<VScrollTableRow> rows) {
                this.rows = rows;
                buildAndInsertAnimatingDiv();
                preparator = new AnimationPreparator(rows.get(0).getIndex() - 1);
                preparator.prepareTableForAnimation();
                for (VScrollTableRow row : rows) {
                    cloneAndAppendRow(row);
                    row.addStyleName("v-table-row-animating");
                    setCellWrapperDivsToDisplayNone(row);
                    row.setHeight(getInitialHeight());
                }
            }

            protected String getInitialHeight() {
                return "0px";
            }

            private void cloneAndAppendRow(VScrollTableRow row) {
                Element clonedTR = null;
                clonedTR = row.getElement().cloneNode(true).cast();
                clonedTR.getStyle().setVisibility(Visibility.VISIBLE);
                cloneTable.appendChild(clonedTR);
            }

            protected double getBaseOffset() {
                return rows.get(0).getAbsoluteTop()
                        - rows.get(0).getParent().getAbsoluteTop()
                        - rows.size() * getRowHeight();
            }

            private void buildAndInsertAnimatingDiv() {
                cloneDiv = DOM.createDiv();
                cloneDiv.addClassName("v-treetable-animation-clone-wrapper");
                cloneTable = DOM.createTable();
                cloneTable.addClassName("v-treetable-animation-clone");
                cloneDiv.appendChild(cloneTable);
                insertAnimatingDiv();
            }

            private void insertAnimatingDiv() {
                Element tableBody = getElement();
                Element tableBodyParent = tableBody.getParentElement();
                tableBodyParent.insertAfter(cloneDiv, tableBody);
            }

            @Override
            protected void onUpdate(double progress) {
                animateDiv(progress);
                animateRowHeights(progress);
            }

            private void animateDiv(double progress) {
                double offset = calculateDivOffset(progress, getRowHeight());

                cloneDiv.getStyle().setTop(getBaseOffset() + offset, Unit.PX);
            }

            private void animateRowHeights(double progress) {
                double rh = getRowHeight();
                double vlh = calculateHeightOfAllVisibleLines(progress, rh);
                int ix = 0;

                while (ix < rows.size()) {
                    double height = vlh < rh ? vlh : rh;
                    rows.get(ix).setHeight(height + "px");
                    vlh -= height;
                    ix++;
                }
            }

            protected double calculateHeightOfAllVisibleLines(double progress,
                    double rh) {
                return rows.size() * rh * progress;
            }

            protected double calculateDivOffset(double progress, double rh) {
                return progress * rows.size() * rh;
            }

            @Override
            protected void onComplete() {
                preparator.restoreTableAfterAnimation();
                for (VScrollTableRow row : rows) {
                    resetCellWrapperDivsDisplayProperty(row);
                    row.removeStyleName("v-table-row-animating");
                }
                Element tableBodyParent = getElement().getParentElement();
                tableBodyParent.removeChild(cloneDiv);
            }

            private void setCellWrapperDivsToDisplayNone(VScrollTableRow row) {
                Element tr = row.getElement();
                for (int ix = 0; ix < tr.getChildCount(); ix++) {
                    getWrapperDiv(tr, ix).getStyle().setDisplay(Display.NONE);
                }
            }

            private Element getWrapperDiv(Element tr, int tdIx) {
                Element td = tr.getChild(tdIx).cast();
                return td.getChild(0).cast();
            }

            private void resetCellWrapperDivsDisplayProperty(VScrollTableRow row) {
                Element tr = row.getElement();
                for (int ix = 0; ix < tr.getChildCount(); ix++) {
                    getWrapperDiv(tr, ix).getStyle().clearProperty("display");
                }
            }

        }

        /**
         * This is the inverse of the RowExpandAnimation and is implemented by
         * extending it and overriding the calculation of offsets and heights.
         */
        private class RowCollapseAnimation extends RowExpandAnimation {

            private final List<VScrollTableRow> rows;

            /**
             * @param rows
             *            List of rows to animate. Must not be empty.
             */
            public RowCollapseAnimation(List<VScrollTableRow> rows) {
                super(rows);
                this.rows = rows;
            }

            @Override
            protected String getInitialHeight() {
                return getRowHeight() + "px";
            }

            @Override
            protected double getBaseOffset() {
                return getRowHeight();
            }

            @Override
            protected double calculateHeightOfAllVisibleLines(double progress,
                    double rh) {
                return rows.size() * rh * (1 - progress);
            }

            @Override
            protected double calculateDivOffset(double progress, double rh) {
                return -super.calculateDivOffset(progress, rh);
            }
        }
    }

    /**
     * Icons rendered into first actual column in TreeTable, not to row header
     * cell
     */
    @Override
    protected String buildCaptionHtmlSnippet(UIDL uidl) {
        if (uidl.getTag().equals("column")) {
            return super.buildCaptionHtmlSnippet(uidl);
        } else {
            String s = uidl.getStringAttribute("caption");
            return s;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    @Override
    public boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (collapseRequest || focusParentResponsePending) {
            // Enqueue the event if there might be pending content changes from
            // the server
            if (pendingNavigationEvents.size() < 10) {
                // Only keep 10 keyboard events in the queue
                PendingNavigationEvent pendingNavigationEvent = new PendingNavigationEvent(
                        keycode, ctrl, shift);
                pendingNavigationEvents.add(pendingNavigationEvent);
            }
            return true;
        }

        VTreeTableRow focusedRow = (VTreeTableRow) getFocusedRow();
        if (focusedRow != null) {
            if (focusedRow.canHaveChildren
                    && ((keycode == KeyCodes.KEY_RIGHT && !focusedRow.open) || (keycode == KeyCodes.KEY_LEFT && focusedRow.open))) {
                if (!ctrl) {
                    client.updateVariable(paintableId, "selectCollapsed", true,
                            false);
                }
                sendSelectedRows(false);
                sendToggleCollapsedUpdate(focusedRow.getKey());
                return true;
            } else if (keycode == KeyCodes.KEY_RIGHT && focusedRow.open) {
                // already expanded, move selection down if next is on a deeper
                // level (is-a-child)
                VTreeTableScrollBody body = (VTreeTableScrollBody) focusedRow
                        .getParent();
                Iterator<Widget> iterator = body.iterator();
                VTreeTableRow next = null;
                while (iterator.hasNext()) {
                    next = (VTreeTableRow) iterator.next();
                    if (next == focusedRow) {
                        next = (VTreeTableRow) iterator.next();
                        break;
                    }
                }
                if (next != null) {
                    if (next.depth > focusedRow.depth) {
                        selectionPending = true;
                        return super.handleNavigation(getNavigationDownKey(),
                                ctrl, shift);
                    }
                } else {
                    // Note, a minor change here for a bit false behavior if
                    // cache rows is disabled + last visible row + no childs for
                    // the node
                    selectionPending = true;
                    return super.handleNavigation(getNavigationDownKey(), ctrl,
                            shift);
                }
            } else if (keycode == KeyCodes.KEY_LEFT) {
                // already collapsed move selection up to parent node
                // do on the server side as the parent is not necessary
                // rendered on the client, could check if parent is visible if
                // a performance issue arises

                client.updateVariable(paintableId, "focusParent",
                        focusedRow.getKey(), true);

                // Set flag that we should enqueue navigation events until we
                // get a response to this request
                focusParentResponsePending = true;

                return true;
            }
        }
        return super.handleNavigation(keycode, ctrl, shift);
    }

    private void sendToggleCollapsedUpdate(String rowKey) {
        collapsedRowKey = rowKey;
        collapseRequest = true;
        client.updateVariable(paintableId, "toggleCollapsed", rowKey, true);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONKEYUP && selectionPending) {
            sendSelectedRows();
        }
    }

    @Override
    protected void sendSelectedRows(boolean immediately) {
        super.sendSelectedRows(immediately);
        selectionPending = false;
    }

    @Override
    protected void reOrderColumn(String columnKey, int newIndex) {
        super.reOrderColumn(columnKey, newIndex);
        // current impl not intelligent enough to survive without visiting the
        // server to redraw content
        client.sendPendingVariableChanges();
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style + " v-treetable");
    }

    @Override
    public void updateTotalRows(UIDL uidl) {
        // Make sure that initializedAndAttached & al are not reset when the
        // totalrows are updated on expand/collapse requests.
        int newTotalRows = uidl.getIntAttribute("totalrows");
        setTotalRows(newTotalRows);
    }

}
