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
package com.vaadin.tests.components.colorpicker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for default caption behavior in color picker.
 * 
 * @author Vaadin Ltd
 */
public class DefaultCaptionWidthTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void setDefaultCaption_sizeAndCaptionAreNotSet_pickerGetsStyle() {
        checkStylePresence(true);
    }

    @Test
    public void setDefaultCaption_explicitSizeIsSet_pickerNoCaptionStyle() {
        findElement(By.className("set-width")).click();
        checkStylePresence(false);
    }

    @Test
    public void setDefaultCaption_explicitCaptionIsSet_pickerNoCaptionStyle() {
        findElement(By.className("set-caption")).click();
        checkStylePresence(false);
    }

    protected void checkStylePresence(boolean expectedStyle) {
        String clazz = $(ColorPickerElement.class).first()
                .getAttribute("class");
        if (expectedStyle) {
            Assert.assertTrue("Default caption style is not found",
                    clazz.contains("v-default-caption-width"));
        } else {
            Assert.assertFalse("Found unexpected default caption style",
                    clazz.contains("v-default-caption-width"));
        }
    }

}
