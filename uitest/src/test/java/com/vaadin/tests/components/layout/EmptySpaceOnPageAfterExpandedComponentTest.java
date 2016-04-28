/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.layout;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to make sure that there is no any empty space (in Google Chrome) on page
 * after expanded component (#12672)
 * 
 * Layout:
 * 
 * [ Panel (auto x auto) [ Grid (auto x auto) ]
 * 
 * AnyComponent (100% x 100%)
 * 
 * <HERE SHOULD NOT BE ANY EMPTY SPACE> ]
 * 
 * @author Vaadin Ltd
 */
public class EmptySpaceOnPageAfterExpandedComponentTest extends
        MultiBrowserTest {

    @Test
    public void testNoEmptySpaceOnPageAfterExpandedComponent() {
        openTestURL();

        final WebElement expandedElement = vaadinElementById("expandedElement");
        final WebElement containerElement = vaadinElementById("container");

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                int expandedElementBottom = expandedElement.getLocation()
                        .getY() + expandedElement.getSize().getHeight();
                int containerElementBottom = containerElement.getLocation()
                        .getY() + containerElement.getSize().getHeight();

                return expandedElementBottom + 1 == containerElementBottom;
            }
        });
    }
}
