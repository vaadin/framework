/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
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
