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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Slider")
public class SliderElement extends AbstractFieldElement {
    /**
     * Get value of the slider
     * 
     * Warning! This method cause slider popup to appear on the screen. To hide
     * this popup just focus any other element on the page.
     */
    public String getValue() {
        List<WebElement> popupElems = findElements(By.vaadin("#popup"));
        // SubPartAware was implemented after 7.2.6, not sure in which release
        // it will be included
        if (popupElems.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Current version of vaadin doesn't support geting values from sliderElement");

        }
        WebElement popupElem = popupElems.get(0);

        if (BrowserType.IE.equals(getCapabilities().getBrowserName())
                && "8".equals(getCapabilities().getVersion())) {
            return popupElem.getAttribute("innerText");
        } else {
            return popupElem.getAttribute("textContent");
        }

    }

    public WebElement getHandle() {
        return findElement(By.className("v-slider-handle"));
    }
}
