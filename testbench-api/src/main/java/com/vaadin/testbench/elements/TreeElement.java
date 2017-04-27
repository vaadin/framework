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
package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

// TODO: Switch to com.vaadin.ui.Tree once inheritance finding is fixed
@ServerClass("com.vaadin.ui.Composite")
public class TreeElement extends AbstractComponentElement {

    public void expand(int index) {
        asTreeGrid().expandWithClick(index);
    }

    public void collapse(int index) {
        asTreeGrid().collapseWithClick(index);
    }

    public boolean isExpanded(int index) {
        return asTreeGrid().isRowExpanded(index, 0);
    }

    public boolean isCollapsed(int index) {
        return !isExpanded(index);
    }

    public List<TestBenchElement> getAllItems() {
        return TestBenchElement.wrapElements(
                asTreeGrid().getBody().findElements(By.tagName("tr")),
                getCommandExecutor());
    }

    public TestBenchElement getItem(int index) {
        return asTreeGrid().getCell(index, 0);
    }

    protected TreeGridElement asTreeGrid() {
        return wrap(TreeGridElement.class);
    }
}
