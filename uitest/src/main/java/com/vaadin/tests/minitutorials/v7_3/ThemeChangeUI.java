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
package com.vaadin.tests.minitutorials.v7_3;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;

public class ThemeChangeUI extends UI {

    private String[] themes = { "valo", "reindeer", "runo", "chameleon" };

    @Override
    protected void init(VaadinRequest request) {
        ComboBox themePicker = new ComboBox("Theme", Arrays.asList(themes));
        themePicker.setValue(getTheme());

        themePicker.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String theme = (String) event.getProperty().getValue();
                setTheme(theme);
            }
        });

        setContent(themePicker);
    }
}
