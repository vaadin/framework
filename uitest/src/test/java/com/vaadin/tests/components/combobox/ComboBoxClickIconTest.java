package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

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

        getDriver().findElements(By.className("gwt-MenuItem")).get(1).click();

        getDriver().findElement(By.className("v-filterselect"))
                .findElement(By.className("v-icon")).click();

        Assert.assertTrue("Unable to find menu items in combobox popup",
                isElementPresent(By.className("gwt-MenuItem")));
    }

}
