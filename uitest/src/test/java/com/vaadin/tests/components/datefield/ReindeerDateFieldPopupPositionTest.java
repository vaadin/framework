/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;

/**
 * Test for date field popup calendar position in reindeer theme.
 *
 * Test method is defined in super class.
 *
 * @author Vaadin Ltd
 */
public class ReindeerDateFieldPopupPositionTest
        extends DateFieldPopupPositionTest {

    @Override
    protected void checkPopupPosition() {
        DateFieldElement field = $(DateFieldElement.class).first();
        int right = field.getLocation().getX() + field.getSize().getWidth();
        WebElement popup = getPopup();

        assertTrue(
                "Calendar popup has wrong X coordinate="
                        + popup.getLocation().getX()
                        + " , right side of the field is " + right,
                popup.getLocation().getX() >= right);
    }
}
