package com.vaadin.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.vaadin.Application;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.ui.Window.CloseListener;

@ClientWidget(VView.class)
public class DefaultRoot extends AbstractComponentContainer implements Root {
    private final Component content;
    private Terminal terminal;
    private Application application;

    /**
     * List of windows in this root.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<Window>();

    public DefaultRoot(Component content) {
        this.content = content;
        addComponent(content);
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
        content.paint(target);

        // Paint subwindows
        for (final Iterator<Window> i = windows.iterator(); i.hasNext();) {
            final Window w = i.next();
            w.paint(target);
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
        return Collections.singleton(content).iterator();
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

    public int getTabIndex() {
        throw new IllegalStateException("Tab index not defined for roots");
    }

    public void setTabIndex(int tabIndex) {
        throw new IllegalStateException("Tab index not defined for roots");
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
}
