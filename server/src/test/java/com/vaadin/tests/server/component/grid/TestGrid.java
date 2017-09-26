package com.vaadin.tests.server.component.grid;

import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.ui.Grid;

/**
 * {@link Grid} class for testing purposes
 * @param <T>
 *            the grid bean type
 */
public class TestGrid<T> extends Grid<T> {

    public TestGrid(Class<T> beanType, DataCommunicator<T> dataCommunicator) {
        super(beanType, dataCommunicator);
    }

}
