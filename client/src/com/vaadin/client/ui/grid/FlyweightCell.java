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
package com.vaadin.client.ui.grid;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.grid.FlyweightRow.CellIterator;

/**
 * A {@link FlyweightCell} represents a cell in the {@link Grid} or
 * {@link Escalator} at a certain point in time.
 * 
 * <p>
 * Since the {@link FlyweightCell} follows the <code>Flyweight</code>-pattern
 * any instance of this object is subject to change without the user knowing it
 * and so should not be stored anywhere outside of the method providing these
 * instances.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class FlyweightCell {
    static final String COLSPAN_ATTR = "colSpan";

    private final int column;
    private final FlyweightRow row;

    private Element element = null;
    private CellIterator currentIterator = null;

    private final Escalator escalator;

    public FlyweightCell(final FlyweightRow row, final int column,
            Escalator escalator) {
        this.row = row;
        this.column = column;
        this.escalator = escalator;
    }

    /**
     * Returns the row index of the cell
     */
    public int getRow() {
        assertSetup();
        return row.getRow();
    }

    /**
     * Returns the column index of the cell
     */
    public int getColumn() {
        assertSetup();
        return column;
    }

    /**
     * Returns the element of the cell. Can be either a <code>TD</code> element
     * or a <code>TH</code> element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Sets the DOM element for this FlyweightCell, either a <code>TD</code> or
     * a <code>TH</code>. This method should only be called when
     * {@code getElement() == null}. It is the caller's responsibility to
     * actually insert the given element to the document when needed.
     * 
     * @param element
     *            the element corresponding to this FlyweightCell
     */
    void setElement(Element element) {
        assert element != null;
        // When asserts are enabled, teardown() resets the element to null
        // so this won't fire simply due to cell reuse
        assert this.element == null : "Cell element can only be set once";
        this.element = element;
    }

    void setup(final CellIterator iterator) {
        currentIterator = iterator;

        if (iterator.areCellsInitialized()) {
            final Element e = (Element) row.getElement().getChild(column);
            e.setPropertyInt(COLSPAN_ATTR, 1);
            e.getStyle().setWidth(row.getColumnWidth(column), Unit.PX);
            e.getStyle().clearDisplay();
            setElement(e);
        }
    }

    /**
     * Tear down the state of the Cell.
     * <p>
     * This is an internal check method, to prevent retrieving uninitialized
     * data by calling {@link #getRow()}, {@link #getColumn()} or
     * {@link #getElement()} at an improper time.
     * <p>
     * This should only be used with asserts ("
     * <code>assert flyweightCell.teardown()</code> ") so that the code is never
     * run when asserts aren't enabled.
     * 
     * @return always <code>true</code>
     * @see FlyweightRow#teardown()
     */
    boolean teardown() {
        currentIterator = null;
        element = null;
        return true;
    }

    /**
     * Asserts that the flyweight cell has properly been set up before trying to
     * access any of its data.
     */
    private void assertSetup() {
        assert currentIterator != null : "FlyweightCell was not properly "
                + "initialized. This is either a bug in Grid/Escalator "
                + "or a Cell reference has been stored and reused "
                + "inappropriately.";
    }

    public void setColSpan(final int numberOfCells) {
        /*-
         * This will default to 1 if unset, as per DOM specifications:
         * http://www.w3.org/TR/html5/tabular-data.html#attributes-common-to-td-and-th-elements
         */
        final int prevColSpan = getElement().getPropertyInt(COLSPAN_ATTR);
        if (numberOfCells == 1 && prevColSpan == 1) {
            return;
        }

        getElement().setPropertyInt(COLSPAN_ATTR, numberOfCells);
        adjustCellWidthForSpan(numberOfCells);
        hideOrRevealAdjacentCellElements(numberOfCells, prevColSpan);
        currentIterator.setSkipNext(numberOfCells - 1);
    }

    private void adjustCellWidthForSpan(final int numberOfCells) {
        final int cellsToTheRight = currentIterator.rawPeekNext(
                numberOfCells - 1).size();

        final int selfWidth = row.getColumnWidth(column);
        int widthsOfColumnsToTheRight = 0;
        for (int i = 0; i < cellsToTheRight; i++) {
            widthsOfColumnsToTheRight += row.getColumnWidth(column + i + 1);
        }
        getElement().getStyle().setWidth(selfWidth + widthsOfColumnsToTheRight,
                Unit.PX);
    }

    private void hideOrRevealAdjacentCellElements(final int numberOfCells,
            final int prevColSpan) {
        final int affectedCellsNumber = Math.max(prevColSpan, numberOfCells);
        final List<FlyweightCell> affectedCells = currentIterator
                .rawPeekNext(affectedCellsNumber - 1);
        if (prevColSpan < numberOfCells) {
            for (int i = 0; i < affectedCells.size(); i++) {
                affectedCells.get(prevColSpan + i - 1).getElement().getStyle()
                        .setDisplay(Display.NONE);
            }
        } else if (prevColSpan > numberOfCells) {
            for (int i = 0; i < affectedCells.size(); i++) {
                affectedCells.get(numberOfCells + i - 1).getElement()
                        .getStyle().clearDisplay();
            }
        }
    }

    /**
     * @deprecated Will be removed in further refactorings
     */
    @Deprecated
    public Widget getWidget() {
        return Escalator.getWidgetFromCell(getElement());
    }

    /**
     * @deprecated Will be removed in further refactorings
     */
    @Deprecated
    public void setWidget(Widget widget) {

        Widget oldWidget = getWidget();

        // Validate
        if (oldWidget == widget) {
            return;
        }

        // Detach old child.
        if (oldWidget != null) {
            // Orphan.
            Escalator.setParent(oldWidget, null);

            // Physical detach.
            getElement().removeChild(oldWidget.getElement());
        }

        // Remove any previous text nodes from previous
        // setInnerText/setInnerHTML
        getElement().removeAllChildren();

        // Attach new child.
        if (widget != null) {
            // Detach new child from old parent.
            widget.removeFromParent();

            // Physical attach.
            getElement().appendChild(widget.getElement());

            Escalator.setParent(widget, escalator);
        }
    }

    /**
     * @deprecated Will be removed in further refactorings
     */
    @Deprecated
    public void setWidget(IsWidget w) {
        setWidget(Widget.asWidgetOrNull(w));
    }

}
