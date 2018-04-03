package com.vaadin.tests.components.button;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ButtonClickTest extends MultiBrowserTest {

    @Test
    public void buttonMouseDownOutOverUp() {
        openTestURL();

        WebElement clickedButton = vaadinElement(
                "/VVerticalLayout[0]/VButton[0]");
        WebElement visitedButton = vaadinElement(
                "/VVerticalLayout[0]/VButton[1]");

        new Actions(driver).moveToElement(clickedButton).clickAndHold()
                .moveToElement(visitedButton).moveToElement(clickedButton)
                .release().perform();

        assertEquals(ButtonClick.SUCCESS_TEXT,
                vaadinElement("/VVerticalLayout[0]/VLabel[0]").getText());
    }
}
