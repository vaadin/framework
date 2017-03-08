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
package com.vaadin.tests.themes.valo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowTitleOverflowTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    private void openWindow(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private String getWindowHeaderMarginRight() {
        return getWindowHeader().getCssValue("margin-right");
    }

    private WebElement getWindowHeader() {
        return findElement(By.className("v-window-header"));
    }

    @Test
    public void headerMarginIsCorrectForResizable() {
        openWindow("Open Resizable");

        assertThat(getWindowHeaderMarginRight(), is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForClosable() {
        openWindow("Open Closable");

        assertThat(getWindowHeaderMarginRight(), is("37px"));
    }

    @Test
    public void headerMarginIsCorrectForResizableAndClosable() {
        openWindow("Open Resizable and Closable");

        assertThat(getWindowHeaderMarginRight(), is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForNonResizableAndNonClosable() {
        openWindow("Open Non-Resizable and Non-Closable");

        assertThat(getWindowHeaderMarginRight(), is("12px"));
    }
}
