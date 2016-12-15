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

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.DateField")
public class DateFieldElement extends AbstractFieldElement {

    /**
     * Clear DateField element
     */
    @Override
    public void clear() {
        WebElement elem = findElement(By.tagName("input"));
        elem.clear();
    }

    /**
     * Return value of the date field element
     *
     * @return value of the date field element
     */
    public String getValue() {
        return findElement(By.tagName("input")).getAttribute("value");
    }

    /**
     * Set value of the date field element
     *
     * @param chars
     *            new value of the date field
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        WebElement elem = findElement(By.tagName("input"));
        TestBenchElement tbElement = (TestBenchElement) elem;
        clearElementClientSide(tbElement);
        tbElement.sendKeys(chars);
        tbElement.sendKeys(Keys.TAB);
    }
}
