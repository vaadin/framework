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

import com.vaadin.ui.FormLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.TextField;

public class CaseInsensitiveBindingTest {

    @Test
    public void caseInsensitivityAndUnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("LastName", new ObjectProperty<String>("Sparrow"));

        class MyForm extends FormLayout {
            TextField lastName = new TextField("Last name");

            public MyForm() {

                // Should bind to the LastName property
                addComponent(lastName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Sparrow".equals(form.lastName.getValue()));
    }

    @Test
    public void UnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name", new ObjectProperty<String>("Jack"));

        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // Should bind to the first_name property
                addComponent(firstName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Jack".equals(form.firstName.getValue()));
    }

    @Test
    public void perfectMatchPriority() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name",
                new ObjectProperty<String>("Not this"));
        item.addItemProperty("firstName", new ObjectProperty<String>("This"));

        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // should bind to the firstName property, not first_name
                // property
                addComponent(firstName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("This".equals(form.firstName.getValue()));
    }

}
