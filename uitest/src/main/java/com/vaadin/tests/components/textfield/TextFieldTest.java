package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.LegacyAbstractTextFieldTest;
import com.vaadin.v7.ui.LegacyTextField;

public class TextFieldTest extends LegacyAbstractTextFieldTest<LegacyTextField>
        implements TextChangeListener {

    @Override
    protected Class<LegacyTextField> getTestClass() {
        return LegacyTextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
