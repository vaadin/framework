package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxEmbeddedInDiv extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        for (int i = 0; i < 20; i++) {
            vl.addComponent(new Label("" + i));
        }
        ComboBox<String> cb = new ComboBox<>();
        vl.addComponent(cb);
        for (int i = 0; i < 20; i++) {
            vl.addComponent(new Label("" + i));
        }
        addComponent(vl);
    }

}
