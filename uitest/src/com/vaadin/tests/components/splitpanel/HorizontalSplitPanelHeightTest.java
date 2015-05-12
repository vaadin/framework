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
package com.vaadin.tests.components.splitpanel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for horizontal split panel height in case when only second component is
 * set.
 * 
 * @author Vaadin Ltd
 */
public class HorizontalSplitPanelHeightTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void testHorizontalWithoutFirstComponent() {
        testSplitPanel("Horizontal 1");
    }

    @Test
    public void testHorizontalWithFirstComponent() {
        testSplitPanel("Horizontal 2");
    }

    @Test
    public void testHorizontalWithFixedHeight() {
        testSplitPanel("Horizontal 3");
    }

    @Test
    public void testVerticalWithoutFirstComponent() {
        testSplitPanel("Vertical 1");
    }

    private void testSplitPanel(String id) {
        WebElement splitPanel = findElement(By.id(id));
        WebElement label = splitPanel.findElement(By.className("target"));
        Assert.assertTrue(id + ": split panel height ("
                + splitPanel.getSize().getHeight() + ") is less than "
                + "height of second component (" + label.getSize().getHeight()
                + ")", splitPanel.getSize().getHeight() >= label.getSize()
                .getHeight());
        Assert.assertEquals("Label text in the second panel is not visible",
                "Label", label.getText());
    }
}
