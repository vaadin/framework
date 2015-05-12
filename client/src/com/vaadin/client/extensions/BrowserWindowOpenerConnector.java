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

package com.vaadin.client.extensions;

import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.shared.ui.BrowserWindowOpenerState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.util.SharedUtil;

/**
 * Client-side code for {@link BrowserWindowOpener}
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Connect(BrowserWindowOpener.class)
public class BrowserWindowOpenerConnector extends AbstractExtensionConnector
        implements ClickHandler {

    @Override
    protected void extend(ServerConnector target) {
        final Widget targetWidget = ((ComponentConnector) target).getWidget();

        targetWidget.addDomHandler(this, ClickEvent.getType());
    }

    @Override
    public BrowserWindowOpenerState getState() {
        return (BrowserWindowOpenerState) super.getState();
    }

    @Override
    public void onClick(ClickEvent event) {
        String url = getResourceUrl(BrowserWindowOpenerState.locationResource);
        url = addParametersAndFragment(url);
        if (url != null) {
            Window.open(url, getState().target, getState().features);
        }
    }

    private String addParametersAndFragment(String url) {
        if (url == null) {
            return null;
        }

        if (!getState().parameters.isEmpty()) {
            StringBuilder params = new StringBuilder();
            for (Entry<String, String> entry : getState().parameters.entrySet()) {
                if (params.length() != 0) {
                    params.append('&');
                }
                params.append(URL.encodeQueryString(entry.getKey()));
                params.append('=');

                String value = entry.getValue();
                if (value != null) {
                    params.append(URL.encodeQueryString(value));
                }
            }

            url = SharedUtil.addGetParameters(url, params.toString());
        }

        if (getState().uriFragment != null) {
            // Replace previous fragment or just add to the end of the url
            url = url.replaceFirst("#.*|$", "#" + getState().uriFragment);
        }

        return url;
    }
}
