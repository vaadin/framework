package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

abstract class ITabsheetBase extends FlowPanel implements Paintable {

    String id;
    ApplicationConnection client;

    protected final ArrayList tabKeys = new ArrayList();
    protected final ArrayList captions = new ArrayList();
    protected int activeTabIndex = 0;
    protected boolean disabled;
    protected boolean readonly;

    public ITabsheetBase(String classname) {
        setStylePrimaryName(classname);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation and let ApplicationConnection handle
        // component caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Update member references
        this.client = client;
        id = uidl.getId();
        disabled = uidl.hasAttribute("disabled");

        // Adjust width and height
        if (uidl.hasAttribute("height")) {
            setHeight(uidl.getStringAttribute("height"));
        } else {
            setHeight("");
        }
        if (uidl.hasAttribute("width")) {
            setWidth(uidl.getStringAttribute("width"));
        } else {
            setWidth("");
        }

        // Render content
        final UIDL tabs = uidl.getChildUIDL(0);
        if (keepCurrentTabs(uidl)) {
            int index = 0;
            for (final Iterator it = tabs.getChildIterator(); it.hasNext();) {
                final UIDL tab = (UIDL) it.next();
                final boolean selected = tab.getBooleanAttribute("selected");
                if (selected) {
                    selectTab(index, tab.getChildUIDL(0));
                }
                index++;
            }
        } else {
            // Clear previous values
            tabKeys.clear();
            captions.clear();
            clear();

            int index = 0;
            for (final Iterator it = tabs.getChildIterator(); it.hasNext();) {
                final UIDL tab = (UIDL) it.next();
                final String key = tab.getStringAttribute("key");
                final boolean selected = tab.getBooleanAttribute("selected");
                String caption = tab.getStringAttribute("caption");
                if (caption == null) {
                    caption = "&nbsp;";
                }

                captions.add(caption);
                tabKeys.add(key);

                if (selected) {
                    activeTabIndex = index;
                }
                renderTab(tab.getChildUIDL(0), caption, index, selected);
                index++;
            }
        }

    }

    protected boolean keepCurrentTabs(UIDL uidl) {
        final UIDL tabs = uidl.getChildUIDL(0);
        boolean retval = tabKeys.size() == tabs.getNumberOfChildren();
        for (int i = 0; retval && i < tabKeys.size(); i++) {
            retval = tabKeys.get(i).equals(
                    tabs.getChildUIDL(i).getStringAttribute("key"))
                    && captions.get(i).equals(
                            tabs.getChildUIDL(i).getStringAttribute("caption"));
        }
        return retval;
    }

    /*
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    protected abstract void renderTab(final UIDL contentUidl, String caption,
            int index, boolean selected);

    /*
     * Implement in extending classes. This method should render any previously
     * non-cached content and set the activeTabIndex property to the specified
     * index.
     */
    protected abstract void selectTab(int index, final UIDL contentUidl);

}
