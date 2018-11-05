package com.vaadin.tests.components.orderedlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class HorizontalLayoutFullsizeContentWithErrorMsgTest
        extends MultiBrowserTest {

    @Test
    public void test() {
        openTestURL();
        WebElement element = getDriver().findElement(
                By.id(HorizontalLayoutFullsizeContentWithErrorMsg.FIELD_ID));
        Point location = element.getLocation();

        WebElement errorToggleButton = getDriver().findElement(
                By.id(HorizontalLayoutFullsizeContentWithErrorMsg.BUTTON_ID));

        errorToggleButton.click();

        assertEquals(location, element.getLocation());

        errorToggleButton.click();

        assertEquals(location, element.getLocation());

    }

}
