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
package com.vaadin.client.ui.tabsheet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VTabsheet;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.tabsheet.TabsheetState;
import com.vaadin.ui.TabSheet;

@Connect(TabSheet.class)
public class TabsheetConnector extends TabsheetBaseConnector implements
        SimpleManagedLayout, MayScrollChildren {

    // Can't use "style" as it's already in use
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (isRealUpdate(uidl)) {
            // Handle stylename changes before generics (might affect size
            // calculations)
            getWidget().handleStyleNames(uidl, getState());
        }

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // tabs; push or not
        if (!isUndefinedWidth()) {
            DOM.setStyleAttribute(getWidget().tabs, "overflow", "hidden");
        } else {
            getWidget().showAllTabs();
            DOM.setStyleAttribute(getWidget().tabs, "width", "");
            DOM.setStyleAttribute(getWidget().tabs, "overflow", "visible");
            getWidget().updateDynamicWidth();
        }

        if (!isUndefinedHeight()) {
            // Must update height after the styles have been set
            getWidget().updateContentNodeHeight();
            getWidget().updateOpenTabSize();
        }

        getWidget().iLayout();

        getWidget().waitingForResponse = false;
    }

    @Override
    public VTabsheet getWidget() {
        return (VTabsheet) super.getWidget();
    }

    @Override
    public TabsheetState getState() {
        return (TabsheetState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        /* Tabsheet does not render its children's captions */
    }

    @Override
    public void layout() {
        VTabsheet tabsheet = getWidget();

        tabsheet.updateContentNodeHeight();

        if (isUndefinedWidth()) {
            tabsheet.contentNode.getStyle().setProperty("width", "");
        } else {
            int contentWidth = tabsheet.getOffsetWidth()
                    - tabsheet.getContentAreaBorderWidth();
            if (contentWidth < 0) {
                contentWidth = 0;
            }
            tabsheet.contentNode.getStyle().setProperty("width",
                    contentWidth + "px");
        }

        tabsheet.updateOpenTabSize();
        if (isUndefinedWidth()) {
            tabsheet.updateDynamicWidth();
        }

        tabsheet.iLayout();

    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {

        TooltipInfo info = null;

        // Find a tooltip for the tab, if the element is a tab
        if (element != getWidget().getElement()) {
            Object node = Util.findWidget(
                    (com.google.gwt.user.client.Element) element,
                    VTabsheet.TabCaption.class);

            if (node != null) {
                VTabsheet.TabCaption caption = (VTabsheet.TabCaption) node;
                info = caption.getTooltipInfo();
            }
        }

        // If not tab tooltip was found, use the default
        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tab tooltips are not processed until updateFromUIDL, so we can't be
         * sure that there are no tooltips during onStateChange when this method
         * is used.
         */
        return true;
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // TODO Move code from updateFromUIDL to this method
    }
}
