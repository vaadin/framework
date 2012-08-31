package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class EnterShortcutMaySendInputPromptAsValue extends TestBase {

    @Override
    protected String getDescription() {
        return "?";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2935;
    }

    @Override
    protected void setup() {

        final TextField testField = new TextField();
        testField.setInputPrompt("Enter a value");

        getMainWindow().addActionHandler(new Action.Handler() {

            final Action enter = new ShortcutAction("enter",
                    ShortcutAction.KeyCode.ENTER, null);

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { enter };
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (action == enter) {

                }
            }

        });
        testField.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String value = event.getProperty().getValue().toString();
                addComponent(new Label("TextField sent value: " + value));
                testField.setValue("");
            }
        });

        addComponent(testField);

    }

}
