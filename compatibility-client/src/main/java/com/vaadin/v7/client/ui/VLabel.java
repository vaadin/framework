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

package com.vaadin.v7.client.ui;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.Util;
import com.vaadin.client.VTooltip;

public class VLabel extends HTML {

    public static final String CLASSNAME = "v-label";
    private static final String CLASSNAME_UNDEFINED_WIDTH = "v-label-undef-w";

    public VLabel() {
        super();
        setStyleName(CLASSNAME);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    public VLabel(String text) {
        super(text);
        setStyleName(CLASSNAME);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
            event.stopPropagation();
            return;
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (width == null || width.equals("")) {
            setStyleName(getElement(), CLASSNAME_UNDEFINED_WIDTH, true);
        } else {
            setStyleName(getElement(), CLASSNAME_UNDEFINED_WIDTH, false);
        }
    }

    // Vaadin 8 does not support IE8, no override for setText
}
