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
package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;

public class SliderResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidth("500px");
        addComponent(layout);

        Slider slider = new Slider();
        slider.setId("horizontal");
        slider.setValue(100.0);
        slider.setWidth("100%");

        Button changeWidth = new Button("Set layout width to 300px",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.setWidth("300px");
                    }
                });
        layout.addComponents(slider, changeWidth);
    }

    @Override
    protected String getTestDescription() {
        return "Slider handle should be updated to correct position when the component size changes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12550;
    }

}
