package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableVisibleColumnsUpdateTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        compareScreen("first");

        $(ButtonElement.class).first().click();

        compareScreen("second");

        $(ButtonElement.class).first().click();

        compareScreen("first2");

        $(ButtonElement.class).first().click();

        compareScreen("second2");

    }
}
