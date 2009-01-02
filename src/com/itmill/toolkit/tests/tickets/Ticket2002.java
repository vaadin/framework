package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2002 extends Application {
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
        Window w = new Window(getClass().getName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);

        TextField f1 = new TextField("Non-immediate/Long text field",
                new MethodProperty(this, "long1"));
        f1.setImmediate(false);
        f1.setNullSettingAllowed(true);
        TextField f2 = new TextField("Immediate/Long text field",
                new MethodProperty(this, "long2"));
        f2.setImmediate(true);
        f2.setNullSettingAllowed(true);

        layout.addComponent(f1);
        layout.addComponent(f2);

        w.setLayout(layout);
    }
}
