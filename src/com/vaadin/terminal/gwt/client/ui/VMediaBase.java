package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public abstract class VMediaBase extends Widget implements Paintable {
    public static final String ATTR_PAUSE = "pause";
    public static final String ATTR_PLAY = "play";
    public static final String ATTR_MUTED = "muted";
    public static final String ATTR_CONTROLS = "ctrl";
    public static final String ATTR_AUTOPLAY = "auto";
    public static final String TAG_SOURCE = "src";
    public static final String ATTR_RESOURCE = "res";
    public static final String ATTR_RESOURCE_TYPE = "type";
    public static final String ATTR_HTML = "html";
    public static final String ATTR_ALT_TEXT = "alt";

    private MediaElement media;
    protected ApplicationConnection client;

    /**
     * Sets the MediaElement that is to receive all commands and properties.
     * 
     * @param element
     */
    public void setMediaElement(MediaElement element) {
        setElement(element);
        media = element;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        this.client = client;

        media.setControls(shouldShowControls(uidl));
        media.setAutoplay(shouldAutoplay(uidl));
        media.setMuted(isMediaMuted(uidl));

        // Add all sources
        for (int ix = 0; ix < uidl.getChildCount(); ix++) {
            UIDL child = uidl.getChildUIDL(ix);
            if (TAG_SOURCE.equals(child.getTag())) {
                Element src = Document.get().createElement("source").cast();
                src.setAttribute("src", getSourceUrl(child));
                src.setAttribute("type", getSourceType(child));
                media.appendChild(src);
            }
        }
        setAltText(uidl);

        evalPauseCommand(uidl);
        evalPlayCommand(uidl);
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

    /**
     * @param uidl
     * @return the URL of a resource to be used as a source for the media
     */
    private String getSourceUrl(UIDL uidl) {
        String url = client.translateVaadinUri(uidl
                .getStringAttribute(ATTR_RESOURCE));
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
        return uidl.getStringAttribute(ATTR_RESOURCE_TYPE);
    }

    private void setAltText(UIDL uidl) {
        String alt = uidl.getStringAttribute(ATTR_ALT_TEXT);

        if (alt == null || "".equals(alt)) {
            alt = getDefaultAltHtml();
        } else if (!allowHtmlContent(uidl)) {
            alt = Util.escapeHTML(alt);
        }
        media.appendChild(Document.get().createTextNode(alt));
    }

    private boolean allowHtmlContent(UIDL uidl) {
        return uidl.getBooleanAttribute(ATTR_HTML);
    }

    private void evalPlayCommand(UIDL uidl) {
        if (uidl.hasAttribute(ATTR_PLAY)) {
            media.play();
        }
    }

    private void evalPauseCommand(UIDL uidl) {
        if (uidl.hasAttribute(ATTR_PAUSE)) {
            media.pause();
        }
    }

    /**
     * @return the default HTML to show users with browsers that do not support
     *         HTML5 media markup.
     */
    protected abstract String getDefaultAltHtml();
}
