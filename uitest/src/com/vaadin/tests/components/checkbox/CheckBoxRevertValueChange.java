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
package com.vaadin.tests.components.checkbox;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

@PreserveOnRefresh
public class CheckBoxRevertValueChange extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox alwaysUnchecked = new CheckBox("You may not check me");
        alwaysUnchecked
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        if (alwaysUnchecked.getValue()) {
                            log("I said no checking!");
                            alwaysUnchecked.setValue(false);
                        }
                    }
                });
        final CheckBox alwaysChecked = new CheckBox("You may not uncheck me");
        alwaysChecked.setValue(true);
        alwaysChecked
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        if (!alwaysChecked.getValue()) {
                            log("I said no unchecking!");
                            alwaysChecked.setValue(true);
                        }
                    }
                });

        addComponent(alwaysUnchecked);
        addComponent(alwaysChecked);
    }

    @Override
    protected String getTestDescription() {
        return "Ensure checking of a checkbox can be reverted on the server side without making the client go out of sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11028;
    }

}
