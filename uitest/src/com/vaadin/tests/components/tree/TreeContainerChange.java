package com.vaadin.tests.components.tree;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class TreeContainerChange extends TestBase {

    private Tree tree;

    @Override
    protected void setup() {
        final IndexedContainer ds1 = new IndexedContainer();
        // ds1.addContainerProperty("caption", String.class, "");
        for (int i = 0; i < 32; i++) {
            ds1.addItem("ds1-" + i);
        }

        final IndexedContainer ds2 = new IndexedContainer();
        // ds2.addContainerProperty("caption", String.class, "");
        for (int i = 0; i < 32; i++) {
            ds2.addItem("ds2-" + i);
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");

        tree = new Tree();
        tree.setImmediate(true);
        hl.addComponent(tree);
        HorizontalLayout state = new HorizontalLayout();
        state.setSpacing(true);
        hl.addComponent(state);

        final Label currentValue = new Label();
        currentValue.setCaption("Current Value:");
        currentValue.setSizeUndefined();
        final Label currentDS = new Label();
        currentDS.setCaption("Current DS:");
        currentDS.setSizeUndefined();
        state.addComponent(currentValue);
        state.addComponent(currentDS);

        Table t = new Table("ds1");
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        t.setContainerDataSource(ds1);
        state.addComponent(t);

        Button b = new Button("Use ds1");
        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.setContainerDataSource(ds1);
                currentDS.setValue("ds1");
            }
        });
        state.addComponent(b);

        t = new Table("ds2");
        t.setContainerDataSource(ds2);
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        state.addComponent(t);

        b = new Button("Use ds2");
        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.setContainerDataSource(ds2);
                currentDS.setValue("ds2");
            }
        });
        state.addComponent(b);

        addComponent(hl);

        tree.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                currentValue
                        .setValue(event.getProperty().getValue().toString());
            }
        });
    }

    @Override
    protected String getDescription() {
        return "A test for tree and its container changes. Value should be cleared on container change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5281;
    }

}
