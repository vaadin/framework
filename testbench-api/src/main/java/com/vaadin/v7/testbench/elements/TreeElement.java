/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.testbench.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.AbstractSelectElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Tree")
@Deprecated
public class TreeElement extends AbstractSelectElement {
    /**
     * Returns selected item of the tree. In multiselect mode returns first
     * selected item. If there is no selected item returns empty string
     *
     * @return selected item of the tree
     */
    public String getValue() {
        List<WebElement> selectedElements = findElements(
                By.className("v-tree-node-selected"));
        if (selectedElements.isEmpty()) {
            return "";
        } else {
            return selectedElements.get(0).getText();
        }
    }
}
