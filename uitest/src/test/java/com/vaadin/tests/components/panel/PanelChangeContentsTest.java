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
package com.vaadin.tests.components.panel;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PanelChangeContentsTest extends MultiBrowserTest {

    @Test
    public void testReattachComponentUsingPush() {
        setPush(true);
        openTestURL();

        Assert.assertEquals(
                "stats",
                vaadinElement(
                        "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());
        vaadinElement(
                "/VVerticalLayout[0]/Slot[0]/VHorizontalLayout[0]/Slot[1]/VButton[0]/domChild[0]/domChild[0]")
                .click();
        Assert.assertEquals(
                "companies",
                vaadinElement(
                        "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());
        vaadinElement(
                "/VVerticalLayout[0]/Slot[0]/VHorizontalLayout[0]/Slot[0]/VButton[0]/domChild[0]/domChild[0]")
                .click();
        Assert.assertEquals(
                "stats",
                vaadinElement(
                        "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());

    }
}
