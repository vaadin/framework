/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.video;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.VMediaBase;

public class VVideo extends VMediaBase {

    private static String CLASSNAME = "v-video";

    private VideoElement video;

    public VVideo() {
        video = Document.get().createVideoElement();
        setMediaElement(video);
        setStyleName(CLASSNAME);

        updateDimensionsWhenMetadataLoaded(getElement());
    }

    /**
     * Registers a listener that updates the dimensions of the widget when the
     * video metadata has been loaded.
     * 
     * @param el
     */
    private native void updateDimensionsWhenMetadataLoaded(Element el)
    /*-{
              var self = this;
              el.addEventListener('loadedmetadata', $entry(function(e) {
                  self.@com.vaadin.terminal.gwt.client.ui.video.VVideo::updateElementDynamicSize(II)(el.videoWidth, el.videoHeight);
              }), false);

    }-*/;

    /**
     * Updates the dimensions of the widget.
     * 
     * @param w
     * @param h
     */
    private void updateElementDynamicSize(int w, int h) {
        video.getStyle().setWidth(w, Unit.PX);
        video.getStyle().setHeight(h, Unit.PX);
        Util.notifyParentOfSizeChange(this, true);
    }

    @Override
    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>video</code> element.";
    }

    public void setPoster(String poster) {
        video.setPoster(poster);
    }

}
