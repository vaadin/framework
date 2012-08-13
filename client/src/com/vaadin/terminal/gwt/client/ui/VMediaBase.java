/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

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
