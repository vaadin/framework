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
                if (i > 0) {
                    builder.append(", ");
                }
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

    @Override
    public void traverse() {

    }

}
