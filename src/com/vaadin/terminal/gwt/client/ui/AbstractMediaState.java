package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.communication.URLReference;

public class AbstractMediaState extends ComponentState {
    private boolean showControls;

    private String altText;

    private boolean htmlContentAllowed;

    private boolean autoplay;

    private boolean muted;

    private List<URLReference> sources = new ArrayList<URLReference>();
    private List<String> sourceTypes = new ArrayList<String>();

    public boolean isShowControls() {
        return showControls;
    }

    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public List<URLReference> getSources() {
        return sources;
    }

    public void setSources(List<URLReference> sources) {
        this.sources = sources;
    }

    public List<String> getSourceTypes() {
        return sourceTypes;
    }

    public void setSourceTypes(List<String> sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

}
