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
package com.vaadin.tests.components.label;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LabelTooltipTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testLabelTooltip() throws IOException {
        openTestURL();
        assertTooltips();
    }

    @Test
    public void testLabelToolTipChameleonTheme() throws IOException {
        openTestURL("theme=chameleon");
        assertTooltips();
    }

    @Test
    public void testLabelToolTipRunoTheme() throws IOException {
        openTestURL("theme=runo");
        assertTooltips();
    }

    private void assertTooltips() throws IOException {
        $(LabelElement.class).get(2).showTooltip();
        Assert.assertEquals("Default tooltip content", getTooltipElement()
                .getText());

        /* Some cases tooltip doesn't disappear without some extra mouse events */
        new Actions(getDriver()).moveByOffset(100, -40).perform();
        new Actions(getDriver()).moveToElement($(LabelElement.class).get(1))
                .click().perform();

        $(LabelElement.class).get(4).showTooltip();
        Assert.assertEquals(
                "Error inside tooltip together with the regular tooltip message.",
                getTooltipErrorElement().getText());
        Assert.assertEquals("Default tooltip content", getTooltipElement()
                .getText());

        /* Visual comparison */
        compareScreen("tooltipVisible");
    }
}
