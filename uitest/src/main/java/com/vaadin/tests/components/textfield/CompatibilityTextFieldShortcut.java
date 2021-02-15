package com.vaadin.tests.components.textfield;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("deprecation")
public class CompatibilityTextFieldShortcut extends AbstractTestUI {

    Registration listenerRegistration;

    @Override
    protected void setup(VaadinRequest request) {
        TextField textField = new TextField("F8 shortcut when focused");
        ShortcutListener c = new ShortcutListener("ShortcutForMAMedRemarks",
                ShortcutAction.KeyCode.F8, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                Notification.show("Received F8: " + textField.getValue());
            }
        };

        textField.addFocusListener(e -> {
            listenerRegistration = textField.addShortcutListener(c);
            Label label = new Label("Focused");
            label.addStyleName("focus-label");
            addComponent(label);
        });

        textField.addBlurListener(e -> {
            listenerRegistration.remove();
        });

        Label label = new Label(
                "F8 will have an effect only if the following component is focused.");
        Button button = new Button("focus");
        button.addClickListener(event -> {
            textField.focus();
        });
        addComponents(label, textField);
    }
}
