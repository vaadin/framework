package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;

public class ComboBoxSlow extends TestBase {

    private Log log = new Log(5);

    @Override
    protected Integer getTicketNumber() {
        return 7949;
    }

    @Override
    protected String getDescription() {
        return "The ComboBox artificially introduces a server delay to more easily spot problems";
    }

    @Override
    protected void setup() {
        addComponent(log);
        final SlowComboBox cb = new SlowComboBox();
        cb.setImmediate(true);
        for (int i = 0; i <= 1000; i++) {
            cb.addItem("Item " + i);
        }
        cb.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changed to " + cb.getValue());
            }
        });
        addComponent(cb);
    }
}
