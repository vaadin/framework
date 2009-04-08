/**
 * 
 */
package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.Window;

/**
 * @author Risto Yrjänä / IT Mill Ltd.
 * 
 */
public class Ticket2742 extends Application {

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.Application#init()
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