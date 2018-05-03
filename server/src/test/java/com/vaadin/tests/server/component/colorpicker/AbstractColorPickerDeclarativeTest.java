package com.vaadin.tests.server.component.colorpicker;

import org.junit.Test;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.AbstractColorPicker.PopupStyle;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ColorPickerArea;

public class AbstractColorPickerDeclarativeTest
        extends DeclarativeTestBase<AbstractColorPicker> {

    @Test
    public void testAllAbstractColorPickerFeatures() {
        String design = "<vaadin-color-picker color='#fafafa' default-caption-enabled position='100,100'"
                + " popup-style='simple' rgb-visibility='false' hsv-visibility='false'"
                + " history-visibility=false textfield-visibility=false />";
        ColorPicker colorPicker = new ColorPicker();
        int colorInt = Integer.parseInt("fafafa", 16);
        colorPicker.setColor(new Color(colorInt));
        colorPicker.setDefaultCaptionEnabled(true);
        colorPicker.setPosition(100, 100);
        colorPicker.setPopupStyle(PopupStyle.POPUP_SIMPLE);
        colorPicker.setRGBVisibility(false);
        colorPicker.setHSVVisibility(false);
        colorPicker.setSwatchesVisibility(true);
        colorPicker.setHistoryVisibility(false);
        colorPicker.setTextfieldVisibility(false);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Test
    public void testEmptyColorPicker() {
        String design = "<vaadin-color-picker />";
        ColorPicker colorPicker = new ColorPicker();
        testRead(design, colorPicker);
        testWrite(design, colorPicker);
    }

    @Test
    public void testAllAbstractColorPickerAreaFeatures() {
        String design = "<vaadin-color-picker-area color='#fafafa' default-caption-enabled position='100,100'"
                + " popup-style='simple' rgb-visibility='false' hsv-visibility='false'"
                + " history-visibility=false textfield-visibility=false />";
        AbstractColorPicker colorPicker = new ColorPickerArea();
        int colorInt = Integer.parseInt("fafafa", 16);
        colorPicker.setColor(new Color(colorInt));
        colorPicker.setDefaultCaptionEnabled(true);
        colorPicker.setPosition(100, 100);
        colorPicker.setPopupStyle(PopupStyle.POPUP_SIMPLE);
        colorPicker.setRGBVisibility(false);
        colorPicker.setHSVVisibility(false);
        colorPicker.setSwatchesVisibility(true);
        colorPicker.setHistoryVisibility(false);
        colorPicker.setTextfieldVisibility(false);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Test
    public void testEmptyColorPickerArea() {
        String design = "<vaadin-color-picker-area />";
        AbstractColorPicker colorPicker = new ColorPickerArea();
        testRead(design, colorPicker);
        testWrite(design, colorPicker);
    }
}
