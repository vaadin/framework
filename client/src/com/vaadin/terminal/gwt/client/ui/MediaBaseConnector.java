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
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.AbstractMediaState;
import com.vaadin.shared.ui.MediaControl;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;

public abstract class MediaBaseConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();

        registerRpc(MediaControl.class, new MediaControl() {
            @Override
            public void play() {
                getWidget().play();
            }

            @Override
            public void pause() {
                getWidget().pause();
            }
        });
    }

    @Override
    public AbstractMediaState getState() {
        return (AbstractMediaState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setControls(getState().isShowControls());
        getWidget().setAutoplay(getState().isAutoplay());
        getWidget().setMuted(getState().isMuted());
        for (int i = 0; i < getState().getSources().size(); i++) {
            URLReference source = getState().getSources().get(i);
            String sourceType = getState().getSourceTypes().get(i);
            getWidget().addSource(source.getURL(), sourceType);
        }
        setAltText(getState().getAltText());
    }

    @Override
    public VMediaBase getWidget() {
        return (VMediaBase) super.getWidget();
    }

    private void setAltText(String altText) {

        if (altText == null || "".equals(altText)) {
            altText = getDefaultAltHtml();
        } else if (!getState().isHtmlContentAllowed()) {
            altText = Util.escapeHTML(altText);
        }
        getWidget().setAltText(altText);
    }

    /**
     * @return the default HTML to show users with browsers that do not support
     *         HTML5 media markup.
     */
    protected abstract String getDefaultAltHtml();

}
