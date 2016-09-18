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
package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.serialization.LegacySerializerUI.LegacySerializerComponent;

@Connect(value = LegacySerializerComponent.class)
public class LegacySerializerConnector extends AbstractComponentConnector
        implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        double doubleAttribute = uidl.getDoubleAttribute("doubleInfinity");
        getWidget().setHTML("doubleInfinity: " + doubleAttribute);
        client.updateVariable(getConnectorId(), "doubleInfinity",
                doubleAttribute, true);
    }

    @Override
    public HTML getWidget() {
        return (HTML) super.getWidget();
    }
}
