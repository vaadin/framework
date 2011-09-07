package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;

public class VAudio extends VMediaBase {
    private static String CLASSNAME = "v-audio";

    private AudioElement audio;

    public VAudio() {
        audio = Document.get().createAudioElement();
        setMediaElement(audio);
        setStyleName(CLASSNAME);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        Style style = audio.getStyle();

        // Make sure that the controls are not clipped if visible.
        if (shouldShowControls(uidl)
                && (style.getHeight() == null || "".equals(style.getHeight()))) {
            if (BrowserInfo.get().isChrome()) {
                style.setHeight(32, Unit.PX);
            } else {
                style.setHeight(25, Unit.PX);
            }
        }
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>audio</code> element.";
    }
}
