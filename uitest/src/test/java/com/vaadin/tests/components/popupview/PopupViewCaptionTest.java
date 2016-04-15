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
package com.vaadin.tests.components.popupview;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class PopupViewCaptionTest extends MultiBrowserTest {

    @Test
    public void testCaption() {
        openTestURL();

        WebElement caption = driver.findElement(By.className("v-caption"));
        Assert.assertNotNull(caption);

        List<WebElement> elements = caption.findElements(By.xpath("*"));

        boolean foundCaptionText = false;
        for (WebElement element : elements) {
            if ("Popup Caption:".equals(element.getText())) {
                foundCaptionText = true;
                break;
            }
        }
        Assert.assertTrue("Unable to find caption text", foundCaptionText);
    }

}
