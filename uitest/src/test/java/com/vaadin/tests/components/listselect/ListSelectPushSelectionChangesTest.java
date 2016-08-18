/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.listselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class ListSelectPushSelectionChangesTest
        extends SingleBrowserTestPhantomJS2 {
    @Test
    public void testMultiSelectBehavior() {
        openTestURL();

        Assert.assertEquals(
                "Should have null item + 3 options in single selection mode", 4,
                getListSelect().getOptions().size());

        $(OptionGroupElement.class).caption("Mode").first()
                .selectByText("Multi");

        Assert.assertEquals(
                "Should have 3 options but no null item in multi selection mode",
                3, getListSelect().getOptions().size());

        selectOptionGroup("a");
        Assert.assertEquals("a", getSelectValue());

        selectOptionGroup("b");
        Assert.assertEquals("a,b", getSelectValue());

        selectOptionGroup("a");
        Assert.assertEquals(
                "Clicking selected item should deselct in multi selection mode",
                "b", getSelectValue());

        selectNull();
        Assert.assertEquals("", getSelectValue());
    }

    @Test
    public void testSingleSelectBehavior() {
        openTestURL();

        selectOptionGroup("a");
        Assert.assertEquals("a", getSelectValue());

        selectOptionGroup("b");
        Assert.assertEquals("b", getSelectValue());

        selectOptionGroup("b");
        Assert.assertEquals(
                "Selecting the selected item again should not deselect in single selection mode",
                "b", getSelectValue());

        selectNull();
        Assert.assertEquals("", getSelectValue());
        Assert.assertEquals(
                "Not even the single select item should be selected after setValue(null)",
                0, getSelectCount());

        selectOptionGroup("c");
        Assert.assertEquals("c", getSelectValue());

        getListSelect().selectByText("");
        Assert.assertEquals("", getSelectValue());
        Assert.assertEquals(
                "Null select item should remain selected if clicked by the user",
                1, getSelectCount());

        selectNull();
        Assert.assertEquals("", getSelectValue());
        Assert.assertEquals(
                "Null select item should remain selected even after a repaint",
                1, getSelectCount());
    }

    private ListSelectElement getListSelect() {
        return $(ListSelectElement.class).first();
    }

    private int getSelectCount() {
        return getSelectedOptions().size();
    }

    private void selectNull() {
        $(ButtonElement.class).first().click();
    }

    private String getSelectValue() {
        List<WebElement> selectedOptions = getSelectedOptions();

        StringBuilder value = new StringBuilder();
        for (int i = 0; i < selectedOptions.size(); i++) {
            if (i != 0) {
                value.append(',');
            }
            value.append(selectedOptions.get(i).getText());
        }
        return value.toString();
    }

    private List<WebElement> getSelectedOptions() {
        ListSelectElement listSelect = getListSelect();
        Select select = new Select(
                listSelect.findElement(By.tagName("select")));
        return select.getAllSelectedOptions();
    }

    private void selectOptionGroup(String value) {
        $(OptionGroupElement.class).caption("OptionGroup").first()
                .selectByText(value);
    }
}
