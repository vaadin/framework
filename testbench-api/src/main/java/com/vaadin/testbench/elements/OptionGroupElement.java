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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.OptionGroup")
@Deprecated
public class OptionGroupElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySelectOption = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byRadioInput = By.tagName("input");

    public List<String> getOptions() {
        List<String> optionTexts = new ArrayList<String>();
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            optionTexts.add(option.findElement(byLabel).getText());
        }
        return optionTexts;
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
     * Return value of the selected option in the option group
     *
     * @return value of the selected option in the option group
     */
    public String getValue() {
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            WebElement checkedItem;
            checkedItem = option.findElement(By.tagName("input"));
            String checked = checkedItem.getAttribute("checked");
            if (checked != null
                    && checkedItem.getAttribute("checked").equals("true")) {
                return option.findElement(By.tagName("label")).getText();
            }
        }
        return null;
    }

    /**
     * Select option in the option group with the specified value
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
