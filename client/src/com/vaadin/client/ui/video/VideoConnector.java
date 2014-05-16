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
package com.vaadin.client.ui.video;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.MediaBaseConnector;
import com.vaadin.client.ui.VVideo;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.video.VideoConstants;
import com.vaadin.shared.ui.video.VideoState;
import com.vaadin.ui.Video;

@Connect(Video.class)
public class VideoConnector extends MediaBaseConnector {

    @Override
    public VideoState getState() {
        return (VideoState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setPoster(getResourceUrl(VideoConstants.POSTER_RESOURCE));
    }

    @Override
    public VVideo getWidget() {
        return (VVideo) super.getWidget();
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>video</code> element.";
    }

}
