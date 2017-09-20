/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.testbench.elements;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.GridLayout")
public class GridLayoutElement extends AbstractLayoutElement {
    /**
     * Gets the total number of rows in the layout.
     *
     * @return the number of rows in the layout
     * @since 8.0.6
     */
    public long getRowCount() {
        Long res = (Long) getCommandExecutor()
                .executeScript("return arguments[0].getRowCount()", this);
        if (res == null) {
            throw new IllegalStateException("getRowCount returned null");
        }

        return res.longValue();
    }

    /**
     * Gets the total number of columns in the layout.
     *
     * @return the number of columns in the layout
     * @since 8.0.6
     */
    public long getColumnCount() {
        Long res = (Long) getCommandExecutor()
                .executeScript("return arguments[0].getColumnCount()", this);
        if (res == null) {
            throw new IllegalStateException("getColumnCount returned null");
        }

        return res.longValue();
    }

    /**
     * Gets the cell element at the given position.
     *
     * @param row
     *            the row coordinate
     * @param column
     *            the column coordinate
     * @return the cell element at the given position
     * @throws NoSuchElementException
     *             if no cell was found at the given position
     * @since 8.0.6
     */
    public WebElement getCell(int row, int column) {
        WebElement res = (WebElement) getCommandExecutor().executeScript(
                "return arguments[0].getCell(" + row + "," + column + ")",
                this);
        if (res == null) {
            throw new NoSuchElementException(
                    "No cell found at " + row + "," + column);
        }

        return res;

    }
}
