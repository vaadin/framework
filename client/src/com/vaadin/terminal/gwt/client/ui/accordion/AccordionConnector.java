/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui.accordion;

import java.util.Iterator;

import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.terminal.gwt.client.ui.accordion.VAccordion.StackItem;
import com.vaadin.terminal.gwt.client.ui.layout.MayScrollChildren;
import com.vaadin.terminal.gwt.client.ui.tabsheet.TabsheetBaseConnector;
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
        } else if (isRealUpdate(uidl) && getWidget().openTab != null) {
            getWidget().close(getWidget().openTab);
        }

        getWidget().iLayout();
        // finally render possible hidden tabs
        if (getWidget().lazyUpdateMap.size() > 0) {
            for (Iterator iterator = getWidget().lazyUpdateMap.keySet()
                    .iterator(); iterator.hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent(getWidget().lazyUpdateMap.get(item));
            }
            getWidget().lazyUpdateMap.clear();
        }

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
        VAccordion accordion = getWidget();

        accordion.updateOpenTabSize();

        if (isUndefinedHeight()) {
            accordion.openTab.setHeightFromWidget();
        }
        accordion.iLayout();

    }

}
