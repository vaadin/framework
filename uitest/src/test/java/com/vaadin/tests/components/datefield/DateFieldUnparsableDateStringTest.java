package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class DateFieldUnparsableDateStringTest extends MultiBrowserTest {

    @Test
    public void testInvalidText() {
        openTestURL();
        waitForElementVisible(By.className("v-datefield"));
        WebElement dateTextbox = $(AbstractDateFieldElement.class).first()
                .findElement(By.className("v-textfield"));
        dateTextbox.sendKeys("0304", Keys.TAB);
        findElement(By.tagName("body")).click();
        assertEquals("03.04.2018", dateTextbox.getAttribute("value"));

        dateTextbox.clear();
        dateTextbox.sendKeys("0304", Keys.TAB);
        findElement(By.tagName("body")).click();
        assertEquals("03.04.2018", dateTextbox.getAttribute("value"));
    }
}
