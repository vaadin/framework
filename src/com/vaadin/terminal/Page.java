/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.event.EventRouter;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.terminal.gwt.client.ui.notification.VNotification;
import com.vaadin.terminal.gwt.client.ui.root.PageClientRpc;
import com.vaadin.terminal.gwt.client.ui.root.VRoot;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Root;

public class Page implements Serializable {

    /**
     * Listener that gets notified when the size of the browser window
     * containing the root has changed.
     * 
     * @see Root#addListener(BrowserWindowResizeListener)
     */
    public interface BrowserWindowResizeListener extends Serializable {
        /**
         * Invoked when the browser window containing a Root has been resized.
         * 
         * @param event
         *            a browser window resize event
         */
        public void browserWindowResized(BrowserWindowResizeEvent event);
    }

    /**
     * Event that is fired when a browser window containing a root is resized.
     */
    public class BrowserWindowResizeEvent extends EventObject {

        private final int width;
        private final int height;

        /**
         * Creates a new event
         * 
         * @param source
         *            the root for which the browser window has been resized
         * @param width
         *            the new width of the browser window
         * @param height
         *            the new height of the browser window
         */
        public BrowserWindowResizeEvent(Page source, int width, int height) {
            super(source);
            this.width = width;
            this.height = height;
        }

        @Override
        public Page getSource() {
            return (Page) super.getSource();
        }

        /**
         * Gets the new browser window height
         * 
         * @return an integer with the new pixel height of the browser window
         */
        public int getHeight() {
            return height;
        }

        /**
         * Gets the new browser window width
         * 
         * @return an integer with the new pixel width of the browser window
         */
        public int getWidth() {
            return width;
        }
    }

    /**
     * Private class for storing properties related to opening resources.
     */
    private class OpenResource implements Serializable {

        /**
         * The resource to open
         */
        private final Resource resource;

        /**
         * The name of the target window
         */
        private final String name;

        /**
         * The width of the target window
         */
        private final int width;

        /**
         * The height of the target window
         */
        private final int height;

        /**
         * The border style of the target window
         */
        private final int border;

        /**
         * Creates a new open resource.
         * 
         * @param resource
         *            The resource to open
         * @param name
         *            The name of the target window
         * @param width
         *            The width of the target window
         * @param height
         *            The height of the target window
         * @param border
         *            The border style of the target window
         */
        private OpenResource(Resource resource, String name, int width,
                int height, int border) {
            this.resource = resource;
            this.name = name;
            this.width = width;
            this.height = height;
            this.border = border;
        }

        /**
         * Paints the open request. Should be painted inside the window.
         * 
         * @param target
         *            the paint target
         * @throws PaintException
         *             if the paint operation fails
         */
        private void paintContent(PaintTarget target) throws PaintException {
            target.startTag("open");
            target.addAttribute("src", resource);
            if (name != null && name.length() > 0) {
                target.addAttribute("name", name);
            }
            if (width >= 0) {
                target.addAttribute("width", width);
            }
            if (height >= 0) {
                target.addAttribute("height", height);
            }
            switch (border) {
            case BORDER_MINIMAL:
                target.addAttribute("border", "minimal");
                break;
            case BORDER_NONE:
                target.addAttribute("border", "none");
                break;
            }

            target.endTag("open");
        }
    }

    private static final Method BROWSWER_RESIZE_METHOD = ReflectTools
            .findMethod(BrowserWindowResizeListener.class,
                    "browserWindowResized", BrowserWindowResizeEvent.class);

    /**
     * A border style used for opening resources in a window without a border.
     */
    public static final int BORDER_NONE = 0;

    /**
     * A border style used for opening resources in a window with a minimal
     * border.
     */
    public static final int BORDER_MINIMAL = 1;

    /**
     * A border style that indicates that the default border style should be
     * used when opening resources.
     */
    public static final int BORDER_DEFAULT = 2;

    /**
     * Listener that listens changes in URI fragment.
     */
    public interface FragmentChangedListener extends Serializable {
        public void fragmentChanged(FragmentChangedEvent event);
    }

    private static final Method FRAGMENT_CHANGED_METHOD = ReflectTools
            .findMethod(Page.FragmentChangedListener.class, "fragmentChanged",
                    FragmentChangedEvent.class);

    /**
     * Resources to be opened automatically on next repaint. The list is
     * automatically cleared when it has been sent to the client.
     */
    private final LinkedList<OpenResource> openList = new LinkedList<OpenResource>();

    /**
     * A list of notifications that are waiting to be sent to the client.
     * Cleared (set to null) when the notifications have been sent.
     */
    private List<Notification> notifications;

    /**
     * Event fired when uri fragment changes.
     */
    public class FragmentChangedEvent extends EventObject {

