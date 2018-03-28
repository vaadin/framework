package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPopupPositionEmbeddedInDivTest extends MultiBrowserTest {

    @Test
    public void popupBelow() {
        driver.get(StringUtils.strip(getBaseURL(), "/")
                + "/statictestfiles/ComboBoxEmbeddingHtmlPage.html");

        // Chrome requires document.scrollTop (<body>)
        // Firefox + IE wants document.documentElement.scrollTop (<html>)
        executeScript(
                "document.body.scrollTop=200;document.documentElement.scrollTop=200;document.body.scrollLeft=50;document.documentElement.scrollLeft=50;");

        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.openPopup();
        WebElement popup = $(ComboBoxElement.class).first()
                .getSuggestionPopup();

        Point comboboxLocation = combobox.getLocation();
        Point popupLocation = popup.getLocation();
        assertTrue("Popup should be below combobox",
                popupLocation.getY() > comboboxLocation.getY());

        assertTrue("Popup should be left aligned with the combobox",
                popupLocation.getX() == comboboxLocation.getX());
    }

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxEmbeddedInDiv.class;
    }
}
