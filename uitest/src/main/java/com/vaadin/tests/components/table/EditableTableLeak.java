package com.vaadin.tests.components.table;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class EditableTableLeak extends TestBase {
    private final Table table = new Table("ISO-3166 Country Codes and flags");
    private final CheckBox useFieldFactory = new CheckBox(
            "Use a caching TableFieldFactory");
    private final Label sizeLabel = new Label("", ContentMode.HTML);

    private long size = 0;

    static class DebugUtils {
        private static class ByteCountNullOutputStream extends OutputStream
                implements Serializable {
            private static final long serialVersionUID = 4220043426041762877L;
            private long bytes;

            @Override
            public void write(int b) {
                bytes++;
            }

            public long getBytes() {
                return bytes;
            }
        }

        public static long getSize(Object object) {
            ByteCountNullOutputStream os = new ByteCountNullOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(os);
                oos.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return os.getBytes();
        }
    }

    private static class CachingFieldFactory extends DefaultFieldFactory {
        private final HashMap<Object, HashMap<Object, Field<?>>> cache = new HashMap<Object, HashMap<Object, Field<?>>>();

        @Override
        public Field<?> createField(Container container, Object itemId,
                Object propertyId, Component uiContext) {
            if (cache.containsKey(itemId)) {
                if (cache.get(itemId) != null
                        && cache.get(itemId).containsKey(propertyId)) {
                    return cache.get(itemId).get(propertyId);
                }
            }
            Field<?> f = super.createField(container, itemId, propertyId,
                    uiContext);
            if (!cache.containsKey(itemId)) {
                cache.put(itemId, new HashMap<Object, Field<?>>());
            }
            cache.get(itemId).put(propertyId, f);
            return f;
        }

    }

    @Override
    protected void setup() {
        addComponent(useFieldFactory);
        useFieldFactory.setImmediate(true);
        useFieldFactory.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (useFieldFactory.getValue()) {
                    table.setTableFieldFactory(new CachingFieldFactory());
                } else {
                    table.setTableFieldFactory(DefaultFieldFactory.get());
                }
            }
        });
        addComponent(table);
        table.setEditable(true);
        table.setWidth("100%");
        table.setHeight("170px");
        table.setSelectable(true);
        table.setContainerDataSource(TestUtils.getISO3166Container());
        table.setColumnHeaders(new String[] { "Country", "Code" });
        table.setColumnAlignment(TestUtils.iso3166_PROPERTY_SHORT,
                Table.ALIGN_CENTER);
        table.setColumnExpandRatio(TestUtils.iso3166_PROPERTY_NAME, 1);
        table.setColumnWidth(TestUtils.iso3166_PROPERTY_SHORT, 70);

        addComponent(sizeLabel);

        addComponent(new Button("Show size of the table", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.markAsDirtyRecursive();
                updateSize();
            }

        }));

        addComponent(new Button("Select the second row", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.select("AL");
                updateSize();
            }
        }));
    }

    private void updateSize() {
        System.gc();
        long newSize = DebugUtils.getSize(table);
        sizeLabel.setValue("Size of the table: " + newSize
                + " bytes<br/>Delta: " + (newSize - size));
        size = newSize;
    }

    @Override
    protected String getDescription() {
        return "Table leaks memory while scrolling/selecting when in editable mode";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6071;
    }

}
