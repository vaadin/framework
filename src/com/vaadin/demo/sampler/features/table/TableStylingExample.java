package com.vaadin.demo.sampler.features.table;

import java.util.HashMap;
import java.util.HashSet;

import com.vaadin.data.Item;
import com.vaadin.demo.sampler.ExampleUtil;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.CellStyleGenerator;

public class TableStylingExample extends VerticalLayout {

    Table table = new Table();

    HashMap<Object, String> markedRows = new HashMap<Object, String>();
    HashMap<Object, HashSet<Object>> markedCells = new HashMap<Object, HashSet<Object>>();

    static final Action ACTION_RED = new Action("red");
    static final Action ACTION_BLUE = new Action("blue");
    static final Action ACTION_GREEN = new Action("green");
    static final Action ACTION_NONE = new Action("none");
    static final Action[] ACTIONS = new Action[] { ACTION_RED, ACTION_GREEN,
            ACTION_BLUE, ACTION_NONE };

    public TableStylingExample() {
        setSpacing(true);

        addComponent(table);

        // set a style name, so we can style rows and cells
        table.setStyleName("contacts");

        // size
        table.setWidth("100%");
        table.setPageLength(7);

        // connect data source
        table.setContainerDataSource(ExampleUtil.getPersonContainer());

        // Generate the email-link from firstname & lastname
        table.addGeneratedColumn("Email", new Table.ColumnGenerator() {
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = table.getItem(itemId);
                String fn = (String) item.getItemProperty(
                        ExampleUtil.PERSON_PROPERTY_FIRSTNAME).getValue();
                String ln = (String) item.getItemProperty(
                        ExampleUtil.PERSON_PROPERTY_LASTNAME).getValue();
                String email = fn.toLowerCase() + "." + ln.toLowerCase()
                        + "@example.com";
                // the Link -component:
                Link emailLink = new Link(email, new ExternalResource("mailto:"
                        + email));
                return emailLink;
            }

        });

        // turn on column reordering and collapsing
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        // Actions (a.k.a context menu)
        table.addActionHandler(new Action.Handler() {
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS;
            }

            public void handleAction(Action action, Object sender, Object target) {
                markedRows.remove(target);
                if (!ACTION_NONE.equals(action)) {
                    // we're using the cations caption as stylename as well:
                    markedRows.put(target, action.getCaption());
                }
                // this causes the CellStyleGenerator to return new styles,
                // but table can't automatically know, we must tell it:
                table.requestRepaint();
            }

        });

        // style generator
        table.setCellStyleGenerator(new CellStyleGenerator() {
            public String getStyle(Object itemId, Object propertyId) {
                if (propertyId == null) {
                    // no propertyId, styling row
                    return (markedRows.get(itemId));
                } else if (propertyId.equals("Email")) {
                    // style the generated email column
                    return "email";
                } else {
                    HashSet<Object> cells = markedCells.get(itemId);
                    if (cells != null && cells.contains(propertyId)) {
                        // marked cell
                        return "marked";
                    } else {
                        // no style
                        return null;
                    }
                }

            }

        });

        // toggle cell 'marked' styling when double-clicked
        table.addListener(new ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    Object itemId = event.getItemId();
                    Object propertyId = event.getPropertyId();
                    HashSet<Object> cells = markedCells.get(itemId);
                    if (cells == null) {
                        cells = new HashSet<Object>();
                        markedCells.put(itemId, cells);
                    }
                    if (cells.contains(propertyId)) {
                        // toggle marking off
                        cells.remove(propertyId);
                    } else {
                        // toggle marking on
                        cells.add(propertyId);
                    }
                    // this causes the CellStyleGenerator to return new styles,
                    // but table can't automatically know, we must tell it:
                    table.requestRepaint();
                }
            }
        });

        // Editing
        // we don't want to update container before pressing 'save':
        table.setWriteThrough(false);
        // edit button
        final Button editButton = new Button("Edit");
        addComponent(editButton);
        editButton.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                table.setEditable(!table.isEditable());
                editButton.setCaption((table.isEditable() ? "Save" : "Edit"));
            }
        });
        setComponentAlignment(editButton, "right");
    }
}
