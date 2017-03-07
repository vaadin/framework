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
package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UITabIndexTest extends MultiBrowserTest {

    @Test
    public void testTabIndexOnUIRoot() throws Exception {
        openTestURL();
        assertTabIndex("1");
        $(ButtonElement.class).first().click();
        assertTabIndex("-1");
        $(ButtonElement.class).get(1).click();
        assertTabIndex("0");
        $(ButtonElement.class).get(2).click();
        assertTabIndex("1");
    }

    private void assertTabIndex(String expected) {
        Assert.assertEquals("Unexpected tab index,", expected,
                $(UIElement.class).first().getAttribute("tabIndex"));
    }
}
