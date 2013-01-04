/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public abstract class VMediaBase extends Widget {

    private MediaElement media;

    /**
     * Sets the MediaElement that is to receive all commands and properties.
     * 
     * @param element
     */
    public void setMediaElement(MediaElement element) {
        setElement(element);
        media = element;
    }

    public void play() {
        media.play();
    }

    public void pause() {
        media.pause();
    }

    public void setAltText(String alt) {
        media.appendChild(Document.get().createTextNode(alt));
    }

    public void setControls(boolean shouldShowControls) {
        media.setControls(shouldShowControls);
    }

    public void setAutoplay(boolean shouldAutoplay) {
        media.setAutoplay(shouldAutoplay);
    }

    public void setMuted(boolean mediaMuted) {
        media.setMuted(mediaMuted);
    }

    public void addSource(String sourceUrl, String sourceType) {
        Element src = Document.get().createElement("source").cast();
        src.setAttribute("src", sourceUrl);
        src.setAttribute("type", sourceType);
        media.appendChild(src);
    }
}
