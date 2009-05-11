package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

abstract class VTabsheetBase extends ComplexPanel implements Container {

    String id;
    ApplicationConnection client;

    protected final ArrayList tabKeys = new ArrayList();
    protected int activeTabIndex = 0;
    protected boolean disabled;
    protected boolean readonly;
    protected Set disabledTabKeys = new HashSet();
    protected boolean cachedUpdate = false;

    public VTabsheetBase(String classname) {
        setElement(DOM.createDiv());
        setStylePrimaryName(classname);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        // Ensure correct implementation
        cachedUpdate = client.updateComponent(this, uidl, true);
        if (cachedUpdate) {
            return;
        }

        // Update member references
        id = uidl.getId();
        disabled = uidl.hasAttribute("disabled");

        // Render content
        final UIDL tabs = uidl.getChildUIDL(0);

        // Paintables in the TabSheet before update
        ArrayList oldPaintables = new ArrayList();
        for (Iterator iterator = getPaintableIterator(); iterator.hasNext();) {
            oldPaintables.add(iterator.next());
        }

        // Clear previous values
        tabKeys.clear();
        disabledTabKeys.clear();

        int index = 0;
        for (final Iterator it = tabs.getChildIterator(); it.hasNext();) {
            final UIDL tab = (UIDL) it.next();
            final String key = tab.getStringAttribute("key");
            final boolean selected = tab.getBooleanAttribute("selected");
            final boolean hidden = tab.getBooleanAttribute("hidden");

            if (tab.getBooleanAttribute("disabled")) {
                disabledTabKeys.add(key);
            }

            tabKeys.add(key);

            if (selected) {
                activeTabIndex = index;
            }
            renderTab(tab, index, selected, hidden);
            index++;
        }

        int tabCount = getTabCount();
        while (tabCount-- > index) {
            removeTab(index);
        }

        for (int i = 0; i < getTabCount(); i++) {
            Paintable p = getTab(i);
            oldPaintables.remove(p);
        }

        // Perform unregister for any paintables removed during update
        for (Iterator iterator = oldPaintables.iterator(); iterator.hasNext();) {
            Object oldPaintable = iterator.next();
            if (oldPaintable instanceof Paintable) {
                Widget w = (Widget) oldPaintable;
                if (w.isAttached()) {
                    w.removeFromParent();
                }
                client.unregisterPaintable((Paintable) oldPaintable);
            }
        }

    }

    /**
     * @return a list of currently shown Paintables
     */
    abstract protected Iterator getPaintableIterator();

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
    protected abstract Paintable getTab(int index);

    /**
     * Implement in extending classes. This method should remove the rendered
     * tab with the specified index.
     */
    protected abstract void removeTab(int index);
}
