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
package com.vaadin.v7.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UndefinedHeightSubWindowAndContentTest extends MultiBrowserTest {

    @Test
    public void testUndefinedHeight() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();

        textField.click();
        textField.sendKeys("invalid", Keys.ENTER);

        WindowElement window = $(WindowElement.class).first();
        int height = window.getSize().getHeight();
        Assert.assertTrue("Window height with validation failure",
                161 <= height && height <= 164);

        textField.setValue("valid");
        textField.sendKeys(Keys.ENTER);
        height = window.getSize().getHeight();
        Assert.assertTrue("Window height with validation success",
                136 <= height && height <= 139);
    }

}
