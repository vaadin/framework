package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("xvfb-test")
public class ComboBoxItemIconTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities(),
                Browser.FIREFOX.getDesiredCapabilities());
    }

    @Test
    public void testIconsInComboBox() throws Exception {
        openTestURL();

        ComboBoxElement firstCombo = $(ComboBoxElement.class).first();

        firstCombo.openPopup();
        compareScreen(firstCombo.getSuggestionPopup(), "first-combobox-open");

        // null item not on the list, so use index 1
        firstCombo.selectByText(firstCombo.getPopupSuggestions().get(1));

        compareScreen(firstCombo, "fi-hu-selected");

        ComboBoxElement secondCombo = $(ComboBoxElement.class).get(1);

        secondCombo.openPopup();
        compareScreen(secondCombo.getSuggestionPopup(), "second-combobox-open");

        secondCombo.selectByText(secondCombo.getPopupSuggestions().get(2));
        compareScreen(secondCombo, "fi-au-selected");

        ComboBoxElement thirdCombo = $(ComboBoxElement.class).get(2);

        thirdCombo.openPopup();
        compareScreen(thirdCombo.getSuggestionPopup(), "third-combobox-open");

        thirdCombo.selectByText(thirdCombo.getPopupSuggestions().get(3));
        compareScreen(thirdCombo, "classresource");
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
        assertEquals(caption, cb.getValue());
        String imgSrc = cb.findElement(By.className("v-icon"))
                .getAttribute("src");
        imgSrc = imgSrc.substring(imgSrc.lastIndexOf('/') + 1);
        assertEquals(imageSuffix, imgSrc);

    }

}
