/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.sass.internal.tree;

import org.w3c.css.sac.SACMediaList;

import com.vaadin.sass.internal.visitor.ImportNodeHandler;

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

    @Override
    public void traverse() {
        // nested imports
        ImportNodeHandler.traverse(getParentNode());
    }
}
