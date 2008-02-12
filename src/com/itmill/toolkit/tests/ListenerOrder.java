package com.itmill.toolkit.tests;

import java.util.HashMap;
import java.util.Iterator;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Container.ItemSetChangeEvent;
import com.itmill.toolkit.data.Container.ItemSetChangeListener;
import com.itmill.toolkit.data.Container.PropertySetChangeEvent;
import com.itmill.toolkit.data.Container.PropertySetChangeListener;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ListenerOrder extends com.itmill.toolkit.Application implements
        Button.ClickListener, PropertySetChangeListener, ItemSetChangeListener,
        ValueChangeListener {

    Button b1;

    Select s1;

    HashMap buttonListeners = new HashMap();

    public void init() {
        createNewView();
    }

    public void createNewView() {
        final Window main = new Window("Test window");
        setMainWindow(main);

        main.removeAllComponents();
        main.addComponent(new Label("Testing multiple listeners."));

        //
        // Button listeners
        //
        b1 = new Button("Button 1");
        main.addComponent(b1);

        MyClickListener mutualListener = new MyClickListener("mutual1");

        addListeners(b1, 1);
        b1.addListener(mutualListener);
        b1.addListener(mutualListener);
        b1.addListener((Button.ClickListener) this);
        b1.addListener(mutualListener);
        Button.ClickListener b1Listener = addListeners(b1, 3);
        b1.addListener(mutualListener);
        b1.addListener((Button.ClickListener) this);
        b1.addListener((ValueChangeListener) this);
        b1.addListener(mutualListener);
        b1.removeListener(b1Listener);
        // remove non-existing listener
        b1.removeListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            }
        });

        //
        // Select listeners
        //
        s1 = new Select("Select 1");
        main.addComponent(s1);
        s1.setImmediate(true);
        s1.setNewItemsAllowed(true);

        s1.addItem("first");
        s1.addItem("first");
        s1.addItem("first");
        s1.addItem("second");
        s1.addItem("third");
        s1.addItem("fourth");
        s1.addListener((ValueChangeListener) this);
        s1.addListener((PropertySetChangeListener) this);
        s1.addListener((ItemSetChangeListener) this);
        s1.addListener((ItemSetChangeListener) this);
        s1.addListener((PropertySetChangeListener) this);
        s1.addListener((PropertySetChangeListener) this);
        s1.addListener((ItemSetChangeListener) this);
        s1.addListener((ValueChangeListener) this);
        s1.addListener((ValueChangeListener) this);

        Item i = s1.getItem("second");
        for (Iterator it = i.getItemPropertyIds().iterator(); it.hasNext();) {
            Object o = it.next();
            System.out.println("[" + o + "]");
        }

    }

    private Button.ClickListener addListeners(Button b, int count) {
        String name = b.getCaption();
        // System.out.println("Adding listener for " + name);
        Button.ClickListener listener = null;
        for (int i = 0; i < count; i++) {
            listener = new MyClickListener(name);
            b.addListener(listener);
        }
        // return last listener added
        return listener;
    }

    public void buttonClick(ClickEvent event) {
        System.out.println("ClickEvent from Test1");
        s1.addItem("new item " + System.currentTimeMillis());
    }

    public class MyClickListener implements Button.ClickListener {
        String name = "";
        int count = 0;

        public MyClickListener(String name) {
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
            String msg = "ClickEvent from MyClickListener " + name + ", id="
                    + count;
            System.out.println(msg);
            getMainWindow().showNotification(msg);
        }

    }

    public void containerPropertySetChange(PropertySetChangeEvent event) {
        String msg = "containerPropertySetChange from " + this;
        System.out.println(msg);
        getMainWindow().showNotification(msg);
    }

    public void containerItemSetChange(ItemSetChangeEvent event) {
        String msg = "containerItemSetChange from " + this;
        System.out.println(msg);
        getMainWindow().showNotification(msg);
    }

    public void valueChange(ValueChangeEvent event) {
        String msg = "valueChange from " + this;
        System.out.println(msg);
        getMainWindow().showNotification(msg);
    }

}
