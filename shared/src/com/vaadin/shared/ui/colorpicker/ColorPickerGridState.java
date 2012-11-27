package com.vaadin.shared.ui.colorpicker;

import com.vaadin.shared.AbstractComponentState;

/**
 * Default shared state implementation for ColorPickerGrid.
 * 
 * @since 7.0.0
 */
public class ColorPickerGridState extends AbstractComponentState {

    public int rowCount;

    public int columnCount;

    public String[] changedX;

    public String[] changedY;

    public String[] changedColor;

}
