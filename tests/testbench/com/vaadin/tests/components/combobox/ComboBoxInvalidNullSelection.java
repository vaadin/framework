package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;

public class ComboBoxInvalidNullSelection extends TestBase {

    private static final Object CAPTION = "C";
    private IndexedContainer ds1;
    private IndexedContainer ds2;
    private ComboBox combo;
    private Log log = new Log(5);

    @Override
    protected void setup() {

        createDataSources();

        Button b = new Button("Swap data source");
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (combo.getContainerDataSource() == ds1) {
                    combo.setContainerDataSource(ds2);
                } else {
                    combo.setContainerDataSource(ds1);
                }
                combo.setValue("Item 3");
            }
        });

        combo = new ComboBox();
        combo.setImmediate(true);
        combo.setContainerDataSource(ds1);
        combo.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value is now: " + combo.getValue());
            }
        });
        addComponent(log);
        addComponent(b);
        addComponent(combo);
        addComponent(new Button("Dummy for TestBench"));
    }

    private void createDataSources() {
        ds1 = new IndexedContainer();
        ds1.addContainerProperty(CAPTION, String.class, "");
        ds1.addItem("Item 1");
        ds1.addItem("Item 2");
        ds1.addItem("Item 3");
        ds1.addItem("Item 4");

        ds2 = new IndexedContainer();
        ds2.addContainerProperty(CAPTION, String.class, "");
        ds2.addItem("Item 3");

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
