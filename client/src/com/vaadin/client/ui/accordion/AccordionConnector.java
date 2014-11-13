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

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VAccordion;
import com.vaadin.client.ui.VAccordion.StackItem;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.client.ui.tabsheet.TabsheetBaseConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.accordion.AccordionState;
import com.vaadin.ui.Accordion;

@Connect(Accordion.class)
public class AccordionConnector extends TabsheetBaseConnector implements
        SimpleManagedLayout, MayScrollChildren {

    @Override
    protected void init() {
        super.init();
        getWidget().setConnector(this);
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        /*
         * Render content after all tabs have been created and we know how large
         * the content area is
         */
        if (getWidget().selectedItemIndex >= 0) {
            StackItem selectedItem = getWidget().getStackItem(
                    getWidget().selectedItemIndex);

            ComponentConnector contentConnector = getChildComponents().get(0);
            if (contentConnector != null) {
                selectedItem.setContent(contentConnector.getWidget());
            }

            getWidget().open(getWidget().selectedItemIndex);

        } else if (getWidget().getOpenStackItem() != null) {
            getWidget().close(getWidget().getOpenStackItem());
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ConnectorHierarchyChangeEvent.
     * ConnectorHierarchyChangeHandler
     * #onConnectorHierarchyChange(com.vaadin.client
     * .ConnectorHierarchyChangeEvent)
     */
    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    }

    @Override
    public AccordionState getState() {
        return (AccordionState) super.getState();
    }
}
