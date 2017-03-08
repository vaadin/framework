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
package com.vaadin.tests.components.abstractfield;

import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.PopupDateField;

public class DateFieldBasedOnLong extends AbstractComponentDataBindingTest {

    private Long l = null;
    private ObjectProperty<Long> property;

    @Override
    protected void createFields() {
        PopupDateField pdf = new PopupDateField("DateField");
        addComponent(pdf);
        property = new ObjectProperty<>(l, Long.class);
        pdf.setPropertyDataSource(property);

        property.setValue(new Date(2011 - 1900, 4, 6).getTime());

        addComponent(new Button("Set property value to 10000L",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        property.setValue(10000L);

                    }
                }));
    }

}
