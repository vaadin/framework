/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;

public abstract class MediaBaseConnector extends AbstractComponentConnector {

    public static final String TAG_SOURCE = "src";

    public static final String ATTR_MUTED = "muted";
    public static final String ATTR_CONTROLS = "ctrl";
    public static final String ATTR_AUTOPLAY = "auto";
    public static final String ATTR_RESOURCE = "res";
    public static final String ATTR_RESOURCE_TYPE = "type";
    public static final String ATTR_HTML = "html";
    public static final String ATTR_ALT_TEXT = "alt";

    /**
     * Server to client RPC interface for controlling playback of the media.
     * 
     * @since 7.0
     */
    public static interface MediaControl extends ClientRpc {
        /**
         * Start playing the media.
         */
        public void play();

        /**
         * Pause playback of the media.
         */
        public void pause();
    }

    @Override
    protected void init() {
        super.init();

        registerRpc(MediaControl.class, new MediaControl() {
            public void play() {
                getWidget().play();
            }

            public void pause() {
                getWidget().pause();
            }
        });
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().setControls(shouldShowControls(uidl));
        getWidget().setAutoplay(shouldAutoplay(uidl));
        getWidget().setMuted(isMediaMuted(uidl));

        // Add all sources
        for (int ix = 0; ix < uidl.getChildCount(); ix++) {
            UIDL child = uidl.getChildUIDL(ix);
            if (TAG_SOURCE.equals(child.getTag())) {
                getWidget()
                        .addSource(getSourceUrl(child), getSourceType(child));
            }
        }
        setAltText(uidl);
    }

    protected boolean shouldShowControls(UIDL uidl) {
        return uidl.getBooleanAttribute(ATTR_CONTROLS);
    }

    private boolean shouldAutoplay(UIDL uidl) {
        return uidl.getBooleanAttribute(ATTR_AUTOPLAY);
    }

    private boolean isMediaMuted(UIDL uidl) {
        return uidl.getBooleanAttribute(ATTR_MUTED);
    }

    private boolean allowHtmlContent(UIDL uidl) {
        return uidl.getBooleanAttribute(ATTR_HTML);
    }

    @Override
    public VMediaBase getWidget() {
        return (VMediaBase) super.getWidget();
    }

    /**
     * @param uidl
     * @return the URL of a resource to be used as a source for the media
     */
    private String getSourceUrl(UIDL uidl) {
        String url = getConnection().translateVaadinUri(
                uidl.getStringAttribute(MediaBaseConnector.ATTR_RESOURCE));
        if (url == null) {
            return "";
        }
        return url;
    }

    /**
     * @param uidl
     * @return the mime type of the media
     */
    private String getSourceType(UIDL uidl) {
        return uidl.getStringAttribute(MediaBaseConnector.ATTR_RESOURCE_TYPE);
    }

    private void setAltText(UIDL uidl) {
        String alt = uidl.getStringAttribute(MediaBaseConnector.ATTR_ALT_TEXT);

        if (alt == null || "".equals(alt)) {
            alt = getWidget().getDefaultAltHtml();
        } else if (!allowHtmlContent(uidl)) {
            alt = Util.escapeHTML(alt);
        }
        getWidget().setAltText(alt);
    }

}
