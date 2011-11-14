package com.vaadin.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.ui.Window.CloseListener;

@ClientWidget(VView.class)
public class Root extends AbstractComponentContainer {
    private final RootLayout rootLayout;
    private Terminal terminal;
    private Application application;

    /**
     * A list of notifications that are waiting to be sent to the client.
     * Cleared (set to null) when the notifications have been sent.
     */
    private List<Notification> notifications;

    /**
     * A list of javascript commands that are waiting to be sent to the client.
     * Cleared (set to null) when the commands have been sent.
     */
    private List<String> jsExecQueue = null;

    /**
     * List of windows in this root.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<Window>();

    /**
     * The component that should be scrolled into view after the next repaint.
     * Null if nothing should be scrolled into view.
     */
    private Component scrollIntoView;

    private static final ThreadLocal<Root> currentRoot = new ThreadLocal<Root>();

    public Root(RootLayout rootLayout) {
        this.rootLayout = rootLayout;
        addComponent(rootLayout);
    }

    @Override
    public Root getRoot() {
        return this;
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        getRootLayout().paint(target);

        // Paint subwindows
        for (final Iterator<Window> i = windows.iterator(); i.hasNext();) {
            final Window w = i.next();
            w.paint(target);
        }

        // Paint notifications
        if (notifications != null) {
            target.startTag("notifications");
            for (final Iterator<Notification> it = notifications.iterator(); it
                    .hasNext();) {
                final Notification n = it.next();
                target.startTag("notification");
                if (n.getCaption() != null) {
                    target.addAttribute("caption", n.getCaption());
                }
                if (n.getDescription() != null) {
                    target.addAttribute("message", n.getDescription());
                }
                if (n.getIcon() != null) {
                    target.addAttribute("icon", n.getIcon());
                }
                if (!n.isHtmlContentAllowed()) {
                    target.addAttribute(
                            VView.NOTIFICATION_HTML_CONTENT_NOT_ALLOWED, true);
                }
                target.addAttribute("position", n.getPosition());
                target.addAttribute("delay", n.getDelayMsec());
                if (n.getStyleName() != null) {
                    target.addAttribute("style", n.getStyleName());
                }
                target.endTag("notification");
            }
            target.endTag("notifications");
            notifications = null;
        }

        // Add executable javascripts if needed
        if (jsExecQueue != null) {
            for (String script : jsExecQueue) {
                target.startTag("execJS");
                target.addAttribute("script", script);
                target.endTag("execJS");
            }
            jsExecQueue = null;
        }

        if (scrollIntoView != null) {
            target.addAttribute("scrollTo", scrollIntoView);
            scrollIntoView = null;
        }

        if (pendingFocus != null) {
            // ensure focused component is still attached to this main window
            if (pendingFocus.getRoot() == this
                    || (pendingFocus.getRoot() != null && pendingFocus
                            .getRoot().getParent() == this)) {
                target.addAttribute("focused", pendingFocus);
            }
            pendingFocus = null;
        }
    }

    public Iterator<Component> getComponentIterator() {
        return Collections.singleton((Component) getRootLayout()).iterator();
    }

