/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import org.w3c.css.sac.SACMediaList;

public class MediaNode extends Node {
    private static final long serialVersionUID = 2502097081457509523L;

    SACMediaList media;

    public MediaNode(SACMediaList media) {
        super();
        this.media = media;
    }

    public SACMediaList getMedia() {
        return media;
    }

    public void setMedia(SACMediaList media) {
        this.media = media;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("@media ");
        if (media != null) {
            for (int i = 0; i < media.getLength(); i++) {
                builder.append(media.item(i));
            }
        }
        builder.append(" {\n");
        for (Node child : children) {
            if (child instanceof BlockNode) {
                builder.append("\t" + ((BlockNode) child).toString(true) + "\n");
            } else {
                builder.append("\t" + child.toString() + "\n");
            }

        }
        builder.append("}");
        return builder.toString();
    }
}
