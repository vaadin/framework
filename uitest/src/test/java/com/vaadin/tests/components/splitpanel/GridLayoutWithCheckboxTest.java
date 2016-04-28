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
package com.vaadin.tests.components.splitpanel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutWithCheckboxTest extends MultiBrowserTest {

    private TextFieldElement tf;
    private WebElement tfSlot;
    private CheckBoxElement cb;
    private WebElement cbSlot;
    private Dimension tfSize;
    private Dimension tfSlotSize;
    private Dimension cbSize;
    private Dimension cbSlotSize;

    @Test
    public void layoutShouldStayTheSame() {
        openTestURL();
        tf = $(TextFieldElement.class).first();
        tfSlot = tf.findElement(By.xpath(".."));
        cb = $(CheckBoxElement.class).first();
        cbSlot = cb.findElement(By.xpath(".."));

        // Doing anything with the textfield or checkbox should not affect
        // layout

        tf.setValue("a");
        assertSizes();
        cb.click();
        assertSizes();
        tf.setValue("b");
        assertSizes();
        cb.click();
        assertSizes();

    }

    private void assertSizes() {
        if (tfSize == null) {
            tfSize = tf.getSize();
            tfSlotSize = tfSlot.getSize();
            cbSize = cb.getSize();
            cbSlotSize = cbSlot.getSize();
        } else {
            Assert.assertEquals(tfSize, tf.getSize());
            Assert.assertEquals(tfSlotSize, tfSlot.getSize());
            Assert.assertEquals(cbSize, cb.getSize());
            Assert.assertEquals(cbSlotSize, cbSlot.getSize());
        }

    }
}
