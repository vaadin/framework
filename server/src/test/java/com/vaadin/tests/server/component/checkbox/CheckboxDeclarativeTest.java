package com.vaadin.tests.server.component.checkbox;

import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.CheckBox;

/**
 * Tests declarative support for implementations of {@link CheckBox}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class CheckboxDeclarativeTest
        extends AbstractFieldDeclarativeTest<CheckBox, Boolean> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = "<vaadin-check-box checked />";
        CheckBox checkBox = new CheckBox();

        checkBox.setValue(true);

        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String design = "<vaadin-check-box readonly checked />";

        CheckBox checkBox = new CheckBox();

        checkBox.setValue(true);
        checkBox.setReadOnly(true);

        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-check-box";
    }

    @Override
    protected Class<CheckBox> getComponentClass() {
        return CheckBox.class;
    }

}
