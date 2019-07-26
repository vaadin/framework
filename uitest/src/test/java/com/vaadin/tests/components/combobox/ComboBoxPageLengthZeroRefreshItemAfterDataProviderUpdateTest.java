package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.openqa.selenium.By;

public class ComboBoxPageLengthZeroRefreshItemAfterDataProviderUpdateTest
        extends MultiBrowserTest {

    @Test
    public void refreshItemAfterDataProviderUpdateWithPageLengthZero() {
        openTestURL();
        waitForElementVisible(By.id("combo-0"));
        ComboBoxElement pageLengthZeroCombo = $(ComboBoxElement.class)
                .id("combo-0");
        ButtonElement updateButton = $(ButtonElement.class).id("update-0");
        ButtonElement refreshButton = $(ButtonElement.class).id("refresh-0");
        String comboText = getComboBoxInputTextAfterUpdateAndRefresh(
                pageLengthZeroCombo, updateButton, refreshButton);
        assertEquals("Expected item containing (updated), got " + comboText,
                true, comboText.contains("(updated)"));
    }

    @Test
    public void refreshItemAfterDataProviderUpdateWithDefaultPageLength() {
        openTestURL();
        waitForElementVisible(By.id("combo-n"));
        ComboBoxElement pageLengthRegularCombo = $(ComboBoxElement.class)
                .id("combo-n");
        ButtonElement updateButton = $(ButtonElement.class).id("update-n");
        ButtonElement refreshButton = $(ButtonElement.class).id("refresh-n");
        String comboText = getComboBoxInputTextAfterUpdateAndRefresh(
                pageLengthRegularCombo, updateButton, refreshButton);
        assertEquals("Expected item containing (updated), got " + comboText,
                true, comboText.contains("(updated)"));
    }

    public String getComboBoxInputTextAfterUpdateAndRefresh(
            ComboBoxElement combo, ButtonElement updateDataProvider,
            ButtonElement refreshItem) {
        updateDataProvider.click();
        refreshItem.click();
        return combo.getValue();
    }
}
