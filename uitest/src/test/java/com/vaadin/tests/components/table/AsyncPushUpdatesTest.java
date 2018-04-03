package com.vaadin.tests.components.table;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if VScrollTable handles Push updates correctly.
 *
 * @author Vaadin Ltd
 */
public class AsyncPushUpdatesTest extends MultiBrowserTest {

    @Test(expected = NoSuchElementException.class)
    public void InsertedRowsAreNotDuplicated() {
        openTestURL();

        WebElement button = $(ButtonElement.class).first();

        button.click();

        $(TableElement.class).first().getCell(12, 0);
        fail("Duplicates are present.");
    }

}
