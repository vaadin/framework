/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.testbench.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Element class for testing InlineDateField.
 */
@ServerClass("com.vaadin.ui.InlineDateField")
public class InlineDateFieldElement extends AbstractFieldElement {

    /**
     * Returns the element which receives focus when the component is focused.
     *
     * @return the element which receives focus when the component is focused
     * @since 8.1.1
     */
    public WebElement getFocusElement() {
        return findElement(By.tagName("table"));

    }
}
