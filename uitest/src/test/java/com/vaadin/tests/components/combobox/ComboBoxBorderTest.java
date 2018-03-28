package com.vaadin.tests.components.combobox;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxBorderTest extends MultiBrowserTest {
    @Test
    public void testComboBoxArrow() throws IOException {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        cb.sendKeys(Keys.DOWN, Keys.ENTER);
        Actions actions = new Actions(getDriver());
        actions.moveToElement($(LabelElement.class).first()).perform();
        compareScreen("arrow");
    }
}
