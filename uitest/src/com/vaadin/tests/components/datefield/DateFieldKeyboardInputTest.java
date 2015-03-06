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
package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldKeyboardInputTest extends MultiBrowserTest {

    @Test
    public void testValueChangeEvent() {
        openTestURL();
        WebElement dateFieldText = $(DateFieldElement.class).first()
                .findElement(By.tagName("input"));
        dateFieldText.clear();
        int numLabelsBeforeUpdate = $(LabelElement.class).all().size();
        dateFieldText.sendKeys("20.10.2013", Keys.RETURN);
        int numLabelsAfterUpdate = $(LabelElement.class).all().size();
        assertTrue("Changing the date failed.",
                numLabelsAfterUpdate == numLabelsBeforeUpdate + 1);
    }
}