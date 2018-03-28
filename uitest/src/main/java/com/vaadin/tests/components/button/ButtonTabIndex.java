package com.vaadin.tests.components.button;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.TextField;

public class ButtonTabIndex extends TestBase {

    @Override
    protected void setup() {
        TextField tf1 = new TextField("Tab index 0");
        tf1.setTabIndex(0);
        TextField tf2 = new TextField("Tab index -1, focused initially");
        tf2.setTabIndex(-1);
        tf2.focus();
        addComponent(tf1);
        addComponent(tf2);

        addComponent(createButton(1));
        addComponent(createButton(5));
        addComponent(createNativeButton(3));
        addComponent(createButton(4));
        addComponent(createNativeButton(2));

    }

    private Button createButton(int i) {
        Button b = new Button("Button with tab index " + i);
        b.setTabIndex(i);
        return b;
    }

    private NativeButton createNativeButton(int i) {
        NativeButton b = new NativeButton("NativeButton with tab index " + i);
        b.setTabIndex(i);
        return b;
    }

    @Override
    protected String getDescription() {
        return "Test for tab indexes for Button and NativeButton";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9022;
    }

}
