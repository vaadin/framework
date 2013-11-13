/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.ui.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;
import com.vaadin.client.ui.grid.PositionFunction.AbsolutePosition;
import com.vaadin.client.ui.grid.PositionFunction.Translate3DPosition;
import com.vaadin.client.ui.grid.PositionFunction.TranslatePosition;
import com.vaadin.client.ui.grid.PositionFunction.WebkitTranslate3DPosition;

/*-

 Maintenance Notes! Reading these might save your day.


 == Row Container Structure

 AbstractRowContainer
 |-- AbstractStaticRowContainer
 | |-- HeaderRowContainer
 | `-- FooterContainer
 `-- BodyRowContainer

 AbstractRowContainer is intended to contain all common logic
 between RowContainers. It manages the bookkeeping of row
 count, makes sure that all individual cells are rendered
 the same way, and so on.

 AbstractStaticRowContainer has some special logic that is
 required by all RowContainers that don't scroll (hence the
 word "static"). HeaderRowContainer and FooterRowContainer
 are pretty thin special cases of a StaticRowContainer
 (mostly relating to positioning of the root element).

 BodyRowContainer could also be split into an additional
 "AbstractScrollingRowContainer", but I felt that no more
 inner classes were needed. So it contains both logic
 required for making things scroll about, and equivalent
 special cases for layouting, as are found in
 Header/FooterRowContainers.


 == The Three Indices

 Each RowContainer can be thought to have three levels of
 indices for any given displayed row (but the distinction
 matters primarily for the BodyRowContainer, because of the
 way it scrolls through data):

 - Logical index
 - Physical (or DOM) index
 - Visual index

 LOGICAL INDEX is the index that is linked to the data
 source. If you want your data source to represent a SQL
 database with 10 000 rows, the 7 000:th row in the SQL has a
 logical index of 6 999, since the index is 0-based (unless
 that data source does some funky logic).

 PHYSICAL INDEX is the index for a row that you see in a
 browser's DOM inspector. If your row is the second <tr>
 element within a <tbody> tag, it has a physical index of 1
 (because of 0-based indices). In Header and
 FooterRowContainers, you are safe to assume that the logical
 index is the same as the physical index. But because the
 BodyRowContainer never displays large data sources entirely
 in the DOM, a physical index usually has no apparent direct
 relationship with its logical index.

 VISUAL INDEX is the index relating to the order that you
 see a row in, in the browser, as it is rendered. The
 topmost row is 0, the second is 1, and so on. The visual
 index is similar to the physical index in the sense that
 Header and FooterRowContainers can assume a 1:1
 relationship between visual index and logical index. And
 again, BodyRowContainer has no such relationship. The
 body's visual index has additionally no apparent
 relationship with its physical index. Because the <tr> tags
 are reused in the body and visually repositioned with CSS
 as the user scrolls, the relationship between physical
 index and visual index is quickly broken. You can get an
 element's visual index via the field
 BodyRowContainer.visualRowOrder.

 */

/**
 * A workaround-class for GWT and JSNI.
 * <p>
 * GWT is unable to handle some method calls to Java methods in inner-classes
 * from within JSNI blocks. Having that inner class implement a non-inner-class
 * (or interface), makes it possible for JSNI to indirectly refer to the inner
 * class, by invoking methods and fields in the non-inner-class.
 * 
 * @see Escalator.Scroller
 */
abstract class JsniWorkaround {
    /**
     * A JavaScript function that handles the scroll DOM event, and passes it on
     * to Java code.
     * 
     * @see #createScrollListenerFunction(Escalator)
     * @see Escalator#onScroll(double,double)
     * @see Escalator.Scroller#onScroll(double, double)
     */
    protected final JavaScriptObject scrollListenerFunction;

    /**
     * A JavaScript function that handles the mousewheel DOM event, and passes
     * it on to Java code.
     * 
     * @see #createMousewheelListenerFunction(Escalator)
     * @see Escalator#onScroll(double,double)
     * @see Escalator.Scroller#onScroll(double, double)
     */
    protected final JavaScriptObject mousewheelListenerFunction;

    protected JsniWorkaround(final Escalator escalator) {
        scrollListenerFunction = createScrollListenerFunction(escalator);
        mousewheelListenerFunction = createMousewheelListenerFunction(escalator);
    }

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #scrollListenerFunction}.
     * 
     * @param esc
     *            a reference to the current instance of {@link Escalator}
     * @see Escalator#onScroll(double,double)
     */
    protected abstract JavaScriptObject createScrollListenerFunction(
            Escalator esc);

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #mousewheelListenerFunction}.
     * 
     * @param esc
     *            a reference to the current instance of {@link Escalator}
     * @see Escalator#onScroll(double,double)
     */
    protected abstract JavaScriptObject createMousewheelListenerFunction(
            Escalator esc);
}

