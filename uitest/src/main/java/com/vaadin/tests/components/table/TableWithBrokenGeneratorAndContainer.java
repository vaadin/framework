package com.vaadin.tests.components.table;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.event.ListenerMethod.MethodException;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CacheUpdateException;
import com.vaadin.v7.ui.Table.ColumnGenerator;

public class TableWithBrokenGeneratorAndContainer extends TestBase {

    private CheckBox brokenContainer = new CheckBox("Broken container");
    private CheckBox brokenGenerator = new CheckBox("Broken generator");
    private CheckBox clearTableOnError = new CheckBox("Clear Table on Error");

    /**
     * Container which throws an exception on every fifth call to
     * {@link #getContainerProperty(Object, Object)}.
     *
     * @author Vaadin Ltd
     * @since 7.0
     *
     */
    public class BrokenContainer extends IndexedContainer {
        private int counter = 0;

        public BrokenContainer() {
            super();
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            if (counter++ % 5 == 0
                    && Boolean.TRUE.equals(brokenContainer.getValue())) {
                throw new RuntimeException(getClass().getSimpleName()
                        + " cannot fetch the property for " + itemId + "/"
                        + propertyId + " right now");
            }
            return super.getContainerProperty(itemId, propertyId);
        }
    }

    public class BrokenColumnGenerator implements ColumnGenerator {
        private int brokenInterval;
        private int counter = 0;

        public BrokenColumnGenerator(int brokenInterval) {
            this.brokenInterval = brokenInterval;
        }

        @Override
        public Object generateCell(Table source, Object itemId,
                Object columnId) {
            if (counter++ % brokenInterval == 0
                    && Boolean.TRUE.equals(brokenGenerator.getValue())) {
                throw new IllegalArgumentException(
                        "Broken generator for " + itemId + "/" + columnId);
            } else {
                return "Generated " + itemId + "/" + columnId;
            }
        }

    }

    @Override
    protected void setup() {
        clearTableOnError.addValueChangeListener(event -> {
            Boolean value = clearTableOnError.getValue();
            setErrorHandler(value != null ? value : false);
        });
        final Table table = new Table("Semi-broken table");
        table.setContainerDataSource(createBrokenContainer(10, 4));
        table.addGeneratedColumn("Gen", new BrokenColumnGenerator(4));
        table.setPageLength(20);

        Button refreshTableCache = new Button("Refresh table cache", event -> {
            table.markAsDirty();
            table.refreshRowCache();
        });
        addComponent(refreshTableCache);
        addComponent(brokenContainer);
        addComponent(brokenGenerator);
        addComponent(clearTableOnError);
        addComponent(table);
    }

    protected void setErrorHandler(boolean enabled) {
        if (enabled) {
            VaadinSession.getCurrent().setErrorHandler(event -> {
                Throwable t = event.getThrowable();
                if (t instanceof RpcInvocationException) {
                    t = t.getCause();
                    if (t instanceof InvocationTargetException) {
                        t = t.getCause();
                        if (t instanceof MethodException) {
                            t = t.getCause();
                            if (t instanceof CacheUpdateException) {
                                Table table = ((CacheUpdateException) t)
                                        .getTable();
                                table.removeAllItems();
                                Notification.show(
                                        "Problem updating table. Please try again later",
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });
        } else {
            VaadinSession.getCurrent().setErrorHandler(this);
        }
    }

    private BrokenContainer createBrokenContainer(int rows, int cols) {
        BrokenContainer container = new BrokenContainer();
        for (int j = 1; j <= cols; j++) {
            container.addContainerProperty("prop" + j, String.class, null);
        }
        for (int i = 1; i <= rows; i++) {
            Item item = container.addItem("item" + i);
            for (int j = 1; j <= cols; j++) {
                item.getItemProperty("prop" + j)
                        .setValue("item" + i + "/prop" + j);
            }
        }
        return container;
    }

    @Override
    protected Integer getTicketNumber() {
        return 10312;
    }

    @Override
    protected String getDescription() {
        return "A Table should not show 'Internal Error' just because a column generator or container throws an exception during filling of the cache";
    }

}
