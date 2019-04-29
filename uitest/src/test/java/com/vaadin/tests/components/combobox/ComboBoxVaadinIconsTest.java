package com.vaadin.tests.components.combobox;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxVaadinIconsTest extends MultiBrowserTest {

    @Test
    public void testComboBoxIconRendering() throws IOException {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        waitForElementPresent(By.id("value-label"));
        Assert.assertEquals(findElement(By.id("value-label")).getText(),
                "Test");

        comboBox.openPopup();
        comboBox.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER);
        Assert.assertEquals(findElement(By.id("value-label")).getText(),
                "PAPERPLANE");


    }
}
