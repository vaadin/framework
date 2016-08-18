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
package com.vaadin.tests.components.listselect;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;

public class ListSelectPushSelectionChanges extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Collection<String> options = Arrays.asList("a", "b", "c");

        final ListSelect listSelect = new ListSelect("ListSelect", options);
        listSelect.setNullSelectionAllowed(true);

        final OptionGroup optionGroup = new OptionGroup("OptionGroup", options);

        // Option group changes propagate to the list select
        listSelect.setPropertyDataSource(optionGroup);

        final OptionGroup modeSelect = new OptionGroup("Mode",
                Arrays.asList("Single", "Multi"));
        modeSelect.setValue("Single");
        modeSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                optionGroup.setValue(null);

                boolean multiSelect = "Multi".equals(modeSelect.getValue());

                listSelect.setMultiSelect(multiSelect);
                optionGroup.setMultiSelect(multiSelect);
            }
        });

        Button selectNullButton = new Button("Select null",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        listSelect.setValue(null);
                        listSelect.markAsDirty();
                    }
                });

        addComponents(modeSelect, listSelect, optionGroup, selectNullButton);
    }
}
