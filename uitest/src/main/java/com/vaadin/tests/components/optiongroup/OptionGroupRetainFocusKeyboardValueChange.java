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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.OptionGroup;

/**
 * Testcase for #10451
 * 
 * @author Vaadin Ltd
 */
public class OptionGroupRetainFocusKeyboardValueChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final OptionGroup optiongroup = new OptionGroup();
        optiongroup.addItem(1);
        optiongroup.addItem(2);
        optiongroup.addItem(3);
        optiongroup.setItemCaption(1, "A");
        optiongroup.setItemCaption(2, "B");
        optiongroup.setItemCaption(3, "C");
        optiongroup.setImmediate(true);

        optiongroup.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (optiongroup.isSelected(2)) {
                    optiongroup.setItemCaption(1, "A+");
                } else if (optiongroup.isSelected(3)) {
                    optiongroup.removeItem(2);
                    optiongroup.addItem(2);
                    optiongroup.setItemCaption(2, "B");
                }
            }
        });

        addComponent(optiongroup);

        optiongroup.focus();
    }

    @Override
    protected String getTestDescription() {
        return "OptionGroup should retain focus after it's value being changed with keyboard";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10451;
    }
}
