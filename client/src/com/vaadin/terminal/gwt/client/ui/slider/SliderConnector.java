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
package com.vaadin.terminal.gwt.client.ui.slider;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.ui.Slider;

@Connect(Slider.class)
public class SliderConnector extends AbstractFieldConnector implements
        Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().immediate = getState().isImmediate();
        getWidget().disabled = !isEnabled();
        getWidget().readonly = isReadOnly();

        getWidget().vertical = uidl.hasAttribute("vertical");

        // TODO should style names be used?

        if (getWidget().vertical) {
            getWidget().addStyleName(VSlider.CLASSNAME + "-vertical");
        } else {
            getWidget().removeStyleName(VSlider.CLASSNAME + "-vertical");
        }

        getWidget().min = uidl.getDoubleAttribute("min");
        getWidget().max = uidl.getDoubleAttribute("max");
        getWidget().resolution = uidl.getIntAttribute("resolution");
        getWidget().value = new Double(uidl.getDoubleVariable("value"));

        getWidget().setFeedbackValue(getWidget().value);

        getWidget().buildBase();

        if (!getWidget().vertical) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    getWidget().buildHandle();
                    getWidget().setValue(getWidget().value, false);
                }
            });
        } else {
            getWidget().buildHandle();
            getWidget().setValue(getWidget().value, false);
        }
    }

    @Override
    public VSlider getWidget() {
        return (VSlider) super.getWidget();
    }

}
