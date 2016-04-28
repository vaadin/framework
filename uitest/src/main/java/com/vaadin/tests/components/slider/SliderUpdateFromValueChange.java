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

/**
 * 
 */
package com.vaadin.tests.components.slider;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;

/**
 * Testcase for #12133
 * 
 * @author Vaadin Ltd
 */
public class SliderUpdateFromValueChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Slider slider = new Slider(0, 100, 1);
        slider.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Double value = (Double) event.getProperty().getValue();
                if (value < 100.0) {
                    slider.setValue(100.0);
                }
                slider.markAsDirty();
            }

        });
        slider.setImmediate(true);
        slider.setWidth(200, Unit.PIXELS);

        addComponent(slider);
    }

    @Override
    protected String getTestDescription() {
        return "Slider.setValue() does not update graphical representation of Slider component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12133;
    }
}
