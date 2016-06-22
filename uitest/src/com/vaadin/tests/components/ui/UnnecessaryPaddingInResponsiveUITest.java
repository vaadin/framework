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
package com.vaadin.tests.components.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class UnnecessaryPaddingInResponsiveUITest extends SingleBrowserTest {

    @Test
    public void testUIShouldHaveNoPaddingTop() {
        openTestURL();

        WebElement ui = vaadinElementById("UI");

        String paddingTop = ui.getCssValue("padding-top");

        Integer paddingHeight = Integer.parseInt(paddingTop.substring(0,
                paddingTop.length() - 2));

        assertThat(paddingHeight, equalTo(0));

    }
}
