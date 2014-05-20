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

package com.vaadin.client.ui.optiongroup;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VOptionGroup;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;
import com.vaadin.shared.ui.optiongroup.OptionGroupState;
import com.vaadin.ui.OptionGroup;

@Connect(OptionGroup.class)
public class OptionGroupConnector extends OptionGroupBaseConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().htmlContentAllowed = uidl
                .hasAttribute(OptionGroupConstants.HTML_CONTENT_ALLOWED);

        super.updateFromUIDL(uidl, client);

        getWidget().sendFocusEvents = client.hasEventListeners(this,
                EventId.FOCUS);
        getWidget().sendBlurEvents = client.hasEventListeners(this,
                EventId.BLUR);

        if (getWidget().focusHandlers != null) {
            for (HandlerRegistration reg : getWidget().focusHandlers) {
                reg.removeHandler();
            }
            getWidget().focusHandlers.clear();
            getWidget().focusHandlers = null;

            for (HandlerRegistration reg : getWidget().blurHandlers) {
                reg.removeHandler();
            }
            getWidget().blurHandlers.clear();
            getWidget().blurHandlers = null;
        }

        if (getWidget().sendFocusEvents || getWidget().sendBlurEvents) {
            getWidget().focusHandlers = new ArrayList<HandlerRegistration>();
            getWidget().blurHandlers = new ArrayList<HandlerRegistration>();

            // add focus and blur handlers to checkboxes / radio buttons
            for (Widget wid : getWidget().panel) {
                if (wid instanceof CheckBox) {
                    getWidget().focusHandlers.add(((CheckBox) wid)
                            .addFocusHandler(getWidget()));
                    getWidget().blurHandlers.add(((CheckBox) wid)
                            .addBlurHandler(getWidget()));
                }
            }
        }
    }

    @Override
    public VOptionGroup getWidget() {
        return (VOptionGroup) super.getWidget();
    }

    @Override
    public OptionGroupState getState() {
        return (OptionGroupState) super.getState();
    }
}
