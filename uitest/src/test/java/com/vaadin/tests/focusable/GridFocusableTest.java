package com.vaadin.tests.focusable;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basics.GridBasics;

public class GridFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return GridBasics.class;
    }

    @Override
    protected String getTabIndex() {
        return $(GridElement.class).first().getAttribute("tabindex");
    }

    @Override
    protected boolean isFocused() {
        return getFocusElement().isFocused();
    }

    @Override
    protected GridCellElement getFocusElement() {
        return $(GridElement.class).first().getCell(0, 0);
    }
}
