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

import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.grid.PositionFunction.AbsolutePosition;
import com.vaadin.client.ui.grid.PositionFunction.Translate3DPosition;
import com.vaadin.client.ui.grid.PositionFunction.TranslatePosition;
import com.vaadin.client.ui.grid.PositionFunction.WebkitTranslate3DPosition;

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
     * [[escalator]]: This needs to be re-inspected once the escalator pattern
     * is actually implemented.
     */
    /*
     * [[rowwidth]] [[colwidth]]: This needs to be re-inspected once hard-coded
     * values are removed, and cell dimensions are actually being calculated.
     * NOTE: these bits can most often also be identified by searching for code
     * reading the ROW_HEIGHT_PX and COL_WIDTH_PX constans.
     */
    /*
     * [[API]]: Implementing this suggestion would require a change in the
     * public API. These suggestions usually don't come lightly.
     */

    private static final int ROW_HEIGHT_PX = 20;
    private static final int COLUMN_WIDTH_PX = 100;

    private static class CellImpl implements Cell {
        private final Element cellElem;
        private final int row;
        private final int column;

        public CellImpl(final Element cellElem, final int row, final int column) {
            this.cellElem = cellElem;
            this.row = row;
            this.column = column;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public int getColumn() {
            return column;
        }

        @Override
        public Element getElement() {
            return cellElem;
        }

    }

    private static final String CLASS_NAME = "v-escalator";

    private class RowContainerImpl implements RowContainer {
        private CellRenderer renderer = CellRenderer.NULL_RENDERER;

        private int rows;

        /**
         * The table section element ({@code <thead>}, {@code <tbody>} or
         * {@code <tfoot>}) the rows (i.e. {@code <tr>} tags) are contained in.
         */
        private final Element root;

        /**
         * What cell type to contain in this {@link RowContainer}. Usually
         * either a {@code <th>} or {@code <td>}.
         */
        private final String cellElementTag;

        public RowContainerImpl(final Element rowContainerElement,
                final String cellElementTag) {
            root = rowContainerElement;
            this.cellElementTag = cellElementTag;
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
         * @param newPxHeight
         *            The new pixel height
         */
        protected void sectionHeightCalculated(final double newPxHeight) {
            // override if implementation is needed
        };

        private Element createCellElement() {
            return DOM.createElement(cellElementTag);
        }

        @Override
        public CellRenderer getCellRenderer() {
            return renderer;
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
        public void setCellRenderer(final CellRenderer cellRenderer) {
            if (cellRenderer == null) {
                throw new IllegalArgumentException(
                        "cell renderer cannot be null");
            }

            renderer = cellRenderer;

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
        public void removeRows(final int offset, final int numberOfRows) {
            assertArgumentsAreValidAndWithinRange(offset, numberOfRows);

            rows -= numberOfRows;

            if (hasSomethingInDom()) {
                for (int i = 0; i < numberOfRows; i++) {
                    root.getChild(offset).removeFromParent();
                }
            }
            refreshRowPositions(offset, getRowCount());
            recalculateSectionHeight();
        }

        private void assertArgumentsAreValidAndWithinRange(final int offset,
                final int numberOfRows) throws IllegalArgumentException,
                IndexOutOfBoundsException {
            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            if (offset < 0 || offset + numberOfRows > getRowCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "row range (" + offset + ".."
                        + (offset + numberOfRows)
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
        public void insertRows(final int offset, final int numberOfRows) {
            if (offset < 0 || offset > getRowCount()) {
                throw new IndexOutOfBoundsException("The given offset ("
                        + offset
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
             * TODO [[escalator]]: modify offset and numberOfRows so that they
             * suit the current viewport. If a partial dataset is shown,update
             * only the part that is visible. If the viewport doesn't show any
             * of the modifications, this method does nothing.
             */

            /*
             * TODO [[escalator]]: assert that escalatorChildIndex is a number
             * equal or less than the number of escalator rows
             */

            Node referenceNode;
            if (root.getChildCount() != 0 && offset != 0) {
                // get the row node we're inserting stuff after
                referenceNode = root.getChild(offset - 1);
            } else {
                // there are now rows, so just append.
                referenceNode = null;
            }

            for (int row = offset; row < offset + numberOfRows; row++) {
                final Element tr = DOM.createTR();

                for (int col = 0; col < columnConfiguration.getColumnCount(); col++) {
                    final Element cellElem = createCellElement();
                    paintCell(cellElem, row, col);
                    tr.appendChild(cellElem);
                }

                /*
                 * TODO [[optimize]] [[rowwidth]]: When this method is updated
                 * to measure things instead of using hardcoded values, it would
                 * be better to do everything at once after all rows have been
                 * updated to reduce the number of reflows.
                 */
                recalculateRowWidth(tr);
                tr.addClassName(CLASS_NAME + "-row");

                position.set(tr, 0, row * ROW_HEIGHT_PX);

                if (referenceNode != null) {
                    root.insertAfter(tr, referenceNode);
                } else {
                    /*
                     * referencenode being null means we have offset 0, i.e.
                     * make it the first row
                     */
                    /*
                     * TODO [[optimize]]: Is insertFirst or append faster for an
                     * empty root?
                     */
                    root.insertFirst(tr);
                }

                /*
                 * to get the rows to appear one after another in a logical
                 * order, update the reference
                 */
                referenceNode = tr;
            }

            /*
             * we need to update the positions of all rows beneath the ones
             * added right now.
             */
            refreshRowPositions(offset + numberOfRows, getRowCount());

            /*
             * TODO [[optimize]]: maybe the height doesn't always change?
             */
            recalculateSectionHeight();
        }

        /**
         * Re-evaluates the positional coordinates for the rows in the given
         * range. The given range is truncated to suit the given viewport.
         * 
         * @param offset
         *            starting row index
         * @param numberOfRows
         *            the number of rows after {@code offset} to refresh the
         *            positions of
         */
        private void refreshRowPositions(final int offset,
                final int numberOfRows) {
            final int startRow = Math.max(0, offset);
            final int endRow = Math.min(getRowCount(), offset + numberOfRows);

            for (int row = startRow; row < endRow; row++) {
                Element tr = (Element) root.getChild(row);
                position.set(tr, 0, row * ROW_HEIGHT_PX);
            }
        }

        private void recalculateSectionHeight() {
            /* TODO [[optimize]]: only do this if the height has changed */
            sectionHeightCalculated(root.getChildCount() * ROW_HEIGHT_PX);
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
        public void refreshRows(final int offset, final int numberOfRows) {
            assertArgumentsAreValidAndWithinRange(offset, numberOfRows);

            /*
             * TODO [[escalator]]: modify offset and numberOfRows to fit in the
             * current viewport. If they don't fall into the current viewport,
             * NOOP
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

                for (int row = offset; row < offset + numberOfRows; row++) {
                    Node tr = root.getChild(row);
                    for (int col = 0; col < tr.getChildCount(); col++) {
                        paintCell((Element) tr.getChild(col), row, col);
                    }
                }
            }
        }

        private void paintCell(final Element cellElem, final int row,
                final int col) {
            /*
             * TODO [[optimize]]: Only do this for new cells or when a row
             * height or column width actually changes. Or is it a NOOP when
             * re-setting a property to its current value?
             */
            cellElem.getStyle().setHeight(ROW_HEIGHT_PX, Unit.PX);
            cellElem.getStyle().setWidth(COLUMN_WIDTH_PX, Unit.PX);

            /*
             * TODO [[optimize]]: Don't create a new instance every time a cell
             * is rendered
             */
            final CellImpl cell = new CellImpl(cellElem, row, col);
            /*
             * TODO [[optimize]] [[API]]: Let the renderer know whether the cell
             * is new so that it can use a quicker route if it can deduct that
             * the elements that it has put there in a previous rendering is
             * still there and the contents only need to be updated.
             */
            renderer.renderCell(cell);

            /*
             * TODO [[optimize]]: Only do this for cells that have not already
             * been rendered.
             */
            cellElem.addClassName(CLASS_NAME + "-cell");
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
        public void removeColumns(final int offset, final int numberOfColumns) {
            assertArgumentsAreValidAndWithinRange(offset, numberOfColumns);

            columns--;

            if (hasSomethingInDom()) {
                for (RowContainerImpl rowContainer : rowContainers) {
                    for (int row = 0; row < rowContainer.getRowCount(); row++) {
                        Node tr = rowContainer.root.getChild(row);
                        for (int col = 0; col < numberOfColumns; col++) {
                            tr.getChild(offset).removeFromParent();
                        }
                    }
                }
            }
        }

        private void assertArgumentsAreValidAndWithinRange(final int offset,
                final int numberOfColumns) {
            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns can't be less than 1 (was "
                                + numberOfColumns + ")");
            }

            if (offset < 0 || offset + numberOfColumns > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "column range (" + offset + ".."
                        + (offset + numberOfColumns)
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
        public void insertColumns(final int offset, final int numberOfColumns) {
            if (offset < 0 || offset > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given offset("
                        + offset
                        + ") was outside of the current number of columns (0.."
                        + getColumnCount() + ")");
            }

            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns must be 1 or greater (was "
                                + numberOfColumns);
            }

            columns += numberOfColumns;
            if (!hasColumnAndRowData()) {
                return;
            }

            for (final RowContainerImpl rowContainer : rowContainers) {
                final Element element = rowContainer.root;

                for (int row = 0; row < element.getChildCount(); row++) {
                    final Element tr = (Element) element.getChild(row);

                    Node referenceElement;
                    if (offset != 0) {
                        referenceElement = tr.getChild(offset - 1);
                    } else {
                        referenceElement = null;
                    }

                    for (int col = offset; col < offset + numberOfColumns; col++) {
                        final Element cellElem = rowContainer
                                .createCellElement();
                        rowContainer.paintCell(cellElem, row, col);

                        if (referenceElement != null) {
                            tr.insertAfter(cellElem, referenceElement);
                        } else {
                            /*
                             * referenceElement being null means we have offset
                             * 0, make it the first cell.
                             */
                            /*
                             * TODO [[optimize]]: Is insertFirst or append
                             * faster for an empty tr?
                             */
                            tr.insertFirst(cellElem);
                        }

                        /*
                         * update reference to insert cells in logical order,
                         * the latter after the former
                         */
                        referenceElement = cellElem;
                    }

                    /*
                     * TODO [[optimize]] [[colwidth]]: When this method is
                     * updated to measure things instead of using hardcoded
                     * values, it would be better to do everything at once after
                     * all rows have been updated to reduce the number of
                     * reflows.
                     */
                    recalculateRowWidth(tr);
                }
            }
        }

        @Override
        public int getColumnCount() {
            return columns;
        }
    }

    private final Element headElem = DOM.createTHead();
    private final Element bodyElem = DOM.createTBody();
    private final Element footElem = DOM.createTFoot();
    private final Element scroller;
    private final Element innerScroller;

    private final RowContainerImpl header = new RowContainerImpl(headElem, "th") {
        @Override
        protected void sectionHeightCalculated(final double newPxHeight) {
            bodyElem.getStyle().setTop(newPxHeight, Unit.PX);
        };
    };

    private final RowContainerImpl body = new RowContainerImpl(bodyElem, "td");

    private final RowContainerImpl footer = new RowContainerImpl(footElem, "td") {
        @Override
        protected void sectionHeightCalculated(final double newPxHeight) {
            footElem.getStyle().setBottom(newPxHeight, Unit.PX);
        }
    };

    private final RowContainerImpl[] rowContainers = new RowContainerImpl[] {
            header, body, footer };

    private final ColumnConfigurationImpl columnConfiguration = new ColumnConfigurationImpl();
    private final Element tableWrapper;

    private PositionFunction position;

    /**
     * Creates a new Escalator widget instance.
     */
    public Escalator() {

        detectAndApplyPositionFunction();

        final Element root = DOM.createDiv();
        setElement(root);
        setStyleName(CLASS_NAME);

        scroller = DOM.createDiv();
        scroller.setClassName(CLASS_NAME + "-scroller");
        root.appendChild(scroller);

        innerScroller = DOM.createDiv();
        scroller.appendChild(innerScroller);

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
                    recalculateElementSizes();
                }
            }
        });
    }

    private void detectAndApplyPositionFunction() {
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

        getLogger().info(
                "Using " + position.getClass().getSimpleName()
                        + " for position");
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

    private void recalculateElementSizes() {
        for (final RowContainerImpl rowContainer : rowContainers) {
            rowContainer.recalculateSectionHeight();
        }

        /*
         * TODO [[escalator]]: take scrollbar size into account only if there is
         * something to scroll, and only for the dimension it applies to.
         */
        // recalculate required space for scroll underlay
        tableWrapper.getStyle().setHeight(getElement().getOffsetHeight(),
                Unit.PX);
        tableWrapper.getStyle()
                .setWidth(getElement().getOffsetWidth(), Unit.PX);
    }

    private static void recalculateRowWidth(Element tr) {
        // TODO [[colwidth]]: adjust for variable column widths
        tr.getStyle().setWidth(tr.getChildCount() * COLUMN_WIDTH_PX, Unit.PX);
    }
}
