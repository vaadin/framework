package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;

public class TableClickAndDragOnIconAndComponents extends AbstractTestUI {

    private static final long serialVersionUID = -2534880024131980135L;
    private Table table;

    @Override
    protected void setup(VaadinRequest request) {
        table = new Table();
        table.addContainerProperty("foo", String.class, "foo");
        table.addContainerProperty("red", String.class, "red");
        table.addContainerProperty("icon", Resource.class, null);
        table.setSelectable(true);
        table.setRowHeaderMode(RowHeaderMode.ICON_ONLY);
        table.setItemIconPropertyId("icon");
        table.setId("testable-table");
        addComponent(table);
        for (int i = 0; i < 5; i++) {
            addItemAfter(i + "foo", null);
        }

        table.addGeneratedColumn("Label", new ColumnGenerator() {

            private static final long serialVersionUID = -5042109683675242407L;

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = source.getItem(itemId);
                return new Label("" + item.getItemProperty("foo").getValue());
            }
        });
        table.addGeneratedColumn("textField", new ColumnGenerator() {

            private static final long serialVersionUID = -5042109683675242407L;

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = source.getItem(itemId);
                TextField textfield = new TextField();
                textfield.setValue(String.valueOf(item.getItemProperty("foo")
                        .getValue()));
                return textfield;
            }
        });
        table.addGeneratedColumn("readOnlyTextField", new ColumnGenerator() {

            private static final long serialVersionUID = -5042109683675242407L;

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = source.getItem(itemId);
                TextField textfield = new TextField();
                textfield.setValue(String.valueOf(item.getItemProperty("foo")
                        .getValue()));
                textfield.setReadOnly(true);
                return textfield;
            }
        });
        table.addGeneratedColumn("embedded", new ColumnGenerator() {

            private static final long serialVersionUID = -5042109683675242407L;

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Embedded embedded = new Embedded(null, new ThemeResource(
                        "../runo/icons/16/ok.png"));
                return embedded;
            }
        });

        table.setDragMode(TableDragMode.ROW);
        table.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                DataBoundTransferable t = (DataBoundTransferable) event
                        .getTransferable();
                Object dragged = t.getItemId();

                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) event
                        .getTargetDetails());
                Object target = dropData.getItemIdOver();

                if (dragged == target || target == null) {
                    return;
                }

                IndexedContainer container = (IndexedContainer) table
                        .getContainerDataSource();
                container.removeItem(dragged);
                addItemAfter(dragged, target);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void addItemAfter(Object itemId, Object afterItemId) {
        Item item;
        if (afterItemId != null) {
            item = table.addItemAfter(afterItemId, itemId);
        } else {
            item = table.addItem(itemId);
        }
        item.getItemProperty("foo").setValue("foo " + itemId);
        item.getItemProperty("red").setValue("red " + itemId);
        item.getItemProperty("icon").setValue(
                new ThemeResource("../runo/icons/16/ok.png"));
    }

    @Override
    protected String getTestDescription() {
        return "Tests that you can click on a row icon in a table to select the row, or to drag the row. Verifies also that the table doesn't capture the click events meant for components inside the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7026;
    }

}
