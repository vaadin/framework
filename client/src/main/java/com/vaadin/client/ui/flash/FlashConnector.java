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
package com.vaadin.client.ui.flash;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VFlash;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.flash.FlashState;

@Connect(com.vaadin.ui.Flash.class)
public class FlashConnector extends AbstractComponentConnector {

    @Override
    public VFlash getWidget() {
        return (VFlash) super.getWidget();
    }

    @Override
    public FlashState getState() {
        return (FlashState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {

        super.onStateChanged(stateChangeEvent);
        getWidget().setSource(
                getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE));
        getWidget().setArchive(getState().archive);
        getWidget().setClassId(getState().classId);
        getWidget().setCodebase(getState().codebase);
        getWidget().setCodetype(getState().codetype);
        getWidget().setStandby(getState().standby);
        getWidget().setAlternateText(getState().alternateText);
        getWidget().setEmbedParams(getState().embedParams);

        getWidget().rebuildIfNeeded();
    }

    private final ElementResizeListener listener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            Element slot = e.getElement().getParentElement();
            getWidget().setSlotHeightAndWidth(slot.getOffsetHeight(),
                    slot.getOffsetWidth());
        }
    };

    @Override
    protected void init() {
        super.init();
        getLayoutManager().addElementResizeListener(getWidget().getElement(),
                listener);
    }

    @Override
    public void onUnregister() {
        getLayoutManager().removeElementResizeListener(getWidget().getElement(),
                listener);
    }

}
