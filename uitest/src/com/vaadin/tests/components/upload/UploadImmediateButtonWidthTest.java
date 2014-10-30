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
package com.vaadin.tests.components.upload;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class UploadImmediateButtonWidthTest extends MultiBrowserTest {

    protected abstract String getTheme();

    protected double getButtonWidth(String id) {
        WebElement upload = driver.findElement(By.id(id));
        WebElement button = upload.findElement(By.className("v-button"));

        return button.getSize().getWidth();
    }

    @Override
    protected Class<?> getUIClass() {
        return UploadImmediateButtonWidth.class;
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL(String.format("theme=%s", getTheme()));
    }

    @Test
    public void immediateButtonWithPixelWidth() {
        assertThat(getButtonWidth("upload1"), is(300.0));
    }

    @Test
    public void immediateButtonWithPercentageWidth() {
        assertThat(getButtonWidth("upload2"), is(250.0));
    }
}
