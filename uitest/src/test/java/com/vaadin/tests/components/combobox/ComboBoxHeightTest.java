package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxHeightTest extends SingleBrowserTest {

    @Test
    public void testPopupHeight() {
        openTestURL();
        assertPopupHeight();
    }

    @Test
    public void testPopupHeightCustomTheme() {
        openTestURL("theme=tests-valo-combobox-height");
        assertPopupHeight();
    }

    private void assertPopupHeight() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        comboBox.openPopup();
        WebElement suggestionPopup = comboBox.getSuggestionPopup();

        int suggestionPopupBottom =
                suggestionPopup.getLocation().getY() + suggestionPopup.getSize()
                        .getHeight();

        assertGreaterOrEqual(
                "Combo box suggestion popup should not exceed the browser's viewport",
                driver.manage().window().getSize().getHeight(),
                suggestionPopupBottom);
    }
}
