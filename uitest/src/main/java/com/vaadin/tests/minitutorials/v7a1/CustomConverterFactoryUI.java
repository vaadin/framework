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
package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.TextField;

public class CustomConverterFactoryUI extends AbstractReindeerTestUI {
    @Override
    public void setup(VaadinRequest request) {
        getSession().setConverterFactory(new MyConverterFactory());

        TextField tf = new TextField("This is my double field");
        tf.setImmediate(true);
        tf.setConverter(Double.class);
        addComponent(tf);

        // As we do not set the locale explicitly for the field we set the value
        // after the field has been attached so it uses the application locale
        // for conversion
        tf.setConvertedValue(50.1);

    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Changing%20the%20default%20converters%20for%20an%20application";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
