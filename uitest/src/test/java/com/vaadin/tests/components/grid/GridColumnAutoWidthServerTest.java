package com.vaadin.tests.components.grid;

import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("grid")
public class GridColumnAutoWidthServerTest
        extends AbstractGridColumnAutoWidthTest {
    @Override
    protected Class<?> getUIClass() {
        return GridColumnAutoWidth.class;
    }
}
