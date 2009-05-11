/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests.util;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

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
