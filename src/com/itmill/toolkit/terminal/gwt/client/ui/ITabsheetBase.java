package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
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
    protected Set disabledTabKeys = new HashSet();

    public ITabsheetBase(String classname) {
        setStylePrimaryName(classname);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Update member references
        this.client = client;
        id = uidl.getId();
        disabled = uidl.hasAttribute("disabled");

        // Render content
        final UIDL tabs = uidl.getChildUIDL(0);
        if (keepCurrentTabs(uidl)) {
            int index = 0;
            for (final Iterator it = tabs.getChildIterator(); it.hasNext();) {
                final UIDL tab = (UIDL) it.next();
                final boolean selected = tab.getBooleanAttribute("selected");
                if (selected) {
                    selectTab(index, tab.getChildUIDL(0));
                } else if (tab.getChildCount() > 0) {
                    // updating a drawn child on hidden tab
                    Paintable paintable = client.getPaintable(tab
                            .getChildUIDL(0));
                    // TODO widget may flash on screen
                    paintable.updateFromUIDL(tab.getChildUIDL(0), client);
                    // Hack #1 in ITabsheetBase: due ITabsheets content has no
                    // wrappers for each tab, we need to hide the actual widgets
                    ((Widget) paintable).setVisible(false);
                }
                index++;
            }
        } else {
            // Clear previous values
            tabKeys.clear();
            captions.clear();
            disabledTabKeys.clear();
            clear();

            int index = 0;
            for (final Iterator it = tabs.getChildIterator(); it.hasNext();) {
                final UIDL tab = (UIDL) it.next();
                final String key = tab.getStringAttribute("key");
                final boolean selected = tab.getBooleanAttribute("selected");
                String caption = tab.getStringAttribute("caption");
                if (caption == null) {
                    caption = " ";
                }

                if (tab.getBooleanAttribute("disabled")) {
                    disabledTabKeys.add(key);
                }

                captions.add(caption);
                tabKeys.add(key);

                if (selected) {
                    activeTabIndex = index;
                }
                renderTab(tab, index, selected);
                index++;
            }
        }

    }

    protected boolean keepCurrentTabs(UIDL uidl) {
        final UIDL tabs = uidl.getChildUIDL(0);
        boolean retval = tabKeys.size() == tabs.getNumberOfChildren();
        for (int i = 0; retval && i < tabKeys.size(); i++) {
            String key = (String) tabKeys.get(i);
            UIDL tabUIDL = tabs.getChildUIDL(i);
            retval = key.equals(tabUIDL.getStringAttribute("key"))
                    && captions.get(i).equals(
                            tabUIDL.getStringAttribute("caption"))
                    && (tabUIDL.hasAttribute("disabled") == disabledTabKeys
                            .contains(key));
        }
        return retval;
    }

    /*
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    protected abstract void renderTab(final UIDL tabUidl, int index,
            boolean selected);

    /*
     * Implement in extending classes. This method should render any previously
     * non-cached content and set the activeTabIndex property to the specified
     * index.
     */
    protected abstract void selectTab(int index, final UIDL contentUidl);

}
