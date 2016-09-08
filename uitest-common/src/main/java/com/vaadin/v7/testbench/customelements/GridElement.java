/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.v7.testbench.customelements;

import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * TestBench Element API for Grid
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
@ServerClass("com.vaadin.v7.ui.Grid")
public class GridElement extends com.vaadin.testbench.elements.GridElement {

    /**
     * Gets the element that contains the details of a row.
     *
     * @since 8.0
     * @param rowIndex
     *            the index of the row for the details
     * @return the element that contains the details of a row. <code>null</code>
     *         if no widget is defined for the detials row
     * @throws NoSuchElementException
     *             if the given details row is currently not open
     */
    public TestBenchElement getDetails(int rowIndex)
            throws NoSuchElementException {
        return getSubPart("#details[" + rowIndex + "]");
    }

    private TestBenchElement getSubPart(String subPartSelector) {
        return (TestBenchElement) findElement(By.vaadin(subPartSelector));
    }

}
