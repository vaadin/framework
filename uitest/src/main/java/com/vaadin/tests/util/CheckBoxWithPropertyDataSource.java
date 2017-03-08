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
package com.vaadin.tests.util;

import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeNotifier;
import com.vaadin.v7.data.Validator.InvalidValueException;

public class CheckBoxWithPropertyDataSource extends CheckBox {

    public CheckBoxWithPropertyDataSource(String caption) {
        super(caption);
    }

    public CheckBoxWithPropertyDataSource(String caption,
            Property<Boolean> property) {
        super(caption);

        setValue(property.getValue());
        addValueChangeListener(event -> property.setValue(event.getValue()));

        if (property instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) property)
                    .addValueChangeListener(event -> setValue(
                            (Boolean) event.getProperty().getValue()));
        }
    }

    public void validate() {
        if (isRequiredIndicatorVisible() && !getValue()) {
            throw new InvalidValueException(
                    "Required CheckBox should be checked");
        }
    }

}
