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
package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class BarInUIDLTest extends MultiBrowserTest {
    @Test
    public void sendBarInUIDL() {
        openTestURL();
        getButton().click();
        Assert.assertEquals(
                "Thank you for clicking | bar",
                vaadinElement(
                        "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]")
                        .getText());
        getButton().click();
        Assert.assertEquals(
                "Thank you for clicking | bar",
                vaadinElement(
                        "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VLabel[0]")
                        .getText());
    }

    private WebElement getButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VButton[0]");
    }
}
