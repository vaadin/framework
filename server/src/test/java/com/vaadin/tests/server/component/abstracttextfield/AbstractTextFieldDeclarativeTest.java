package com.vaadin.tests.server.component.abstracttextfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for AbstractTextField.
 *
 * @since
 * @author Vaadin Ltd
 */
public class AbstractTextFieldDeclarativeTest
        extends DeclarativeTestBase<AbstractTextField> {

    @Test
    public void testAttributes() {
        String design = "<vaadin-text-field null-representation=this-is-null "
                + "null-setting-allowed maxlength=5 columns=3 "
                + "input-prompt=input text-change-event-mode=eager "
                + "text-change-timeout=100 />";
        AbstractTextField tf = new TextField();
        tf.setNullRepresentation("this-is-null");
        tf.setNullSettingAllowed(true);
        tf.setMaxLength(5);
        tf.setColumns(3);
        tf.setInputPrompt("input");
        tf.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf.setTextChangeTimeout(100);
        testRead(design, tf);
        testWrite(design, tf);
    }

}
