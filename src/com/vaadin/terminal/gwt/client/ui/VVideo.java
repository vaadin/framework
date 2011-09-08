/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VVideo extends VMediaBase {
    public static final String ATTR_POSTER = "poster";

    private static String CLASSNAME = "v-video";

    private VideoElement video;

    public VVideo() {
        video = Document.get().createVideoElement();
        setMediaElement(video);
        setStyleName(CLASSNAME);

        updateDimensionsWhenMetadataLoaded(getElement());
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        super.updateFromUIDL(uidl, client);
        setPosterFromUIDL(uidl);
    }

    private void setPosterFromUIDL(UIDL uidl) {
        if (uidl.hasAttribute(ATTR_POSTER)) {
            video.setPoster(client.translateVaadinUri(uidl
                    .getStringAttribute(ATTR_POSTER)));
        }
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
              el.addEventListener('loadedmetadata', function(e) {
                  $entry(self.@com.vaadin.terminal.gwt.client.ui.VVideo::updateElementDynamicSize(II)(el.videoWidth, el.videoHeight));
              }, false);

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
}
