package com.vaadin.tests.server.component.abstracttextfield;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractTextField;

/**
 * @author Vaadin Ltd
 *
 */
public class AbstractTextFieldTest {

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        AbstractTextField field = Mockito.mock(AbstractTextField.class);
        Mockito.doCallRealMethod().when(field).setValue(null);

        field.setValue(null);
    }
}
