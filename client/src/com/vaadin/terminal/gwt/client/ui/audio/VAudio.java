/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.audio;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Document;
import com.vaadin.terminal.gwt.client.ui.VMediaBase;

public class VAudio extends VMediaBase {
    private static String CLASSNAME = "v-audio";

    private AudioElement audio;

    public VAudio() {
        audio = Document.get().createAudioElement();
        setMediaElement(audio);
        setStyleName(CLASSNAME);
    }

}
