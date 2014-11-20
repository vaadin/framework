package com.vaadin.tests.components.grid.basicfeatures.client;

import org.junit.Before;

public class GridClientCompositeEditorRowTest extends GridEditorRowClientTest {

    @Override
    @Before
    public void setUp() {
        setUseComposite(true);
        super.setUp();
    }
}
