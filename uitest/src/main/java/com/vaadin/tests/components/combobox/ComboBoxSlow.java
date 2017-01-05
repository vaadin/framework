package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;

public class ComboBoxSlow extends AbstractReindeerTestUI {

    private Log log = new Log(5);

    @Override
    protected Integer getTicketNumber() {
        return 7949;
    }

    @Override
    protected String getTestDescription() {
        return "The ComboBox artificially introduces a server delay to more easily spot problems";
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        final SlowComboBox cb = new SlowComboBox();
        cb.setImmediate(true);
        for (int i = 0; i <= 1000; i++) {
            cb.addItem("Item " + i);
        }
        cb.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changed to " + cb.getValue());
            }
        });
        addComponent(cb);
    }
}
