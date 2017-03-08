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
package com.vaadin.tests.components.upload;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledUploadButtonTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    private String getUploadButtonClass() {
        WebElement uploadButton = getUploadButton();

        return uploadButton.getAttribute("class");
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private WebElement getUploadButton() {
        UploadElement upload = $(UploadElement.class).first();
        return upload.findElement(By.className("v-button"));
    }

    @Test
    public void buttonIsDisabled() {
        assertThat(getUploadButtonClass(), not(containsString("v-disabled")));

        clickButton("Set disabled");

        assertThat(getUploadButtonClass(), containsString("v-disabled"));
    }
}
