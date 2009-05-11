/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

public class IContextMenu extends IToolkitOverlay implements SubPartAware {

    private ActionOwner actionOwner;

    private final CMenuBar menu = new CMenuBar();

    private int left;

    private int top;

    /**
     * This method should be used only by Client object as only one per client
     * should exists. Request an instance via client.getContextMenu();
     * 
     * @param cli
     *            to be set as an owner of menu
     */
    public IContextMenu() {
        super(true, false, true);
        setWidget(menu);
        setStyleName("i-contextmenu");
    }

    /**
     * Sets the element from which to build menu
     * 
     * @param ao
     */
    public void setActionOwner(ActionOwner ao) {
        actionOwner = ao;
    }

    /**
     * Shows context menu at given location.
     * 
     * @param left
     * @param top
     */
    public void showAt(int left, int top) {
        this.left = left;
        this.top = top;
        menu.clearItems();
        final Action[] actions = actionOwner.getActions();
        for (int i = 0; i < actions.length; i++) {
            final Action a = actions[i];
            menu.addItem(new MenuItem(a.getHTML(), true, a));
        }

        setPopupPositionAndShow(new PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                // mac FF gets bad width due GWT popups overflow hacks,
                // re-determine width
                offsetWidth = menu.getOffsetWidth();
                int left = IContextMenu.this.left;
                int top = IContextMenu.this.top;
                if (offsetWidth + left > Window.getClientWidth()) {
                    left = left - offsetWidth;
                    if (left < 0) {
                        left = 0;
                    }
                }
                if (offsetHeight + top > Window.getClientHeight()) {
                    top = top - offsetHeight;
                    if (top < 0) {
                        top = 0;
                    }
                }
                setPopupPosition(left, top);
            }
        });
    }

    public void showAt(ActionOwner ao, int left, int top) {
        setActionOwner(ao);
        showAt(left, top);
    }

    /**
     * Extend standard Gwt MenuBar to set proper settings and to override
     * onPopupClosed method so that PopupPanel gets closed.
     */
    class CMenuBar extends MenuBar {
        public CMenuBar() {
            super(true);
        }

        @Override
        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            super.onPopupClosed(sender, autoClosed);
            hide();
        }

        /*
         * public void onBrowserEvent(Event event) { // Remove current selection
         * when mouse leaves if (DOM.eventGetType(event) == Event.ONMOUSEOUT) {
         * Element to = DOM.eventGetToElement(event); if
         * (!DOM.isOrHasChild(getElement(), to)) { DOM.setElementProperty(
         * super.getSelectedItem().getElement(), "className",
         * super.getSelectedItem().getStylePrimaryName()); } }
         * 
         * super.onBrowserEvent(event); }
         */
    }

    public Element getSubPartElement(String subPart) {
        int index = Integer.parseInt(subPart.substring(6));
        ApplicationConnection.getConsole().log(
                "Searching element for selection index " + index);
        Element wrapperdiv = menu.getElement();
        com.google.gwt.dom.client.TableSectionElement tBody = (TableSectionElement) wrapperdiv
                .getFirstChildElement().getFirstChildElement();
        TableRowElement item = tBody.getRows().getItem(index);
        com.google.gwt.dom.client.Element clickableDivElement = item
                .getFirstChildElement().getFirstChildElement();
        return clickableDivElement.cast();
    }

    public String getSubPartName(Element subElement) {
        if (getElement().isOrHasChild(subElement)) {
            com.google.gwt.dom.client.Element e = subElement;
            {
                while (e != null && !e.getTagName().toLowerCase().equals("tr")) {
                    e = e.getParentElement();
                    ApplicationConnection.getConsole().log("Found row");
                }
            }
            com.google.gwt.dom.client.TableSectionElement parentElement = (TableSectionElement) e
                    .getParentElement();
            NodeList<TableRowElement> rows = parentElement.getRows();
            for (int i = 0; i < rows.getLength(); i++) {
                if (rows.getItem(i) == e) {
                    ApplicationConnection.getConsole().log(
                            "Found index for row" + 1);
                    return "option" + i;
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
