package com.vaadin.tests.server.component.colorpicker;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractColorPicker;

/**
 * @author Vaadin Ltd
 *
 */
public class AbstractColorPickerTest {

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        AbstractColorPicker picker = Mockito.mock(AbstractColorPicker.class);
        Mockito.doCallRealMethod().when(picker).setValue(null);

        picker.setValue(null);
    }
}
