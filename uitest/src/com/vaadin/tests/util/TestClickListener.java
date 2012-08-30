package com.vaadin.tests.util;

import java.util.HashMap;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class TestClickListener implements Button.ClickListener {

    private static final HashMap<String, Integer> buttonListeners = new HashMap<String, Integer>();

    String name = "";
    int count = 0;

    public TestClickListener(String name) {
        Integer count = null;
        try {
            count = buttonListeners.get(name);
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

    @Override
    public void buttonClick(ClickEvent event) {
        System.out
                .println("ClickEvent from listener " + name + ", id=" + count);
    }

}
