/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;

public class TextFieldWithDataSourceAndInputPrompt extends AbstractTestUI {
    public static class Pojo {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        TextField textField = new TextField("TextField with null value");
        textField.setInputPrompt("Me is input prompt");
        textField.setNullRepresentation(null);
        textField.setValue(null);
        addComponent(textField);

        TextField textField2 = new TextField(
                "TextField with null data source value");
        textField2.setInputPrompt("Me is input prompt");
        textField2.setNullRepresentation(null);
        BeanItem<Pojo> beanItem = new BeanItem<Pojo>(new Pojo());
        textField2.setPropertyDataSource(beanItem.getItemProperty("string"));
        addComponent(textField2);
    }

    @Override
    protected String getTestDescription() {
        return "Input prompt should be shown when data source provides null";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11021;
    }

}
