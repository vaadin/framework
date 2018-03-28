package com.vaadin.tests.components.grid;

import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("grid")
public class GridColumnAutoWidthClientTest
        extends AbstractGridColumnAutoWidthTest {
    @Override
    protected Class<?> getUIClass() {
        return GridColumnAutoWidthClient.class;
    }
}
