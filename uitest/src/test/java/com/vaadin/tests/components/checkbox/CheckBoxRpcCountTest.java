package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CheckBoxRpcCountTest extends MultiBrowserTest {

    @Test
    public void numberOfRpcCallsIsEqualToClicks() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement labelElem = checkBoxElement.findElement(By.tagName("label"));
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count-label");

        // Click on the actual checkbox.
        inputElem.click();
        // Have to use waitUntil to make this test more stable.
        waitUntilLabelIsUpdated(countElem, "1 RPC call(s) made.");

        // Click on the checkbox label.
        labelElem.click();
        waitUntilLabelIsUpdated(countElem, "2 RPC call(s) made.");

        // Again on the label.
        labelElem.click();
        waitUntilLabelIsUpdated(countElem, "3 RPC call(s) made.");
    }

    private void waitUntilLabelIsUpdated(final WebElement countElem,
            final String expectedText) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return countElem.getText().equals(expectedText);
            }
        }, 5);
    }
}
