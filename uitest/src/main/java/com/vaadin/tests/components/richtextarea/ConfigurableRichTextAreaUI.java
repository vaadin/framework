package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.RichTextArea;

public class ConfigurableRichTextAreaUI
        extends AbstractFieldTest<RichTextArea, String> {

    @Override
    protected Class<RichTextArea> getTestClass() {
        return RichTextArea.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createClickAction("Select all", CATEGORY_FEATURES, (rta, a, b) -> {
            rta.selectAll();
        }, null);
    }

}
