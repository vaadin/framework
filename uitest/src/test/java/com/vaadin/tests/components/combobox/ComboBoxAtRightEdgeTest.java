package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxAtRightEdgeTest extends MultiBrowserTest {

    @Test
    public void ensurePopupInView() {
        openTestURL();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int cbRight = cb.getLocation().getX() + cb.getSize().getWidth();
        int popupRight = popup.getLocation().getX()
                + popup.getSize().getWidth();
        assertGreaterOrEqual(String.format(
                "Popup should not reach further right than the ComboBox at the "
                        + "right edge of the viewport. ComboBox: %s, Popup: %s",
                cbRight, popupRight), cbRight, popupRight);
    }
}
