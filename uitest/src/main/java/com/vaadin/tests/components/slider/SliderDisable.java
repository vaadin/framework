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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;

public class SliderDisable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        final Slider slider = new Slider(0, 5);
        slider.setWidth(200, Unit.PIXELS);
        slider.setValue(1.0D);

        Button disableButton = new Button("Disable slider");
        disableButton.setId("disableButton");
        disableButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                slider.setEnabled(false);
            }
        });

        content.addComponent(slider);
        content.addComponent(disableButton);
        setContent(content);
    }

    @Override
    protected String getTestDescription() {
        return "The apparent value of the slider should not change when the slider is disabled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12676;
    }

}
