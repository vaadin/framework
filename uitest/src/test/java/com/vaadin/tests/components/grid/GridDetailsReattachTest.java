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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDetailsReattachTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void clickToAddCaption() {
        openTestURL();
        Assert.assertTrue("Grid details don't exist",  hasDetailsElement());
        $(ButtonElement.class).first().click();
        Assert.assertTrue("Grid details don't exist after deattach and reattach",hasDetailsElement() );
    }

    private final By locator = By.className("v-grid-spacer");

    private boolean hasDetailsElement() {
        return !findElements(locator).isEmpty();
    }

}
