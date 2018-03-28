package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check whether combobox is expanded when icon is clicked.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxClickIconTest extends MultiBrowserTest {

    @Test
    public void testClickOnIconInCombobox() {
        openTestURL();

        $(ComboBoxElement.class).first().openPopup();

        clickElement(
                getDriver().findElements(By.className("gwt-MenuItem")).get(1));

        clickElement(getDriver().findElement(By.className("v-filterselect"))
                .findElement(By.className("v-icon")));

        assertTrue("Unable to find menu items in combobox popup",
                isElementPresent(By.className("gwt-MenuItem")));
    }

}
