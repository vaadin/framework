package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CustomDateFormatEEETest extends MultiBrowserTest {

    @Test
    public void verifyDatePattern() {
        openTestURL();

        String dateValue = driver
                .findElement(By.className("v-datefield-textfield"))
                .getAttribute("value");
        assertEquals("14/03/2014 Fri", dateValue);
    }

}
