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
package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.components.FocusTest;

public class CheckBoxGroupFocusTest extends FocusTest {

    @Test
    public void focusOnInit() {
        openTestURL();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .first();
        assertTrue(isFocusInsideElement(checkBoxGroup));
    }

    @Test
    public void moveFocusAfterClick() {
        openTestURL();
        $(ButtonElement.class).first().click();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .last();
        assertTrue(isFocusInsideElement(checkBoxGroup));
    }

}
