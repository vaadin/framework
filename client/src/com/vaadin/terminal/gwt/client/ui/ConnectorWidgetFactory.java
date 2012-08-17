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
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

public abstract class ConnectorWidgetFactory extends
        ConnectorClassBasedFactory<Widget> {
    private static ConnectorWidgetFactory impl = null;

    // TODO Move to generator
    {
        addCreator(TextFieldConnector.class, new Creator<Widget>() {
            @Override
            public Widget create() {
                return GWT.create(VTextField.class);
            }
        });
    }

    /**
     * Creates a widget using GWT.create for the given connector, based on its
     * {@link AbstractComponentConnector#getWidget()} return type.
     * 
     * @param connector
     * @return
     */
    public static Widget createWidget(
            Class<? extends AbstractComponentConnector> connector) {
        return getImpl().create(connector);
    }

    private static ConnectorWidgetFactory getImpl() {
        if (impl == null) {
            impl = GWT.create(ConnectorWidgetFactory.class);
        }
        return impl;
    }
}
