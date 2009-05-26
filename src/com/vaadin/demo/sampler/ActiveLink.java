package com.vaadin.demo.sampler;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ActiveLink extends Link {

    private static final String TAG = "activelink";

    private static final Method LINK_FOLLOWED_METHOD;

    private HashSet<LinkActivatedListener> listeners = new HashSet<LinkActivatedListener>();

    public ActiveLink() {
        super();
    }

    public ActiveLink(String caption, Resource resource, String targetName,
            int width, int height, int border) {
        super(caption, resource, targetName, width, height, border);
    }

    public ActiveLink(String caption, Resource resource) {
        super(caption, resource);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    static {
        try {
            LINK_FOLLOWED_METHOD = LinkActivatedListener.class
                    .getDeclaredMethod("linkActivated",
                            new Class[] { LinkActivatedEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in ActiveLink");
        }
    }

    /**
     * Adds the link activated listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(LinkActivatedListener listener) {
        listeners.add(listener);
        addListener(LinkActivatedEvent.class, listener, LINK_FOLLOWED_METHOD);
        if (listeners.size() == 1) {
            requestRepaint();
        }
    }

    /**
     * Removes the link activated listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(LinkActivatedListener listener) {
        listeners.remove(listener);
        removeListener(ClickEvent.class, listener, LINK_FOLLOWED_METHOD);
        if (listeners.size() == 0) {
            requestRepaint();
        }
    }

    /**
     * Emits the options change event.
     */
    protected void fireClick(boolean linkOpened) {
        fireEvent(new LinkActivatedEvent(this, linkOpened));
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (listeners.size() > 0) {
            target.addVariable(this, "activated", false);
            target.addVariable(this, "opened", false);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        if (!isReadOnly() && variables.containsKey("activated")) {
            final Boolean activated = (Boolean) variables.get("activated");
            final Boolean opened = (Boolean) variables.get("opened");
            if (activated != null && activated.booleanValue() && !isReadOnly()) {
                fireClick((opened != null && opened.booleanValue() ? true
                        : false));
            }
        }
    }

    public class LinkActivatedEvent extends Component.Event {

        private boolean linkOpened;

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public LinkActivatedEvent(Component source, boolean linkOpened) {
            super(source);
            this.linkOpened = linkOpened;
        }

        /**
         * Gets the ActiveLink where the event occurred.
         * 
         * @return the Source of the event.
         */
        public ActiveLink getActiveLink() {
            return (ActiveLink) getSource();
        }

        /**
         * Indicates whether or not the link was opened on the client, i.e in a
         * new window/tab. If the link was not opened, the listener should react
         * to the event and "do something", otherwise the link does nothing.
         * 
         * @return true if the link was opened on the client
         */
        public boolean isLinkOpened() {
            return linkOpened;
        }
    }

    /**
     * ActiveLink click listener
     */
    public interface LinkActivatedListener extends Serializable {

        /**
         * ActiveLink has been activated.
         * 
         * @param event
         *            ActiveLink click event.
         */
        public void linkActivated(LinkActivatedEvent event);

    }

}
