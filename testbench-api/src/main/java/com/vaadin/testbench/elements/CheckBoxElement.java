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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.CheckBox")
public class CheckBoxElement extends AbstractFieldElement {

    /**
     * Return string representation of value of the checkbox Return either
     * checked or unchecked
     */
    public String getValue() {
        if (isChecked()) {
            return "checked";
        } else {
            return "unchecked";
        }
    }

    /**
     * Checks if the checkbox is checked.
     *
     * @return <code>true</code> if the checkbox is checked, <code>false</code>
     *         otherwise.
     */
    public boolean isChecked() {
        return getInputElement().isSelected();
    }

    /**
     * Clears the check box, setting unchecked value. The check box is unchecked
     * by sending a click event on it.
     *
     */
    @Override
    public void clear() {
        if (isChecked()) {
            click();
        }
    }

    @Override
    public String getCaption() {
        WebElement elem = findElement(By.xpath(".."))
                .findElement(By.tagName("label"));
        return elem.getText();
    }

    @Override
    public void click() {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        WebElement input = getInputElement();
        if (isFirefox()) {
            // When using Valo, the input element is covered by a
            // pseudo-element, which Firefox will complain about
            getCommandExecutor().executeScript("arguments[0].click()", input);
        } else if (isChrome()) {
            ((TestBenchElementCommands) (input)).click(0, 0);
        } else {
            input.click();
        }
    }

    /**
     * Gets the &lt;input&gt; element of the checkbox.
     *
     * @return the input element
     */
    public WebElement getInputElement() {
        return findElement(By.tagName("input"));
    }
}
