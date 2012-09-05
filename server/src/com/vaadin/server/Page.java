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

package com.vaadin.server;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.event.EventRouter;
import com.vaadin.server.WrappedRequest.BrowserDetails;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

public class Page implements Serializable {

    /**
     * Listener that gets notified when the size of the browser window
     * containing the uI has changed.
     * 
     * @see UI#addListener(BrowserWindowResizeListener)
     */
    public interface BrowserWindowResizeListener extends Serializable {
        /**
         * Invoked when the browser window containing a UI has been resized.
         * 
         * @param event
         *            a browser window resize event
         */
        public void browserWindowResized(BrowserWindowResizeEvent event);
    }

    /**
     * Event that is fired when a browser window containing a uI is resized.
     */
    public static class BrowserWindowResizeEvent extends EventObject {

        private final int width;
        private final int height;

        /**
         * Creates a new event
         * 
         * @param source
         *            the uI for which the browser window has been resized
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
        private final BorderStyle border;

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
                int height, BorderStyle border) {
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
            case MINIMAL:
                target.addAttribute("border", "minimal");
                break;
            case NONE:
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
    @Deprecated
    public static final BorderStyle BORDER_NONE = BorderStyle.NONE;

    /**
     * A border style used for opening resources in a window with a minimal
     * border.
     */
    public static final BorderStyle BORDER_MINIMAL = BorderStyle.MINIMAL;

    /**
     * A border style that indicates that the default border style should be
     * used when opening resources.
     */
    public static final BorderStyle BORDER_DEFAULT = BorderStyle.DEFAULT;

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
    public static class FragmentChangedEvent extends EventObject {

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
         * Gets the uI in which the fragment has changed.
         * 
         * @return the uI in which the fragment has changed
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

    private final UI uI;

    private int browserWindowWidth = -1;
    private int browserWindowHeight = -1;

    private JavaScript javaScript;

    public Page(UI uI) {
        this.uI = uI;
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

    public void addFragmentChangedListener(Page.FragmentChangedListener listener) {
        addListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addFragmentChangedListener(FragmentChangedListener)}
     **/
    @Deprecated
    public void addListener(Page.FragmentChangedListener listener) {
        addFragmentChangedListener(listener);
    }

    public void removeFragmentChangedListener(
            Page.FragmentChangedListener listener) {
        removeListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeFragmentChangedListener(FragmentChangedListener)}
     **/
    @Deprecated
    public void removeListener(Page.FragmentChangedListener listener) {
        removeFragmentChangedListener(listener);
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
            uI.markAsDirty();
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
        return uI.getSession().getBrowser();
    }

    public void setBrowserWindowSize(int width, int height) {
        boolean fireEvent = false;

        if (width != browserWindowWidth) {
            browserWindowWidth = width;
            fireEvent = true;
        }

        if (height != browserWindowHeight) {
            browserWindowHeight = height;
            fireEvent = true;
        }

        if (fireEvent) {
            fireEvent(new BrowserWindowResizeEvent(this, browserWindowWidth,
                    browserWindowHeight));
        }

    }

    /**
     * Adds a new {@link BrowserWindowResizeListener} to this uI. The listener
     * will be notified whenever the browser window within which this uI resides
     * is resized.
     * 
     * @param resizeListener
     *            the listener to add
     * 
     * @see BrowserWindowResizeListener#browserWindowResized(BrowserWindowResizeEvent)
     * @see #setResizeLazy(boolean)
     */
    public void addBrowserWindowResizeListener(
            BrowserWindowResizeListener resizeListener) {
        addListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addBrowserWindowResizeListener(BrowserWindowResizeListener)}
     **/
    @Deprecated
    public void addListener(BrowserWindowResizeListener resizeListener) {
        addBrowserWindowResizeListener(resizeListener);
    }

    /**
     * Removes a {@link BrowserWindowResizeListener} from this UI. The listener
     * will no longer be notified when the browser window is resized.
     * 
     * @param resizeListener
     *            the listener to remove
     */
    public void removeBrowserWindowResizeListener(
            BrowserWindowResizeListener resizeListener) {
        removeListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeBrowserWindowResizeListener(BrowserWindowResizeListener)}
     **/
    @Deprecated
    public void removeListener(BrowserWindowResizeListener resizeListener) {
        removeBrowserWindowResizeListener(resizeListener);
    }

    /**
     * Gets the last known height of the browser window in which this UI
     * resides.
     * 
     * @return the browser window height in pixels
     */
    public int getBrowserWindowHeight() {
        return browserWindowHeight;
    }

    /**
     * Gets the last known width of the browser window in which this uI resides.
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
            javaScript.extend(uI);
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
                            UIConstants.ATTRIBUTE_NOTIFICATION_CAPTION,
                            n.getCaption());
                }
                if (n.getDescription() != null) {
                    target.addAttribute(
                            UIConstants.ATTRIBUTE_NOTIFICATION_MESSAGE,
                            n.getDescription());
                }
                if (n.getIcon() != null) {
                    target.addAttribute(
                            UIConstants.ATTRIBUTE_NOTIFICATION_ICON,
                            n.getIcon());
                }
                if (!n.isHtmlContentAllowed()) {
                    target.addAttribute(
                            UIConstants.NOTIFICATION_HTML_CONTENT_NOT_ALLOWED,
                            true);
                }
                target.addAttribute(
                        UIConstants.ATTRIBUTE_NOTIFICATION_POSITION, n
                                .getPosition().ordinal());
                target.addAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_DELAY,
                        n.getDelayMsec());
                if (n.getStyleName() != null) {
                    target.addAttribute(
                            UIConstants.ATTRIBUTE_NOTIFICATION_STYLE,
                            n.getStyleName());
                }
                target.endTag("notification");
            }
            target.endTag("notifications");
            notifications = null;
        }

        if (fragment != null) {
            target.addAttribute(UIConstants.FRAGMENT_VARIABLE, fragment);
        }

    }

    /**
     * Opens the given resource in this uI. The contents of this UI is replaced
     * by the {@code Resource}.
     * 
     * @param resource
     *            the resource to show in this uI
     */
    public void open(Resource resource) {
        openList.add(new OpenResource(resource, null, -1, -1, BORDER_DEFAULT));
        uI.markAsDirty();
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
        uI.markAsDirty();
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
     *            the border style of the window.
     */
    public void open(Resource resource, String windowName, int width,
            int height, BorderStyle border) {
        openList.add(new OpenResource(resource, windowName, width, height,
                border));
        uI.markAsDirty();
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
        uI.markAsDirty();
    }

    /**
     * Shows a notification message.
     * 
     * @see Notification
     * 
     * @param notification
     *            The notification message to show
     * 
     * @deprecated Use Notification.show(Page) instead.
     */
    @Deprecated
    public void showNotification(Notification notification) {
        addNotification(notification);
    }

    /**
     * Gets the Page to which the current uI belongs. This is automatically
     * defined when processing requests to the server. In other cases, (e.g.
     * from background threads), the current uI is not automatically defined.
     * 
     * @see UI#getCurrent()
     * 
     * @return the current page instance if available, otherwise
     *         <code>null</code>
     */
    public static Page getCurrent() {
        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            return null;
        }
        return currentUI.getPage();
    }

    /**
     * Sets the page title. The page title is displayed by the browser e.g. as
     * the title of the browser window or as the title of the tab.
     * 
     * @param title
     *            the new page title to set
     */
    public void setTitle(String title) {
        uI.getRpcProxy(PageClientRpc.class).setTitle(title);
    }

}
