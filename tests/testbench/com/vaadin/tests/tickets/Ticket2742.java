/**
 * 
 */
package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Window;

/**
 * @author Risto Yrjänä / IT Mill Ltd.
 * 
 */
public class Ticket2742 extends Application {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.Application#init()
     */
    @Override
    public void init() {
        Window mainWindow = new Window();
        setMainWindow(mainWindow);

        String shortString = "Short";
        String longString = "Very, very long";

        HorizontalLayout hl = new HorizontalLayout();

        for (int i = 0; i < 2; i++) {
            NativeSelect ns = new NativeSelect(shortString);
            ns.addItem(longString);
            ns.setNullSelectionAllowed(false);
            ns.select(longString);
            hl.addComponent(ns);
        }
        mainWindow.addComponent(hl);
    }

}