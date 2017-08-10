package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

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

    @Test
    public void iconResetOnSelectionCancelByEscape() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);

        assertSelection(cb, "hu.gif", "Hungary");
        cb.openPopup();
        cb.sendKeys(Keys.UP);
        assertSelection(cb, "au.gif", "Australia");
        cb.sendKeys(Keys.ESCAPE);
        assertSelection(cb, "hu.gif", "Hungary");
    }

    @Test
    public void iconResetOnSelectionCancelByClickingOutside() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);

        assertSelection(cb, "hu.gif", "Hungary");
        cb.openPopup();
        cb.sendKeys(Keys.UP);
        assertSelection(cb, "au.gif", "Australia");
        findElement(By.tagName("body")).click();
        assertSelection(cb, "hu.gif", "Hungary");

    }

    private void assertSelection(ComboBoxElement cb, String imageSuffix,
            String caption) {
        Assert.assertEquals(caption, cb.getValue());
        String imgSrc = cb.findElement(By.className("v-icon"))
                .getAttribute("src");
        imgSrc = imgSrc.substring(imgSrc.lastIndexOf('/') + 1);
        Assert.assertEquals(imageSuffix, imgSrc);

    }

}
