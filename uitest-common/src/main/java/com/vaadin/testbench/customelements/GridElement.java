package com.vaadin.testbench.customelements;

import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Grid")
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
