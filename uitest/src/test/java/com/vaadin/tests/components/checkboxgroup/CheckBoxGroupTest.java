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
package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.components.checkbox.CheckBoxGroupTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for CheckBoxGroup
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroupTest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        openTestURL();
    }

    @Test
    public void initialLoad_containsCorrectItems() {
        assertItems(20);
    }

    @Test
    public void initialItems_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void disabled_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void disabled_increaseItemCountWithinPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "20");
        assertItems(20);
    }

    @Test
    public void disabled_increaseItemCountBeyondPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));

        getSelect().selectByText("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals("2. Selected: [Item 4, Item 2]", getLogRow(0));

        getSelect().selectByText("Item 4");
        Assert.assertEquals("3. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");

        Assert.assertTrue(getSelect().findElements(By.tagName("input")).stream()
                .allMatch(element -> element.getAttribute("disabled") != null));

        selectMenuPath("Component", "Listeners", "Selection listener");

        String lastLogRow = getLogRow(0);

        getSelect().selectByText("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));

        getSelect().selectByText("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals(lastLogRow, getLogRow(0));

        getSelect().selectByText("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 4");

        selectMenuPath("Component", "State", "Enabled");

        getSelect().selectByText("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));

        getSelect().selectByText("Item 2");
        Assert.assertEquals("4. Selected: [Item 5, Item 2]", getLogRow(0));

        getSelect().selectByText("Item 5");
        Assert.assertEquals("5. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void itemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator",
                "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void nullItemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator",
                "Null Caption Generator");
        for (String text : getSelect().getOptions()) {
            Assert.assertEquals("", text);
        }
    }

    @Test
    public void itemIconGenerator() {
        selectMenuPath("Component", "Item Generator",
                "Use Item Icon Generator");
        assertItemSuffices(20);
        List<WebElement> icons = getSelect()
                .findElements(By.cssSelector(".v-select-optiongroup .v-icon"));
        Assert.assertTrue(icons.size() > 0);
        for (int i = 0; i < icons.size(); i++) {
            Assert.assertEquals(VaadinIcons.values()[i + 1].getCodepoint(),
                    icons.get(i).getText().charAt(0));
        }
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("2. Selected: [Item 5]", getLogRow(0));
        assertSelected("Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 1");
        // Selection order (most recently selected is last)
        Assert.assertEquals("4. Selected: [Item 5, Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1", "Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("6. Selected: [Item 1]", getLogRow(0));
        assertSelected("Item 1");
    }

    private void assertSelected(String... expectedSelection) {
        Assert.assertEquals(Arrays.asList(expectedSelection),
                getSelect().getValue());
    }

    @Override
    protected Class<?> getUIClass() {
        return CheckBoxGroupTestUI.class;
    }

    protected CheckBoxGroupElement getSelect() {
        return $(CheckBoxGroupElement.class).first();
    }

    protected void assertItems(int count) {
        assertItems(count, "");
    }

    protected void assertItems(int count, String suffix) {
        int i = 0;
        for (String text : getSelect().getOptions()) {
            assertEquals("Item " + i + suffix, text);
            i++;
        }
        assertEquals("Number of items", count, i);
    }

    protected void assertItemSuffices(int count) {
        int i = 0;
        for (String text : getSelect().getOptions()) {
            assertTrue(text.endsWith("Item " + i));
            i++;
        }
        assertEquals("Number of items", count, i);
    }

    @Test
    public void testDisabled() {
        List<String> optionsCssClasses = getSelect().getOptionElements()
                .stream().map(e -> e.getAttribute("class"))
                .collect(Collectors.toList());
        for (int i = 0; i < optionsCssClasses.size(); i++) {
            String cssClassList = optionsCssClasses.get(i);
            if (i == 10) {
                assertTrue("10th item should be disabled",
                        cssClassList.toLowerCase().contains("disabled"));
            } else {
                assertFalse("Only 10th item should be disabled",
                        cssClassList.toLowerCase().contains("disabled"));
            }
        }
    }

    @Test
    public void testIconUrl() {
        List<String> optionsIcons = new ArrayList<>();
        for (WebElement option : getSelect().getOptionElements()) {
            List<WebElement> images = option.findElements(By.tagName("img"));
            if (images.size() > 0) {
                optionsIcons.add(images.get(0).getAttribute("src"));
            } else {
                optionsIcons.add(null);
            }
        }
        for (int i = 0; i < optionsIcons.size(); i++) {
            String icon = optionsIcons.get(i);
            if (i == 2) {
                assertNotNull("2nd item should have icon", icon);
            } else {
                assertNull("Only 2nd item should have icon", icon);
            }
        }
    }

}
