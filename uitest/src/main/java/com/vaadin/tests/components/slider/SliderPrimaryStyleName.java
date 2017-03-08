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
package com.vaadin.tests.components.slider;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Slider;

public class SliderPrimaryStyleName extends TestBase {

    @Override
    protected void setup() {
        final Slider slider = new Slider(0, 100);
        slider.setWidth("100px");
        slider.setPrimaryStyleName("my-slider");
        addComponent(slider);

        addComponent(
                new Button("Change primary style", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        slider.setPrimaryStyleName("my-second-slider");
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Setting the primary stylename should work both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9898;
    }

}
