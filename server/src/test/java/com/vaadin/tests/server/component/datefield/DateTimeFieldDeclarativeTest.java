package com.vaadin.tests.server.component.datefield;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateTimeFieldDeclarativeTest;
import com.vaadin.ui.DateTimeField;

/**
 * Tests the declarative support for implementations of {@link DateTimeField}.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class DateTimeFieldDeclarativeTest
        extends AbstractLocalDateTimeFieldDeclarativeTest<DateTimeField> {

    @Test
    public void remainingAttributes()
            throws InstantiationException, IllegalAccessException {
        String placeholder = "foo";
        String assistiveText = "at";
        boolean textFieldEnabled = false;
        String design = String.format(
                "<%s placeholder='%s' "
                        + "assistive-text='%s' text-field-enabled='%s'/>",
                getComponentTag(), placeholder, assistiveText,
                textFieldEnabled);

        DateTimeField component = getComponentClass().newInstance();
        component.setPlaceholder(placeholder);
        component.setTextFieldEnabled(textFieldEnabled);
        component.setAssistiveText(assistiveText);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-date-time-field";
    }

    @Override
    protected Class<? extends DateTimeField> getComponentClass() {
        return DateTimeField.class;
    }

}
