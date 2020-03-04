package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.WebElement;

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

}
