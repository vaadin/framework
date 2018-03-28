package com.vaadin.tests.server.component.abstracttextfield;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractTextField;

/**
 * Tests declarative support for AbstractTextField.
 *
 * @since
 * @author Vaadin Ltd
 */
public abstract class AbstractTextFieldDeclarativeTest<T extends AbstractTextField>
        extends AbstractFieldDeclarativeTest<T, String> {

    @Test
    public void abstractTextFieldAttributes()
            throws InstantiationException, IllegalAccessException {
        int maxLength = 5;
        String placeholder = "foo";
        ValueChangeMode mode = ValueChangeMode.EAGER;
        int timeout = 100;
        String design = String.format(
                "<%s maxlength='%d' placeholder='%s' "
                        + "value-change-mode='%s' value-change-timeout='%d'/>",
                getComponentTag(), maxLength, placeholder,
                mode.name().toLowerCase(Locale.ROOT), timeout);

        T component = getComponentClass().newInstance();

        component.setMaxLength(maxLength);
        component.setPlaceholder(placeholder);
        component.setValueChangeMode(mode);
        component.setValueChangeTimeout(timeout);

        testRead(design, component);
        testWrite(design, component);
    }

}
