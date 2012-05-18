/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.audio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.MediaBaseConnector;
import com.vaadin.ui.Audio;

@Connect(Audio.class)
public class AudioConnector extends MediaBaseConnector {

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        Style style = getWidget().getElement().getStyle();

        // Make sure that the controls are not clipped if visible.
        if (getState().isShowControls()
                && (style.getHeight() == null || "".equals(style.getHeight()))) {
            if (BrowserInfo.get().isChrome()) {
                style.setHeight(32, Unit.PX);
            } else {
                style.setHeight(25, Unit.PX);
            }
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAudio.class);
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>audio</code> element.";
    }
}
