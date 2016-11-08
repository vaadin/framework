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
package com.vaadin.tests.components.select;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class EnumSelectTest extends SingleBrowserTest {

    @Test
    public void enumInNativeSelect() {
        openTestURL();
        NativeSelectElement ns = $(NativeSelectElement.class).first();
        List<TestBenchElement> options = ns.getOptions();
        Assert.assertEquals("Some value", options.get(1).getText());
        Assert.assertEquals("Some other value", options.get(2).getText());
    }

    @Test
    public void enumInComboBox() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        List<String> options = cb.getPopupSuggestions();
        Assert.assertEquals("Some value", options.get(1));
        Assert.assertEquals("Some other value", options.get(2));
    }

    @Test
    public void enumInComboBoxFiltering() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.findElement(By.vaadin("#textbox")).sendKeys(" other ");
        List<String> options = cb.getPopupSuggestions();
        Assert.assertEquals("Only one item should match filter", 1,
                options.size());
        Assert.assertEquals("Invalid option matched filter", "Some other value",
                options.get(0));
    }
}
