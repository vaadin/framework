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
package com.vaadin.tests.components.colorpicker;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ColorPicker;

/**
 * Test for color picker with default caption.
 * 
 * @author Vaadin Ltd
 */
public class DefaultCaptionWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ColorPicker colorPicker = new ColorPicker();
        addComponent(colorPicker);
        colorPicker.setDefaultCaptionEnabled(true);

        Button setWidth = new Button("Set explicit width",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        colorPicker.setCaption(null);
                        colorPicker.setWidth("150px");
                    }
                });
        setWidth.addStyleName("set-width");
        addComponent(setWidth);

        Button setCaption = new Button("Set explicit caption",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        colorPicker.setCaption("caption");
                        colorPicker.setWidthUndefined();
                    }
                });
        setCaption.addStyleName("set-caption");
        addComponent(setCaption);

    }

    @Override
    protected String getTestDescription() {
        return "Color picker with default caption enabled should get appropriate style";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17140;
    }
}
