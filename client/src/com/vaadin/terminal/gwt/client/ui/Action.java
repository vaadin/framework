/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.vaadin.terminal.gwt.client.Util;

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
        if (getIconUrl() != null) {
            sb.append("<img src=\"" + Util.escapeAttribute(getIconUrl())
                    + "\" alt=\"icon\" />");
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
}
