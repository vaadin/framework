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
package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxNoTextInputTest extends MultiBrowserTest {

    @Test
    public void testComboBoxNoTextInputPopupOpensOnClick() throws Exception {
        openTestURL();

        // deactivate text input
        click($(CheckBoxElement.class).id("textInput"));

        // click and check that popup appears
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        click(cb);
        // popup is opened lazily
        waitForElementPresent(By.vaadin("//com.vaadin.ui.ComboBox[0]#popup"));
    }

    @Test
    public void testComboBoxWithTextInputNoPopupOpensOnClick() throws Exception {
        openTestURL();

        // click and check that no popup appears
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        click(cb);
        // popup is opened lazily
        sleep(1000);
        Assert.assertFalse(cb.isElementPresent(By.vaadin("#popup")));
    }

    private void click(ComboBoxElement cb) throws Exception {
        WebElement element = cb.findElement(By.vaadin("#textbox"));
        ((TestBenchElementCommands) element).click(8, 7);
    }

}
