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

import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.NativeSelect")
public class NativeSelectElement extends AbstractSelectElement {
    private Select selectElement;

    @Override
    protected void init() {
        super.init();
        selectElement = new Select(findElement(By.tagName("select")));
    }

    public List<TestBenchElement> getOptions() {
        return wrapElements(selectElement.getOptions(), getCommandExecutor());
    }

    public void selectByText(String text) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        selectElement.selectByVisibleText(text);
        waitForVaadin();
    }

    /**
     * Clear operation is not supported for Native Select. This operation has no
     * effect on Native Select element.
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * Return value of the selected item in the native select element
     *
     * @return value of the selected item in the native select element
     */
    public String getValue() {
        return selectElement.getFirstSelectedOption().getText();
    }

    /**
     * Select item of the native select element with the specified value
     *
     * @param chars
     *            value of the native select item will be selected
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        selectByText((String) chars);
    }
}
