/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;

abstract class VTabsheetBase extends ComplexPanel {

    String id;
    ApplicationConnection client;

    protected final ArrayList<String> tabKeys = new ArrayList<String>();
    protected int activeTabIndex = 0;
    protected boolean disabled;
    protected boolean readonly;
    protected Set<String> disabledTabKeys = new HashSet<String>();

    public VTabsheetBase(String classname) {
        setElement(DOM.createDiv());
        setStyleName(classname);
    }

    /**
     * @return a list of currently shown Widgets
     */
    abstract protected Iterator<Widget> getWidgetIterator();

    /**
     * Clears current tabs and contents
     */
    abstract protected void clearPaintables();

    /**
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    protected abstract void renderTab(final UIDL tabUidl, int index,
            boolean selected, boolean hidden);

    /**
     * Implement in extending classes. This method should render any previously
     * non-cached content and set the activeTabIndex property to the specified
     * index.
     */
    protected abstract void selectTab(int index, final UIDL contentUidl);

    /**
     * Implement in extending classes. This method should return the number of
     * tabs currently rendered.
     */
    protected abstract int getTabCount();

    /**
     * Implement in extending classes. This method should return the Paintable
     * corresponding to the given index.
     */
    protected abstract ComponentConnector getTab(int index);

    /**
     * Implement in extending classes. This method should remove the rendered
     * tab with the specified index.
     */
    protected abstract void removeTab(int index);
}
