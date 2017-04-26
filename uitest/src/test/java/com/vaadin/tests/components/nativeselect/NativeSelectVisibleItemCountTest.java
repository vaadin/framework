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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NativeSelectVisibleItemCountTest extends SingleBrowserTest {

    @Test
    public void changeItemCount() {
        openTestURL();
        WebElement select = $(NativeSelectElement.class).first()
                .findElement(By.xpath("select"));
        Assert.assertEquals("1", select.getAttribute("size"));
        selectMenuPath("Component", "Size", "Visible item count", "5");
        Assert.assertEquals("5", select.getAttribute("size"));
    }

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }
}
