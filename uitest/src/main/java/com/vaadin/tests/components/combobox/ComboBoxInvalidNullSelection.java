package com.vaadin.tests.components.combobox;

import com.vaadin.server.data.DataProvider;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxInvalidNullSelection extends TestBase {

    private static final Object CAPTION = "C";
    private DataProvider<String> ds1;
    private DataProvider<String> ds2;
    private ComboBox<String> combo;
    private Log log = new Log(5);

    @Override
    protected void setup() {

        createDataProviders();

        Button b = new Button("Swap data provider");
        b.addClickListener(event -> {
            if (combo.getDataProvider() == ds1) {
                combo.setDataProvider(ds2);
            } else {
                combo.setDataProvider(ds1);
            }
            combo.setValue("Item 3");
        });

        combo = new ComboBox<>();
        combo.setDataProvider(ds1);
        combo.addValueChangeListener(
                event -> log.log("Value is now: " + combo.getValue()));
        addComponent(log);
        addComponent(b);
        addComponent(combo);
        addComponent(new Button("Dummy for TestBench"));
    }

    private void createDataProviders() {
        ds1 = DataProvider.create("Item 1", "Item 2", "Item 3", "Item 4");

        ds2 = DataProvider.create("Item 3");

    }

    @Override
    protected String getDescription() {
        return "Select \"Item 3\" in the ComboBox, change the data provider, focus and blur the ComboBox. The value should temporarily change to null when changing data provider but not when focusing and blurring the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6170;
    }

}
