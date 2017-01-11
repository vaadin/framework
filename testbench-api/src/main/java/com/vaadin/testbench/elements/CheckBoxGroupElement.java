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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.CheckBoxGroup")
public class CheckBoxGroupElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySelectOption = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byRadioInput = By.tagName("input");

    public List<String> getOptions() {
        return getOptionElements().stream().map(
                option -> option.findElement(byLabel).getText())
                .collect(Collectors.toList());
    }

    /**
     * Gets the list of option elements for this check box group.
     *
     * @return list of option elements
     */
    public List<WebElement> getOptionElements() {
        return findElements(bySelectOption);
    }

    public void selectByText(String text) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            if (text.equals(option.findElement(byLabel).getText())) {
                WebElement input = option.findElement(byRadioInput);
                ((TestBenchElement) (input)).clickHiddenElement();
            }
        }
    }

    /**
     * Return list of the selected options in the checkbox group.
     *
     * @return list of the selected options in the checkbox group
     */
    public List<String> getSelection() {
        List<String> values = new ArrayList<>();
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            WebElement checkedItem;
            checkedItem = option.findElement(By.tagName("input"));
            String checked = checkedItem.getAttribute("checked");
            if (checked != null
                    && checkedItem.getAttribute("checked").equals("true")) {
                values.add(option.findElement(By.tagName("label")).getText());
            }
        }
        return values;
    }

    /**
     * Select option in the option group with the specified value.
     *
     * @param chars
     *            value of the option in the option group which will be selected
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        selectByText((String) chars);
    }

    /**
     * Clear operation is not supported for Option Group. This operation has no
     * effect on Option Group element.
     */
    @Override
    public void clear() {
        super.clear();
    }
}
