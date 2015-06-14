package com.vaadin.tests.applicationservlet;

import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.launcher.ApplicationRunnerServlet;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeSelect;

import elemental.json.JsonObject;

public class SystemMessages extends AbstractTestUI {

    public class MyButton extends Button {
        private boolean fail = false;

        @Override
        public JsonObject encodeState() {
            // Set the error message to contain the current locale.
            VaadinService.getCurrentRequest().setAttribute(
                    ApplicationRunnerServlet.CUSTOM_SYSTEM_MESSAGES_PROPERTY,
                    new CustomizedSystemMessages() {
                        @Override
                        public String getInternalErrorMessage() {
                            return "MessagesInfo locale: " + getLocale();
                        }
                    });
            if (fail) {
                throw new RuntimeException("Failed on purpose");
            } else {
                return super.encodeState();
            }
        }
    }

    @Override
    protected void setup(final VaadinRequest request) {
        final NativeSelect localeSelect = new NativeSelect("UI locale");
        localeSelect.setImmediate(true);
        localeSelect.addItem(new Locale("en", "US"));
        localeSelect.addItem(new Locale("fi", "FI"));
        localeSelect.addItem(Locale.GERMANY);
        localeSelect.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Locale locale = (Locale) localeSelect.getValue();
                setLocale(locale);
            }
        });
        localeSelect.setValue(new Locale("fi", "FI"));
        addComponent(localeSelect);
        final MyButton failButton = new MyButton();
        failButton.setCaption("Generate server side error");
        failButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                failButton.fail = true;
                failButton.markAsDirty();
            }
        });
        addComponent(failButton);

    }

    @Override
    protected String getTestDescription() {
        return "SystemMessagesProvider.getSystemMessages should get an event object";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10226;
    }

}
