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
package com.vaadin.client.connectors;

import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;

/**
 * Abstract class for listing widget connectors that contains focusable children
 * to track their focus/blur events.
 *
 * @author Vaadin Ltd
 *
 * @param <WIDGET>
 *            widget type which has to allow to register focus/blur handlers
 * @since 8.0
 */
public abstract class AbstractFocusableListingConnector<WIDGET extends Widget & HasAllFocusHandlers>
        extends AbstractListingConnector {

    private ConnectorFocusAndBlurHandler handler;

    @Override
    protected void init() {
        handler = ConnectorFocusAndBlurHandler.addHandlers(this);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        handler.removeHandlers();
        handler = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WIDGET getWidget() {
        return (WIDGET) super.getWidget();
    }
}
