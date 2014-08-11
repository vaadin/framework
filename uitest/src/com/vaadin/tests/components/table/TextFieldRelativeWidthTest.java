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
package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a TextField with 100% width fills the expanded column.
 * 
 * @author Vaadin Ltd
 */
public class TextFieldRelativeWidthTest extends MultiBrowserTest {

    @Test
    public void testWidth() throws IOException {
        openTestURL();

        compareScreen("initial");

        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        button.click();

        compareScreen("after");
    }

}