/**
 * A low-level table-like widget that features a scrolling virtual viewport and
 * lazily generated rows.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Escalator extends Widget {

    // todo comments legend
    /*
     * [[optimize]]: There's an opportunity to rewrite the code in such a way
     * that it _might_ perform better (rememeber to measure, implement,
     * re-measure)
     */
    /*
     * [[escalator]]: This code will require alterations that are relevant for
     * the escalator functionality.
     */
    /*
     * [[rowwidth]] [[colwidth]]: This code will require alterations that are
     * relevant for being able to support variable row heights or column widths.
     * NOTE: these bits can most often also be identified by searching for code
     * reading the ROW_HEIGHT_PX and COL_WIDTH_PX constans.
     */
    /*
     * [[API]]: Implementing this suggestion would require a change in the
     * public API. These suggestions usually don't come lightly.
     */
    /*
     * [[mpixscroll]]: This code will require alterations that are relevant for
     * supporting the scrolling through more pixels than some browsers normally
     * would support. (i.e. when we support more than "a million" pixels in the
     * escalator DOM). NOTE: these bits can most often also be identified by
     * searching for code that call scrollElem.getScrollTop();.
     */
    /*
     * [[frozencol]]: This needs to be re-inspected once frozen columns are
     * being implemented.
     */
    /*
     * [[widgets]]: This needs to be re-inspected once GWT/Vaadin widgets are
     * being supported.
     */

    private static final int ROW_HEIGHT_PX = 20;
    private static final int COLUMN_WIDTH_PX = 100;

    /** An inner class that handles all logic related to scrolling. */
    private class Scroller extends JsniWorkaround {
        private double lastScrollTop = -1;
        private double lastScrollLeft = -1;

        public Scroller() {
            super(Escalator.this);
        }

        @Override
        protected native JavaScriptObject createScrollListenerFunction(
                Escalator esc)
        /*-{
            return $entry(function(e) {
                esc.@com.vaadin.client.ui.grid.Escalator::onScroll()();
            });
        }-*/;

        @Override
        protected native JavaScriptObject createMousewheelListenerFunction(
                Escalator esc)
        /*-{
            return $entry(function(e) {
                var deltaX = e.deltaX ? e.deltaX : -0.5*e.wheelDeltaX;
                var deltaY = e.deltaY ? e.deltaY : -0.5*e.wheelDeltaY;
                
                // IE8 has only delta y
                if (isNaN(deltaY)) {
                    deltaY = -0.5*e.wheelDelta;
                }

                // TODO [[optimize]]: instead of using "+=" that potentially
                // causes a reflow, update a new scroll absolute position.
                if (!isNaN(deltaY)) {
                    // the scroll event handler will make sure the content is moved around appropriately
                    esc.@com.vaadin.client.ui.grid.Escalator::scrollerElem.scrollTop += deltaY;
                }
                
                if (!isNaN(deltaX)) {
                    // the scroll event handler will make sure the content is moved around appropriately
                    esc.@com.vaadin.client.ui.grid.Escalator::scrollerElem.scrollLeft += deltaX;
                }

                // TODO: only prevent if not scrolled to end/bottom. Or no? UX team needs to decide.
                if (e.preventDefault) {
                    e.preventDefault();
                } else {
                    e.returnValue = false;
                }
            });
        }-*/;

        /**
         * Recalculates the virtual viewport represented by the scrollbars, so
         * that the sizes of the scroll handles appear correct in the browser
         */
        public void recalculateScrollbarsForVirtualViewport() {
            double scrollerHeight = height - header.height;

            /*
             * It looks better this way: if we only have a vertical scrollbar,
             * keep it between the header and footer. But if we have a
             * horizontal scrollbar, envelop the footer also.
             */
            if (!needsHorizontalScrollbars()) {
                scrollerHeight -= footer.height;
            }

            scrollerElem.getStyle().setHeight(scrollerHeight, Unit.PX);

            final double scrollerTop = header.height;
            scrollerElem.getStyle().setTop(scrollerTop, Unit.PX);

            /*
             * TODO [[freezecol]]: cut scrollerElem from the left to take freeze
             * columns into account. innerScroller probably needs some
             * adjustments too.
             */

            // TODO [[rowheight]]: adjust for variable row heights.
            double innerScrollerHeight = ROW_HEIGHT_PX * body.getRowCount();
            if (needsHorizontalScrollbars()) {
                innerScrollerHeight += footer.height;
            }
            innerScrollerElem.getStyle()
                    .setHeight(innerScrollerHeight, Unit.PX);

            // TODO [[colwidth]]: adjust for variable column widths.
            innerScrollerElem.getStyle().setWidth(
                    COLUMN_WIDTH_PX * columnConfiguration.getColumnCount(),
                    Unit.PX);

            // we might've got new or got rid of old scrollbars.
            recalculateTableWrapperSize();
        }

        /**
         * Makes sure that the viewport of the table is the correct size, and
         * that it takes any possible scrollbars into account
         */
        public void recalculateTableWrapperSize() {
            double wrapperHeight = height;
            if (scrollerElem.getOffsetWidth() < innerScrollerElem
                    .getOffsetWidth()) {
                wrapperHeight -= Util.getNativeScrollbarSize();
            }

            double wrapperWidth = width;
            if (scrollerElem.getOffsetHeight() - footer.height < innerScrollerElem
                    .getOffsetHeight()) {
                wrapperWidth -= Util.getNativeScrollbarSize();
            }

            tableWrapper.getStyle().setHeight(wrapperHeight, Unit.PX);
            tableWrapper.getStyle().setWidth(wrapperWidth, Unit.PX);
        }

        /**
         * Logical scrolling event handler for the entire widget.
         * 
         * @param scrollLeft
         *            the current number of pixels that the user has scrolled
         *            from left
         * @param scrollTop
         *            the current number of pixels that the user has scrolled
         *            from the top
         */
        public void onScroll() {
            if (internalScrollEventCalls > 0) {
                internalScrollEventCalls--;
                return;
            }

            final int scrollLeft = scrollerElem.getScrollLeft();
            final int scrollTop = scrollerElem.getScrollTop();

            if (lastScrollLeft != scrollLeft) {
                // TODO [[frozen]]: frozen columns should be offset here.

                position.set(headElem, -scrollLeft, 0);

                /*
                 * TODO [[optimize]]: cache this value in case the instanceof
                 * check has undesirable overhead. This could also be a
                 * candidate for some deferred binding magic so that e.g.
                 * AbsolutePosition is not even considered in permutations that
                 * we know support something better. That would let the compiler
                 * completely remove the entire condition since it knows that
                 * the if will never be true.
                 */
                if (position instanceof AbsolutePosition) {
                    /*
                     * we don't want to put "top: 0" on the footer, since it'll
                     * render wrong, as we already have
                     * "bottom: $footer-height".
                     */
                    footElem.getStyle().setLeft(-scrollLeft, Unit.PX);
                } else {
                    position.set(footElem, -scrollLeft, 0);
                }

                lastScrollLeft = scrollLeft;
            }

            body.setBodyScrollPosition(scrollLeft, scrollTop);

            lastScrollTop = scrollTop;
            body.updateEscalatorRowsOnScroll();
            /*
             * TODO [[optimize]]: Might avoid a reflow by first calculating new
             * scrolltop and scrolleft, then doing the escalator magic based on
             * those numbers and only updating the positions after that.
             */
        }

        public native void attachScrollListener(Element element)
        /*-{
             if (element.addEventListener) {
                 element.addEventListener("scroll", this.@com.vaadin.client.ui.grid.JsniWorkaround::scrollListenerFunction);
             } else {
                 element.attachEvent("onscroll", this.@com.vaadin.client.ui.grid.JsniWorkaround::scrollListenerFunction);
             }
        }-*/;

        public native void detachScrollListener(Element element)
        /*-{
            if (element.addEventListener) {
                element.removeEventListener("scroll", this.@com.vaadin.client.ui.grid.JsniWorkaround::scrollListenerFunction);
            } else {
                element.detachEvent("onscroll", this.@com.vaadin.client.ui.grid.JsniWorkaround::scrollListenerFunction);
            }
        }-*/;

        public native void attachMousewheelListener(Element element)
        /*-{

            if (element.addEventListener) {
                // firefox likes "wheel", while others use "mousewheel"
                var eventName = element.onwheel===undefined?"mousewheel":"wheel";
                element.addEventListener(eventName, this.@com.vaadin.client.ui.grid.JsniWorkaround::mousewheelListenerFunction);
            } else {
                // IE8
                element.attachEvent("onmousewheel", this.@com.vaadin.client.ui.grid.JsniWorkaround::mousewheelListenerFunction);
            }
        }-*/;

        public native void detachMousewheelListener(Element element)
        /*-{
            if (element.addEventListener) {
                // firefox likes "wheel", while others use "mousewheel"
                var eventName = element.onwheel===undefined?"mousewheel":"wheel";
                element.removeEventListener(eventName, this.@com.vaadin.client.ui.grid.JsniWorkaround::mousewheelListenerFunction);
            } else {
                // IE8
                element.detachEvent("onmousewheel", this.@com.vaadin.client.ui.grid.JsniWorkaround::mousewheelListenerFunction);
            }
        }-*/;

        public void scrollToColumn(final int columnIndex,
                final ScrollDestination destination, final int padding) {
            // TODO [[colwidth]]
            final int targetStartPx = COLUMN_WIDTH_PX * columnIndex;
            final int targetEndPx = targetStartPx + COLUMN_WIDTH_PX;

            /*
             * TODO [[frozencol]]: offset startPx by the pixels occupied by
             * frozen columns.
             */
            final int viewportStartPx = getScrollLeft();
            int viewportEndPx = viewportStartPx + getElement().getOffsetWidth();
            if (needsVerticalScrollbars()) {
                viewportEndPx -= Util.getNativeScrollbarSize();
            }

            final double scrollLeft = destination.getScrollPos(targetStartPx,
                    targetEndPx, viewportStartPx, viewportEndPx, padding);

            /*
             * note that it doesn't matter if the scroll would go beyond the
             * content, since the browser will adjust for that, and everything
             * fall into line accordingly.
             */
            setScrollLeft((int) scrollLeft);
        }

        public void scrollToRow(final int rowIndex,
                final ScrollDestination destination, final int padding) {
            // TODO [[rowheight]]
            final int targetStartPx = ROW_HEIGHT_PX * rowIndex;
            final int targetEndPx = targetStartPx + ROW_HEIGHT_PX;

            final double viewportStartPx = getScrollTop();
            final double viewportEndPx = viewportStartPx
                    + body.calculateHeight();

            final double scrollTop = destination.getScrollPos(targetStartPx,
                    targetEndPx, viewportStartPx, viewportEndPx, padding);

            /*
             * note that it doesn't matter if the scroll would go beyond the
             * content, since the browser will adjust for that, and everything
             * falls into line accordingly.
             */
            setScrollTop(scrollTop);
        }
    }

    private static final String CLASS_NAME = "v-escalator";

    private abstract class AbstractRowContainer implements RowContainer {
        private EscalatorUpdater updater = EscalatorUpdater.NULL;

        private int rows;

        /**
         * The table section element ({@code <thead>}, {@code <tbody>} or
         * {@code <tfoot>}) the rows (i.e. {@code <tr>} tags) are contained in.
         */
        protected final Element root;

        /** The height of the combined rows in the DOM. */
        protected double height = -1;

        public AbstractRowContainer(final Element rowContainerElement) {
            root = rowContainerElement;
        }

        /**
         * Informs the row container that the height of its respective table
         * section has changed.
         * <p>
         * These calculations might affect some layouting logic, such as the
         * body is being offset by the footer, the footer needs to be readjusted
         * according to its height, and so on.
         * <p>
         * A table section is either header, body or footer.
         * 
         */
        protected void sectionHeightCalculated() {
            // Does nothing by default. Override to catch the "event".
        }

        /**
         * Gets the tag name of an element to represent a cell in a row.
         * <p>
         * Usually {@code "th"} or {@code "td"}.
         * <p>
         * <em>Note:</em> To actually <em>create</em> such an element, use
         * {@link #createCellElement()} instead.
         * 
         * @return the tag name for the element to represent cells as
         * @see #createCellElement()
         */
        protected abstract String getCellElementTagName();

        @Override
        public EscalatorUpdater getEscalatorUpdater() {
            return updater;
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for rows or columns
         * when this method is called.
         * 
         * @see #hasColumnAndRowData()
         */
        @Override
        public void setEscalatorUpdater(final EscalatorUpdater escalatorUpdater) {
            if (escalatorUpdater == null) {
                throw new IllegalArgumentException(
                        "escalator updater cannot be null");
            }

            updater = escalatorUpdater;

            if (hasColumnAndRowData() && getRowCount() > 0) {
                refreshRows(0, getRowCount());
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there are no rows in the DOM when
         * this method is called.
         * 
         * @see #hasSomethingInDom()
         */
        @Override
        public void removeRows(final int index, final int numberOfRows) {
            assertArgumentsAreValidAndWithinRange(index, numberOfRows);

            rows -= numberOfRows;

            if (!isAttached()) {
                return;
            }

            if (hasSomethingInDom()) {
                paintRemoveRows(index, numberOfRows);
            }
        }

        protected abstract void paintRemoveRows(final int index,
                final int numberOfRows);

        private void assertArgumentsAreValidAndWithinRange(final int index,
                final int numberOfRows) throws IllegalArgumentException,
                IndexOutOfBoundsException {
            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            if (index < 0 || index + numberOfRows > getRowCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "row range (" + index + ".." + (index + numberOfRows)
                        + ") was outside of the current number of rows ("
                        + getRowCount() + ")");
            }
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for columns when
         * this method is called.
         * 
         * @see #hasColumnAndRowData()
         */
        @Override
        public void insertRows(final int index, final int numberOfRows) {
            if (index < 0 || index > getRowCount()) {
                throw new IndexOutOfBoundsException("The given index (" + index
                        + ") was outside of the current number of rows (0.."
                        + getRowCount() + ")");
            }

            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            rows += numberOfRows;

            /*
             * only add items in the DOM if the widget itself is attached to the
             * DOM. We can't calculate sizes otherwise.
             */
            if (isAttached()) {
                paintInsertRows(index, numberOfRows);
            }
        }

        /**
         * Actually add rows into the DOM, now that everything can be
         * calculated.
         * 
         * @param visualIndex
         *            the DOM index to add rows into
         * @param numberOfRows
         *            the number of rows to insert
         * @return a list of the added row elements
         */
        protected List<Element> paintInsertRows(final int visualIndex,
                final int numberOfRows) {
            assert isAttached() : "Can't paint rows if Escalator is not attached";

            final List<Element> addedRows = new ArrayList<Element>();

            if (numberOfRows < 1) {
                return addedRows;
            }

            Node referenceRow;
            if (root.getChildCount() != 0 && visualIndex != 0) {
                // get the row node we're inserting stuff after
                referenceRow = root.getChild(visualIndex - 1);
            } else {
                // index is 0, so just prepend.
                referenceRow = null;
            }

            for (int row = visualIndex; row < visualIndex + numberOfRows; row++) {
                final Element tr = DOM.createTR();
                addedRows.add(tr);
                tr.addClassName(CLASS_NAME + "-row");
                referenceRow = insertAfterReferenceAndUpdateIt(root, tr,
                        referenceRow);

                for (int col = 0; col < columnConfiguration.getColumnCount(); col++) {
                    final Element cellElem = createCellElement();
                    tr.appendChild(cellElem);
                }

                refreshRow(tr, row);

                /*
                 * TODO [[optimize]] [[rowwidth]]: When this method is updated
                 * to measure things instead of using hardcoded values, it would
                 * be better to do everything at once after all rows have been
                 * updated to reduce the number of reflows.
                 */
                recalculateRowWidth(tr);
            }

            recalculateSectionHeight();

            return addedRows;
        }

        private Node insertAfterReferenceAndUpdateIt(final Element parent,
                final Element elem, final Node referenceNode) {
            if (referenceNode != null) {
                parent.insertAfter(elem, referenceNode);
            } else {
                /*
                 * referencenode being null means we have offset 0, i.e. make it
                 * the first row
                 */
                /*
                 * TODO [[optimize]]: Is insertFirst or append faster for an
                 * empty root?
                 */
                parent.insertFirst(elem);
            }
            return elem;
        }

        protected void recalculateSectionHeight() {
            final double newHeight = root.getChildCount() * ROW_HEIGHT_PX;
            if (newHeight != height) {
                height = newHeight;
                sectionHeightCalculated();
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for columns when
         * this method is called.
         * 
         * @see #hasColumnAndRowData()
         */
        @Override
        public void refreshRows(final int index, final int numberOfRows) {
            assertArgumentsAreValidAndWithinRange(index, numberOfRows);

            if (!isAttached()) {
                return;
            }

            /*
             * TODO [[escalator]][[rowheight]]: even if no rows are evaluated in
             * the current viewport, the heights of some unrendered rows might
             * change in a refresh. This would cause the scrollbar to be
             * adjusted (in scrollHeight and/or scrollTop). Do we want to take
             * this into account?
             */
            if (hasColumnAndRowData()) {
                /*
                 * TODO [[rowheight]]: nudge rows down with
                 * refreshRowPositions() as needed
                 */
                /*
                 * TODO [[colwidth]]: reapply column and colspan widths as
                 * needed
                 */

                for (int row = index; row < index + numberOfRows; row++) {
                    final Node tr = getTrByVisualIndex(row);
                    refreshRow(tr, row);
                }
            }
        }

        void refreshRow(final Node tr, final int logicalRowIndex) {
            flyweightRow.setup((Element) tr, logicalRowIndex);
            updater.updateCells(flyweightRow, flyweightRow.getCells());

            /*
             * the "assert" guarantees that this code is run only during
             * development/debugging.
             */
            assert flyweightRow.teardown();
        }

        /**
         * Create and setup an empty cell element.
         * 
         * @return a set-up empty cell element
         */
        public Element createCellElement() {
            final Element cellElem = DOM.createElement(getCellElementTagName());
            cellElem.getStyle().setHeight(ROW_HEIGHT_PX, Unit.PX);
            cellElem.getStyle().setWidth(COLUMN_WIDTH_PX, Unit.PX);
            cellElem.addClassName(CLASS_NAME + "-cell");
            return cellElem;
        }

        /**
         * Gets the child element that is visually at a certain index
         * 
         * @param index
         *            the index of the element to retrieve
         * @return the element at position {@code index}
         * @throws IndexOutOfBoundsException
         *             if {@code index} is not valid within {@link #root}
         */
        abstract protected Element getTrByVisualIndex(int index)
                throws IndexOutOfBoundsException;

        abstract protected int getTopVisualRowLogicalIndex();

        protected void paintRemoveColumns(final int offset,
                final int numberOfColumns) {
            final NodeList<Node> childNodes = root.getChildNodes();
            for (int visualRowIndex = 0; visualRowIndex < childNodes
                    .getLength(); visualRowIndex++) {
                final Node tr = childNodes.getItem(visualRowIndex);

                for (int column = 0; column < numberOfColumns; column++) {
                    // TODO [[widgets]]
                    tr.getChild(offset).removeFromParent();
                }
                recalculateRowWidth((Element) tr);
            }

            final int firstRemovedColumnLeft = offset * COLUMN_WIDTH_PX;
            final boolean columnsWereRemovedFromLeftOfTheViewport = scroller.lastScrollLeft > firstRemovedColumnLeft;

            if (columnsWereRemovedFromLeftOfTheViewport) {
                final int removedColumnsPxAmount = numberOfColumns
                        * COLUMN_WIDTH_PX;
                final int leftByDiff = (int) (scroller.lastScrollLeft - removedColumnsPxAmount);
                final int newScrollLeft = Math.max(firstRemovedColumnLeft,
                        leftByDiff);
                scrollerElem.setScrollLeft(newScrollLeft);
            }

            // this needs to be after the scroll position adjustment above.
            scroller.recalculateScrollbarsForVirtualViewport();

        }

        protected void paintInsertColumns(final int offset,
                final int numberOfColumns) {
            final NodeList<Node> childNodes = root.getChildNodes();
            final int topVisualRowLogicalIndex = getTopVisualRowLogicalIndex();

            for (int row = 0; row < childNodes.getLength(); row++) {
                final Element tr = getTrByVisualIndex(row);

                Node referenceCell;
                if (offset != 0) {
                    referenceCell = tr.getChild(offset - 1);
                } else {
                    referenceCell = null;
                }

                for (int col = offset; col < offset + numberOfColumns; col++) {
                    final Element cellElem = createCellElement();
                    referenceCell = insertAfterReferenceAndUpdateIt(tr,
                            cellElem, referenceCell);
                }

                refreshRow(tr, topVisualRowLogicalIndex + row);

                /*
                 * TODO [[optimize]] [[colwidth]]: When this method is updated
                 * to measure things instead of using hardcoded values, it would
                 * be better to do everything at once after all rows have been
                 * updated to reduce the number of reflows.
                 */
                recalculateRowWidth(tr);
            }

            // this needs to be before the scrollbar adjustment.
            scroller.recalculateScrollbarsForVirtualViewport();

            final boolean columnsWereAddedToTheLeftOfViewport = scroller.lastScrollLeft > offset
                    * COLUMN_WIDTH_PX;

            if (columnsWereAddedToTheLeftOfViewport) {
                scrollerElem
                        .setScrollLeft((int) (scroller.lastScrollLeft + numberOfColumns
                                * COLUMN_WIDTH_PX));
            }
        }
    }

    private abstract class AbstractStaticRowContainer extends
            AbstractRowContainer {
        public AbstractStaticRowContainer(final Element headElement) {
            super(headElement);
        }

        @Override
        protected void paintRemoveRows(final int index, final int numberOfRows) {
            for (int i = index; i < index + numberOfRows; i++) {
                final Element tr = (Element) root.getChild(i);
                // TODO [[widgets]]
                tr.removeFromParent();
            }
            recalculateSectionHeight();
        }

        @Override
        protected Element getTrByVisualIndex(final int index)
                throws IndexOutOfBoundsException {
            if (index >= 0 && index < root.getChildCount()) {
                return (Element) root.getChild(index);
            } else {
                throw new IndexOutOfBoundsException("No such visual index: "
                        + index);
            }
        }

        @Override
        protected int getTopVisualRowLogicalIndex() {
            return 0;
        }
    }

    private class HeaderRowContainer extends AbstractStaticRowContainer {
        public HeaderRowContainer(final Element headElement) {
            super(headElement);
        }

        @Override
        protected void sectionHeightCalculated() {
            bodyElem.getStyle().setMarginTop(height, Unit.PX);
        }

        @Override
        protected String getCellElementTagName() {
            return "th";
        }
    }

    private class FooterRowContainer extends AbstractStaticRowContainer {
        public FooterRowContainer(final Element footElement) {
            super(footElement);
        }

        @Override
        protected String getCellElementTagName() {
            return "td";
        }
    }

    private class BodyRowContainer extends AbstractRowContainer {
        /*
         * TODO [[optimize]]: check whether a native JsArray might be faster
         * than LinkedList
         */
        /**
         * The order in which row elements are rendered visually in the browser,
         * with the help of CSS tricks. Usually has nothing to do with the DOM
         * order.
         */
        private final LinkedList<Element> visualRowOrder = new LinkedList<Element>();

        /**
         * Don't use this field directly, because it will not take proper care
         * of all the bookkeeping required.
         * 
         * @deprecated Use {@link #setRowPosition(Element, int, int)} and
         *             {@link #getRowTop(Element)} instead.
         */
        @Deprecated
        private final Map<Element, Integer> rowTopPosMap = new HashMap<Element, Integer>();

        private int tBodyScrollTop = 0;
        private int tBodyScrollLeft = 0;

        public BodyRowContainer(final Element bodyElement) {
            super(bodyElement);
        }

        public void updateEscalatorRowsOnScroll() {
            final int topRowPos = getRowTop(visualRowOrder.getFirst());
            // TODO [[mpixscroll]]
            final int scrollTop = tBodyScrollTop;
            final int viewportOffset = topRowPos - scrollTop;

            /*
             * TODO [[optimize]] this if-else can most probably be refactored
             * into a neater block of code
             */

            if (viewportOffset > 0) {
                // there's empty room on top

                int rowsToMove = (int) Math.ceil((double) viewportOffset
                        / (double) ROW_HEIGHT_PX);
                rowsToMove = Math.min(rowsToMove, root.getChildCount());

                final int end = root.getChildCount();
                final int start = end - rowsToMove;
                final int logicalRowIndex = scrollTop / ROW_HEIGHT_PX;
                moveAndUpdateEscalatorRows(Range.between(start, end), 0,
                        logicalRowIndex);
            }

            else if (viewportOffset + ROW_HEIGHT_PX <= 0) {
                /*
                 * the viewport has been scrolled more than the topmost visual
                 * row.
                 */

                /*
                 * Using the fact that integer division has implicit
                 * floor-function to our advantage here.
                 */
                int rowsToMove = Math.abs(viewportOffset / ROW_HEIGHT_PX);
                rowsToMove = Math.min(rowsToMove, root.getChildCount());

                int logicalRowIndex;
                if (rowsToMove < root.getChildCount()) {
                    /*
                     * We scroll so little that we can just keep adding the rows
                     * below the current escalator
                     */
                    logicalRowIndex = getLogicalRowIndex(visualRowOrder
                            .getLast()) + 1;
                } else {
                    /*
                     * Since we're moving all escalator rows, we need to
                     * calculate the first logical row index from the scroll
                     * position.
                     */
                    logicalRowIndex = scrollTop / ROW_HEIGHT_PX;
                }

                /*
                 * Since we're moving the viewport downwards, the visual index
                 * is always at the bottom. Note: Due to how
                 * moveAndUpdateEscalatorRows works, this will work out even if
                 * we move all the rows, and try to place them "at the end".
                 */
                final int targetVisualIndex = root.getChildCount();

                // make sure that we don't move rows over the data boundary
                boolean aRowWasLeftBehind = false;
                if (logicalRowIndex + rowsToMove > getRowCount()) {
                    /*
                     * TODO [[rowheight]]: with constant row heights, there's
                     * always exactly one row that will be moved beyond the data
                     * source, when viewport is scrolled to the end. This,
                     * however, isn't guaranteed anymore once row heights start
                     * varying.
                     */
                    rowsToMove--;
                    aRowWasLeftBehind = true;
                }

                moveAndUpdateEscalatorRows(Range.between(0, rowsToMove),
                        targetVisualIndex, logicalRowIndex);

                if (aRowWasLeftBehind) {
                    /*
                     * To keep visualRowOrder as a spatially contiguous block of
                     * rows, let's make sure that the one row we didn't move
                     * visually still stays with the pack.
                     */
                    final Range strayRow = Range.withOnly(0);
                    final int topLogicalIndex = getLogicalRowIndex(visualRowOrder
                            .get(1)) - 1;
                    moveAndUpdateEscalatorRows(strayRow, 0, topLogicalIndex);
                }
            }
        }

        @Override
        protected List<Element> paintInsertRows(final int index,
                final int numberOfRows) {
            if (numberOfRows == 0) {
                return Collections.emptyList();
            }

            /*
             * TODO: this method should probably only add physical rows, and not
             * populate them - let everything be populated as appropriate by the
             * logic that follows.
             * 
             * This also would lead to the fact that paintInsertRows wouldn't
             * need to return anything.
             */
            final List<Element> addedRows = fillAndPopulateEscalatorRowsIfNeeded(
                    index, numberOfRows);

            /*
             * insertRows will always change the number of rows - update the
             * scrollbar sizes.
             */
            scroller.recalculateScrollbarsForVirtualViewport();

            final boolean addedRowsAboveCurrentViewport = index * ROW_HEIGHT_PX < scrollerElem
                    .getScrollTop();
            final boolean addedRowsBelowCurrentViewport = index * ROW_HEIGHT_PX > scrollerElem
                    .getScrollTop() + calculateHeight();

            if (addedRowsAboveCurrentViewport) {
                /*
                 * We need to tweak the virtual viewport (scroll handle
                 * positions, table "scroll position" and row locations), but
                 * without re-evaluating any rows.
                 */

                final int yDelta = numberOfRows * ROW_HEIGHT_PX;
                adjustScrollPosIgnoreEvents(yDelta);
            }

            else if (addedRowsBelowCurrentViewport) {
                // NOOP, we already recalculated scrollbars.
            }

            else { // some rows were added inside the current viewport

                final int unupdatedLogicalStart = index + addedRows.size();
                final int visualOffset = getLogicalRowIndex(visualRowOrder
                        .getFirst());

                /*
                 * At this point, we have added new escalator rows, if so
                 * needed.
                 * 
                 * If more rows were added than the new escalator rows can
                 * account for, we need to start to spin the escalator to update
                 * the remaining rows aswell.
                 */
                final int rowsStillNeeded = numberOfRows - addedRows.size();
                final Range unupdatedVisual = convertToVisual(Range.withLength(
                        unupdatedLogicalStart, rowsStillNeeded));
                final int end = root.getChildCount();
                final int start = end - unupdatedVisual.length();
                final int visualTargetIndex = unupdatedLogicalStart
                        - visualOffset;
                moveAndUpdateEscalatorRows(Range.between(start, end),
                        visualTargetIndex, unupdatedLogicalStart);

                // move the surrounding rows to their correct places.
                int rowTop = (unupdatedLogicalStart + (end - start))
                        * ROW_HEIGHT_PX;
                final ListIterator<Element> i = visualRowOrder
                        .listIterator(visualTargetIndex + (end - start));
                while (i.hasNext()) {
                    final Element tr = i.next();
                    setRowPosition(tr, 0, rowTop);
                    rowTop += ROW_HEIGHT_PX;
                }
            }
            return addedRows;
        }

        /**
         * Move escalator rows around, and make sure everything gets
         * appropriately repositioned and repainted.
         * 
         * @param visualSourceRange
         *            the range of rows to move to a new place
         * @param visualTargetIndex
         *            the visual index where the rows will be placed to
         * @param logicalTargetIndex
         *            the logical index to be assigned to the first moved row
         * @throws IllegalArgumentException
         *             if any of <code>visualSourceRange.getStart()</code>,
         *             <code>visualTargetIndex</code> or
         *             <code>logicalTargetIndex</code> is a negative number; or
         *             if <code>visualTargetInfo</code> is greater than the
         *             number of escalator rows.
         */
        private void moveAndUpdateEscalatorRows(final Range visualSourceRange,
                final int visualTargetIndex, final int logicalTargetIndex)
                throws IllegalArgumentException {

            if (visualSourceRange.isEmpty()) {
                return;
            }

            if (visualSourceRange.getStart() < 0) {
                throw new IllegalArgumentException(
                        "Logical source start must be 0 or greater (was "
                                + visualSourceRange.getStart() + ")");
            } else if (logicalTargetIndex < 0) {
                throw new IllegalArgumentException(
                        "Logical target must be 0 or greater");
            } else if (visualTargetIndex < 0) {
                throw new IllegalArgumentException(
                        "Visual target must be 0 or greater");
            } else if (visualTargetIndex > root.getChildCount()) {
                throw new IllegalArgumentException(
                        "Visual target must not be greater than the number of escalator rows");
            } else if (logicalTargetIndex + visualSourceRange.length() > getRowCount()) {
                final int logicalEndIndex = logicalTargetIndex
                        + visualSourceRange.length() - 1;
                throw new IllegalArgumentException(
                        "Logical target leads to rows outside of the data range ("
                                + logicalTargetIndex + ".." + logicalEndIndex
                                + ")");
            }

            /*
             * Since we move a range into another range, the indices might move
             * about. Having 10 rows, if we move 0..1 to index 10 (to the end of
             * the collection), the target range will end up being 8..9, instead
             * of 10..11.
             * 
             * This applies only if we move elements forward in the collection,
             * not backward.
             */
            final int adjustedVisualTargetIndex;
            if (visualSourceRange.getStart() < visualTargetIndex) {
                adjustedVisualTargetIndex = visualTargetIndex
                        - visualSourceRange.length();
            } else {
                adjustedVisualTargetIndex = visualTargetIndex;
            }

            if (visualSourceRange.getStart() != adjustedVisualTargetIndex) {

                /*
                 * Reorder the rows to their correct places within
                 * visualRowOrder (unless rows are moved back to their original
                 * places)
                 */

                /*
                 * TODO [[optimize]]: move whichever set is smaller: the ones
                 * explicitly moved, or the others. So, with 10 escalator rows,
                 * if we are asked to move idx[0..8] to the end of the list,
                 * it's faster to just move idx[9] to the beginning.
                 */

                final List<Element> removedRows = new ArrayList<Element>(
                        visualSourceRange.length());
                for (int i = 0; i < visualSourceRange.length(); i++) {
                    final Element tr = visualRowOrder.remove(visualSourceRange
                            .getStart());
                    removedRows.add(tr);
                }
                visualRowOrder.addAll(adjustedVisualTargetIndex, removedRows);
            }

            { // Refresh the contents of the affected rows
                final ListIterator<Element> iter = visualRowOrder
                        .listIterator(adjustedVisualTargetIndex);
                for (int logicalIndex = logicalTargetIndex; logicalIndex < logicalTargetIndex
                        + visualSourceRange.length(); logicalIndex++) {
                    final Element tr = iter.next();
                    refreshRow(tr, logicalIndex);
                }
            }

            { // Reposition the rows that were moved
                int newRowTop = logicalTargetIndex * ROW_HEIGHT_PX;

                final ListIterator<Element> iter = visualRowOrder
                        .listIterator(adjustedVisualTargetIndex);
                for (int i = 0; i < visualSourceRange.length(); i++) {
                    final Element tr = iter.next();
                    setRowPosition(tr, 0, newRowTop);
                    newRowTop += ROW_HEIGHT_PX;
                }
            }
        }

        /**
         * Adjust the scroll position without having the scroll handler have any
         * side-effects.
         * <p>
         * <em>Note:</em> {@link Scroller#onScroll(double, double)}
         * <em>will</em> be triggered, but it not do anything, with the help of
         * {@link Escalator#internalScrollEventCalls}.
         * 
         * @param yDelta
         *            the delta of pixels to scrolls. A positive value moves the
         *            viewport downwards, while a negative value moves the
         *            viewport upwards
         */
        public void adjustScrollPosIgnoreEvents(final int yDelta) {
            if (yDelta == 0) {
                return;
            }

            internalScrollEventCalls++;
            scrollerElem.setScrollTop(scrollerElem.getScrollTop() + yDelta);

            final int snappedYDelta = yDelta - yDelta % ROW_HEIGHT_PX;
            for (final Element tr : visualRowOrder) {
                setRowPosition(tr, 0, getRowTop(tr) + snappedYDelta);
            }
            setBodyScrollPosition(tBodyScrollLeft, tBodyScrollTop + yDelta);
        }

        private void setRowPosition(final Element tr, final int x, final int y) {
            position.set(tr, x, y);
            rowTopPosMap.put(tr, y);
        }

        private int getRowTop(final Element tr) {
            return rowTopPosMap.get(tr);
        }

        private List<Element> fillAndPopulateEscalatorRowsIfNeeded(
                final int index, final int numberOfRows) {

            final int maxEscalatorRowCapacity = (int) Math
                    .ceil(calculateHeight() / ROW_HEIGHT_PX) + 1;
            final int escalatorRowsStillFit = maxEscalatorRowCapacity
                    - root.getChildCount();
            final int escalatorRowsNeeded = Math.min(numberOfRows,
                    escalatorRowsStillFit);

            if (escalatorRowsNeeded > 0) {

                final List<Element> addedRows = super.paintInsertRows(index,
                        escalatorRowsNeeded);
                visualRowOrder.addAll(index, addedRows);

                /*
                 * We need to figure out the top positions for the rows we just
                 * added.
                 */
                for (int i = 0; i < addedRows.size(); i++) {
                    setRowPosition(addedRows.get(i), 0, (index + i)
                            * ROW_HEIGHT_PX);
                }

                /* Move the other rows away from above the added escalator rows */
                for (int i = index + addedRows.size(); i < visualRowOrder
                        .size(); i++) {
                    final Element tr = visualRowOrder.get(i);
                    setRowPosition(tr, 0, i * ROW_HEIGHT_PX);
                }

                return addedRows;
            } else {
                return new ArrayList<Element>();
            }
        }

        @Override
        protected void paintRemoveRows(final int index, final int numberOfRows) {

            final Range viewportRange = Range.withLength(
                    getLogicalRowIndex(visualRowOrder.getFirst()),
                    visualRowOrder.size());

            final Range removedRowsRange = Range
                    .withLength(index, numberOfRows);

            final Range[] partitions = removedRowsRange
                    .partitionWith(viewportRange);
            final Range removedAbove = partitions[0];
            final Range removedLogicalInside = partitions[1];
            final Range removedVisualInside = convertToVisual(removedLogicalInside);

            /*
             * TODO: extract the following if-block to a separate method. I'll
             * leave this be inlined for now, to make linediff-based code
             * reviewing easier. Probably will be moved in the following patch
             * set.
             */

            /*
             * Adjust scroll position in one of two scenarios:
             * 
             * 1) Rows were removed above. Then we just need to adjust the
             * scrollbar by the height of the removed rows.
             * 
             * 2) There are no logical rows above, and at least the first (if
             * not more) visual row is removed. Then we need to snap the scroll
             * position to the first visible row (i.e. reset scroll position to
             * absolute 0)
             * 
             * The logic is optimized in such a way that the
             * adjustScrollPosIgnoreEvents is called only once, to avoid extra
             * reflows, and thus the code might seem a bit obscure.
             */
            final boolean firstVisualRowIsRemoved = !removedVisualInside
                    .isEmpty() && removedVisualInside.getStart() == 0;

            if (!removedAbove.isEmpty() || firstVisualRowIsRemoved) {
                // TODO [[rowheight]]
                final int yDelta = removedAbove.length() * ROW_HEIGHT_PX;
                final int firstLogicalRowHeight = ROW_HEIGHT_PX;
                final boolean removalScrollsToShowFirstLogicalRow = scrollerElem
                        .getScrollTop() - yDelta < firstLogicalRowHeight;

                if (removedVisualInside.isEmpty()
                        && (!removalScrollsToShowFirstLogicalRow || !firstVisualRowIsRemoved)) {
                    /*
                     * rows were removed from above the viewport, so all we need
                     * to do is to adjust the scroll position to account for the
                     * removed rows
                     */
                    adjustScrollPosIgnoreEvents(-yDelta);
                } else if (removalScrollsToShowFirstLogicalRow) {
                    /*
                     * It seems like we've removed all rows from above, and also
                     * into the current viewport. This means we'll need to even
                     * out the scroll position to exactly 0 (i.e. adjust by the
                     * current negative scrolltop, presto!), so that it isn't
                     * aligned funnily
                     */
                    adjustScrollPosIgnoreEvents(-scrollerElem.getScrollTop());
                }
            }

            // ranges evaluated, let's do things.
            if (!removedVisualInside.isEmpty()) {
                int escalatorRowCount = bodyElem.getChildCount();

                /*
                 * If we're left with less rows than the number of escalators,
                 * remove the unused ones.
                 */
                final int escalatorRowsToRemove = escalatorRowCount
                        - getRowCount();
                if (escalatorRowsToRemove > 0) {
                    for (int i = 0; i < escalatorRowsToRemove; i++) {
                        final Element tr = visualRowOrder
                                .remove(removedVisualInside.getStart());
                        // TODO [[widgets]]
                        tr.removeFromParent();
                        rowTopPosMap.remove(tr);
                    }
                    escalatorRowCount -= escalatorRowsToRemove;

                    /*
                     * Because we're removing escalator rows, we don't have
                     * anything to scroll by. Let's make sure the viewport is
                     * scrolled to top, to render any rows possibly left above.
                     */
                    body.setBodyScrollPosition(body.tBodyScrollLeft, 0);

                    /*
                     * We might have removed some rows from the middle, so let's
                     * make sure we're not left with any holes. Also remember:
                     * visualIndex == logicalIndex applies now.
                     */
                    final int dirtyRowsStart = removedLogicalInside.getStart();
                    for (int i = dirtyRowsStart; i < escalatorRowCount; i++) {
                        final Element tr = visualRowOrder.get(i);
                        setRowPosition(tr, 0, i * ROW_HEIGHT_PX);
                    }

                    /*
                     * this is how many rows appeared into the viewport from
                     * below
                     */
                    final int rowsToUpdateDataOn = numberOfRows
                            - escalatorRowsToRemove;
                    final int start = Math.max(0, escalatorRowCount
                            - rowsToUpdateDataOn);
                    final int end = escalatorRowCount;
                    for (int i = start; i < end; i++) {
                        final Element tr = visualRowOrder.get(i);
                        refreshRow(tr, i);
                    }
                }

                else {
                    // No escalator rows need to be removed.

                    /*
                     * Two things (or a combination thereof) can happen:
                     * 
                     * 1) We're scrolled to the bottom, the last rows are
                     * removed. SOLUTION: moveAndUpdateEscalatorRows the
                     * bottommost rows, and place them at the top to be
                     * refreshed.
                     * 
                     * 2) We're scrolled somewhere in the middle, arbitrary rows
                     * are removed. SOLUTION: moveAndUpdateEscalatorRows the
                     * removed rows, and place them at the bottom to be
                     * refreshed.
                     * 
                     * Since a combination can also happen, we need to handle
                     * this in a smart way, all while avoiding
                     * double-refreshing.
                     */

                    final int contentBottom = getRowCount() * ROW_HEIGHT_PX;
                    final int viewportBottom = (int) (tBodyScrollTop + calculateHeight());
                    if (viewportBottom <= contentBottom) {
                        /*
                         * We're in the middle of the row container, everything
                         * is added to the bottom
                         */
                        paintRemoveRowsAtMiddle(removedLogicalInside,
                                removedVisualInside, 0);
                    }

                    else if (contentBottom + (numberOfRows * ROW_HEIGHT_PX)
                            - viewportBottom < ROW_HEIGHT_PX) {
                        /*
                         * We're at the end of the row container, everything is
                         * added to the top.
                         */
                        paintRemoveRowsAtBottom(removedLogicalInside,
                                removedVisualInside);
                    }

                    else {
                        /*
                         * We're in a combination, where we need to both scroll
                         * up AND show new rows at the bottom.
                         * 
                         * Example: Scrolled down to show the second to last
                         * row. Remove two. Viewport scrolls up, revealing the
                         * row above row. The last element collapses up and into
                         * view.
                         * 
                         * Reminder: this use case handles only the case when
                         * there are enough escalator rows to still render a
                         * full view. I.e. all escalator rows will _always_ be
                         * populated
                         */
                        /*-
                         *  1       1      |1| <- newly rendered
                         * |2|     |2|     |2|
                         * |3| ==> |*| ==> |5| <- newly rendered
                         * |4|     |*|
                         *  5       5
                         *  
                         *  1       1      |1| <- newly rendered
                         * |2|     |*|     |4|
                         * |3| ==> |*| ==> |5| <- newly rendered
                         * |4|     |4|
                         *  5       5
                         */

                        /*
                         * STEP 1:
                         * 
                         * reorganize deprecated escalator rows to bottom, but
                         * don't re-render anything yet
                         */
                        /*-
                         *  1       1       1
                         * |2|     |*|     |4|
                         * |3| ==> |*| ==> |*|
                         * |4|     |4|     |*|
                         *  5       5       5
                         */
                        int newTop = getRowTop(visualRowOrder
                                .get(removedVisualInside.getStart()));
                        for (int i = 0; i < removedVisualInside.length(); i++) {
                            final Element tr = visualRowOrder
                                    .remove(removedVisualInside.getStart());
                            visualRowOrder.addLast(tr);
                        }

                        for (int i = removedVisualInside.getStart(); i < escalatorRowCount; i++) {
                            final Element tr = visualRowOrder.get(i);
                            setRowPosition(tr, 0, newTop);
                            newTop += ROW_HEIGHT_PX;
                        }

                        /*
                         * STEP 2:
                         * 
                         * manually scroll
                         */
                        /*-
                         *  1      |1| <-- newly rendered (by scrolling)
                         * |4|     |4|
                         * |*| ==> |*|
                         * |*|       
                         *  5       5
                         */
                        final double newScrollTop = contentBottom
                                - calculateHeight();
                        setScrollTop(newScrollTop);
                        /*
                         * Manually call the scroll handler, so we get immediate
                         * effects in the escalator.
                         */
                        scroller.onScroll();
                        internalScrollEventCalls++;

                        /*
                         * Move the bottommost (n+1:th) escalator row to top,
                         * because scrolling up doesn't handle that for us
                         * automatically
                         */
                        moveAndUpdateEscalatorRows(
                                Range.withOnly(escalatorRowCount - 1),
                                0,
                                getLogicalRowIndex(visualRowOrder.getFirst()) - 1);

                        /*
                         * STEP 3:
                         * 
                         * update remaining escalator rows
                         */
                        /*-
                         * |1|     |1|
                         * |4| ==> |4|
                         * |*|     |5| <-- newly rendered
                         *           
                         *  5
                         */
                        final int rowsScrolled = (int) (Math
                                .ceil((viewportBottom - (double) contentBottom)
                                        / ROW_HEIGHT_PX));
                        final int start = escalatorRowCount
                                - (removedVisualInside.length() - rowsScrolled);
                        final Range visualRefreshRange = Range.between(start,
                                escalatorRowCount);
                        final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                                .getFirst()) + start;
                        // in-place move simply re-renders the rows.
                        moveAndUpdateEscalatorRows(visualRefreshRange, start,
                                logicalTargetIndex);
                    }
                }
            }

            /*
             * this needs to be done after the escalator has been shrunk down,
             * or it won't work correctly (due to setScrollTop invocation)
             */
            scroller.recalculateScrollbarsForVirtualViewport();
        }

        private void paintRemoveRowsAtMiddle(final Range removedLogicalInside,
                final Range removedVisualInside, final int logicalOffset) {
            /*-
             *  :       :       :
             * |2|     |2|     |2|
             * |3| ==> |*| ==> |4|
             * |4|     |4|     |6| <- newly rendered
             *  :       :       :
             */

            final int escalatorRowCount = visualRowOrder.size();

            final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                    .getLast())
                    - (removedVisualInside.length() - 1)
                    + logicalOffset;
            moveAndUpdateEscalatorRows(removedVisualInside, escalatorRowCount,
                    logicalTargetIndex);

            // move the surrounding rows to their correct places.
            final ListIterator<Element> iterator = visualRowOrder
                    .listIterator(removedVisualInside.getStart());
            int rowTop = (removedLogicalInside.getStart() + logicalOffset)
                    * ROW_HEIGHT_PX;
            for (int i = removedVisualInside.getStart(); i < escalatorRowCount
                    - removedVisualInside.length(); i++) {
                final Element tr = iterator.next();
                setRowPosition(tr, 0, rowTop);
                rowTop += ROW_HEIGHT_PX;
            }
        }

        private void paintRemoveRowsAtBottom(final Range removedLogicalInside,
                final Range removedVisualInside) {
            /*-
             *                  :
             *  :       :      |4| <- newly rendered
             * |5|     |5|     |5|
             * |6| ==> |*| ==> |7|
             * |7|     |7|     
             */

            final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                    .getFirst()) - removedVisualInside.length();
            moveAndUpdateEscalatorRows(removedVisualInside, 0,
                    logicalTargetIndex);

            // move the surrounding rows to their correct places.
            final ListIterator<Element> iterator = visualRowOrder
                    .listIterator(removedVisualInside.getEnd());
            int rowTop = removedLogicalInside.getStart() * ROW_HEIGHT_PX;
            while (iterator.hasNext()) {
                final Element tr = iterator.next();
                setRowPosition(tr, 0, rowTop);
                rowTop += ROW_HEIGHT_PX;
            }
        }

        private int getLogicalRowIndex(final Element element) {
            // TODO [[rowheight]]
            return getRowTop(element) / ROW_HEIGHT_PX;
        }

        @Override
        protected void recalculateSectionHeight() {
            // disable for body, since it doesn't make any sense.
        }

        /**
         * Adjusts the row index and number to be relevant for the current
         * virtual viewport.
         * <p>
         * It converts a logical range of rows index to the matching visual
         * range, truncating the resulting range with the viewport.
         * <p>
         * <ul>
         * <li>Escalator contains logical rows 0..100
         * <li>Current viewport showing logical rows 20..29
         * <li>convertToVisual([20..29]) &rarr; [0..9]
         * <li>convertToVisual([15..24]) &rarr; [0..4]
         * <li>convertToVisual([25..29]) &rarr; [5..9]
         * <li>convertToVisual([26..39]) &rarr; [6..9]
         * <li>convertToVisual([0..5]) &rarr; [0..-1] <em>(empty)</em>
         * <li>convertToVisual([35..1]) &rarr; [0..-1] <em>(empty)</em>
         * <li>convertToVisual([0..100]) &rarr; [0..9]
         * </ul>
         * 
         * @return a logical range converted to a visual range, truncated to the
         *         current viewport. The first visual row has the index 0.
         */
        private Range convertToVisual(final Range logicalRange) {
            if (logicalRange.isEmpty()) {
                return logicalRange;
            }

            /*
             * TODO [[rowheight]]: these assumptions will be totally broken with
             * variable row heights.
             */
            final int topRowHeight = ROW_HEIGHT_PX;
            final int maxEscalatorRows = (int) Math
                    .ceil((calculateHeight() + topRowHeight) / ROW_HEIGHT_PX);
            final int currentTopRowIndex = getLogicalRowIndex(visualRowOrder
                    .getFirst());

            final Range[] partitions = logicalRange.partitionWith(Range
                    .withLength(currentTopRowIndex, maxEscalatorRows));
            final Range insideRange = partitions[1];
            return insideRange.offsetBy(-currentTopRowIndex);
        }

        @Override
        protected String getCellElementTagName() {
            return "td";
        }

        private double calculateHeight() {
            final int tableHeight = tableWrapper.getOffsetHeight();
            final double footerHeight = footer.height;
            final double headerHeight = header.height;
            return tableHeight - footerHeight - headerHeight;
        }

        @Override
        public void refreshRows(final int index, final int numberOfRows) {
            final Range visualRange = convertToVisual(Range.withLength(index,
                    numberOfRows));

            if (!visualRange.isEmpty()) {
                final int firstLogicalRowIndex = getLogicalRowIndex(visualRowOrder
                        .getFirst());
                for (int rowNumber = visualRange.getStart(); rowNumber < visualRange
                        .getEnd(); rowNumber++) {
                    refreshRow(visualRowOrder.get(rowNumber),
                            firstLogicalRowIndex + rowNumber);
                }
            }
        }

        @Override
        protected Element getTrByVisualIndex(final int index)
                throws IndexOutOfBoundsException {
            if (index >= 0 && index < visualRowOrder.size()) {
                return visualRowOrder.get(index);
            } else {
                throw new IndexOutOfBoundsException("No such visual index: "
                        + index);
            }
        }

        private void setBodyScrollPosition(final int scrollLeft,
                final int scrollTop) {
            tBodyScrollLeft = scrollLeft;
            tBodyScrollTop = scrollTop;
            position.set(bodyElem, -tBodyScrollLeft, -tBodyScrollTop);
        }

        @Override
        protected int getTopVisualRowLogicalIndex() {
            if (!visualRowOrder.isEmpty()) {
                return getLogicalRowIndex(visualRowOrder.getFirst());
            } else {
                return 0;
            }
        }
    }

    private class ColumnConfigurationImpl implements ColumnConfiguration {
        private int columns = 0;

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there are no rows in the DOM when
         * this method is called.
         * 
         * @see #hasSomethingInDom()
         */
        @Override
        public void removeColumns(final int index, final int numberOfColumns) {
            assertArgumentsAreValidAndWithinRange(index, numberOfColumns);

            flyweightRow.removeCells(index, numberOfColumns);
            columns -= numberOfColumns;

            if (hasSomethingInDom()) {
                for (final AbstractRowContainer rowContainer : rowContainers) {
                    rowContainer.paintRemoveColumns(index, numberOfColumns);
                }
            }
        }

        private void assertArgumentsAreValidAndWithinRange(final int index,
                final int numberOfColumns) {
            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns can't be less than 1 (was "
                                + numberOfColumns + ")");
            }

            if (index < 0 || index + numberOfColumns > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "column range (" + index + ".."
                        + (index + numberOfColumns)
                        + ") was outside of the current "
                        + "number of columns (" + getColumnCount() + ")");
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for rows when this
         * method is called.
         * 
         * @see #hasColumnAndRowData()
         */
        @Override
        public void insertColumns(final int index, final int numberOfColumns) {
            if (index < 0 || index > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given index(" + index
                        + ") was outside of the current number of columns (0.."
                        + getColumnCount() + ")");
            }

            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns must be 1 or greater (was "
                                + numberOfColumns);
            }

            flyweightRow.addCells(index, numberOfColumns);
            columns += numberOfColumns;
            if (hasColumnAndRowData()) {
                for (final AbstractRowContainer rowContainer : rowContainers) {
                    rowContainer.paintInsertColumns(index, numberOfColumns);
                }
            }
        }

        @Override
        public int getColumnCount() {
            return columns;
        }
    }

    private FlyweightRow flyweightRow = new FlyweightRow(this);

    /** The {@code <thead/>} tag. */
    private final Element headElem = DOM.createTHead();
    /** The {@code <tbody/>} tag. */
    private final Element bodyElem = DOM.createTBody();
    /** The {@code <tfoot/>} tag. */
    private final Element footElem = DOM.createTFoot();

    private final Element scrollerElem = DOM.createDiv();
    private final Element innerScrollerElem = DOM.createDiv();

    private final HeaderRowContainer header = new HeaderRowContainer(headElem);
    private final BodyRowContainer body = new BodyRowContainer(bodyElem);
    private final FooterRowContainer footer = new FooterRowContainer(footElem);

    private final Scroller scroller = new Scroller();

    private final AbstractRowContainer[] rowContainers = new AbstractRowContainer[] {
            header, body, footer };

    private final ColumnConfigurationImpl columnConfiguration = new ColumnConfigurationImpl();
    private final Element tableWrapper;

    private PositionFunction position;

    private int internalScrollEventCalls = 0;

    /** The cached width of the escalator, in pixels. */
    private double width;
    /** The cached height of the escalator, in pixels. */
    private double height;

    private static native double getPreciseWidth(Element element)
    /*-{
        if (element.getBoundingClientRect) {
            var rect = element.getBoundingClientRect();
            return rect.right - rect.left;
        } else {
            return element.offsetWidth;
        }
    }-*/;

    private static native double getPreciseHeight(Element element)
    /*-{
        if (element.getBoundingClientRect) {
            var rect = element.getBoundingClientRect();
            return rect.bottom - rect.top;
        } else {
            return element.offsetHeight;
        }
    }-*/;

    /**
     * Creates a new Escalator widget instance.
     */
    public Escalator() {

        detectAndApplyPositionFunction();
        getLogger().info(
                "Using " + position.getClass().getSimpleName()
                        + " for position");

        final Element root = DOM.createDiv();
        setElement(root);
        setStyleName(CLASS_NAME);

        scrollerElem.setClassName(CLASS_NAME + "-scroller");
        root.appendChild(scrollerElem);

        scrollerElem.appendChild(innerScrollerElem);

        tableWrapper = DOM.createDiv();
        tableWrapper.setClassName(CLASS_NAME + "-tablewrapper");
        root.appendChild(tableWrapper);

        final Element table = DOM.createTable();
        tableWrapper.appendChild(table);

        headElem.setClassName(CLASS_NAME + "-header");
        table.appendChild(headElem);

        bodyElem.setClassName(CLASS_NAME + "-body");
        table.appendChild(bodyElem);

        footElem.setClassName(CLASS_NAME + "-footer");
        table.appendChild(footElem);

        /*
         * Size calculations work only after the Escalator has been attached to
         * the DOM. It doesn't matter if the table is populated or not by this
         * point, there's a lot of other stuff to calculate also. All sizes
         * start working once the first sizes have been initialized.
         */
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(final AttachEvent event) {
                if (event.isAttached()) {

                    /*
                     * this specific order of method calls matters: header and
                     * footer get defined heights, the body assumes to get the
                     * rest.
                     */
                    header.paintInsertRows(0, header.getRowCount());
                    footer.paintInsertRows(0, footer.getRowCount());
                    recalculateElementSizes();
                    body.paintInsertRows(0, body.getRowCount());

                    scroller.attachScrollListener(scrollerElem);
                    scroller.attachMousewheelListener(getElement());
                } else {
                    scroller.detachScrollListener(scrollerElem);
                    scroller.detachMousewheelListener(getElement());
                }
            }
        });
    }

    private void detectAndApplyPositionFunction() {
        /*
         * firefox has a bug in its translate operation, showing white space
         * when adjusting the scrollbar in BodyRowContainer.paintInsertRows
         */
        if (Window.Navigator.getUserAgent().contains("Firefox")) {
            position = new AbsolutePosition();
            return;
        }

        final Style docStyle = Document.get().getBody().getStyle();
        if (hasProperty(docStyle, "transform")) {
            if (hasProperty(docStyle, "transformStyle")) {
                position = new Translate3DPosition();
            } else {
                position = new TranslatePosition();
            }
        } else if (hasProperty(docStyle, "webkitTransform")) {
            position = new WebkitTranslate3DPosition();
        } else {
            position = new AbsolutePosition();
        }
    }

    private Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    private static native boolean hasProperty(Style style, String name)
    /*-{
        return style[name] !== undefined;
    }-*/;

    /**
     * Check whether there are both columns and any row data (for either
     * headers, body or footer).
     * 
     * @return <code>true</code> iff header, body or footer has rows && there
     *         are columns
     */
    private boolean hasColumnAndRowData() {
        return (header.getRowCount() > 0 || body.getRowCount() > 0 || footer
                .getRowCount() > 0) && columnConfiguration.getColumnCount() > 0;
    }

    /**
     * Check whether there are any cells in the DOM.
     * 
     * @return <code>true</code> iff header, body or footer has any child
     *         elements
     */
    private boolean hasSomethingInDom() {
        return headElem.hasChildNodes() || bodyElem.hasChildNodes()
                || footElem.hasChildNodes();
    }

    /**
     * Returns the representation of this Escalator header.
     * 
     * @return the header. Never <code>null</code>
     */
    public RowContainer getHeader() {
        return header;
    }

    /**
     * Returns the representation of this Escalator body.
     * 
     * @return the body. Never <code>null</code>
     */
    public RowContainer getBody() {
        return body;
    }

    /**
     * Returns the representation of this Escalator footer.
     * 
     * @return the footer. Never <code>null</code>
     */
    public RowContainer getFooter() {
        return footer;
    }

    /**
     * Returns the configuration object for the columns in this Escalator.
     * 
     * @return the configuration object for the columns in this Escalator. Never
     *         <code>null</code>
     */
    public ColumnConfiguration getColumnConfiguration() {
        return columnConfiguration;
    }

    /*
     * TODO remove method once RequiresResize and the Vaadin layoutmanager
     * listening mechanisms are implemented (https://trello.com/c/r3Kh0Kfy)
     */
    @Override
    public void setWidth(final String width) {
        super.setWidth(width);
        recalculateElementSizes();
    }

    /*
     * TODO remove method once RequiresResize and the Vaadin layoutmanager
     * listening mechanisms are implemented (https://trello.com/c/r3Kh0Kfy)
     */
    @Override
    public void setHeight(final String height) {
        super.setHeight(height);
        recalculateElementSizes();
    }

    /**
     * Returns the vertical scroll offset. Note that this is not necessarily the
     * same as the scroll top in the DOM
     * 
     * @return the logical vertical scroll offset
     */
    public double getScrollTop() {
        return scrollerElem.getScrollTop();
    }

    /**
     * Sets the vertical scroll offset. Note that this is not necessarily the
     * same as the scroll top in the DOM
     * 
     * @param scrollTop
     *            the number of pixels to scroll vertically
     */
    public void setScrollTop(final double scrollTop) {
        scrollerElem.setScrollTop((int) scrollTop);
    }

    /**
     * Returns the logical horizontal scroll offset. Note that this is not
     * necessarily the same as the scroll left in the DOM.
     * 
     * @return the logical horizontal scroll offset
     */
    public int getScrollLeft() {
        return scrollerElem.getScrollLeft();
    }

    /**
     * Sets the logical horizontal scroll offset. Note that this is not
     * necessarily the same as the scroll left in the DOM.
     * 
     * @param scrollLeft
     *            the number of pixels to scroll horizontally
     */
    public void setScrollLeft(final int scrollLeft) {
        scrollerElem.setScrollLeft(scrollLeft);
    }

    /**
     * Scrolls the body horizontally so that the column at the given index is
     * visible.
     * 
     * @param columnIndex
     *            the index of the column to scroll to
     * @param destination
     *            where the column should be aligned visually after scrolling
     * @throws IndexOutOfBoundsException
     *             if {@code columnIndex} is not a valid index for an existing
     *             column
     */
    public void scrollToColumn(final int columnIndex,
            final ScrollDestination destination)
            throws IndexOutOfBoundsException {
        // TODO [[frozencol]] throw IAE if frozen
        verifyValidColumnIndex(columnIndex);

        scroller.scrollToColumn(columnIndex, destination, 0);
    }

    /**
     * Scrolls the body horizontally so that the column at the given index is
     * visible and there is at least {@code padding} pixels to the given scroll
     * destination.
     * 
     * @param columnIndex
     *            the index of the column to scroll to
     * @param destination
     *            where the column should be aligned visually after scrolling
     * @param padding
     *            the number pixels to place between the scrolled-to column and
     *            the viewport edge.
     * @throws IndexOutOfBoundsException
     *             if {@code columnIndex} is not a valid index for an existing
     *             column
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE},
     *             because having a padding on a centered column is undefined
     *             behavior
     */
    public void scrollToColumn(final int columnIndex,
            final ScrollDestination destination, final int padding)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        // TODO [[frozencol]] throw IAE if frozen
        if (destination == ScrollDestination.MIDDLE) {
            throw new IllegalArgumentException(
                    "You cannot have a padding with a MIDDLE destination");
        }
        verifyValidColumnIndex(columnIndex);

        scroller.scrollToColumn(columnIndex, destination, padding);
    }

    private void verifyValidColumnIndex(final int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0
                || columnIndex >= columnConfiguration.getColumnCount()) {
            throw new IndexOutOfBoundsException("The given column index "
                    + columnIndex + " does not exist.");
        }
    }

    /**
     * Scrolls the body vertically so that the row at the given index is
     * visible.
     * 
     * @param rowIndex
     *            the index of the row to scroll to
     * @param destination
     *            where the row should be aligned visually after scrolling
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex} is not a valid index for an existing
     *             logical row
     */
    public void scrollToRow(final int rowIndex,
            final ScrollDestination destination)
            throws IndexOutOfBoundsException {
        verifyValidRowIndex(rowIndex);

        scroller.scrollToRow(rowIndex, destination, 0);
    }

    /**
     * Scrolls the body vertically so that the row at the given index is visible
     * and there is at least {@literal padding} pixels to the given scroll
     * destination.
     * 
     * @param rowIndex
     *            the index of the logical row to scroll to
     * @param destination
     *            where the row should be aligned visually after scrolling
     * @param padding
     *            the number pixels to place between the scrolled-to row and the
     *            viewport edge.
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex} is not a valid index for an existing row
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE},
     *             because having a padding on a centered row is undefined
     *             behavior
     */
    public void scrollToRow(final int rowIndex,
            final ScrollDestination destination, final int padding)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        if (destination == ScrollDestination.MIDDLE) {
            throw new IllegalArgumentException(
                    "You cannot have a padding with a MIDDLE destination");
        }
        verifyValidRowIndex(rowIndex);

        scroller.scrollToRow(rowIndex, destination, padding);
    }

    private void verifyValidRowIndex(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= body.getRowCount()) {
            throw new IndexOutOfBoundsException("The given row index "
                    + rowIndex + " does not exist.");
        }
    }

    private void recalculateElementSizes() {
        width = getPreciseWidth(getElement());
        height = getPreciseHeight(getElement());
        for (final AbstractRowContainer rowContainer : rowContainers) {
            rowContainer.recalculateSectionHeight();
        }

        scroller.recalculateTableWrapperSize();
        scroller.recalculateScrollbarsForVirtualViewport();
    }

    private boolean needsVerticalScrollbars() {
        // TODO [[rowheight]]: take variable row heights into account
        final double bodyHeight = ROW_HEIGHT_PX * body.getRowCount();
        return height < header.height + bodyHeight + footer.height;
    }

    private boolean needsHorizontalScrollbars() {
        // TODO [[colwidth]]: take variable column widths into account
        return width < COLUMN_WIDTH_PX * columnConfiguration.getColumnCount();
    }

    private static void recalculateRowWidth(final Element tr) {
        // TODO [[colwidth]]: adjust for variable column widths
        tr.getStyle().setWidth(tr.getChildCount() * COLUMN_WIDTH_PX, Unit.PX);
    }

    /**
     * A routing method for {@link Scroller#onScroll(double, double)}
     * <p>
     * This is a workaround for GWT and JSNI unable to properly handle inner
     * classes, so instead we call the outer class' method, which calls the
     * inner class' respective method.
     * <p>
     * Ideally, this method would not exist, and
     * {@link Scroller#onScroll(double, double)} would be called directly.
     */
    private void onScroll() {
        scroller.onScroll();
    }
}
