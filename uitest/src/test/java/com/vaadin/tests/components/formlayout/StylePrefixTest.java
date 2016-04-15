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
package com.vaadin.tests.components.formlayout;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for FormLayout style prefix: custom additional styles should be prefixed
 * with "v-formlayout-", not "v-layout-".
 * 
 * @author Vaadin Ltd
 */
public class StylePrefixTest extends MultiBrowserTest {

    @Test
    public void testStylePrefix() {
        openTestURL();

        Assert.assertTrue("Custom style has unexpected prefix",
                isElementPresent(By.className("v-formlayout-mystyle")));
    }

}
