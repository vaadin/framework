package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer ClickListener
 *
 * @since
 * @author Vaadin Ltd
 */
public class HeaderUpdateWhenNoRowsTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        TestBenchElement header0 = table.getHeaderCell(0);
        header0.click();

        compareScreen("headerVisible");

        $(CheckBoxElement.class).first().click();

        compareScreen("headerHidden");

        $(CheckBoxElement.class).first().click();

        compareScreen("headerVisible2");

    }
}
