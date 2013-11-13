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

import com.google.gwt.user.client.Element;

/**
 * An internal implementation of the {@link Cell} interface.
 * <p>
 * These instances are populated into a {@link FlyweightRow} instance, and
 * intended to be reused when rendering cells in an escalator.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see FlyweightRow#getCells()
 * @see FlyweightRow#addCells(int, int)
 * @see FlyweightRow#removeCells(int, int)
 */
class FlyweightCell implements Cell {
    private final int column;
    private final FlyweightRow row;

    public FlyweightCell(final FlyweightRow row, final int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row.getRow();
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public Element getElement() {
        return (Element) row.getElement().getChild(column);
    }

}
