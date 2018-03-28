package com.vaadin.tests.server.component.checkbox;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.CheckBox;

/**
 * Tests declarative support for implementations of {@link CheckBox}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class CheckboxDeclarativeTest extends DeclarativeTestBase<CheckBox> {

    @Test
    public void testChecked() {
        String design = "<vaadin-check-box />";
        CheckBox checkBox = new CheckBox();
        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Test
    public void testUnchecked() {
        String design = "<vaadin-check-box checked />";
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(true);
        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-check-box readonly checked />";
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(true);
        checkBox.setReadOnly(true);
        testRead(design, checkBox);
        testWrite(design, checkBox);
    }
}
