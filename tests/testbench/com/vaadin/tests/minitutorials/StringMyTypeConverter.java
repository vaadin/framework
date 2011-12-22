package com.vaadin.tests.minitutorials;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

public class StringMyTypeConverter extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        Name name = new Name("Rudolph", "Reindeer");

        final TextField textField = new TextField("Name");
        textField.setConverter(new StringToNameConverter());
        textField.setConvertedValue(name);

        addComponent(textField);
        addComponent(new Button("Submit value", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                    Name name = (Name) textField.getConvertedValue();
                    getRoot().showNotification(
                            "First name: " + name.getFirstName()
                                    + "<br />Last name: " + name.getLastName());
                } catch (ConversionException e) {
                    e.printStackTrace();
                    getRoot().showNotification(e.getCause().getMessage());
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
    public Name convertToModel(String text, Locale locale)
            throws ConversionException {
        if (text == null) {
            return null;
        }
        String[] parts = text.split(" ");
        if (parts.length != 2) {
            throw new ConversionException("Can not convert text to a name: "
                    + text);
        }
        return new Name(parts[0], parts[1]);
    }

    public String convertToPresentation(Name name, Locale locale)
            throws ConversionException {
        if (name == null) {
            return null;
        } else {
            return name.getFirstName() + " " + name.getLastName();
        }
    }

    public Class<Name> getModelType() {
        return Name.class;
    }

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
