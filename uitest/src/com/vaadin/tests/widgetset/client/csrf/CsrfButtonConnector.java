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
package com.vaadin.tests.widgetset.client.csrf;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.client.MockApplicationConnection;
import com.vaadin.tests.widgetset.server.csrf.CsrfButton;

/**
 * Dummy connector to test our CSRF bug. See #14111.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Connect(CsrfButton.class)
public class CsrfButtonConnector extends AbstractComponentConnector {

    static Logger logger = Logger
            .getLogger(CsrfButtonConnector.class.getName());
    static {
        logger.setLevel(Level.ALL);
    }

    @Override
    public VButton getWidget() {
        return (VButton) super.getWidget();
    }

    @Override
    protected VButton createWidget() {
        return GWT.create(VButton.class);
    }

    public final static String ID = "CsrfButton";

    @Override
    public void init() {
        super.init();

        getWidget().getElement().setId(ID);
        getWidget().setText(csrfTokenInfo());
        getWidget().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getWidget().setText(csrfTokenInfo());
            }
        });
    }

    private String csrfTokenInfo() {
        return getMockConnection().getCsrfToken() + ", "
                + getMockConnection().getLastCsrfTokenReceiver() + ", "
                + getMockConnection().getLastCsrfTokenSent();
    }

    private MockApplicationConnection getMockConnection() {
        return (MockApplicationConnection) getConnection();
    }

}
