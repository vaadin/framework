package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldPopupClosingOnDetachTest extends MultiBrowserTest {

    @Test
    public void testDateFieldPopupClosingLongClick()
            throws InterruptedException, IOException {
        openTestURL();

        // Open the DateField popup.
        DateFieldElement df = $(DateFieldElement.class).first();
        df.findElement(By.tagName("button")).click();

        // Test UI will remove the DateField after 1 second.
        waitForElementNotPresent(By.className("v-datefield"));

        // The popup should be also removed now.
        assertElementNotPresent(By.className("v-datefield-popup"));
    }

}
