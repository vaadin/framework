package com.vaadin.tests.server.component.grid;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.RpcDataProviderExtension;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

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

        Assert.assertEquals("DetailsGenerator changed", detGen,
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

        Assert.assertEquals("foo", grid.getColumns().get(0).getPropertyId());
        Assert.assertEquals("baz", grid.getColumns().get(1).getPropertyId());
        Assert.assertEquals("bar", grid.getColumns().get(2).getPropertyId());
    }

    @Test
    public void addColumnNotInContainer() {
        Grid grid = new Grid();
        grid.setContainerDataSource(new IndexedContainer());
        try {
            grid.addColumn("notInContainer");
            Assert.fail(
                    "Adding a property id not in the container should throw an exception");
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("notInContainer"));
            Assert.assertTrue(
                    e.getMessage().contains("does not exist in the container"));
        }
    }

    @Test
    public void setColumnsForPropertyIdNotInContainer() {
        Grid grid = new Grid();
        grid.setContainerDataSource(new IndexedContainer());
        try {
            grid.setColumns("notInContainer", "notThereEither");
            Assert.fail(
                    "Setting columns for property ids not in the container should throw an exception");
        } catch (IllegalStateException e) {
            // addColumn is run in random order..
            Assert.assertTrue(e.getMessage().contains("notInContainer")
                    || e.getMessage().contains("notThereEither"));
            Assert.assertTrue(
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
