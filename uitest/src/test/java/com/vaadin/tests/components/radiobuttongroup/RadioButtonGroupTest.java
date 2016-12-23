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
package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.components.radiobutton.RadioButtonGroupTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for RadioButtonGroup
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class RadioButtonGroupTest extends MultiBrowserTest {

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
        Assert.assertEquals("1. Selected: Optional[Item 4]", getLogRow(0));

        getSelect().selectByText("Item 2");
        Assert.assertEquals("2. Selected: Optional[Item 2]", getLogRow(0));

        getSelect().selectByText("Item 4");
        Assert.assertEquals("3. Selected: Optional[Item 4]", getLogRow(0));
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
        Assert.assertEquals(lastLogRow, getLogRow(0));

        getSelect().selectByText("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
    }

    @Test
    public void itemIconGenerator() {
        selectMenuPath("Component", "Item Icon Generator",
                "Use Item Icon Generator");
        assertItemsSuffices(20);

        List<WebElement> icons = getSelect()
                .findElements(By.cssSelector(".v-select-optiongroup .v-icon"));

        assertEquals(20, icons.size());

        for (int i = 0; i < icons.size(); i++) {
            Assert.assertEquals(VaadinIcons.values()[i + 1].getCodepoint(),
                    icons.get(i).getText().charAt(0));
        }
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 4");

        selectMenuPath("Component", "State", "Enabled");

        getSelect().selectByText("Item 5");
        Assert.assertEquals("3. Selected: Optional[Item 5]", getLogRow(0));

        getSelect().selectByText("Item 2");
        Assert.assertEquals("4. Selected: Optional[Item 2]", getLogRow(0));

        getSelect().selectByText("Item 4");
        Assert.assertEquals("5. Selected: Optional[Item 4]", getLogRow(0));
    }

    @Test
    public void itemCaptionGenerator() {
        selectMenuPath("Component", "Item Caption Generator",
                "Item Caption Generator", "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void nullItemCaptionGenerator() {
        selectMenuPath("Component", "Item Caption Generator",
                "Item Caption Generator", "Null Caption Generator");
        for (String text : getSelect().getOptions()) {
            Assert.assertEquals("", text);
        }
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("2. Selected: Optional[Item 5]", getLogRow(0));
        assertSelected("Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 1");
        Assert.assertEquals("4. Selected: Optional[Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("6. Selected: Optional[Item 5]", getLogRow(0));
        assertSelected("Item 5");
    }

    private void assertSelected(String expectedSelection) {
        Assert.assertEquals(expectedSelection, getSelect().getValue());
    }

    @Override
    protected Class<?> getUIClass() {
        return RadioButtonGroupTestUI.class;
    }

    protected RadioButtonGroupElement getSelect() {
        return $(RadioButtonGroupElement.class).first();
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

    protected void assertItemsSuffices(int count) {
        int i = 0;
        for (String text : getSelect().getOptions()) {
            assertTrue(text.endsWith("Item " + i));
            i++;
        }
        assertEquals("Number of items", count, i);
    }
}
