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

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Testbench Element API for {@code Tree}.
 * <p>
 * <strong>Note:</strong> This TreeElement is for the Vaadin 8 version of Tree.
 * Use {@link com.vaadin.v7.testbench.elements.TreeElement} for the
 * compatibility version.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
// TODO: Switch to com.vaadin.ui.Tree once inheritance finding is fixed
@ServerClass("com.vaadin.ui.Composite")
public class TreeElement extends AbstractComponentElement {

    /**
     * Expands the row at the given index in the tree.
     *
     * @param index
     *            0-based row index to expand
     */
    public void expand(int index) {
        if (isExpanded(index)) {
            throw new IllegalStateException(
                    "The element at row " + index + " was expanded already");
        }
        getExpandElement(index).click();
    }

    /**
     * Returns whether the row at the given index is expanded or not.
     *
     * @param index
     *            0-based row index
     * @return {@code true} if expanded, {@code false} if collapsed
     */
    public boolean isExpanded(int index) {
        WebElement expandElement = getExpandElement(index);
        List<String> classes = Arrays
                .asList(expandElement.getAttribute("class").split(" "));
        return classes.contains("expanded") && !classes.contains("collapsed");
    }

    /**
     * Returns whether the row at the given index is collapsed or not.
     *
     * @param rowIndex
     *            0-based row index
     *
     * @return {@code true} if collapsed, {@code false} if expanded
     */
    public boolean isCollapsed(int rowIndex) {
        return !isExpanded(rowIndex);
    }

    /**
     * Gets the expand/collapse element for the given row.
     *
     * @param rowIndex
     *            0-based row index
     * @return the {@code span} element that is clicked for expanding/collapsing
     *         a row
     * @throws NoSuchElementException
     *             if there is no expand element for this row
     */
    public WebElement getExpandElement(int rowIndex) {
        return asTreeGrid().getCell(rowIndex, 0)
                .findElement(By.className("v-newtree-expander"));

    }

    /**
     * Collapses the row at the given index in the tree.
     *
     * @param index
     *            0-based row index to collapse
     */
    public void collapse(int index) {
        if (isCollapsed(index)) {
            throw new IllegalStateException(
                    "The element at row " + index + " was collapsed already");
        }
        getExpandElement(index).click();
    }

    /**
     * Gets all items currently shown in this tree.
     *
     * @return list of all items
     */
    public List<TestBenchElement> getAllItems() {
        return TestBenchElement.wrapElements(
                asTreeGrid().getBody().findElements(By.tagName("tr")),
                getCommandExecutor());
    }

    /**
     * Gets an item at given index.
     *
     * @param index
     *            0-based row index
     * @return item at given index
     */
    public TestBenchElement getItem(int index) {
        return asTreeGrid().getCell(index, 0);
    }

    /**
     * Convenience method for accessing the underlying TreeGrid.
     *
     * @return this tree element as a tree grid element.
     */
    protected TreeGridElement asTreeGrid() {
        return wrap(TreeGridElement.class);
    }
}
