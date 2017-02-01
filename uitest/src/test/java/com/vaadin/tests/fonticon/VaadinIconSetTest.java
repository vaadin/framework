package com.vaadin.tests.fonticon;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class VaadinIconSetTest extends MultiBrowserTest {

    @Test
    public void checkScreenshot_initial() throws IOException {
        openTestURL();
        compareScreen("allVaadinIcons");
    }

    @Test
    public void checkScreenshot_changeIcon() throws IOException {
        openTestURL();

        $(ButtonElement.class).first().click();

        compareScreen("allVaadinIcons-switch");
    }

    @Test
    public void comboBoxItemIconsOnKeyboardNavigation() throws Exception {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        // No initial value.
        assertEquals("", comboBox.getText());

        // Navigate to the first item with keyboard navigation.
        comboBox.sendKeys(400, Keys.ARROW_DOWN, Keys.ARROW_DOWN);

        // Value must be "One" without any extra characters.
        // See ticket #14660
        assertEquals("One", comboBox.getText());

        // Check also the second item.
        comboBox.sendKeys(Keys.ARROW_DOWN);
        assertEquals("Two", comboBox.getText());
    }

}
