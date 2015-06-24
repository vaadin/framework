package com.vaadin.tests.components.grid.basicfeatures.client;

import org.junit.Before;

public class GridClientCompositeKeyEventsTest extends GridClientKeyEventsTest {

    @Before
    public void setUp() {
        setUseComposite(true);
    }
}
