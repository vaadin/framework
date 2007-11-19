package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;

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
    public abstract void execute();

    public String getHTML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<div>");
        if (getIconUrl() != null) {
            sb.append("<img src=\"" + getIconUrl() + "\" alt=\"icon\" />");
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

/**
 * Action owner must provide a set of actions for context menu and IAction
 * objects.
 */
interface ActionOwner {

    /**
     * @return Array of IActions
     */
    public Action[] getActions();

    public ApplicationConnection getClient();

    public String getPaintableId();
}