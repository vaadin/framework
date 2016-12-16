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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Deprecated
public class TableHeaderElement extends AbstractComponentElement {

    @Override
    public String getCaption() {
        WebElement captionElement = findElement(
                By.className("v-table-caption-container"));
        return captionElement.getText();
    }

    /**
     * Returns column resize handle.
     *
     * You can resize column by using selenium Actions i.e. new
     * Actions(getDriver()).clickAndHold(handle).moveByOffset(x,
     * y).release().build().perform();
     *
     * @return column resize handle
     */
    public WebElement getResizeHandle() {
        return findElement(By.className("v-table-resizer"));
    }
}
