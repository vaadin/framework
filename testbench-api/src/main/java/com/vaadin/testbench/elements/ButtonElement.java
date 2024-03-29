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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Button")
public class ButtonElement extends AbstractComponentElement {

    @Override
    public String getCaption() {
        WebElement captElem = findElement(By.className("v-button-caption"));
        return captElem.getText();
    }

    private boolean tryClickChild(WebElement e) {
        List<WebElement> children = e.findElements(By.xpath(".//*"));
        for (WebElement c : children) {
            if (c.isDisplayed()) {
                c.click();
                return true;
            } else {
                if (tryClickChild(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void click() {
        if (!isDisplayed()) {
            if (tryClickChild(this)) {
                return;
            }
        }

        super.click();
    }
}
