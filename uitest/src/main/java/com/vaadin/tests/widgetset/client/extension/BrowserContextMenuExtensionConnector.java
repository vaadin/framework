/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.widgetset.client.extension;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.contextclick.BrowserContextMenuInSubComponent.BrowserContextMenuExtension;

/**
 * Client-side connector of the {@link BrowserContextMenuExtension}.
 */
@Connect(BrowserContextMenuExtension.class)
public class BrowserContextMenuExtensionConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        getParent().getWidget().addDomHandler(new ContextMenuHandler() {

            @Override
            public void onContextMenu(ContextMenuEvent event) {
                // Stop context click events from propagating.
                event.stopPropagation();
            }
        }, ContextMenuEvent.getType());
    }

    @Override
    public AbstractComponentConnector getParent() {
        return (AbstractComponentConnector) super.getParent();
    }
}
