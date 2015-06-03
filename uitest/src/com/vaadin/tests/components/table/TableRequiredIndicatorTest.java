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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Checks that Table that has required flag set to true is also indicated as
 * such on the client side.
 * 
 * @author Vaadin Ltd
 */
public class TableRequiredIndicatorTest extends MultiBrowserTest {

    @Test
    public void testRequiredIndicatorIsVisible() {
        openTestURL();
        Assert.assertTrue(isElementPresent(By
                .className("v-required-field-indicator")));
    }

}
