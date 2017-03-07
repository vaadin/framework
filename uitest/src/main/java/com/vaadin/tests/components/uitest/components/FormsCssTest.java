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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;

public class FormsCssTest extends HorizontalLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public FormsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setWidth("100%");

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        FieldGroup fg = new BeanFieldGroup<>(Person.class);
        fg.setItemDataSource(new BeanItem<>(new Person()));
        for (Object propId : fg.getUnboundPropertyIds()) {
            if (!"address".equals(propId)) {
                vl.addComponent(fg.buildAndBind(propId));
            }
        }

        addComponent(vl);

        LoginForm login = new LoginForm();
        login.setId("form" + debugIdCounter++);
        login.setHeight("150px");
        addComponent(login);

    }

    @Override
    public void addComponent(com.vaadin.ui.Component c) {
        parent.registerComponent(c);
        super.addComponent(c);
    }

}
