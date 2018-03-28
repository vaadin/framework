package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableChildMeasurementHintTest extends MultiBrowserTest {

    @Test
    public void testCacheSize() throws IOException {

        openTestURL();

        $(ButtonElement.class).first().click();

        compareScreen("initial");

        $(ButtonElement.class).get(1).click();

        compareScreen("initial");

        $(ButtonElement.class).get(2).click();

        compareScreen("initial");
    }

}
