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
package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.RadioButtonGroup;

/**
 * @author Vaadin Ltd
 *
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class RadioButtonGroupFocus extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBox cb = new CheckBox("CB");
        RadioButtonGroup<String> rbg = new RadioButtonGroup<>("Radios");
        rbg.setItems("Test1", "Test2", "Test3");
        rbg.setSelectedItem("Test2");
        rbg.setItemCaptionGenerator(item -> "Option " + item);
        rbg.focus();
        RadioButtonGroup<String> rbg2 = new RadioButtonGroup<>("No selection");
        rbg2.setItems("Foo1", "Foo2", "Foo3");
        Button button = new Button("focus second group", e -> rbg2.focus());
        addComponents(cb, rbg, rbg2, button);
    }

}
