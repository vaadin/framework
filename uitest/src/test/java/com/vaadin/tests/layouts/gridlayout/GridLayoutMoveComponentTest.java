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
package com.vaadin.tests.layouts.gridlayout;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutMoveComponentTest extends MultiBrowserTest {

    @Test
    public void componentsShouldMoveRight() throws IOException {
        openTestURL();

        compareScreen("all-left");

        clickButtonWithCaption("Shift label right");
        compareScreen("label-right");

        clickButtonWithCaption("Shift button right");
        compareScreen("label-button-right");

        clickButtonWithCaption("Shift text field right");
        compareScreen("label-button-textfield-right");
    }

    private void clickButtonWithCaption(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

}
