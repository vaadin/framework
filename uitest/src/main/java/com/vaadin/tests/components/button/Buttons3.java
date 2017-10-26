package com.vaadin.tests.components.button;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class Buttons3<T extends Button> extends Buttons2<T> {

    @Override
    public void buttonClick(ClickEvent event) {
        event.getButton().setEnabled(true);
        super.buttonClick(event);
    }

}
