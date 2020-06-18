package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxReadOnlyPopupTest extends MultiBrowserTest {

    @Test
    public void expandedComboBoxSetToReadOnlyShouldHidePopup() {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.openPopup();

        // Calls comboBox.setReadOnly(true);
        comboBox.sendKeys(String.valueOf('q'));

        assertFalse("Read-only ComboBox's popup should be hidden!",
                comboBox.isPopupOpen());
    }

}
