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
package com.vaadin.tests.components.optiongroup;

import java.util.Collections;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;

/**
 * Test UI for unset read-only flag of Option group with new items allowed.
 * 
 * @author Vaadin Ltd
 */
public class ReadOnlyOptionGroup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final OptionGroup optionGroup = new OptionGroup("test field",
                Collections.singletonList("Option"));
        optionGroup.setNewItemsAllowed(true);

        final CheckBox readOnlyCheckbox = new CheckBox("read-only");
        readOnlyCheckbox.setImmediate(true);
        readOnlyCheckbox
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        optionGroup.setReadOnly(readOnlyCheckbox.getValue());
                    }
                });
        readOnlyCheckbox.setValue(Boolean.TRUE);

        addComponent(optionGroup);
        addComponent(readOnlyCheckbox);
    }

    @Override
    protected String getTestDescription() {
        return "Unset read-only state for Option group should not throw an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11772;
    }

}
