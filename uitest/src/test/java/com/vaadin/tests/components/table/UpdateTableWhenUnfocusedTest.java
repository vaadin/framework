package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UpdateTableWhenUnfocusedTest extends MultiBrowserTest {

    @Test
    public void testWindowIsNotScrolled() throws IOException {
        openTestURL();

        TestBenchElement cell = $(TableElement.class).first().getCell(3, 0);
        cell.click();

        TestBenchElement button = $(ButtonElement.class).first();
        button.focus();

        int buttonLocation = button.getLocation().getY();

        button.click();

        int newButtonLocation = button.getLocation().getY();

        assertThat(
                "Button location has changed after table refresh, window has scrolled and it shouldn't have",
                newButtonLocation, is(buttonLocation));

    }
}
