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

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
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
    public void checkValueIsVidinlr() throws InterruptedException {
        WebElement input = driver.findElement(By
                .className("v-filterselect-input"));
        String value = input.getAttribute("value");
        org.junit.Assert.assertEquals("Item 50", value);
    }

}
