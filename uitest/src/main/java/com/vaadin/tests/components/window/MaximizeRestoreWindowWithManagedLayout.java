package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class MaximizeRestoreWindowWithManagedLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gridLayout = new GridLayout(1, 2);
        TextField textField = new TextField();
        textField.setCaption("textfield");
        textField.setWidth("100%");
        gridLayout.addComponent(textField, 0, 1);
        gridLayout.setSizeFull();

        Window window = new Window();
        window.setWidth("400px");
        window.setHeight("300px");
        window.center();
        window.setResizable(true);
        window.setContent(gridLayout);
        addWindow(window);
    }

}
