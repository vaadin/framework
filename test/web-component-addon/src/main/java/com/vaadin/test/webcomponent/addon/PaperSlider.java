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
package com.vaadin.test.webcomponent.addon;

import org.vaadin.elements.ElementIntegration;
import org.vaadin.elements.Root;

import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.JavaScript;

@JavaScript("frontend://webcomponentsjs/webcomponents-lite.js")
@JavaScript("paperslider.js")
@HtmlImport("frontend://paper-slider/paper-slider.html")
public class PaperSlider extends AbstractJavaScriptField<Integer> {

    private Root element;

    public PaperSlider() {
        element = ElementIntegration.getRoot(this);
        element.addEventListener("value-changed", args -> {
            setValue((int) args.getNumber(0), true);
        }, "event.target.value");
    }

    @Override
    public Integer getValue() {
        return getIntegerAttribute("value", 0);
    }

    @Override
    protected void doSetValue(Integer value) {
        element.setAttribute("value", String.valueOf(value));
    }

    public Integer getMin() {
        return getIntegerAttribute("min", 0);
    }

    private Integer getIntegerAttribute(String attr, Integer defaultValue) {
        if (!element.hasAttribute(attr)) {
            return defaultValue;
        } else {
            return Integer.parseInt(element.getAttribute(attr));
        }
    }

    public void setMin(Integer min) {
        element.setAttribute("min", String.valueOf(min));
    }

    public Integer getMax() {
        return getIntegerAttribute("max", 100);
    }

    public void setMax(Integer max) {
        element.setAttribute("max", String.valueOf(max));
    }

    @Override
    public boolean isReadOnly() {
        return element.hasAttribute("disabled");
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        element.setAttribute("disabled", readOnly);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return element.hasAttribute("required");
    }

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        element.setAttribute("required", visible);
    }
}
