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
package com.vaadin.client.ui.image;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.VImage;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.image.ImageServerRpc;
import com.vaadin.shared.ui.image.ImageState;

@Connect(com.vaadin.ui.Image.class)
public class ImageConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().addHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                getLayoutManager().setNeedsMeasure(ImageConnector.this);
            }

        }, LoadEvent.getType());
    }

    @Override
    public VImage getWidget() {
        return (VImage) super.getWidget();
    }

    @Override
    public ImageState getState() {
        return (ImageState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        String url = getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE);
        getWidget().setUrl(url != null ? url : "");

        String alt = getState().alternateText;
        // Some browsers turn a null alt text into a literal "null"
        getWidget().setAltText(alt != null ? alt : "");
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(ImageServerRpc.class).click(mouseDetails);
        }

    };

}
