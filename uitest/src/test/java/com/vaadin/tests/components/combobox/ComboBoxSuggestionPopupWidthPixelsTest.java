package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

/**
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupWidthPixelsTest extends MultiBrowserTest {

    @Test
    public void suggestionPopupFixedWidthTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("pixels"));

        WebElement selectTextbox = $(ComboBoxElement.class).first()
                .findElement(By.vaadin("#textbox"));
        selectTextbox.click();

        CustomComboBoxElement cb = $(CustomComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int width = popup.getSize().getWidth();
        assertTrue(width == 300);

    }

};
