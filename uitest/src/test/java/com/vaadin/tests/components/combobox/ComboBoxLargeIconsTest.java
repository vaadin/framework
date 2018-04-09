package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxLargeIconsTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.components.combobox.Comboboxes.class;
    }

    @Test
    public void testComboBoxIcons() throws Exception {
        openTestURL();
        NativeSelectElement iconSelect = $(NativeSelectElement.class).first();
        iconSelect.selectByText("16x16");

        ComboBoxElement cb = $(ComboBoxElement.class)
                .caption("Undefined wide select with 50 items").first();
        cb.openPopup();
        compareScreen("icons-16x16-page1");
        cb.openNextPage();
        compareScreen("icons-16x16-page2");
        clickElement(cb.findElement(By.vaadin("#popup/item0")));
        compareScreen("icons-16x16-selected-1-3-5-9");

        iconSelect.selectByText("32x32");
        cb.openPopup();
        compareScreen("icons-32x32-page2");

        // Closes the popup
        cb.openPopup();

        iconSelect.selectByText("64x64");

        ComboBoxElement pageLength0cb = $(ComboBoxElement.class)
                .caption("Pagelength 0").first();
        pageLength0cb.openPopup();

        // Using a larger icon size causes some flickering in the position of
        // popup. Wait for it to stabilize.
        Thread.sleep(1000);

        clickElement(pageLength0cb.findElement(By.vaadin("#popup/item1")));

        ComboBoxElement cb200px = $(ComboBoxElement.class)
                .caption("200px wide select with 50 items").first();
        cb200px.openPopup();
        clickElement(cb200px.findElement(By.vaadin("#popup/item1")));

        ComboBoxElement cb150px = $(ComboBoxElement.class)
                .caption("150px wide select with 5 items").first();
        new Actions(driver).sendKeys(cb150px, Keys.DOWN).perform();

        compareScreen("icons-64x64-page1-highlight-first");
    }
}
