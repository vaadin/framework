package com.vaadin.tests.server.component.colorpicker;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.components.colorpicker.ColorPickerGradient;
import com.vaadin.ui.components.colorpicker.ColorPickerGrid;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;

/**
 * @author Vaadin Ltd
 *
 */
public class ColorPickerComponentsTest {

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerGradientThrowsNPE() {
        ColorPickerGradient gradient = new ColorPickerGradient("foo",
                Mockito.mock(Coordinates2Color.class));
        gradient.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerGridThrowsNPE() {
        ColorPickerGrid grid = new ColorPickerGrid();
        grid.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerPopupThrowsNPE() {
        ColorPickerPopup popup = new ColorPickerPopup(Color.WHITE);
        popup.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerPreviewThrowsNPE() {
        ColorPickerPreview preview = new ColorPickerPreview(Color.WHITE);
        preview.setValue(null);
    }
}
