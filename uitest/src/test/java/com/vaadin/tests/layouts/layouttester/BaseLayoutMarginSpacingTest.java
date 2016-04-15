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
package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class BaseLayoutMarginSpacingTest extends MultiBrowserTest {

    @Test
    public void LayoutMarginSpacing() throws IOException, InterruptedException {
        openTestURL();
        sleep(500);
        compareScreen("initial");
        String[] states = { "marginOnSpaceOff", "marginOnfSpaceOn" };
        ButtonElement marginBtn = $(ButtonElement.class).get(0);
        ButtonElement spaceBtn = $(ButtonElement.class).get(1);
        marginBtn.click();
        sleep(1000);
        compareScreen(states[0]);
        spaceBtn.click();
        sleep(1000);
        compareScreen(states[1]);
    }
}