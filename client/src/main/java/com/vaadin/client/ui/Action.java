/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.user.client.Command;

/**
 * 
 */
public abstract class Action implements Command {

    protected ActionOwner owner;

    protected String iconUrl = null;

    protected String caption = "";

    public Action(ActionOwner owner) {
        this.owner = owner;
    }

    /**
     * Executed when action fired
     */
    @Override
    public abstract void execute();

    public String getHTML() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<div>");
        // Could store the icon in a field instead, but it doesn't really matter
        // right now because Actions are recreated every time they are needed
        Icon icon = owner.getClient().getIcon(getIconUrl());
        if (icon != null) {
            icon.setAlternateText("icon");
            sb.append(icon.getElement().getString());
        }
        sb.append(getCaption());
        sb.append("</div>");
        return sb.toString();
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String url) {
        iconUrl = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Action [owner=" + owner + ", iconUrl=" + iconUrl + ", caption="
                + caption + "]";
    }
}
