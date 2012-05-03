package com.vaadin.terminal.gwt.client.ui.video;

import com.vaadin.terminal.gwt.client.communication.URLReference;
import com.vaadin.terminal.gwt.client.ui.AbstractMediaState;

public class VideoState extends AbstractMediaState {
    private URLReference poster;

    public URLReference getPoster() {
        return poster;
    }

    public void setPoster(URLReference poster) {
        this.poster = poster;
    }

}
