/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Locale;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractComponent")
public class AbstractComponentElement extends AbstractElement {

    /**
     * Returns the caption of the Component element.
     *
     * @since 8.0
     * @return component caption
     */
    public String getCaption() {
        final String gwtIdAttribute = "aria-labelledby";
        WebElement captElem = null;
        String captionId = null;
        captionId = getAttribute(gwtIdAttribute);
        // IE8 getAttribute returns empty string instead of null
        // when there is no attribute with specified name
        if (captionId == null || captionId.isEmpty()) {
            WebElement elem = findElement(
                    By.xpath(".//*[@" + gwtIdAttribute + "]"));
            captionId = elem.getAttribute(gwtIdAttribute);
        }
        // element ids are unique, we can search the whole page
        captElem = getDriver().findElement(By.id(captionId));
        return captElem.getText();
    }

    public String getHTML() {
        return getWrappedElement().getAttribute("innerHTML");
    }

    public boolean isReadOnly() {
        String readonlyClass = getAttribute("class");
        // looking for READONLY_CSS_CLASS string
        String[] cssSelectors = readonlyClass.split("\\s");
        for (String selector : cssSelectors) {
            if (selector.equals("v-readonly")) {
                return true;
            }
        }
        return false;
    }

    protected String getStyleAttribute(WebElement element, String styleName) {
        String style = element.getAttribute("style");

        String[] styles = style.split(";");
        for (String stylePart : styles) {
            // IE8 has uppercased styles
            String lowercasePart = stylePart.toLowerCase(Locale.ROOT);
            if (lowercasePart.startsWith(styleName + ":")) {
                return lowercasePart.substring(styleName.length() + 1).trim();
            }
        }

        return null;
    }

    public class ReadOnlyException extends RuntimeException {

    }
}
