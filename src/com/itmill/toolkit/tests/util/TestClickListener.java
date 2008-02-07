package com.itmill.toolkit.tests.util;

import java.util.HashMap;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TestClickListener implements Button.ClickListener {

    private static final HashMap buttonListeners = new HashMap();

    String name = "";
    int count = 0;

    public TestClickListener(String name) {
        Integer count = null;
        try {
            count = (Integer) buttonListeners.get(name);
            count = new Integer(count.intValue() + 1);
            buttonListeners.put(name, count);
        } catch (Exception e) {
            count = new Integer(1);
            buttonListeners.put(name, count);
        }

        this.name = name;
        this.count = count.intValue();

        System.out.println("Created listener " + name + ", id=" + count);
    }

    public void buttonClick(ClickEvent event) {
        System.out
                .println("ClickEvent from listener " + name + ", id=" + count);
    }

}
