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
package com.vaadin.tests.components.draganddropwrapper;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DragStartModesTest extends MultiBrowserTest {

    @Test
    public void testDragStartModes() throws IOException {
        openTestURL();
        WebElement dropTarget = vaadinElement("/VVerticalLayout[0]/VVerticalLayout[0]/VLabel[0]");
        dragToTarget("COMPONENT", dropTarget);
        dragToTarget("WRAPPER", dropTarget);
        dragToTarget("COMPONENT_OTHER", dropTarget);
    }

    private void dragToTarget(String dragMode, WebElement dropTarget)
            throws IOException {
        WebElement draggable = vaadinElementById("label" + dragMode);
        new Actions(driver).moveToElement(draggable, 10, 10).clickAndHold()
                .moveByOffset(5, 0).perform();
        new Actions(driver).moveToElement(dropTarget, 12, 10).perform();
        compareScreen("dragImageMode" + dragMode);
        new Actions(driver).release().perform();
    }

}
