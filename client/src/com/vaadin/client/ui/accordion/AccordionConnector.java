/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui.accordion;

import java.util.Iterator;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VAccordion;
import com.vaadin.client.ui.VAccordion.StackItem;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.client.ui.tabsheet.TabsheetBaseConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Accordion;

@Connect(Accordion.class)
public class AccordionConnector extends TabsheetBaseConnector implements
        SimpleManagedLayout, MayScrollChildren {

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
            getWidget().open(getWidget().selectedUIDLItemIndex);

            selectedItem.setContent(selectedTabUIDL);
        } else if (isRealUpdate(uidl) && getWidget().getOpenStackItem() != null) {
            getWidget().close(getWidget().getOpenStackItem());
        }

        // finally render possible hidden tabs
        if (getWidget().lazyUpdateMap.size() > 0) {
            for (Iterator iterator = getWidget().lazyUpdateMap.keySet()
                    .iterator(); iterator.hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent(getWidget().lazyUpdateMap.get(item));
            }
            getWidget().lazyUpdateMap.clear();
        }
        getLayoutManager().setNeedsVerticalLayout(this);
    }

    @Override
    public VAccordion getWidget() {
        return (VAccordion) super.getWidget();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        /* Accordion does not render its children's captions */
    }

    @Override
    public void layout() {
        StackItem openTab = getWidget().getOpenStackItem();
        if (openTab == null) {
            return;
        }

        // WIDTH
        if (!isUndefinedWidth()) {
            openTab.setWidth("100%");
        } else {
            int maxWidth = 40;
            for (StackItem si : getWidget().getStackItems()) {
                int captionWidth = si.getCaptionWidth();
                if (captionWidth > maxWidth) {
                    maxWidth = captionWidth;
                }
            }
            int widgetWidth = openTab.getWidgetWidth();
            if (widgetWidth > maxWidth) {
                maxWidth = widgetWidth;
            }
            openTab.setWidth(maxWidth);
        }

        // HEIGHT
        if (!isUndefinedHeight()) {
            int usedPixels = 0;
            for (StackItem item : getWidget().getStackItems()) {
                if (item == openTab) {
                    usedPixels += item.getCaptionHeight();
                } else {
                    // This includes the captionNode borders
                    usedPixels += Util.getRequiredHeight(item.getElement());
                }
            }
            int rootElementInnerHeight = getLayoutManager().getInnerHeight(
                    getWidget().getElement());
            int spaceForOpenItem = rootElementInnerHeight - usedPixels;

            if (spaceForOpenItem < 0) {
                spaceForOpenItem = 0;
            }

            openTab.setHeight(spaceForOpenItem);
        } else {
            openTab.setHeightFromWidget();

        }

    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // TODO Move code from updateFromUIDL to this method
    }
}
