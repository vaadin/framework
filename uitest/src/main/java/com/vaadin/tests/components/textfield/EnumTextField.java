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
package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

public class EnumTextField extends AbstractTestUIWithLog {

    public enum MyEnum {
        FIRST_VALUE, VALUE, THE_LAST_VALUE;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.addValueChangeListener(event -> {
            if (tf.isValid()) {
                log(tf.getValue() + " (valid)");
            } else {
                log(tf.getValue() + " (INVALID)");
            }
        });

        tf.setPropertyDataSource(new ObjectProperty<Enum>(MyEnum.FIRST_VALUE));
        addComponent(tf);
    }

}
