package com.vaadin.tests.components.table;

import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.FooterClickEvent;
import com.vaadin.v7.ui.Table.FooterClickListener;
import com.vaadin.v7.ui.Table.HeaderClickEvent;
import com.vaadin.v7.ui.Table.HeaderClickListener;

public class HeaderFooterClickLeftRightMiddle extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);
        table.setFooterVisible(true);

        CheckBox immediateCheckbox = new CheckBox("Immediate");
        immediateCheckbox.setValue(table.isImmediate());
        immediateCheckbox.addValueChangeListener(
                event -> table.setImmediate(event.getValue()));

        CheckBox headerClickListenerCheckbox = new CheckBox(
                "Header click listener");
        headerClickListenerCheckbox.addValueChangeListener(
                new HasValue.ValueChangeListener<Boolean>() {

                    private HeaderClickListener headerClickListener = new HeaderClickListener() {

                        @Override
                        public void headerClick(HeaderClickEvent event) {
                            String type = event.isDoubleClick() ? "Double click"
                                    : "Click";
                            log(type + " on header "
                                    + event.getPropertyId().toString()
                                    + " using " + event.getButtonName());
                        }

                    };

                    @Override
                    public void valueChange(ValueChangeEvent<Boolean> event) {
                        if (table.getListeners(HeaderClickEvent.class)
                                .isEmpty()) {
                            table.addHeaderClickListener(headerClickListener);
                        } else {
                            table.removeHeaderClickListener(
                                    headerClickListener);
                        }
                    }
                });
        headerClickListenerCheckbox.setValue(true);

        CheckBox footerClickListenerCheckbox = new CheckBox(
                "Footer click listener");
        footerClickListenerCheckbox.addValueChangeListener(
                new HasValue.ValueChangeListener<Boolean>() {

                    private FooterClickListener footerClickListener = new FooterClickListener() {

                        @Override
                        public void footerClick(FooterClickEvent event) {
                            String type = event.isDoubleClick() ? "Double click"
                                    : "Click";
                            log(type + " on footer "
                                    + event.getPropertyId().toString()
                                    + " using " + event.getButtonName());
                        }
                    };

                    @Override
                    public void valueChange(ValueChangeEvent<Boolean> event) {
                        if (table.getListeners(FooterClickEvent.class)
                                .isEmpty()) {
                            table.addFooterClickListener(footerClickListener);
                        } else {
                            table.removeFooterClickListener(
                                    footerClickListener);
                        }
                    }
                });
        footerClickListenerCheckbox.setValue(true);

        CheckBox sortEnabledCheckbox = new CheckBox("Sortable");
        sortEnabledCheckbox.setValue(table.isSortEnabled());
        sortEnabledCheckbox.addValueChangeListener(
                event -> table.setSortEnabled(event.getValue()));

        CheckBox columnReorderingCheckbox = new CheckBox(
                "Column reordering allowed");
        columnReorderingCheckbox.setValue(table.isColumnReorderingAllowed());
        columnReorderingCheckbox.addValueChangeListener(
                event -> table.setColumnReorderingAllowed(event.getValue()));

        addComponent(immediateCheckbox);
        addComponent(headerClickListenerCheckbox);
        addComponent(footerClickListenerCheckbox);
        addComponent(sortEnabledCheckbox);
        addComponent(columnReorderingCheckbox);
        addComponent(table);

    }

    @Override
    protected String getTestDescription() {
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
