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
package com.vaadin.tests.components;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if AbstractOrderedLayout displays captions correctly with
 * expanding ratios.
 * 
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutWithCaptionsTest extends MultiBrowserTest {

    @Test
    public void CaptionHeightMeasuredCorrectly() {
        openTestURL();

        WebElement div = getDriver().findElement(
                By.cssSelector(".v-panel-content > div > div"));
        String paddingTop = div.getCssValue("padding-top");
        Integer paddingHeight = Integer.parseInt(paddingTop.substring(0,
                paddingTop.length() - 2));
        List<WebElement> children = getDriver().findElements(
                By.cssSelector(".v-panel-content .v-slot"));
        assertThat(children.size(), is(3));

        Integer neededHeight = children.get(0).getSize().getHeight()
                + children.get(2).getSize().getHeight();

        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 Reports the first element height incorrectly.
            --neededHeight;
        }
        assertThat(neededHeight, is(lessThanOrEqualTo(paddingHeight)));

    }
}
