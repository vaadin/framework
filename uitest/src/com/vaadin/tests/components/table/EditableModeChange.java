package com.vaadin.tests.components.table;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;

public class EditableModeChange extends AbstractTestUI {

    private ItemClickEvent selectionEvent;

    private final String[] names = { "Teemu", "Teppo", "Seppo", "Matti",
            "Pekka" };

    @SuppressWarnings("unchecked")
    @Override
    public void setup(VaadinRequest request) {

        final Table items = new Table("Items - double-click to edit");
        items.setSelectable(true);
        items.addContainerProperty("name", String.class, "");
        items.addContainerProperty("birthday", Date.class, "");

        final TableFieldFactory fieldFactory = new ItemFieldFactory();
        items.setTableFieldFactory(fieldFactory);

        Calendar cal = Calendar.getInstance();
        cal.set(2010, 7, 12, 12, 7, 54);

        for (String name : names) {
            items.addItem(name);
            items.getItem(name).getItemProperty("name").setValue(name);
            items.getItem(name).getItemProperty("birthday")
                    .setValue(new FormattedDate(cal.getTime().getTime()));
        }

        items.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    selectionEvent = event;
                    items.setEditable(true);
                } else if (items.isEditable()) {
                    items.setEditable(false);
                }
            }
        });

        addComponent(items);
    }

    private class FormattedDate extends Date {

        private DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("en", "US"));

        public FormattedDate(long time) {
            super(time);
        }

        @Override
        public String toString() {
            return formatter.format(this);
        }
    }

    private class ItemFieldFactory extends DefaultFieldFactory {
        @Override
        public Field<?> createField(Container container, Object itemId,
                Object propertyId, Component uiContext) {
            if (selectionEvent != null) {
                if ((selectionEvent.getItemId().equals(itemId))
                        && (selectionEvent.getPropertyId().equals(propertyId))) {
                    return super.createField(container, itemId, propertyId,
                            uiContext);
                }
            }
            return null;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Double click a cell to edit, then click on another row to select it (editmode is set to false). The clicked row should now be selected without any flickering.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5427;
    }
}
