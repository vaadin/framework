package com.vaadin.tests.components.combobox;

import com.vaadin.server.data.DataSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxInvalidNullSelection extends TestBase {

    private static final Object CAPTION = "C";
    private DataSource<String> ds1;
    private DataSource<String> ds2;
    private ComboBox<String> combo;
    private Log log = new Log(5);

    @Override
    protected void setup() {

        createDataSources();

        Button b = new Button("Swap data source");
        b.addClickListener(event -> {
            if (combo.getDataSource() == ds1) {
                combo.setDataSource(ds2);
            } else {
                combo.setDataSource(ds1);
            }
            combo.setValue("Item 3");
        });

        combo = new ComboBox<>();
        combo.setDataSource(ds1);
        combo.addValueChangeListener(
                event -> log.log("Value is now: " + combo.getValue()));
        addComponent(log);
        addComponent(b);
        addComponent(combo);
        addComponent(new Button("Dummy for TestBench"));
    }

    private void createDataSources() {
        ds1 = DataSource.create("Item 1", "Item 2", "Item 3", "Item 4");

        ds2 = DataSource.create("Item 3");

    }

    @Override
    protected String getDescription() {
        return "Select \"Item 3\" in the ComboBox, change the data source, focus and blur the ComboBox. The value should temporarily change to null when changing data source but not when focusing and blurring the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6170;
    }

}
