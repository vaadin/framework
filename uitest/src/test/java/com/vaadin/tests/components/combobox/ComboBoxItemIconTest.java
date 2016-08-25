package com.vaadin.tests.components.combobox;

import org.junit.Test;

import com.vaadin.testbench.customelements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxItemIconTest extends MultiBrowserTest {
    @Test
    public void testIconsInComboBox() throws Exception {
        openTestURL();

        ComboBoxElement firstCombo = $(ComboBoxElement.class).first();

        firstCombo.openPopup();
        compareScreen("first-combobox-open");

        // null item not on the list, so use index 1
        firstCombo.selectByText(firstCombo.getPopupSuggestions().get(1));

        compareScreen("fi-hu-selected");

        ComboBoxElement secondCombo = $(ComboBoxElement.class).get(1);

        secondCombo.openPopup();
        compareScreen("second-combobox-open");

        secondCombo.selectByText(secondCombo.getPopupSuggestions().get(2));
        compareScreen("fi-au-selected");
    }

}