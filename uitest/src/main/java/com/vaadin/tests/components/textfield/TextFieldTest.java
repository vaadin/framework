package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.abstractfield.LegacyAbstractTextFieldTest;

public class TextFieldTest extends LegacyAbstractTextFieldTest<LegacyTextField> implements
        TextChangeListener {

    @Override
    protected Class<LegacyTextField> getTestClass() {
        return LegacyTextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
