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
package com.vaadin.tests.components.richtextarea;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RichTextAreaRelativeHeightResizeTest extends MultiBrowserTest {

    @Test
    public void testCenteredClosingAndPostLayout() {
        openTestURL();

        int originalHeight = driver
                .findElement(By.cssSelector(".v-richtextarea")).getSize()
                .getHeight();
        int originalEditorHeight = driver
                .findElement(By.cssSelector(".v-richtextarea iframe"))
                .getSize().getHeight();

        // Increase the component height
        driver.findElement(By.cssSelector(".v-button")).click();

        int newHeight = driver.findElement(By.cssSelector(".v-richtextarea"))
                .getSize().getHeight();
        int newEditorHeight = driver
                .findElement(By.cssSelector(".v-richtextarea iframe"))
                .getSize().getHeight();

        // Check that the component height changed and that the editor height
        // changed equally as much
        Assert.assertTrue("RichTextArea height didn't change",
                newHeight != originalHeight);
        Assert.assertEquals(
                "Editor height change didn't match the Component height change",
                newHeight - originalHeight, newEditorHeight
                        - originalEditorHeight);
    }
}
