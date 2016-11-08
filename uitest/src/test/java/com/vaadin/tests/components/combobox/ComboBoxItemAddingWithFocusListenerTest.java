/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxItemAddingWithFocusListenerTest extends MultiBrowserTest {

    @Test
    public void testPopupViewContainsAddedItem() {
        openTestURL();
        ComboBoxElement cBox = $(ComboBoxElement.class).first();
        ButtonElement focusTarget = $(ButtonElement.class).first();
        cBox.openPopup();
        int i = 0;
        while (i < 3) {
            assertTrue("No item added on focus",
                    cBox.getPopupSuggestions().contains("Focus" + i++));
            focusTarget.focus();
            ((TestBenchElement) cBox.findElement(By.vaadin("#textbox")))
                    .focus();
        }
        assertTrue("No item added on focus",
                cBox.getPopupSuggestions().contains("Focus" + i));
    }
}
