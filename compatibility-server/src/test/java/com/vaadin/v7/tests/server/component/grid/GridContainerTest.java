package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.Grid.RowReference;

public class GridContainerTest {

    /**
     * Null Stream used with serialization tests
     */
    protected static OutputStream NULLSTREAM = new OutputStream() {
        @Override
        public void write(int b) {
        }
    };

    @Test
    public void testDetailsGeneratorDoesNotResetOnContainerChange() {
        Grid grid = new Grid();
        DetailsGenerator detGen = new DetailsGenerator() {

            @Override
            public Component getDetails(RowReference rowReference) {
                return new Label("Empty details");
            }
        };
        grid.setDetailsGenerator(detGen);

        grid.setContainerDataSource(createContainer());

        assertEquals("DetailsGenerator changed", detGen,
                grid.getDetailsGenerator());
    }

    @Test
    public void testSetContainerTwice() throws Exception {

        TestGrid grid = new TestGrid();

        grid.setContainerDataSource(createContainer());

        // Simulate initial response to ensure "lazy" state changes are done
        // before resetting the datasource
        grid.beforeClientResponse(true);
        grid.getDataProvider().beforeClientResponse(true);

        grid.setContainerDataSource(createContainer());
    }

    @SuppressWarnings("unchecked")
    private IndexedContainer createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("x", String.class, null);
        container.addItem(0).getItemProperty("x").setValue("y");
        return container;
    }

    @Test
    public void setColumnsOrder() {
        Grid grid = new Grid();
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("foo", String.class, "");
        ic.addContainerProperty("baz", String.class, "");
        ic.addContainerProperty("bar", String.class, "");
        grid.setContainerDataSource(ic);
        grid.setColumns("foo", "baz", "bar");

        assertEquals("foo", grid.getColumns().get(0).getPropertyId());
        assertEquals("baz", grid.getColumns().get(1).getPropertyId());
        assertEquals("bar", grid.getColumns().get(2).getPropertyId());
    }

    @Test
    public void addColumnNotInContainer() {
        Grid grid = new Grid();
        grid.setContainerDataSource(new IndexedContainer());
        try {
            grid.addColumn("notInContainer");
            fail("Adding a property id not in the container should throw an exception");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("notInContainer"));
            assertTrue(
                    e.getMessage().contains("does not exist in the container"));
        }
    }

    @Test
    public void setColumnsForPropertyIdNotInContainer() {
        Grid grid = new Grid();
        grid.setContainerDataSource(new IndexedContainer());
        try {
            grid.setColumns("notInContainer", "notThereEither");
            fail("Setting columns for property ids not in the container should throw an exception");
        } catch (IllegalStateException e) {
            // addColumn is run in random order..
            assertTrue(e.getMessage().contains("notInContainer")
                    || e.getMessage().contains("notThereEither"));
            assertTrue(
                    e.getMessage().contains("does not exist in the container"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void multipleAddColumnsForDefaultContainer() {
        Grid grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("foo");
    }

    @Test
    public void testSerializeRpcDataProviderWithRowChanges()
            throws IOException {
        Grid grid = new Grid();
        IndexedContainer container = new IndexedContainer();
        grid.setContainerDataSource(container);
        container.addItem();
        serializeComponent(grid);
    }

    protected void serializeComponent(Component component) throws IOException {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(NULLSTREAM);
            stream.writeObject(component);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
