package com.vaadin.tests.server.component.datefield;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.DateField;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldDeclarativeTest
        extends AbstractLocalDateFieldDeclarativeTest<DateField> {

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

        DateField component = getComponentClass().newInstance();
        component.setPlaceholder(placeholder);
        component.setTextFieldEnabled(textFieldEnabled);
        component.setAssistiveText(assistiveText);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-date-field";
    }

    @Override
    protected Class<? extends DateField> getComponentClass() {
        return DateField.class;
    }

}
