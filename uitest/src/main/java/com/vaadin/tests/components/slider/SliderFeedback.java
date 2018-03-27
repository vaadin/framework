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
package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;

public class SliderFeedback extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Slider slider = new Slider(0, 5);
        slider.setWidth(800, Unit.PIXELS);
        slider.setMin(0);
        slider.setMax(1e12);
        addComponent(slider);
    }

    @Override
    protected String getTestDescription() {
        return "Slider feedback popup should display the correct value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18192;
    }

}
