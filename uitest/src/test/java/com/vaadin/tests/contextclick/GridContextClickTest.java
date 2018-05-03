package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;

public class GridContextClickTest extends AbstractContextClickTest {

    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(GridElement.class).first().getCell(0, 0));

        assertEquals(
                "1. ContextClickEvent value: Lisa Schneider, propertyId: address, section: BODY",
                getLogRow(0));

        contextClick($(GridElement.class).first().getCell(0, 3));

        assertEquals(
                "2. ContextClickEvent value: Lisa Schneider, propertyId: lastName, section: BODY",
                getLogRow(0));
    }

    @Test
    public void testHeaderContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(GridElement.class).first().getHeaderCell(0, 0));

        assertEquals(
                "1. ContextClickEvent value: Address, propertyId: address, section: HEADER",
                getLogRow(0));

        contextClick($(GridElement.class).first().getHeaderCell(0, 3));

        assertEquals(
                "2. ContextClickEvent value: Last Name, propertyId: lastName, section: HEADER",
                getLogRow(0));
    }

    @Test
    public void testFooterContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(GridElement.class).first().getFooterCell(0, 0));

        assertEquals(
                "1. ContextClickEvent value: , propertyId: address, section: FOOTER",
                getLogRow(0));

        contextClick($(GridElement.class).first().getFooterCell(0, 3));

        assertEquals(
                "2. ContextClickEvent value: , propertyId: lastName, section: FOOTER",
                getLogRow(0));
    }

    @Test
    public void testContextClickInEmptyGrid() {
        addOrRemoveTypedListener();

        $(ButtonElement.class).caption("Remove all content").first().click();

        contextClick($(GridElement.class).first(), 100, 100);

        assertEquals(
                "1. ContextClickEvent value: , propertyId: null, section: BODY",
                getLogRow(0));

    }

    /**
     * Performs a context click on given element at coordinates 20, 10 followed
     * by a regular click. This prevents browser context menu from blocking
     * future operations.
     *
     * A smaller X offset might hit the resize handle of the previous cell that
     * overlaps with the next header cell.
     *
     * @param e
     *            web element
     */
    @Override
    protected void contextClick(WebElement e) {
        contextClick(e, 20, 10);
    }

}
