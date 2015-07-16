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

package com.vaadin.tests.components.textfield;

import com.vaadin.annotations.Theme;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("valo")
public class NullRepresentationLegacyMode extends AbstractTestUI {

    public static class Entity {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class Form extends VerticalLayout {
        TextField value = new TextField();

        public Form() {
            setMargin(true);
            setSpacing(true);
            addComponent(value);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Form formWithoutNulls = new Form();
        formWithoutNulls.setCaption("No 'null's here please");
        formWithoutNulls.setId("without");
        BeanFieldGroup.bindFieldsUnbuffered(new Entity(), formWithoutNulls);

        // Use the legacy default
        AbstractTextField.setNullRepresentationDefault("null");

        Form formWithNulls = new Form();
        formWithNulls.setCaption("'null's please");
        formWithNulls.setId("with");
        BeanFieldGroup.bindFieldsUnbuffered(new Entity(), formWithNulls);
        AbstractTextField.setNullRepresentationDefault("");

        addComponents(formWithoutNulls, formWithNulls);
    }

    @Override
    protected String getTestDescription() {
        return "Text field must not truncate underscores in modal dialogs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12974;
    }

}
