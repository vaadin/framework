/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ui.VAccordion.StackItem;

public class VAccordionPaintable extends VTabsheetBasePaintable implements
        SimpleManagedLayout {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().selectedUIDLItemIndex = -1;
        super.updateFromUIDL(uidl, client);
        /*
         * Render content after all tabs have been created and we know how large
         * the content area is
         */
        if (getWidget().selectedUIDLItemIndex >= 0) {
            StackItem selectedItem = getWidget().getStackItem(
                    getWidget().selectedUIDLItemIndex);
            UIDL selectedTabUIDL = getWidget().lazyUpdateMap
                    .remove(selectedItem);
            getWidget().open(
                    getWidget().selectedUIDLItemIndex);

            selectedItem.setContent(selectedTabUIDL);
        } else if (isRealUpdate(uidl)
                && getWidget().openTab != null) {
            getWidget().close(getWidget().openTab);
        }

        getWidget().iLayout();
        // finally render possible hidden tabs
        if (getWidget().lazyUpdateMap.size() > 0) {
            for (Iterator iterator = getWidget().lazyUpdateMap
                    .keySet().iterator(); iterator.hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent(getWidget().lazyUpdateMap.get(item));
            }
            getWidget().lazyUpdateMap.clear();
        }

        getWidget().renderInformation
                .updateSize(getWidget().getElement());
    }

    @Override
    public VAccordion getWidget() {
        return (VAccordion) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAccordion.class);
    }

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        /* Accordion does not render its children's captions */
    }

    public void layout() {
        VAccordion accordion = getWidget();

        accordion.updateOpenTabSize();

        if (isUndefinedHeight()) {
            accordion.openTab.setHeightFromWidget();
        }
        accordion.iLayout();

    }

}
