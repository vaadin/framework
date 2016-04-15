package com.vaadin.tests.components.checkbox;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;

public class CheckBoxes2 extends AbstractFieldTest<CheckBox> implements
        ClickListener {

    @Override
    protected Class<CheckBox> getTestClass() {
        return CheckBox.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

    }

    @Override
    public void buttonClick(ClickEvent event) {
        log(event.getClass().getSimpleName());
    }
}
