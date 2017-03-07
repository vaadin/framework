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
package com.vaadin.tests.components.combobox;

import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxPageLength extends ComboBoxes2<ComboBox> {

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        getComponent().addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty() != null) {
                    if (event.getProperty().getValue() != null) {
                        Integer value = Integer.parseInt(
                                ((String) event.getProperty().getValue())
                                        .split(" ")[1]);
                        getComponent().setPageLength(value);
                    } else {
                        getComponent().setPageLength(0);
                    }
                }
            }
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 5381;
    }

    @Override
    protected String getTestDescription() {
        return super.getTestDescription()
                + ", changing ComboBox value will change the ComboBox pageLength to the # of the selected item, or to 0 in null selection.";
    }
}
