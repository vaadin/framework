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

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.components.colorpicker.ColorPickerPreview")
public class ColorPickerPreviewElement extends CssLayoutElement {

    /**
     * Get whether TextField in ColorPickerPreview has validation errors.
     *
     * @return true if field has errors, false otherwise
     *
     * @since 8.4
     */
    public boolean getColorFieldContainsErrors() {
        List<WebElement> caption = findElements(
                By.className("v-caption-v-colorpicker-preview-textfield"));
        return !caption.isEmpty() && !caption.get(0)
                .findElements(By.className("v-errorindicator")).isEmpty();
    }

    /**
     * Get the value of the input element TextField in ColorPickerPreview.
     *
     * @return the value of the attribute 'value' of the input element
     *
     * @since 8.4
     */
    public String getColorFieldValue() {
        return getColorTextField().getAttribute("value");
    }

    /**
     * Set value of TextField in ColorPickerPreview. Any existing value in the
     * field is replaced.
     *
     * @param value
     *            text to insert
     *
     * @since 8.4
     */
    public void setColorTextFieldValue(String value) {
        // Select all text
        getColorTextField().sendKeys(Keys.chord(Keys.CONTROL, "a"));
        getColorTextField().sendKeys(value);
    }

    /**
     * @return <code>WebElement</code> representing TextField in
     *         ColorPickerPreviewComponent
     * 
     * @since 8.4
     */
    public WebElement getColorTextField() {
        return findElement(By.className("v-colorpicker-preview-textfield"));
    }
}
