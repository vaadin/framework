package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

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

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int width = popup.getSize().getWidth();
        assertTrue(width == 400);

    }

};
