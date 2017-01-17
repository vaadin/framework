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

import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingToPageDisabledTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void checkValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        org.junit.Assert.assertEquals("Item 50", combo.getText());
    }

    @Test
    public void checkLastValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        combo.selectByText("Item 99");
        // this shouldn't clear the selection
        combo.openPopup();
        // close popup
        $(LabelElement.class).first().click();

        org.junit.Assert.assertEquals("Item 99", combo.getText());
    }
}
