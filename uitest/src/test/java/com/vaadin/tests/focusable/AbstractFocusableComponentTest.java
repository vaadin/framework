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
package com.vaadin.tests.focusable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class AbstractFocusableComponentTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testProgrammaticFocus() {
        selectMenuPath("Component", "State", "Set focus");
        assertTrue("Component should be focused", isFocused());
    }

    @Test
    public void testTabIndex() {
        assertEquals("0", getTabIndex());

        selectMenuPath("Component", "State", "Tab index", "-1");
        assertEquals("-1", getTabIndex());

        selectMenuPath("Component", "State", "Tab index", "10");
        assertEquals("10", getTabIndex());
    }

    protected String getTabIndex() {
        return getFocusElement().getAttribute("tabindex");
    }

    protected boolean isFocused() {
        return getFocusElement().equals(getDriver().switchTo().activeElement());
    }

    protected abstract WebElement getFocusElement();
}
