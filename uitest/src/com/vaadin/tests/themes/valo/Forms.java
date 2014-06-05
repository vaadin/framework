/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.themes.valo;

import java.util.Date;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class Forms extends VerticalLayout implements View {
    public Forms() {
        setSpacing(true);
        setMargin(true);

        Label title = new Label("Form");
        title.addStyleName("h1");
        addComponent(title);

        final FormLayout form = new FormLayout();
        form.setMargin(false);
        form.setWidth("800px");
        form.addStyleName("light");
        addComponent(form);

        Label section = new Label("Personal Info");
        section.addStyleName("h4");
        form.addComponent(section);

        TextField name = new TextField("Name");
        name.setValue(ValoThemeTest.nextString(true) + " "
                + ValoThemeTest.nextString(true));
        name.setWidth("50%");
        form.addComponent(name);

        DateField birthday = new DateField("Birthday");
        birthday.setValue(new Date(80, 0, 31));
        form.addComponent(birthday);

        TextField username = new TextField("Username");
        username.setValue(ValoThemeTest.nextString(false)
                + ValoThemeTest.nextString(false));
        username.setRequired(true);
        form.addComponent(username);

        OptionGroup sex = new OptionGroup("Sex");
        sex.addItem("Female");
        sex.addItem("Male");
        sex.select("Male");
        sex.addStyleName("horizontal");
        form.addComponent(sex);

        section = new Label("Contact Info");
        section.addStyleName("h4");
        form.addComponent(section);

        TextField email = new TextField("Email");
        email.setValue(ValoThemeTest.nextString(false) + "@"
                + ValoThemeTest.nextString(false) + ".com");
        email.setWidth("50%");
        email.setRequired(true);
        form.addComponent(email);

        TextField location = new TextField("Location");
        location.setValue(ValoThemeTest.nextString(true) + ", "
                + ValoThemeTest.nextString(true));
        location.setWidth("50%");
        location.setComponentError(new UserError("This address doesn't exist"));
        form.addComponent(location);

        TextField phone = new TextField("Phone");
        phone.setWidth("50%");
        form.addComponent(phone);

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.setSpacing(true);
        wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        wrap.setCaption("Newsletter");
        CheckBox newsletter = new CheckBox("Subscribe to newsletter", true);
        wrap.addComponent(newsletter);

        ComboBox period = new ComboBox();
        period.setTextInputAllowed(false);
        period.addItem("Daily");
        period.addItem("Weekly");
        period.addItem("Montly");
        period.setNullSelectionAllowed(false);
        period.select("Weekly");
        period.addStyleName("small");
        period.setWidth("10em");
        wrap.addComponent(period);
        form.addComponent(wrap);

        section = new Label("Additional Info");
        section.addStyleName("h4");
        form.addComponent(section);

        TextField website = new TextField("Website");
        website.setInputPrompt("http://");
        website.setWidth("100%");
        form.addComponent(website);

        TextArea shortbio = new TextArea("Short Bio");
        shortbio.setValue("Quis aute iure reprehenderit in voluptate velit esse. Cras mattis iudicium purus sit amet fermentum.");
        shortbio.setWidth("100%");
        shortbio.setRows(2);
        form.addComponent(shortbio);

        RichTextArea bio = new RichTextArea("Bio");
        bio.setWidth("100%");
        form.addComponent(bio);

        form.setReadOnly(true);

        Button edit = new Button("Edit", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                boolean readOnly = form.isReadOnly();
                if (readOnly) {
                    form.setReadOnly(false);
                    form.removeStyleName("light");
                    event.getButton().setCaption("Save");
                    event.getButton().addStyleName("primary");
                } else {
                    form.setReadOnly(true);
                    form.addStyleName("light");
                    event.getButton().setCaption("Edit");
                    event.getButton().removeStyleName("primary");
                }
            }
        });

        addComponent(edit);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
