package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/*
 * Differs from TableContextMenu by number of items, their numbering and 
 * immediate/selectable/multiselect toggling
 */
public class TableContextMenuTouch extends AbstractTestUI {

    private static final Action ACTION_MYACTION = new Action("Action!!");

    @Override
    protected void setup(VaadinRequest req) {

        HorizontalLayout hlay = new HorizontalLayout();
        addComponent(hlay);
        hlay.setSpacing(true);

        final Table table = new Table();

        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction(Action action, Object sender, Object target) {
                Notification.show("Done that :-)");
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { ACTION_MYACTION };
            }
        });

        table.addContainerProperty("Foo", String.class, "BAR1");
        table.addContainerProperty("Bar", String.class, "FOO2");

        table.setHeight("200px");

        for (int i = 0; i < 30; i++) {
            Object key = table.addItem();
            table.getItem(key).getItemProperty("Foo")
                    .setValue(new Integer(i).toString());
        }

        hlay.addComponent(table);

        VerticalLayout vlay = new VerticalLayout();
        hlay.addComponent(vlay);

        final CheckBox immediateCheckBox = new CheckBox("Immediate");
        vlay.addComponent(immediateCheckBox);
        immediateCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setImmediate(immediateCheckBox.getValue());
            }
        });
        immediateCheckBox.setValue(true);
        table.setImmediate(immediateCheckBox.getValue());

        final CheckBox selectableCheckBox = new CheckBox("Selectable");
        final CheckBox multiselectCheckBox = new CheckBox("Multiselect");
        vlay.addComponent(selectableCheckBox);
        selectableCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setSelectable(selectableCheckBox.getValue());
                multiselectCheckBox.setEnabled(selectableCheckBox.getValue());
            }
        });
        selectableCheckBox.setValue(true);
        multiselectCheckBox.setEnabled(selectableCheckBox.getValue());
        table.setSelectable(selectableCheckBox.getValue());

        vlay.addComponent(multiselectCheckBox);
        multiselectCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setMultiSelect(multiselectCheckBox.getValue());
            }
        });
        multiselectCheckBox.setValue(true);
        table.setMultiSelect(multiselectCheckBox.getValue());

    }

    @Override
    protected String getTestDescription() {
        return "Context menu in Table on touch devices should not open on selection tapping";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15297;
    }

}