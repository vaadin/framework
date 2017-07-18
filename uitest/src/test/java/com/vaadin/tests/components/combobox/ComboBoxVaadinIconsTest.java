package com.vaadin.tests.components.combobox;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxVaadinIconsTest extends MultiBrowserTest {

    @Test
    public void testComboBoxIconRendering() throws IOException {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.openPopup();
        compareScreen(comboBox.getSuggestionPopup(), "popup");
        comboBox.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER);
        compareScreen(comboBox, "paperplane");
    }

}
