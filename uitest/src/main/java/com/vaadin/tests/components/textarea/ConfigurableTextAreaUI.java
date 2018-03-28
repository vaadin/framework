package com.vaadin.tests.components.textarea;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.TextArea;

public class ConfigurableTextAreaUI
        extends AbstractFieldTest<TextArea, String> {

    @Override
    protected Class<TextArea> getTestClass() {
        return TextArea.class;
    }

}
