package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Footer extends TestBase {

    @Override
    protected void setup() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        final Table table = new Table();
        table.setWidth("400px");
        table.setHeight("400px");

        table.setContainerDataSource(createContainer());
        table.setImmediate(true);

        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);

        table.setFooterVisible(true);

        table.setColumnFooter("col1", "Footer1");
        table.setColumnFooter("col2", "Footer2");
        table.setColumnFooter("col3", "Footer3");

        table.setColumnAlignment("col2", Table.ALIGN_CENTER);
        table.setColumnAlignment("col3", Table.ALIGN_RIGHT);

        layout.addComponent(table);

        // Add some options to play with
        VerticalLayout options = new VerticalLayout();
        options.setSpacing(true);

        final CheckBox visible = new CheckBox("Footers Visible", true);
        visible.setImmediate(true);
        visible.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setFooterVisible(visible.getValue());

            }
        });

        options.addComponent(visible);

        final TextField footer1Value = new TextField(null, "Footer1");
        footer1Value.setImmediate(true);
        Button footer1Btn = new Button("Change", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnFooter("col1",
                        footer1Value.getValue() == null ? "" : footer1Value
                                .getValue().toString());
            }
        });
        HorizontalLayout footer1 = new HorizontalLayout();
        footer1.addComponent(footer1Value);
        footer1.addComponent(footer1Btn);
        options.addComponent(footer1);

        final TextField footer2Value = new TextField(null, "Footer2");
        footer2Value.setImmediate(true);
        Button footer2Btn = new Button("Change", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnFooter("col2",
                        footer2Value.getValue() == null ? "" : footer2Value
                                .getValue().toString());
            }
        });
        HorizontalLayout footer2 = new HorizontalLayout();
        footer2.addComponent(footer2Value);
        footer2.addComponent(footer2Btn);
        options.addComponent(footer2);

        final TextField footer3Value = new TextField(null, "Footer3");
        footer3Value.setImmediate(true);
        Button footer3Btn = new Button("Change", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnFooter("col3",
                        footer3Value.getValue() == null ? "" : footer3Value
                                .getValue().toString());
            }
        });
        HorizontalLayout footer3 = new HorizontalLayout();
        footer3.addComponent(footer3Value);
        footer3.addComponent(footer3Btn);
        options.addComponent(footer3);

        layout.addComponent(options);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Table with footer";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1553;
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
