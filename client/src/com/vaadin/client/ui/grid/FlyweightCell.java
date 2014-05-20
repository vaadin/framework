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
 * An internal implementation of the {@link Cell} interface.
 * <p>
 * These instances are populated into a {@link FlyweightRow} instance, and
 * intended to be reused when rendering cells in an escalator.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see FlyweightRow#getCells()
 * @see FlyweightRow#addCells(int, int)
 * @see FlyweightRow#removeCells(int, int)
 */
class FlyweightCell implements Cell {
    static final String COLSPAN_ATTR = "colSpan";

    private final int column;
    private final FlyweightRow row;

    private CellIterator currentIterator = null;

    private final Escalator escalator;

    public FlyweightCell(final FlyweightRow row, final int column,
            Escalator escalator) {
        this.row = row;
        this.column = column;
        this.escalator = escalator;
    }

    @Override
    public int getRow() {
        assertSetup();
        return row.getRow();
    }

    @Override
    public int getColumn() {
        assertSetup();
        return column;
    }

    @Override
    public Element getElement() {
        return (Element) row.getElement().getChild(column);
    }

    void setup(final CellIterator cellIterator) {
        currentIterator = cellIterator;

        final Element e = getElement();
        e.setPropertyInt(COLSPAN_ATTR, 1);
        e.getStyle().setWidth(row.getColumnWidth(column), Unit.PX);
        e.getStyle().clearDisplay();
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

    @Override
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

    @Override
    public Widget getWidget() {
        return Escalator.getWidgetFromCell(getElement());
    }

    @Override
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

    @Override
    public void setWidget(IsWidget w) {
        setWidget(Widget.asWidgetOrNull(w));
    }

}
