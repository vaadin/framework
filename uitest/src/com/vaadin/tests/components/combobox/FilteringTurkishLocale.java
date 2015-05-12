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
package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;

public class FilteringTurkishLocale extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final ComboBox comboBox = new ComboBox("Box", Arrays.asList(
                "I without dot", "İ with dot"));
        comboBox.setNullSelectionAllowed(false);

        NativeSelect localeSelect = new NativeSelect("Locale", Arrays.asList(
                Locale.ENGLISH, new Locale("tr")));
        localeSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                comboBox.setLocale((Locale) event.getProperty().getValue());
            }
        });
        localeSelect.setValue(Locale.ENGLISH);

        addComponents(localeSelect, comboBox);
    }

    @Override
    public String getDescription() {
        return "When the Turkish locale is used,"
                + " filtering for 'i' should show the option with a dot"
                + " while filtering for 'ı' should show the option witout a dot";
    }

}
