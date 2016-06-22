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
package com.vaadin.tests.components.accordion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Accordion: Tab.setId should be propagated to client side tabs.
 * 
 * @author Vaadin Ltd
 */
public class AccordionTabIdsTest extends MultiBrowserTest {

    @Test
    public void testGeTabByIds() {
        openTestURL();
        ButtonElement setIdButton = $(ButtonElement.class).first();
        ButtonElement clearIdbutton = $(ButtonElement.class).get(1);

        WebElement firstItem = driver
                .findElement(By.id(AccordionTabIds.FIRST_TAB_ID));
        WebElement label = $(LabelElement.class).context(firstItem).first();
        assertEquals(AccordionTabIds.FIRST_TAB_MESSAGE, label.getText());

        clearIdbutton.click();
        assertEquals("", firstItem.getAttribute("id"));

        setIdButton.click();
        assertEquals(AccordionTabIds.FIRST_TAB_ID,
                firstItem.getAttribute("id"));
    }
}
