package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;

public class TableContextClickTest extends TableContextClickTestBase {

    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        assertTypedContextClickListener(1);
    }

    @Test
    public void testHeaderContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(TableElement.class).first().getHeaderCell(0));

        assertEquals(
                "1. ContextClickEvent value: address, propertyId: address, section: HEADER",
                getLogRow(0));

        contextClick($(TableElement.class).first().getHeaderCell(3));

        assertEquals(
                "2. ContextClickEvent value: lastName, propertyId: lastName, section: HEADER",
                getLogRow(0));
    }

    @Test
    public void testFooterContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(TableElement.class).first().getFooterCell(0));

        assertEquals(
                "1. ContextClickEvent value: null, propertyId: address, section: FOOTER",
                getLogRow(0));

        contextClick($(TableElement.class).first().getFooterCell(3));

        assertEquals(
                "2. ContextClickEvent value: null, propertyId: lastName, section: FOOTER",
                getLogRow(0));
    }

    @Test
    public void testContextClickInEmptyTable() {
        addOrRemoveTypedListener();

        $(ButtonElement.class).caption("Remove all content").first().click();

        contextClick($(TableElement.class).first(), 100, 100);

        assertEquals(
                "1. ContextClickEvent value: , propertyId: null, section: BODY",
                getLogRow(0));
    }

}
