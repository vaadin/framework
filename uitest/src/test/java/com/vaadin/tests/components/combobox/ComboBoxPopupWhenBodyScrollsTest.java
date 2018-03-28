package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPopupWhenBodyScrollsTest extends MultiBrowserTest {

    @Test
    public void popupBelow() {
        openTestURL();
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.openPopup();
        WebElement popup = $(ComboBoxElement.class).first()
                .getSuggestionPopup();

        int comboboxTop = combobox.getLocation().getY();
        int popupTop = popup.getLocation().getY();
        assertTrue("Popup should be below combobox", popupTop > comboboxTop);
    }
}
