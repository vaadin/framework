package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;

public class HeaderFooterClickLeftRightMiddle extends TestBase {

    private Log log = new Log(10);

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);
        table.setFooterVisible(true);

        CheckBox immediateCheckbox = new CheckBox("Immediate");
        immediateCheckbox.setImmediate(true);
        immediateCheckbox.setValue(table.isImmediate());
        immediateCheckbox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setImmediate((Boolean) event.getProperty().getValue());
            }
        });

        CheckBox headerClickListenerCheckbox = new CheckBox(
                "Header click listener");
        headerClickListenerCheckbox.setImmediate(true);
        headerClickListenerCheckbox.addListener(new ValueChangeListener() {

            private HeaderClickListener headerClickListener = new HeaderClickListener() {

                @Override
                public void headerClick(HeaderClickEvent event) {
                    String type = event.isDoubleClick() ? "Double click"
                            : "Click";
                    log.log(type + " on header "
                            + event.getPropertyId().toString() + " using "
                            + event.getButtonName());
                }

            };

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (table.getListeners(HeaderClickEvent.class).isEmpty()) {
                    table.addListener(headerClickListener);
                } else {
                    table.removeListener(headerClickListener);
                }
            }
        });
        headerClickListenerCheckbox.setValue(true);

        CheckBox footerClickListenerCheckbox = new CheckBox(
                "Footer click listener");
        footerClickListenerCheckbox.setImmediate(true);
        footerClickListenerCheckbox.addListener(new ValueChangeListener() {

            private FooterClickListener footerClickListener = new FooterClickListener() {

                @Override
                public void footerClick(FooterClickEvent event) {
                    String type = event.isDoubleClick() ? "Double click"
                            : "Click";
                    log.log(type + " on footer "
                            + event.getPropertyId().toString() + " using "
                            + event.getButtonName());
                }
            };

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (table.getListeners(FooterClickEvent.class).isEmpty()) {
                    table.addListener(footerClickListener);
                } else {
                    table.removeListener(footerClickListener);
                }
            }
        });
        footerClickListenerCheckbox.setValue(true);

        CheckBox sortEnabledCheckbox = new CheckBox("Sortable");
        sortEnabledCheckbox.setImmediate(true);
        sortEnabledCheckbox.setValue(table.isSortEnabled());
        sortEnabledCheckbox.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setSortEnabled((Boolean) event.getProperty().getValue());
            }
        });

        CheckBox columnReorderingCheckbox = new CheckBox(
                "Column reordering allowed");
        columnReorderingCheckbox.setImmediate(true);
        columnReorderingCheckbox.setValue(table.isColumnReorderingAllowed());
        columnReorderingCheckbox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setColumnReorderingAllowed((Boolean) event.getProperty()
                        .getValue());
            }
        });

        addComponent(immediateCheckbox);
        addComponent(headerClickListenerCheckbox);
        addComponent(footerClickListenerCheckbox);
        addComponent(sortEnabledCheckbox);
        addComponent(columnReorderingCheckbox);
        addComponent(table);
        addComponent(log);

    }

    @Override
    protected String getDescription() {
        return "Tests the header click listener";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4515;
    }

    private Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("col1", String.class, "");
        container.addContainerProperty("col2", String.class, "");
        container.addContainerProperty("col3", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty("col1").setValue("first" + i);
            item.getItemProperty("col2").setValue("middle" + i);
            item.getItemProperty("col3").setValue("last" + i);
        }

        return container;
    }

}
