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
package com.vaadin.tests.components.gridlayout;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for TOP_CENTER and TOP_RIGHT alignments in VerticalLayout.
 * 
 * @author Vaadin Ltd
 */
public class ComponentAlignmentsTest extends MultiBrowserTest {

    @Test
    public void testTopCenterAlignment() {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        WebElement parent = checkbox.findElement(By.xpath(".."));

        int leftSpaceSize = checkbox.getLocation().getX()
                - parent.getLocation().getX();
        int rightSpaceSize = parent.getLocation().getX()
                + parent.getSize().getWidth() - checkbox.getLocation().getX()
                - checkbox.getSize().getWidth();
        Assert.assertTrue("No space on the left for centered element",
                leftSpaceSize > 0);
        Assert.assertTrue("No space on the right for centered element",
                rightSpaceSize > 0);

        int diff = Math.abs(rightSpaceSize - leftSpaceSize);
        Assert.assertTrue("Element is not in the center, diff:" + diff,
                diff <= 2); // IE11 2pixels
    }

    @Test
    public void testTopRightAlignment() {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).get(1);
        WebElement parent = checkbox.findElement(By.xpath(".."));

        int leftSpaceSize = checkbox.getLocation().getX()
                - parent.getLocation().getX();
        int rightSpaceSize = parent.getLocation().getX()
                + parent.getSize().getWidth() - checkbox.getLocation().getX()
                - checkbox.getSize().getWidth();
        Assert.assertTrue("No space on the left for centered element",
                leftSpaceSize > 0);
        Assert.assertTrue("There is some space on the right for the element",
                rightSpaceSize <= 1);

        int sizeDiff = parent.getSize().getWidth()
                - checkbox.getSize().getWidth();
        Assert.assertTrue("Element is not in aligned to the right",
                Math.abs(sizeDiff - leftSpaceSize) <= 1);
    }
}
