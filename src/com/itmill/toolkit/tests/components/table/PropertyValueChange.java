package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.FieldFactory;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.AbstractSelect.NewItemHandler;
import com.itmill.toolkit.ui.Table.ColumnGenerator;

public class PropertyValueChange extends TestBase {

    @Override
    protected String getDescription() {
        return "Property value change should only update absolutely "
                + "needed cells. Tables have common datasource. The first is "
                + "editable, second one has data in disabled fields, the lastone "
                + "is plain table that directly shows data. Use first table and "
                + "combobox/sync button to send changed values to server and evaluate "
                + "given uidl responses.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2823;
    }

    private IndexedContainer container;

    // Also use column generator in test, to ensure it is possible to build
    // columns that update automatically.
    ColumnGenerator multiplier = new ColumnGenerator() {
        private int getMultipliedValue(Property p) {
            int i = ((Integer) p.getValue()).intValue();
            return i * 3;
        }

        public Component generateCell(Table source, Object itemId,
                Object columnId) {
            final Label l = new Label();
            final Property integer = source.getContainerProperty(itemId,
                    "integer");
            l.setValue(getMultipliedValue(integer));

            // we must hook value change listener to ensure updates in all use
            // cases (eg. edit mode)
            if (integer instanceof Property.ValueChangeNotifier) {
                Property.ValueChangeNotifier notifier = (Property.ValueChangeNotifier) integer;
                notifier.addListener(new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        l.setValue(getMultipliedValue(integer));
                    }
                });
            }
            return l;
        }
    };

    FieldFactory ff = new MyFieldFactory();

    @Override
    public void setup() {
        container = new IndexedContainer();

        container.addContainerProperty("text", String.class, "sampletext");
        container.addContainerProperty("integer", Integer.class, 5);

        container.addItem();
        container.addItem();

        Table t1 = new Table(
                "Editable table with bells and wistles. See description.");
        t1.setDescription("Opening combobox should never fire table"
                + " refresh (for this table). Update from textfield "
                + "(integer) may be sent to server however. The readonly table"
                + " my refresh, but not this one.");
        t1.setPageLength(0);
        t1.setContainerDataSource(container);
        t1.addGeneratedColumn("integer x 3", multiplier);
        t1.setFieldFactory(ff);
        t1.setEditable(true);
        t1.setDebugId("editortable");

        Table t2 = new Table(
                "A clone of table1, but disabled. Properties are in components.");
        t2
                .setDescription("This table is in editable mode."
                        + " Updates to common datasource should not affect redraw for this "
                        + "table. Only the components inside table should get updated.");
        t2.setFieldFactory(ff);
        t2.setEditable(true);
        t2.setEnabled(false);
        t2.setContainerDataSource(container);
        t2.addGeneratedColumn("integer x 3", multiplier);
        t2.setPageLength(0);
        t2.setDebugId("disabled table");

        Table reader = new Table("Reader table");
        reader
                .setDescription("This table should be redrawn on container changes as container data is "
                        + "displayed directly in cells.");
        reader.setContainerDataSource(container);
        reader.addGeneratedColumn("integer x 3", multiplier);
        reader.setPageLength(0);
        reader.setDebugId("reader table");

        getLayout().addComponent(t1);
        getLayout().addComponent(t2);
        getLayout().addComponent(reader);
        getLayout().addComponent(new Button("Sync!"));

    }
}

class MyFieldFactory extends BaseFieldFactory {

    IndexedContainer texts = new IndexedContainer();

    public MyFieldFactory() {
        texts.addItem("sampletext");
        texts.addItem("foo");
        texts.addItem("bar");
        for (int i = 0; i < 100; i++) {
            texts.addItem("foo" + 1);
        }

    }

    @Override
    public Field createField(Container container, Object itemId,
            Object propertyId, Component uiContext) {
        if (propertyId.equals("text")) {
            // replace text fields with comboboxes
            final ComboBox cb = new ComboBox() {
            };
            cb.setContainerDataSource(texts);
            cb.setNewItemsAllowed(true);
            cb.setNewItemHandler(new NewItemHandler() {
                public void addNewItem(String newItemCaption) {
                    texts.addItem(newItemCaption);
                    cb.setValue(newItemCaption);
                }
            });
            return cb;
        }

        return super.createField(container, itemId, propertyId, uiContext);
    }
}
