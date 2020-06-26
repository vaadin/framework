package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxAtBottomEdgeWithinHorizontalLayoutTest
        extends MultiBrowserTest {

    @Test
    public void ensurePopupInView() {
        openTestURL();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int cbBottom = cb.getLocation().getY() + cb.getSize().getHeight();
        int popupBottom = popup.getLocation().getY()
                + popup.getSize().getHeight();
        assertGreaterOrEqual(String.format(
                "Popup should not open below the ComboBox at the "
                        + "bottom edge of the viewport. ComboBox: %s, Popup: %s",
                cbBottom, popupBottom), cbBottom, popupBottom);
    }

    @Test
    public void ensurePopupPositionUpdatesWhenFiltered() {
        openTestURL();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int initialTop = popup.getLocation().getY();

        // filter a bit
        cb.findElement(By.vaadin("#textbox")).sendKeys("2");
        int updatedTop = popup.getLocation().getY();
        assertLessThan(String.format(
                "Popup should be repositioned when "
                        + "filtered. Initial: %s, Updated: %s",
                initialTop, updatedTop), initialTop, updatedTop);
        int cbBottom = cb.getLocation().getY() + cb.getSize().getHeight();
        assertGreaterOrEqual(String.format(
                "Popup should still open above the ComboBox when "
                        + "filtered a bit. ComboBox: %s, Popup: %s",
                cbBottom, updatedTop), cbBottom, updatedTop);

        // filter more
        cb.clear();
        cb.findElement(By.vaadin("#textbox")).sendKeys("1");
        popup = cb.getSuggestionPopup();
        updatedTop = popup.getLocation().getY();
        assertLessThanOrEqual(String.format(
                "Popup should open below the ComboBox when "
                        + "filtered down to one result. ComboBox: %s, Popup: %s",
                cbBottom, updatedTop), cbBottom, updatedTop);
    }

}
