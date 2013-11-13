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
import java.util.List;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Element;

/**
 * An internal implementation of the {@link Row} interface.
 * <p>
 * There is only one instance per Escalator. This is designed to be re-used when
 * rendering rows.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see Escalator.AbstractRowContainer#refreshRow(Node, int)
 */
class FlyweightRow implements Row {
    private static final int BLANK = Integer.MIN_VALUE;

    private int row;
    private Element element;
    private final Escalator escalator;
    private final List<Cell> cells = new ArrayList<Cell>();

    public FlyweightRow(final Escalator escalator) {
        this.escalator = escalator;
    }

    @Override
    public Escalator getEscalator() {
        return escalator;
    }

    void setup(final Element e, final int row) {
        element = e;
        this.row = row;
    }

    /**
     * Tear down the state of the Row.
     * <p>
     * This is an internal check method, to prevent retrieving uninitialized
     * data by calling {@link #getRow()}, {@link #getElement()} or
     * {@link #getCells()} at an improper time.
     * <p>
     * This should only be used with asserts ("
     * <code>assert flyweightRow.teardown()</code> ") so that the code is never
     * run when asserts aren't enabled.
     * 
     * @return always <code>true</code>
     */
    boolean teardown() {
        element = null;
        row = BLANK;
        return true;
    }

    @Override
    public int getRow() {
        assertSetup();
        return row;
    }

    @Override
    public Element getElement() {
        assertSetup();
        return element;
    }

    void addCells(final int index, final int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            final int col = index + i;
            cells.add(col, new FlyweightCell(this, col));
        }
        updateRestOfCells(index + numberOfColumns);
    }

    void removeCells(final int index, final int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            cells.remove(index);
        }
        updateRestOfCells(index);
    }

    private void updateRestOfCells(final int startPos) {
        // update the column number for the cells to the right
        for (int col = startPos; col < cells.size(); col++) {
            cells.set(col, new FlyweightCell(this, col));
        }
    }

    /**
     * Get flyweight cells for the client code to render.
     * 
     * @return a list of {@link FlyweightCell FlyweightCells}. They are
     *         generified into {@link Cell Cells}, because Java's generics
     *         system isn't expressive enough.
     * @see #setup(Element, int)
     * @see #teardown()
     */
    List<Cell> getCells() {
        assertSetup();
        return Collections.unmodifiableList(cells);
    }

    /**
     * Asserts that the flyweight row has properly been set up before trying to
     * access any of its data.
     */
    private void assertSetup() {
        assert element != null && row != BLANK : "Flyweight row was not "
                + "properly initialized. Make sure the setup-method is "
                + "called before retrieving data. This is either a bug "
                + "in Escalator, or the instance of the flyweight row "
                + "has been stored and accessed.";
    }
}
