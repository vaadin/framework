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

package com.vaadin.terminal.gwt.client.ui.progressindicator;

import com.google.gwt.user.client.DOM;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.ui.ProgressIndicator;

@Connect(ProgressIndicator.class)
public class ProgressIndicatorConnector extends AbstractFieldConnector
        implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;

        getWidget().indeterminate = uidl.getBooleanAttribute("indeterminate");

        if (getWidget().indeterminate) {
            String basename = VProgressIndicator.CLASSNAME + "-indeterminate";
            getWidget().addStyleName(basename);
            if (!isEnabled()) {
                getWidget().addStyleName(basename + "-disabled");
            } else {
                getWidget().removeStyleName(basename + "-disabled");
            }
        } else {
            try {
                final float f = Float.parseFloat(uidl
                        .getStringAttribute("state"));
                final int size = Math.round(100 * f);
                DOM.setStyleAttribute(getWidget().indicator, "width", size
                        + "%");
            } catch (final Exception e) {
            }
        }

        if (isEnabled()) {
            getWidget().interval = uidl.getIntAttribute("pollinginterval");
            getWidget().poller.scheduleRepeating(getWidget().interval);
        }
    }

    @Override
    public VProgressIndicator getWidget() {
        return (VProgressIndicator) super.getWidget();
    }
}
