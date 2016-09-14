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
package com.vaadin.testbench.customelements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractSelectElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * TestBench element supporting CheckBoxGroup
 *
 * @author Vaadin Ltd
 */

@ServerClass("com.vaadin.ui.CheckBoxGroup")
public class CheckBoxGroupElement extends AbstractSelectElement {
    private static org.openqa.selenium.By byButtonSpan = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byInput = By.tagName("input");

    public List<String> getOptions() {
        List<String> optionTexts = new ArrayList<>();
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            optionTexts.add(option.findElement(byLabel).getText());
        }
        return optionTexts;
    }

    public List<String> getOptionsCssClasses() {
        List<String> optionTexts = new ArrayList<>();
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            optionTexts.add(option.getAttribute("class"));
        }
        return optionTexts;
    }

    public List<String> getOptionsIconUrls() {
        List<String> icons = new ArrayList<>();
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            List<WebElement> images = option.findElements(By.tagName("img"));
            if (images != null && images.size() > 0) {
                icons.add(images.get(0).getAttribute("src"));
            } else {
                icons.add(null);
            }

        }
        return icons;
    }

    public void selectByText(String text) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        List<WebElement> options = findElements(byButtonSpan);
        for (int i = 0; i < options.size(); i++) {
            WebElement option = options.get(i);
            if (text.equals(option.findElement(byLabel).getText())) {
                option.findElement(byInput).click();

                // Seems like this is needed because of #19753
                waitForVaadin();

                // Toggling selection causes the DOM to be rebuilt, so fetch new
                // items and continue iterating from the same index
                options = findElements(byButtonSpan);
            }
        }
    }

    /**
     * Return list of the selected options in the checkbox group
     *
     * @return list of the selected options in the checkbox group
     */
    public List<String> getSelection() {
        List<String> values = new ArrayList<>();
        List<WebElement> options = findElements(byButtonSpan);
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
     * Select option in the checkbox group with the specified value
     *
     * @param chars
     *            value of the option in the checkbox group which will be
     *            selected
     */
    public void selectOption(CharSequence chars) throws ReadOnlyException {
        selectByText((String) chars);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(
                "Clear operation is not supported for CheckBoxGroup."
                        + " This operation has no effect on the element.");
    }

}
