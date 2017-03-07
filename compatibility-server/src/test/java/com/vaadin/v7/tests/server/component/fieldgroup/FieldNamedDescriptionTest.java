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
package com.vaadin.v7.tests.server.component.fieldgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.annotations.PropertyId;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.TextField;

public class FieldNamedDescriptionTest {

    @Test
    public void bindReadOnlyPropertyToFieldGroup() {
        // Create an item
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("name", new ObjectProperty<String>("Zaphod"));
        item.addItemProperty("description",
                new ObjectProperty<String>("This is a description"));

        // Define a form as a class that extends some layout
        class MyForm extends FormLayout {
            // Member that will bind to the "name" property
            TextField name = new TextField("Name");

            // This member will not bind to the desctiptionProperty as the name
            // description conflicts with something in the binding process
            @PropertyId("description")
            TextField description = new TextField("Description");

            public MyForm() {

                // Add the fields
                addComponent(name);
                addComponent(description);
            }
        }

        // Create one
        MyForm form = new MyForm();

        // Now create a binder that can also creates the fields
        // using the default field factory
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue(form.description.getValue().equals("This is a description"));
    }

}