        /**
         * The new uri fragment
         */
        private final String fragment;

        /**
         * Creates a new instance of UriFragmentReader change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public FragmentChangedEvent(Page source, String fragment) {
            super(source);
            this.fragment = fragment;
        }

        /**
         * Gets the root in which the fragment has changed.
         * 
         * @return the root in which the fragment has changed
         */
        public Page getPage() {
            return (Page) getSource();
        }

        /**
         * Get the new fragment
         * 
         * @return the new fragment
         */
        public String getFragment() {
            return fragment;
        }
    }

    private EventRouter eventRouter;

    /**
     * The current URI fragment.
     */
    private String fragment;

    private final Root root;

    private int browserWindowWidth = -1;
    private int browserWindowHeight = -1;

    private JavaScript javaScript;

    public Page(Root root) {
        this.root = root;
    }

    private void addListener(Class<?> eventType, Object target, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, target, method);
    }

    private void removeListener(Class<?> eventType, Object target, Method method) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target, method);
        }
    }

    public void addListener(Page.FragmentChangedListener listener) {
        addListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    public void removeListener(Page.FragmentChangedListener listener) {
        removeListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    /**
     * Sets URI fragment. Optionally fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @param fireEvent
     *            true to fire event
     * @see FragmentChangedEvent
     * @see Page.FragmentChangedListener
     */
    public void setFragment(String newFragment, boolean fireEvents) {
        if (newFragment == null) {
            throw new NullPointerException("The fragment may not be null");
        }
        if (!newFragment.equals(fragment)) {
            fragment = newFragment;
            if (fireEvents) {
                fireEvent(new FragmentChangedEvent(this, newFragment));
            }
            root.requestRepaint();
        }
    }

    private void fireEvent(EventObject event) {
        if (eventRouter != null) {
            eventRouter.fireEvent(event);
        }
    }

    /**
     * Sets URI fragment. This method fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @see FragmentChangedEvent
     * @see Page.FragmentChangedListener
     */
    public void setFragment(String newFragment) {
        setFragment(newFragment, true);
    }

    /**
     * Gets currently set URI fragment.
     * <p>
     * To listen changes in fragment, hook a
     * {@link Page.FragmentChangedListener}.
     * 
     * @return the current fragment in browser uri or null if not known
     */
    public String getFragment() {
        return fragment;
    }

    public void init(WrappedRequest request) {
        BrowserDetails browserDetails = request.getBrowserDetails();
        if (browserDetails != null) {
            fragment = browserDetails.getUriFragment();
        }
    }

    public WebBrowser getWebBrowser() {
        return ((WebApplicationContext) root.getApplication().getContext())
                .getBrowser();
    }

    public void setBrowserWindowSize(Integer width, Integer height) {
        boolean fireEvent = false;

        if (width != null) {
            int newWidth = width.intValue();
            if (newWidth != browserWindowWidth) {
                browserWindowWidth = newWidth;
                fireEvent = true;
            }
        }

        if (height != null) {
            int newHeight = height.intValue();
            if (newHeight != browserWindowHeight) {
                browserWindowHeight = newHeight;
                fireEvent = true;
            }
        }

        if (fireEvent) {
            fireEvent(new BrowserWindowResizeEvent(this, browserWindowWidth,
                    browserWindowHeight));
        }

    }

    /**
     * Adds a new {@link BrowserWindowResizeListener} to this root. The listener
     * will be notified whenever the browser window within which this root
     * resides is resized.
     * 
     * @param resizeListener
     *            the listener to add
     * 
     * @see BrowserWindowResizeListener#browserWindowResized(BrowserWindowResizeEvent)
     * @see #setResizeLazy(boolean)
     */
    public void addListener(BrowserWindowResizeListener resizeListener) {
        addListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * Removes a {@link BrowserWindowResizeListener} from this root. The
     * listener will no longer be notified when the browser window is resized.
     * 
     * @param resizeListener
     *            the listener to remove
     */
    public void removeListener(BrowserWindowResizeListener resizeListener) {
        removeListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * Gets the last known height of the browser window in which this root
     * resides.
     * 
     * @return the browser window height in pixels
     */
    public int getBrowserWindowHeight() {
        return browserWindowHeight;
    }

    /**
     * Gets the last known width of the browser window in which this root
     * resides.
     * 
     * @return the browser window width in pixels
     */
    public int getBrowserWindowWidth() {
        return browserWindowWidth;
    }

    public JavaScript getJavaScript() {
        if (javaScript == null) {
            // Create and attach on first use
            javaScript = new JavaScript();
            javaScript.extend(root);
        }

        return javaScript;
    }

    public void paintContent(PaintTarget target) throws PaintException {
        if (!openList.isEmpty()) {
            for (final Iterator<OpenResource> i = openList.iterator(); i
                    .hasNext();) {
                (i.next()).paintContent(target);
            }
            openList.clear();
        }

        // Paint notifications
        if (notifications != null) {
            target.startTag("notifications");
            for (final Iterator<Notification> it = notifications.iterator(); it
                    .hasNext();) {
                final Notification n = it.next();
                target.startTag("notification");
                if (n.getCaption() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_CAPTION,
                            n.getCaption());
                }
                if (n.getDescription() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_MESSAGE,
                            n.getDescription());
                }
                if (n.getIcon() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_ICON,
                            n.getIcon());
                }
                if (!n.isHtmlContentAllowed()) {
                    target.addAttribute(
                            VRoot.NOTIFICATION_HTML_CONTENT_NOT_ALLOWED, true);
                }
                target.addAttribute(
                        VNotification.ATTRIBUTE_NOTIFICATION_POSITION,
                        n.getPosition());
                target.addAttribute(VNotification.ATTRIBUTE_NOTIFICATION_DELAY,
                        n.getDelayMsec());
                if (n.getStyleName() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_STYLE,
                            n.getStyleName());
                }
                target.endTag("notification");
            }
            target.endTag("notifications");
            notifications = null;
        }

        if (fragment != null) {
            target.addAttribute(VRoot.FRAGMENT_VARIABLE, fragment);
        }

    }

    /**
     * Opens the given resource in this root. The contents of this Root is
     * replaced by the {@code Resource}.
     * 
     * @param resource
     *            the resource to show in this root
     */
    public void open(Resource resource) {
        openList.add(new OpenResource(resource, null, -1, -1, BORDER_DEFAULT));
        root.requestRepaint();
    }

    /**
     * Opens the given resource in a window with the given name.
     * <p>
     * The supplied {@code windowName} is used as the target name in a
     * window.open call in the client. This means that special values such as
     * "_blank", "_self", "_top", "_parent" have special meaning. An empty or
     * <code>null</code> window name is also a special case.
     * </p>
     * <p>
     * "", null and "_self" as {@code windowName} all causes the resource to be
     * opened in the current window, replacing any old contents. For
     * downloadable content you should avoid "_self" as "_self" causes the
     * client to skip rendering of any other changes as it considers them
     * irrelevant (the page will be replaced by the resource). This can speed up
     * the opening of a resource, but it might also put the client side into an
     * inconsistent state if the window content is not completely replaced e.g.,
     * if the resource is downloaded instead of displayed in the browser.
     * </p>
     * <p>
     * "_blank" as {@code windowName} causes the resource to always be opened in
     * a new window or tab (depends on the browser and browser settings).
     * </p>
     * <p>
     * "_top" and "_parent" as {@code windowName} works as specified by the HTML
     * standard.
     * </p>
     * <p>
     * Any other {@code windowName} will open the resource in a window with that
     * name, either by opening a new window/tab in the browser or by replacing
     * the contents of an existing window with that name.
     * </p>
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     */
    public void open(Resource resource, String windowName) {
        openList.add(new OpenResource(resource, windowName, -1, -1,
                BORDER_DEFAULT));
        root.requestRepaint();
    }

    /**
     * Opens the given resource in a window with the given size, border and
     * name. For more information on the meaning of {@code windowName}, see
     * {@link #open(Resource, String)}.
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     * @param width
     *            the width of the window in pixels
     * @param height
     *            the height of the window in pixels
     * @param border
     *            the border style of the window. See {@link #BORDER_NONE
     *            Window.BORDER_* constants}
     */
    public void open(Resource resource, String windowName, int width,
            int height, int border) {
        openList.add(new OpenResource(resource, windowName, width, height,
                border));
        root.requestRepaint();
    }

    /**
     * Internal helper method to actually add a notification.
     * 
     * @param notification
     *            the notification to add
     */
    private void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new LinkedList<Notification>();
        }
        notifications.add(notification);
        root.requestRepaint();
    }

    /**
     * Shows a notification message.
     * 
     * @see Notification
     * @see #showNotification(String)
     * @see #showNotification(String, int)
     * @see #showNotification(String, String)
     * @see #showNotification(String, String, int)
     * 
     * @param notification
     *            The notification message to show
     */
    public void showNotification(Notification notification) {
        addNotification(notification);
    }

    public static Page getCurrent() {
        Root currentRoot = Root.getCurrentRoot();
        if (currentRoot == null) {
            return null;
        }
        return currentRoot.getPage();
    }

    public void setTitle(String title) {
        root.getRpcProxy(PageClientRpc.class).setTitle(title);
    }

}
