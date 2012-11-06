package com.vaadin.tests.tickets;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket2002 extends LegacyApplication {
    private Long long1 = new Long(1L);
    private Long long2 = new Long(2L);

    public Long getLong1() {
        return long1;
    }

    public void setLong1(Long long1) {
        this.long1 = long1;
    }

    public Long getLong2() {
        return long2;
    }

    public void setLong2(Long long2) {
        this.long2 = long2;
    }

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);

        TextField f1 = new TextField("Non-immediate/Long text field",
                new MethodProperty<Long>(this, "long1"));
        f1.setImmediate(false);
        f1.setNullSettingAllowed(true);
        TextField f2 = new TextField("Immediate/Long text field",
                new MethodProperty<Long>(this, "long2"));
        f2.setImmediate(true);
        f2.setNullSettingAllowed(true);

        layout.addComponent(f1);
        layout.addComponent(f2);

        w.setContent(layout);
    }
}
