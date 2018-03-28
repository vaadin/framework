package com.vaadin.tests.components.grid;

import com.vaadin.tests.components.grid.CustomRendererUI.Data;
import com.vaadin.ui.renderers.AbstractRenderer;

public class IntArrayRenderer extends AbstractRenderer<Data, int[]> {
    public IntArrayRenderer() {
        super(int[].class, "");
    }
}
