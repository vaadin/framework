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

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.TextField;

public class StringMyTypeConverter extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Name name = new Name("Rudolph", "Reindeer");

        final TextField textField = new TextField("Name");
        textField.setConverter(new StringToNameConverter());
        textField.setConvertedValue(name);

        addComponent(textField);
        addComponent(new Button("Submit value", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Name name = (Name) textField.getConvertedValue();
                    Notification.show("First name: " + name.getFirstName()
                            + "<br />Last name: " + name.getLastName());
                } catch (ConversionException e) {
                    e.printStackTrace();
                    Notification.show(e.getCause().getMessage());
                }
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20your%20own%20converter%20for%20String%20-%20MyType%20conversion";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}

class StringToNameConverter implements Converter<String, Name> {
    @Override
    public Name convertToModel(String text, Class<? extends Name> targetType,
            Locale locale) throws ConversionException {
        if (text == null) {
            return null;
        }
        String[] parts = text.split(" ");
        if (parts.length != 2) {
            throw new ConversionException(
                    "Can not convert text to a name: " + text);
        }
        return new Name(parts[0], parts[1]);
    }

    @Override
    public String convertToPresentation(Name name,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (name == null) {
            return null;
        } else {
            return name.getFirstName() + " " + name.getLastName();
        }
    }

    @Override
    public Class<Name> getModelType() {
        return Name.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}

class Name {
    private String firstName;
    private String lastName;

    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
