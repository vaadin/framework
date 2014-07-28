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
package com.vaadin.tests.components.abstractcomponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

import java.util.List;

import com.vaadin.testbench.elements.ButtonElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PrimaryStyleTest extends MultiBrowserTest {

    @Test
    public void testStyleNames() {
        openTestURL();

        // Verify the initial class names for all three components.
        List<WebElement> initialElements = driver.findElements(By
                .className("initial-state"));
        assertThat(initialElements, hasSize(3));

        // Click on a button that updates the styles.
        $(ButtonElement.class).id("update-button").click();

        // Verify that the class names where updated as expected.
        List<WebElement> updatedElements = driver.findElements(By
                .className("updated-correctly"));
        assertThat(updatedElements, hasSize(initialElements.size()));

    }

}