    public String getName() {
        return "";
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void setApplication(Application application) {
        if (application == null) {
            throw new NullPointerException("application");
        } else if (this.application != null) {
            throw new IllegalStateException("Application has already been set");
        } else {
            this.application = application;
        }
    }

    /**
     * Adds a window inside this root.
     * 
     * <p>
     * Adding windows inside another window creates "subwindows". These windows
     * should not be added to application directly and are not accessible
     * directly with any url. Addding windows implicitly sets their parents.
     * </p>
     * 
     * <p>
     * Only one level of subwindows are supported. Thus you can add windows
     * inside such windows whose parent is <code>null</code>.
     * </p>
     * 
     * @param window
     * @throws IllegalArgumentException
     *             if a window is added inside non-application level window.
     * @throws NullPointerException
     *             if the given <code>Window</code> is <code>null</code>.
     */
    public void addWindow(Window window) throws IllegalArgumentException,
            NullPointerException {

        if (window == null) {
            throw new NullPointerException("Argument must not be null");
        }

        if (window.getApplication() != null) {
            throw new IllegalArgumentException(
                    "Window is already attached to an application.");
        }

        attachWindow(window);
    }

    private void attachWindow(Window w) {
        windows.add(w);
        w.setParent(this);
        requestRepaint();
    }

    /**
     * Remove the given subwindow from this root.
     * 
     * Since Vaadin 6.5, {@link CloseListener}s are called also when explicitly
     * removing a window by calling this method.
     * 
     * Since Vaadin 6.5, returns a boolean indicating if the window was removed
     * or not.
     * 
     * @param window
     *            Window to be removed.
     * @return true if the subwindow was removed, false otherwise
     */
    public boolean removeWindow(Window window) {
        if (!windows.remove(window)) {
            // Window window is not a subwindow of this root.
            return false;
        }
        window.setParent(null);
        window.fireClose();
        requestRepaint();

        return true;
    }

    public Collection<Window> getWindows() {
        return Collections.unmodifiableCollection(windows);
    }

    @Override
    public void focus() {
        super.focus();
    }

    /**
     * Component that should be focused after the next repaint. Null if no focus
     * change should take place.
     */
    private Focusable pendingFocus;

    /**
     * This method is used by Component.Focusable objects to request focus to
     * themselves. Focus renders must be handled at window level (instead of
     * Component.Focusable) due we want the last focused component to be focused
     * in client too. Not the one that is rendered last (the case we'd get if
     * implemented in Focusable only).
     * 
     * To focus component from Vaadin application, use Focusable.focus(). See
     * {@link Focusable}.
     * 
     * @param focusable
     *            to be focused on next paint
     */
    public void setFocusedComponent(Focusable focusable) {
        pendingFocus = focusable;
        requestRepaint();
    }

    /**
     * Shows a notification message on the middle of the window. The message
     * automatically disappears ("humanized message").
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption is
     * rendered as html.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The message
     */
    public void showNotification(String caption) {
        addNotification(new Notification(caption));
    }

    /**
     * Shows a notification message the window. The position and behavior of the
     * message depends on the type, which is one of the basic types defined in
     * {@link Notification}, for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption is
     * rendered as html.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The message
     * @param type
     *            The message type
     */
    public void showNotification(String caption, int type) {
        addNotification(new Notification(caption, type));
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description on the middle of the window. The message automatically
     * disappears ("humanized message").
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption and
     * description are rendered as html.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The caption of the message
     * @param description
     *            The message description
     * 
     */
    public void showNotification(String caption, String description) {
        addNotification(new Notification(caption, description));
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification},
     * for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption and
     * description are rendered as html.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The caption of the message
     * @param description
     *            The message description
     * @param type
     *            The message type
     */
    public void showNotification(String caption, String description, int type) {
        addNotification(new Notification(caption, description, type));
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification},
     * for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to avoid XSS vulnerabilities if html content is
     * allowed.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The message caption
     * @param description
     *            The message description
     * @param type
     *            The type of message
     * @param htmlContentAllowed
     *            Whether html in the caption and description should be
     *            displayed as html or as plain text
     */
    public void showNotification(String caption, String description, int type,
            boolean htmlContentAllowed) {
        addNotification(new Notification(caption, description, type,
                htmlContentAllowed));
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

    private void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new LinkedList<Notification>();
        }
        notifications.add(notification);
        requestRepaint();
    }

    /**
     * Executes JavaScript in this root.
     * 
     * <p>
     * This method allows one to inject javascript from the server to client. A
     * client implementation is not required to implement this functionality,
     * but currently all web-based clients do implement this.
     * </p>
     * 
     * <p>
     * Executing javascript this way often leads to cross-browser compatibility
     * issues and regressions that are hard to resolve. Use of this method
     * should be avoided and instead it is recommended to create new widgets
     * with GWT. For more info on creating own, reusable client-side widgets in
     * Java, read the corresponding chapter in Book of Vaadin.
     * </p>
     * 
     * @param script
     *            JavaScript snippet that will be executed.
     */
    public void executeJavaScript(String script) {
        if (jsExecQueue == null) {
            jsExecQueue = new ArrayList<String>();
        }

        jsExecQueue.add(script);

        requestRepaint();
    }

    /**
     * Scrolls any component between the component and root to a suitable
     * position so the component is visible to the user. The given component
     * must belong to this root.
     * 
     * @param component
     *            the component to be scrolled into view
     * @throws IllegalArgumentException
     *             if {@code component} does not belong to this root
     */
    public void scrollIntoView(Component component)
            throws IllegalArgumentException {
        if (component.getRoot() != this) {
            throw new IllegalArgumentException(
                    "The component where to scroll must belong to this root.");
        }
        scrollIntoView = component;
        requestRepaint();
    }

    public RootLayout getRootLayout() {
        return rootLayout;
    }

    public void init(WrappedRequest request) {
        getRootLayout().init(request);
    }

    public static void setCurrentRoot(Root root) {
        currentRoot.set(root);
    }

    public static Root getCurrentRoot() {
        return currentRoot.get();
    }
}
