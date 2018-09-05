package com.vaadin.tests.components.table;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.Table;

public class TableUndefinedSize extends TestBase {

    private Log log;

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

        log = new Log(5);

        controls.addComponent(new Button("Fixed size (200x200)", event -> {
            tbl.setWidth("200px");
            tbl.setHeight("200px");
            log.log("Size 200x200 pixels");
        }));

        controls.addComponent(new Button("Fixed size (600x200)", event -> {
            tbl.setWidth("600px");
            tbl.setHeight("200px");
            log.log("Size 600x200 pixels");
        }));

        controls.addComponent(new Button("Undefined size", event -> {
            tbl.setSizeUndefined();
            log.log("Size undefined");
        }));

        NativeSelect pageLength = new NativeSelect("PageLength",
                Arrays.asList(0, 1, 2, 4, 8, 10));
        pageLength.setImmediate(true);
        pageLength.setNullSelectionAllowed(false);
        pageLength.addValueChangeListener(event -> {
            int pageLengthValue = Integer
                    .valueOf(event.getProperty().getValue().toString());
            tbl.setPageLength(pageLengthValue);
            log.log("Page length: " + pageLengthValue);
        });
        controls.addComponent(pageLength);

        CheckBox cb = new CheckBox("Column 1");
        cb.setValue(true);
        cb.addValueChangeListener(event -> {
            Boolean value = event.getValue();
            tbl.setColumnCollapsed("Column 1", !value);
            if (value) {
                log.log("Column 1 visible");
            } else {
                log.log("Column 1 hidden");
            }
        });
        visibilities.addComponent(cb);

        cb = new CheckBox("Column 2");
        cb.setValue(true);
        cb.addValueChangeListener(event -> {
            Boolean value = event.getValue();
            tbl.setColumnCollapsed("Column 2", !value);

            if (value) {
                log.log("Column 2 visible");
            } else {
                log.log("Column 2 hidden");
            }
        });
        visibilities.addComponent(cb);

        cb = new CheckBox("Column 3");
        cb.setValue(true);
        cb.addValueChangeListener(event -> {
            Boolean value = event.getValue();

            tbl.setColumnCollapsed("Column 3", !value);

            if (value) {
                log.log("Column 3 visible");
            } else {
                log.log("Column 3 hidden");
            }
        });
        visibilities.addComponent(cb);

        addComponent(log);
        addComponent(tbl);

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
