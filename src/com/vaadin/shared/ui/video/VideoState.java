/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.video;

import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.AbstractMediaState;

public class VideoState extends AbstractMediaState {
    private URLReference poster;

    public URLReference getPoster() {
        return poster;
    }

    public void setPoster(URLReference poster) {
        this.poster = poster;
    }

}
