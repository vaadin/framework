/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Document;

public class VAudio extends VMediaBase {
    private static String CLASSNAME = "v-audio";

    private AudioElement audio;

    public VAudio() {
        audio = Document.get().createAudioElement();
        setMediaElement(audio);
        setStyleName(CLASSNAME);
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>audio</code> element.";
    }

}
