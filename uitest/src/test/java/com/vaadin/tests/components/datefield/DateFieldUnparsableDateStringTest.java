package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
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

        getInput().sendKeys("0304", Keys.TAB);
        assertEquals("03.04.2018", getInput().getAttribute("value"));
        getInput().clear();

        getInput().sendKeys("0304", Keys.TAB);
        assertEquals("03.04.2018", getInput().getAttribute("value"));
    }

    private WebElement getInput() {
        return findElement(By.xpath("//input"));
    }
}
