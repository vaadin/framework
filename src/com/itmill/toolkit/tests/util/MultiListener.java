package com.itmill.toolkit.tests.util;

import com.itmill.toolkit.data.Container.ItemSetChangeEvent;
import com.itmill.toolkit.data.Container.ItemSetChangeListener;
import com.itmill.toolkit.data.Container.PropertySetChangeEvent;
import com.itmill.toolkit.data.Container.PropertySetChangeListener;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class MultiListener implements Button.ClickListener,
        PropertySetChangeListener, ItemSetChangeListener, ValueChangeListener {

    public void buttonClick(ClickEvent event) {
        Log.debug("ClickEvent from " + event.getButton().getCaption());
    }

    public void containerPropertySetChange(PropertySetChangeEvent event) {
        Log.debug("containerPropertySetChange from " + event.getContainer());
    }

    public void containerItemSetChange(ItemSetChangeEvent event) {
        Log.debug("containerItemSetChange from " + event.getContainer());
    }

    public void valueChange(ValueChangeEvent event) {
        Log.debug("valueChange from " + event.getProperty());
    }

}
