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
package com.vaadin.tests.tooltip;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * 
 * 
 * @author Vaadin Ltd
 */
public class DragAndDropWrapperTooltipsTest extends TooltipTest {
    @Test
    public void testDragAndDropTooltips() throws Exception {
        openTestURL();
        LabelElement element = $(LabelElement.class).get(4);
        LabelElement targetElement = $(LabelElement.class).get(1);
        checkTooltip(element,
                "Tooltip for the wrapper wrapping all the draggable layouts");
        new Actions(getDriver()).clickAndHold(element)
                .moveToElement(targetElement).perform();
        sleep(500);
        checkTooltipNotPresent();
        new Actions(getDriver()).release().perform();
        checkTooltip(element, "Drag was performed and tooltip was changed");
    }
}
