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
package com.vaadin.tests.components.embedded;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmbeddedAltTextTest extends MultiBrowserTest {

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-embedded"));
    }

    @Test
    public void testEmbeddedAltText() {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();

        Assert.assertEquals("Alt text of the image", getAltText(embedded));
        assertHtmlSource("Alt text of the object");

        $(ButtonElement.class).first().click();

        Assert.assertEquals("New alt text of the image!", getAltText(embedded));
        assertHtmlSource("New alt text of the object!");
    }

    private void assertHtmlSource(String html) {
        String pageSource = driver.getPageSource();
        Assert.assertTrue("Page source does not contain '" + html + "'",
                pageSource.contains(html));
    }

    private String getAltText(EmbeddedElement embedded) {
        return embedded.findElement(By.vaadin("/domChild[0]")).getAttribute(
                "alt");
    }
}
