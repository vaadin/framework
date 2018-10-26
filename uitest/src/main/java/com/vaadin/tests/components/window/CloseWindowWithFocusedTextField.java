package com.vaadin.tests.components.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CloseWindowWithFocusedTextField extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        Button button1 = new Button("open window");
        button1.addClickListener(event -> {

            Window window = new Window();
            window.setModal(true);

            TextField textField = new TextField("focus me");
            textField.focus();

            window.setContent(textField);
            getUI().addWindow(window);
        });

        layout.addComponents(button1);

        addComponent(layout);
    }
}
