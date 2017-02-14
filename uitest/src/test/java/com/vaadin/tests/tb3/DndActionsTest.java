/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.tests.tb3;

import org.junit.Ignore;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Base class for TestBench 3+ tests that use DnD. This class contains utility
 * methods for DnD operations.
 *
 * @author Vaadin Ltd
 */
@Ignore
public abstract class DndActionsTest extends MultiBrowserTest {

    public void dragAndDrop(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(getDriver());
        action.dragAndDropBy(element, xOffset, yOffset);
        action.build().perform();
    }

    public void dragAndDrop(WebElement element, WebElement target) {
        Actions action = new Actions(getDriver());
        action.dragAndDrop(element, target);
        action.build().perform();
    }
}
