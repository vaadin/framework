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
package com.vaadin.tests.elements.link;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LinkUITest extends MultiBrowserTest {
    LinkElement link;

    @Before
    public void init() {
        openTestURL();
        link = $(LinkElement.class).first();
    }

    @Test
    public void testLinkClick() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(
                "Current URL " + currentUrl + " should end with LinkUI?",
                currentUrl.endsWith("LinkUI"));
        link.click();
        currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                "Current URL " + currentUrl + " should not end with LinkUI?",
                currentUrl.endsWith("LinkUI"));

    }

    @Test
    public void getLinkCaption() {
        Assert.assertEquals("server root", link.getCaption());
    }

}
