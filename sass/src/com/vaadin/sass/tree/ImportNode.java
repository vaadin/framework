package com.vaadin.sass.tree;

import org.w3c.css.sac.SACMediaList;

public class ImportNode extends Node {
    private static final long serialVersionUID = 5671255892282668438L;

    private String uri;
    private SACMediaList ml;
    private boolean isURL;

    public ImportNode(String uri, SACMediaList ml, boolean isURL) {
        super();
        this.uri = uri;
        this.ml = ml;
        this.isURL = isURL;
    }

    public boolean isPureCssImport() {
        return (isURL || uri.endsWith(".css") || uri.startsWith("http://") || hasMediaQueries());
    }

    private boolean hasMediaQueries() {
        return (ml != null && ml.getLength() >= 1 && !"all".equals(ml.item(0)));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("@import ");
        if (isURL) {
            builder.append("url(").append(uri).append(")");
        } else {
            builder.append("\"").append(uri).append("\"");
        }
        if (hasMediaQueries()) {
            for (int i = 0; i < ml.getLength(); i++) {
                builder.append(" ").append(ml.item(i));
            }
        }
        builder.append(";");
        return builder.toString();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SACMediaList getMl() {
        return ml;
    }
}
