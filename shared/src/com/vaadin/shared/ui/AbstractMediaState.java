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
package com.vaadin.shared.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.communication.URLReference;

public class AbstractMediaState extends ComponentState {
    private boolean showControls;

    private String altText;

    private boolean htmlContentAllowed;

    private boolean autoplay;

    private boolean muted;

    private List<URLReference> sources = new ArrayList<URLReference>();
    private List<String> sourceTypes = new ArrayList<String>();

    public boolean isShowControls() {
        return showControls;
    }

    @DelegateToWidget("setControls")
    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    @DelegateToWidget
    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public boolean isMuted() {
        return muted;
    }

    @DelegateToWidget
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public List<URLReference> getSources() {
        return sources;
    }

    public void setSources(List<URLReference> sources) {
        this.sources = sources;
    }

    public List<String> getSourceTypes() {
        return sourceTypes;
    }

    public void setSourceTypes(List<String> sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

}
