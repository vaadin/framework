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
package com.vaadin.client.ui.audio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.MediaBaseConnector;
import com.vaadin.client.ui.VAudio;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Audio;

@Connect(Audio.class)
public class AudioConnector extends MediaBaseConnector {

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        Style style = getWidget().getElement().getStyle();

        // Make sure that the controls are not clipped if visible.
        if (getState().showControls
                && (style.getHeight() == null || "".equals(style.getHeight()))) {
            if (BrowserInfo.get().isChrome()) {
                style.setHeight(32, Unit.PX);
            } else {
                style.setHeight(25, Unit.PX);
            }
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAudio.class);
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>audio</code> element.";
    }
}
