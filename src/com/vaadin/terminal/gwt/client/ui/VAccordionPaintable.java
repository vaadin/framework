package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.VAccordion.StackItem;

public class VAccordionPaintable extends VTabsheetBasePaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;
        getWidgetForPaintable().selectedUIDLItemIndex = -1;
        super.updateFromUIDL(uidl, client);
        /*
         * Render content after all tabs have been created and we know how large
         * the content area is
         */
        if (getWidgetForPaintable().selectedUIDLItemIndex >= 0) {
            StackItem selectedItem = getWidgetForPaintable().getStackItem(
                    getWidgetForPaintable().selectedUIDLItemIndex);
            UIDL selectedTabUIDL = getWidgetForPaintable().lazyUpdateMap
                    .remove(selectedItem);
            getWidgetForPaintable().open(
                    getWidgetForPaintable().selectedUIDLItemIndex);

            selectedItem.setContent(selectedTabUIDL);
        } else if (!uidl.getBooleanAttribute("cached")
                && getWidgetForPaintable().openTab != null) {
            getWidgetForPaintable().close(getWidgetForPaintable().openTab);
        }

        getWidgetForPaintable().iLayout();
        // finally render possible hidden tabs
        if (getWidgetForPaintable().lazyUpdateMap.size() > 0) {
            for (Iterator iterator = getWidgetForPaintable().lazyUpdateMap
                    .keySet().iterator(); iterator.hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent(getWidgetForPaintable().lazyUpdateMap.get(item));
            }
            getWidgetForPaintable().lazyUpdateMap.clear();
        }

        getWidgetForPaintable().renderInformation
                .updateSize(getWidgetForPaintable().getElement());

        getWidgetForPaintable().rendering = false;
    }

    @Override
    public VAccordion getWidgetForPaintable() {
        return (VAccordion) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAccordion.class);
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        /* Accordion does not render its children's captions */
    }

}
