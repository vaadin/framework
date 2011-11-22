package com.vaadin.tests.components.table;

import java.util.Arrays;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;

public class TableUndefinedSize extends TestBase {

    private ObjectProperty<String> output = new ObjectProperty<String>("");

    private int counter = 1;

    @Override
    protected void setup() {

        HorizontalLayout controls = new HorizontalLayout();
        controls.setSpacing(true);
        addComponent(controls);

        HorizontalLayout visibilities = new HorizontalLayout();
        visibilities.setSpacing(true);
        addComponent(visibilities);

        final Table tbl = new Table("", createDataSource());
        tbl.setImmediate(true);
        tbl.setColumnCollapsingAllowed(true);

        Label output = new Label(this.output);
        output.setWidth("400px");
        output.setHeight("100px");
        output.setContentMode(Label.CONTENT_XHTML);

        controls.addComponent(new Button("Fixed size (200x200)",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        tbl.setWidth("200px");
                        tbl.setHeight("200px");
                        print("Size 200x200 pixels");
                    }
                }));

        controls.addComponent(new Button("Fixed size (600x200)",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        tbl.setWidth("600px");
                        tbl.setHeight("200px");
                        print("Size 600x200 pixels");
                    }
                }));

        controls.addComponent(new Button("Undefined size",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        tbl.setSizeUndefined();
                        print("Size undefined");
                    }
                }));

        NativeSelect pageLength = new NativeSelect("PageLength", Arrays.asList(
                0, 1, 2, 4, 8, 10));
        pageLength.setImmediate(true);
        pageLength.setNullSelectionAllowed(false);
        pageLength.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                int pageLength = Integer.valueOf(event.getProperty().getValue()
                        .toString());
                tbl.setPageLength(pageLength);
                print("Page length: " + pageLength);
            }
        });
        controls.addComponent(pageLength);

        CheckBox cb = new CheckBox("Column 1");
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Boolean value = (Boolean) event.getProperty().getValue();
                tbl.setColumnCollapsed("Column 1", !value);
                if (value) {
                    print("Column 1 visible");
                } else {
                    print("Column 1 hidden");
                }
            }
        });
        cb.setImmediate(true);
        cb.setValue(true);
        visibilities.addComponent(cb);

        cb = new CheckBox("Column 2");
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Boolean value = (Boolean) event.getProperty().getValue();
                tbl.setColumnCollapsed("Column 2", !value);

                if (value) {
                    print("Column 2 visible");
                } else {
                    print("Column 2 hidden");
                }
            }
        });
        cb.setImmediate(true);
        cb.setValue(true);
        visibilities.addComponent(cb);

        cb = new CheckBox("Column 3");

        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Boolean value = (Boolean) event.getProperty().getValue();

                tbl.setColumnCollapsed("Column 3", !value);

                if (value) {
                    print("Column 3 visible");
                } else {
                    print("Column 3 hidden");
                }
            }
        });
        cb.setImmediate(true);
        cb.setValue(true);
        visibilities.addComponent(cb);

        addComponent(output);
        addComponent(tbl);

    }

    protected void print(String message) {
        output.setValue(counter + ": " + message + "<br/>" + output.getValue());
        counter++;
    }

    protected Container createDataSource() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty("Column 1", String.class, "Column 1");
        c.addContainerProperty("Column 2", String.class, "Column 2");
        c.addContainerProperty("Column 3", String.class, "Column 3");

        for (int i = 0; i < 50; i++) {
            c.addItem();
        }

        return c;
    }

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5789;
    }

}
