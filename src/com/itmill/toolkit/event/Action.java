/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.event;

import java.io.Serializable;

import com.itmill.toolkit.terminal.Resource;

/**
 * Implements the action framework. This class contains subinterfaces for action
 * handling and listing, and for action handler registrations and
 * unregistration.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Action implements Serializable {

    /**
     * Action title.
     */
    private String caption;

    /**
     * Action icon.
     */
    private Resource icon = null;

    /**
     * Constructs a new action with the given caption.
     * 
     * @param caption
     *            the caption for the new action.
     */
    public Action(String caption) {
        this.caption = caption;
    }

    /**
     * Constructs a new action with the given caption string and icon.
     * 
     * @param caption
     *            the caption for the new action.
     * @param icon
     *            the icon for the new action.
     */
    public Action(String caption, Resource icon) {
        this.caption = caption;
        this.icon = icon;
    }

    /**
     * Returns the action's caption.
     * 
     * @return the action's caption as a <code>String</code>.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Returns the action's icon.
     * 
     * @return the action's Icon.
     */
    public Resource getIcon() {
        return icon;
    }

    /**
     * Interface implemented by classes who wish to handle actions.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Handler extends Serializable {

        /**
         * Gets the list of actions applicable to this handler.
         * 
         * @param target
         *            the target handler to list actions for. For item
         *            containers this is the item id.
         * @param sender
         *            the party that would be sending the actions. Most of this
         *            is the action container.
         * @return the list of Action
         */
        public Action[] getActions(Object target, Object sender);

        /**
         * Handles an action for the given target. The handler method may just
         * discard the action if it's not suitable.
         * 
         * @param action
         *            the action to be handled.
         * @param sender
         *            the sender of the action. This is most often the action
         *            container.
         * @param target
         *            the target of the action. For item containers this is the
         *            item id.
         */
        public void handleAction(Action action, Object sender, Object target);
    }

    /**
     * Interface implemented by all components where actions can be registered.
     * This means that the components lets others to register as action handlers
     * to it. When the component receives an action targeting its contents it
     * should loop all action handlers registered to it and let them handle the
     * action.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Container extends Serializable {

        /**
         * Registers a new action handler for this container
         * 
         * @param actionHandler
         *            the new handler to be added.
         */
        public void addActionHandler(Action.Handler actionHandler);

        /**
         * Removes a previously registered action handler for the contents of
         * this container.
         * 
         * @param actionHandler
         *            the handler to be removed.
         */
        public void removeActionHandler(Action.Handler actionHandler);
    }

    /**
     * Sets the caption.
     * 
     * @param caption
     *            the caption to set.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Sets the icon.
     * 
     * @param icon
     *            the icon to set.
     */
    public void setIcon(Resource icon) {
        this.icon = icon;
    }

}
