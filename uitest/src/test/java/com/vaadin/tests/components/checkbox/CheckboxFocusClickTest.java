/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.tests.components.checkbox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckboxFocusClickTest extends MultiBrowserTest {

    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        Assert.assertEquals("checked", checkbox.getValue());
        WebElement label = checkbox.findElement(By.xpath("label"));

        label.click();
        Assert.assertEquals("1. checkbox focused", getLogRow(0));
    }
}
