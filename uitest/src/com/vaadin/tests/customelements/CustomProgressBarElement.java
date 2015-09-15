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
package com.vaadin.tests.customelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ProgressBar")
public class CustomProgressBarElement extends ProgressBarElement {

    public double getValue() {
        WebElement indicator = findElement(By
                .className("v-progressbar-indicator"));
        String width = getStyleAttribute(indicator, "width");
        if (!width.endsWith("%")) {
            return 0;
        }

        return Double.parseDouble(width.replace("%", "")) / 100.0;
    }

    /**
     * @since 7.5.6
     * @param indicator
     * @param string
     * @return
     */
    private String getStyleAttribute(WebElement element, String styleName) {
        String style = element.getAttribute("style");
        String[] styles = style.split(";");
        for (String s : styles) {
            if (s.startsWith(styleName + ":")) {
                return s.substring(styleName.length() + 1).trim();
            }
        }

        return null;
    }

}
