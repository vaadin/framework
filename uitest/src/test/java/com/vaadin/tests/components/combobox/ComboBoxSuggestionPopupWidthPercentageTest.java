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
public class ComboBoxSuggestionPopupWidthPercentageTest
        extends MultiBrowserTest {

    @Test
    public void suggestionPopupPersentageWidthTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("percentage"));

        WebElement selectTextbox = $(ComboBoxElement.class).first()
                .findElement(By.vaadin("#textbox"));
        selectTextbox.click();

        CustomComboBoxElement cb = $(CustomComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int width = popup.getSize().getWidth();
        assertTrue(width == 400);

    }

};
