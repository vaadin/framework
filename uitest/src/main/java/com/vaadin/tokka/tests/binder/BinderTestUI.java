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

package com.vaadin.tokka.tests.binder;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tokka.data.Binder;
import com.vaadin.tokka.data.Converter;
import com.vaadin.tokka.data.Validator;
import com.vaadin.tokka.ui.components.fields.TextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

public class BinderTestUI extends AbstractTestUI {

    private TextField nameField = new TextField("Name");
    private TextField ageField = new TextField("Age");
    private Button save = new Button("Save");

    private Binder<Person> binder = new Binder<>();

    @Override
    protected void setup(VaadinRequest request) {
        setupLayout();
        setupBinder();
    }

    private void setupBinder() {

        final Validator<String> notEmpty = Validator.from(str -> !str.isEmpty(),
                "name cannot be empty");

        final Converter<String, Integer> stringToint = Converter.from(
                Integer::valueOf, String::valueOf,
                e -> "age must be a valid number");

        binder.addField(nameField)
                .addValidator(notEmpty)
                .bind(Person::getFirstName, Person::setFirstName);

        binder.addField(ageField)
                .setConverter(stringToint)
                .bind(Person::getAge, Person::setAge);

        binder.bind(new Person());

        save.addClickListener(e -> binder.save().handle(
                person -> Notification.show("Saved: " + person),
                error -> Notification.show(error, Type.WARNING_MESSAGE)));
    }

    private void setupLayout() {
        nameField.setWidth("100%");
        ageField.setWidth("100%");

        save.setClickShortcut(KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        FormLayout l = new FormLayout();
        l.setWidth("100%");
        l.setMargin(true);
        l.addComponents(nameField, ageField, save);

        Panel pnl = new Panel();
        pnl.setWidth("300px");
        pnl.setContent(l);

        addComponent(pnl);
        getLayout().setComponentAlignment(pnl, Alignment.MIDDLE_CENTER);
        getLayout().setSizeFull();
        getContent().setSizeFull();
    }
}
