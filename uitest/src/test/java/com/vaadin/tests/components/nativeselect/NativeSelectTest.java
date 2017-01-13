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
package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeSelectTest extends MultiBrowserTest {

    @Before
    public void setUp() {
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
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void selectItemProgrammatically_correctItemSelected() {
        selectMenuPath("Component", "Selection", "Select", "Item 2");

        assertEquals("Selected item", "Item 2", getSelect().getValue());
    }

    @Test
    public void selectionListenerAdded_selectItem_listenerInvoked() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 5");

        assertEquals("1. Selected: Item 5", getLogRow(0));
    }

    @Test
    public void selectionListenerRemoved_selectItem_listenerNotInvoked() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 5");

        assertEquals("1. Command: /Selection listener(false)", getLogRow(0));
    }

    @Test(expected = ReadOnlyException.class)
    public void setReadOnly_trySelectItem_throws() {
        selectMenuPath("Component", "State", "Readonly");
        getSelect().selectByText("Item 5");
    }

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }

    protected NativeSelectElement getSelect() {
        return $(NativeSelectElement.class).first();
    }

    protected void assertItems(int count) {
        int i = 0;
        for (TestBenchElement e : getSelect().getOptions()) {
            assertEquals("Item " + i, e.getText());
            i++;
        }
        assertEquals("Number of items", count, i);
    }
}
