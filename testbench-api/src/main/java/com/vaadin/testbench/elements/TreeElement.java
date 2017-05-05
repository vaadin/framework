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
import java.util.stream.Collectors;

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
@ServerClass("com.vaadin.ui.Tree")
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
                .findElement(By.className("v-tree8-expander"));

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
     * Gets all items currently shown in this tree. The returned element objects
     * are the rendered contents for each item.
     *
     * @return list of content elements for all items
     */
    public List<TestBenchElement> getAllItems() {
        return asTreeGrid().getBody().findElements(By.tagName("tr")).stream()
                .map(this::findCellContentFromRow).collect(Collectors.toList());
    }

    /**
     * Gets an item at given index. The returned element object is the rendered
     * content in the given index.
     *
     * @param index
     *            0-based row index
     * @return content element for item at given index
     */
    public TestBenchElement getItem(int index) {
        return findCellContentFromRow(asTreeGrid().getRow(index));
    }

    /**
     * Finds the rendered cell content from given row element. This expects the
     * row to contain only a single column rendered with TreeRenderer.
     *
     * @param rowElement
     *            the row element
     * @return cell content element
     */
    protected TestBenchElement findCellContentFromRow(WebElement rowElement) {
        return TestBenchElement.wrapElement(
                rowElement.findElement(By.className("gwt-HTML")),
                getCommandExecutor());
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
