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

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.window.WindowServerRpc;
import com.vaadin.shared.ui.window.WindowState;

/**
 * A component that represents a floating popup window that can be added to a
 * {@link UI}. A window is added to a {@code UI} using
 * {@link UI#addWindow(Window)}. </p>
 * <p>
 * The contents of a window is set using {@link #setContent(ComponentContainer)}
 * or by using the {@link #Window(String, ComponentContainer)} constructor. The
 * contents can in turn contain other components. By default, a
 * {@link VerticalLayout} is used as content.
 * </p>
 * <p>
 * A window can be positioned on the screen using absolute coordinates (pixels)
 * or set to be centered using {@link #center()}
 * </p>
 * <p>
 * The caption is displayed in the window header.
 * </p>
 * <p>
 * In Vaadin versions prior to 7.0.0, Window was also used as application level
 * windows. This function is now covered by the {@link UI} class.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Window extends Panel implements FocusNotifier, BlurNotifier,
        LegacyComponent {

    private WindowServerRpc rpc = new WindowServerRpc() {

        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Window.this, mouseDetails));
        }
    };

    /**
     * Creates a new unnamed window with a default layout.
     */
    public Window() {
        this("", null);
    }

    /**
     * Creates a new unnamed window with a default layout and given title.
     * 
     * @param caption
     *            the title of the window.
     */
    public Window(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new unnamed window with the given content and title.
     * 
     * @param caption
     *            the title of the window.
     * @param content
     *            the contents of the window
     */
    public Window(String caption, ComponentContainer content) {
        super(caption, content);
        registerRpc(rpc);
        setSizeUndefined();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#addComponent(com.vaadin.ui.Component)
     */

    @Override
    public void addComponent(Component c) {
        if (c instanceof Window) {
            throw new IllegalArgumentException(
                    "Window cannot be added to another via addComponent. "
                            + "Use addWindow(Window) instead.");
        }
        super.addComponent(c);
    }

    /* ********************************************************************* */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#paintContent(com.vaadin.server.PaintTarget)
     */

    @Override
    public synchronized void paintContent(PaintTarget target)
            throws PaintException {
        if (bringToFront != null) {
            target.addAttribute("bringToFront", bringToFront.intValue());
            bringToFront = null;
        }

        // Contents of the window panel is painted
        super.paintContent(target);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#changeVariables(java.lang.Object, java.util.Map)
     */

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        // TODO Are these for top level windows or sub windows?
        boolean sizeHasChanged = false;
        // size is handled in super class, but resize events only in windows ->
        // so detect if size change occurs before super.changeVariables()
        if (variables.containsKey("height")
                && (getHeightUnits() != Unit.PIXELS || (Integer) variables
                        .get("height") != getHeight())) {
            sizeHasChanged = true;
        }
        if (variables.containsKey("width")
                && (getWidthUnits() != Unit.PIXELS || (Integer) variables
                        .get("width") != getWidth())) {
            sizeHasChanged = true;
        }

        super.changeVariables(source, variables);

        // Positioning
        final Integer positionx = (Integer) variables.get("positionx");
        if (positionx != null) {
            final int x = positionx.intValue();
            // This is information from the client so it is already using the
            // position. No need to repaint.
            setPositionX(x < 0 ? -1 : x);
        }
        final Integer positiony = (Integer) variables.get("positiony");
        if (positiony != null) {
            final int y = positiony.intValue();
            // This is information from the client so it is already using the
            // position. No need to repaint.
            setPositionY(y < 0 ? -1 : y);
        }

        if (isClosable()) {
            // Closing
            final Boolean close = (Boolean) variables.get("close");
            if (close != null && close.booleanValue()) {
                close();
            }
        }

        // fire event if size has really changed
        if (sizeHasChanged) {
            fireResize();
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        } else if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }

    }

    /**
     * Method that handles window closing (from UI).
     * 
     * <p>
     * By default, sub-windows are removed from their respective parent windows
     * and thus visually closed on browser-side. Browser-level windows also
     * closed on the client-side, but they are not implicitly removed from the
     * application.
     * </p>
     * 
     * <p>
     * To explicitly close a sub-window, use {@link #removeWindow(Window)}. To
     * react to a window being closed (after it is closed), register a
     * {@link CloseListener}.
     * </p>
     */
    public void close() {
        UI uI = getUI();

        // Don't do anything if not attached to a UI
        if (uI != null) {
            // focus is restored to the parent window
            uI.focus();
            // subwindow is removed from the UI
            uI.removeWindow(this);
        }
    }

    /**
     * Gets the distance of Window left border in pixels from left border of the
     * containing (main window).
     * 
     * @return the Distance of Window left border in pixels from left border of
     *         the containing (main window). or -1 if unspecified.
     * @since 4.0.0
     */
    public int getPositionX() {
        return getState().positionX;
    }

    /**
     * Sets the distance of Window left border in pixels from left border of the
     * containing (main window).
     * 
     * @param positionX
     *            the Distance of Window left border in pixels from left border
     *            of the containing (main window). or -1 if unspecified.
     * @since 4.0.0
     */
    public void setPositionX(int positionX) {
        getState().positionX = positionX;
        getState().centered = false;
    }

    /**
     * Gets the distance of Window top border in pixels from top border of the
     * containing (main window).
     * 
     * @return Distance of Window top border in pixels from top border of the
     *         containing (main window). or -1 if unspecified .
     * 
     * @since 4.0.0
     */
    public int getPositionY() {
        return getState().positionY;
    }

    /**
     * Sets the distance of Window top border in pixels from top border of the
     * containing (main window).
     * 
     * @param positionY
     *            the Distance of Window top border in pixels from top border of
     *            the containing (main window). or -1 if unspecified
     * 
     * @since 4.0.0
     */
    public void setPositionY(int positionY) {
        getState().positionY = positionY;
        getState().centered = false;
    }

    private static final Method WINDOW_CLOSE_METHOD;
    static {
        try {
            WINDOW_CLOSE_METHOD = CloseListener.class.getDeclaredMethod(
                    "windowClose", new Class[] { CloseEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window close method not found");
        }
    }

    public static class CloseEvent extends Component.Event {

        /**
         * 
         * @param source
         */
        public CloseEvent(Component source) {
            super(source);
        }

        /**
         * Gets the Window.
         * 
         * @return the window.
         */
        public Window getWindow() {
            return (Window) getSource();
        }
    }

    /**
     * An interface used for listening to Window close events. Add the
     * CloseListener to a browser level window or a sub window and
     * {@link CloseListener#windowClose(CloseEvent)} will be called whenever the
     * user closes the window.
     * 
     * <p>
     * Since Vaadin 6.5, removing a window using {@link #removeWindow(Window)}
     * fires the CloseListener.
     * </p>
     */
    public interface CloseListener extends Serializable {
        /**
         * Called when the user closes a window. Use
         * {@link CloseEvent#getWindow()} to get a reference to the
         * {@link Window} that was closed.
         * 
         * @param e
         *            Event containing
         */
        public void windowClose(CloseEvent e);
    }

    /**
     * Adds a CloseListener to the window.
     * 
     * For a sub window the CloseListener is fired when the user closes it
     * (clicks on the close button).
     * 
     * For a browser level window the CloseListener is fired when the browser
     * level window is closed. Note that closing a browser level window does not
     * mean it will be destroyed. Also note that Opera does not send events like
     * all other browsers and therefore the close listener might not be called
     * if Opera is used.
     * 
     * <p>
     * Since Vaadin 6.5, removing windows using {@link #removeWindow(Window)}
     * does fire the CloseListener.
     * </p>
     * 
     * @param listener
     *            the CloseListener to add.
     */
    public void addCloseListener(CloseListener listener) {
        addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addCloseListener(CloseListener)}
     **/
    @Deprecated
    public void addListener(CloseListener listener) {
        addCloseListener(listener);
    }

    /**
     * Removes the CloseListener from the window.
     * 
     * <p>
     * For more information on CloseListeners see {@link CloseListener}.
     * </p>
     * 
     * @param listener
     *            the CloseListener to remove.
     */
    public void removeCloseListener(CloseListener listener) {
        removeListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeCloseListener(CloseListener)}
     **/
    @Deprecated
    public void removeListener(CloseListener listener) {
        removeCloseListener(listener);
    }

    protected void fireClose() {
        fireEvent(new Window.CloseEvent(this));
    }

    /**
     * Method for the resize event.
     */
    private static final Method WINDOW_RESIZE_METHOD;
    static {
        try {
            WINDOW_RESIZE_METHOD = ResizeListener.class.getDeclaredMethod(
                    "windowResized", new Class[] { ResizeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window resized method not found");
        }
    }

    /**
     * Resize events are fired whenever the client-side fires a resize-event
     * (e.g. the browser window is resized). The frequency may vary across
     * browsers.
     */
    public static class ResizeEvent extends Component.Event {

        /**
         * 
         * @param source
         */
        public ResizeEvent(Component source) {
            super(source);
        }

        /**
         * Get the window form which this event originated
         * 
         * @return the window
         */
        public Window getWindow() {
            return (Window) getSource();
        }
    }

    /**
     * Listener for window resize events.
     * 
     * @see com.vaadin.ui.Window.ResizeEvent
     */
    public interface ResizeListener extends Serializable {
        public void windowResized(ResizeEvent e);
    }

    /**
     * Add a resize listener.
     * 
     * @param listener
     */
    public void addResizeListener(ResizeListener listener) {
        addListener(ResizeEvent.class, listener, WINDOW_RESIZE_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addResizeListener(ResizeListener)}
     **/
    @Deprecated
    public void addListener(ResizeListener listener) {
        addResizeListener(listener);
    }

    /**
     * Remove a resize listener.
     * 
     * @param listener
     */
    public void removeResizeListener(ResizeListener listener) {
        removeListener(ResizeEvent.class, listener);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeResizeListener(ResizeListener)}
     **/
    @Deprecated
    public void removeListener(ResizeListener listener) {
        removeResizeListener(listener);
    }

    /**
     * Fire the resize event.
     */
    protected void fireResize() {
        fireEvent(new ResizeEvent(this));
    }

    /**
     * Used to keep the right order of windows if multiple windows are brought
     * to front in a single changeset. If this is not used, the order is quite
     * random (depends on the order getting to dirty list. e.g. which window got
     * variable changes).
     */
    private Integer bringToFront = null;

    /**
     * If there are currently several windows visible, calling this method makes
     * this window topmost.
     * <p>
     * This method can only be called if this window connected a UI. Else an
     * illegal state exception is thrown. Also if there are modal windows and
     * this window is not modal, and illegal state exception is thrown.
     * <p>
     */
    public void bringToFront() {
        UI uI = getUI();
        if (uI == null) {
            throw new IllegalStateException(
                    "Window must be attached to parent before calling bringToFront method.");
        }
        int maxBringToFront = -1;
        for (Window w : uI.getWindows()) {
            if (!isModal() && w.isModal()) {
                throw new IllegalStateException(
                        "The UI contains modal windows, non-modal window cannot be brought to front.");
            }
            if (w.bringToFront != null) {
                maxBringToFront = Math.max(maxBringToFront,
                        w.bringToFront.intValue());
            }
        }
        bringToFront = Integer.valueOf(maxBringToFront + 1);
        markAsDirty();
    }

    /**
     * Sets sub-window modal, so that widgets behind it cannot be accessed.
     * <b>Note:</b> affects sub-windows only.
     * 
     * @param modal
     *            true if modality is to be turned on
     */
    public void setModal(boolean modal) {
        getState().modal = modal;
        center();
    }

    /**
     * @return true if this window is modal.
     */
    public boolean isModal() {
        return getState().modal;
    }

    /**
     * Sets sub-window resizable. <b>Note:</b> affects sub-windows only.
     * 
     * @param resizable
     *            true if resizability is to be turned on
     */
    public void setResizable(boolean resizable) {
        getState().resizable = resizable;
    }

    /**
     * 
     * @return true if window is resizable by the end-user, otherwise false.
     */
    public boolean isResizable() {
        return getState().resizable;
    }

    /**
     * 
     * @return true if a delay is used before recalculating sizes, false if
     *         sizes are recalculated immediately.
     */
    public boolean isResizeLazy() {
        return getState().resizeLazy;
    }

    /**
     * Should resize operations be lazy, i.e. should there be a delay before
     * layout sizes are recalculated. Speeds up resize operations in slow UIs
     * with the penalty of slightly decreased usability.
     * 
     * Note, some browser send false resize events for the browser window and
     * are therefore always lazy.
     * 
     * @param resizeLazy
     *            true to use a delay before recalculating sizes, false to
     *            calculate immediately.
     */
    public void setResizeLazy(boolean resizeLazy) {
        getState().resizeLazy = resizeLazy;
    }

    /**
     * Sets this window to be centered relative to its parent window. Affects
     * sub-windows only. If the window is resized as a result of the size of its
     * content changing, it will keep itself centered as long as its position is
     * not explicitly changed programmatically or by the user.
     * <p>
     * <b>NOTE:</b> This method has several issues as currently implemented.
     * Please refer to http://dev.vaadin.com/ticket/8971 for details.
     */
    public void center() {
        getState().centered = true;
    }

    /**
     * Returns the closable status of the sub window. If a sub window is
     * closable it typically shows an X in the upper right corner. Clicking on
     * the X sends a close event to the server. Setting closable to false will
     * remove the X from the sub window and prevent the user from closing the
     * window.
     * 
     * Note! For historical reasons readonly controls the closability of the sub
     * window and therefore readonly and closable affect each other. Setting
     * readonly to true will set closable to false and vice versa.
     * <p/>
     * Closable only applies to sub windows, not to browser level windows.
     * 
     * @return true if the sub window can be closed by the user.
     */
    public boolean isClosable() {
        return !isReadOnly();
    }

    /**
     * Sets the closable status for the sub window. If a sub window is closable
     * it typically shows an X in the upper right corner. Clicking on the X
     * sends a close event to the server. Setting closable to false will remove
     * the X from the sub window and prevent the user from closing the window.
     * 
     * Note! For historical reasons readonly controls the closability of the sub
     * window and therefore readonly and closable affect each other. Setting
     * readonly to true will set closable to false and vice versa.
     * <p/>
     * Closable only applies to sub windows, not to browser level windows.
     * 
     * @param closable
     *            determines if the sub window can be closed by the user.
     */
    public void setClosable(boolean closable) {
        setReadOnly(!closable);
    }

    /**
     * Indicates whether a sub window can be dragged or not. By default a sub
     * window is draggable.
     * <p/>
     * Draggable only applies to sub windows, not to browser level windows.
     * 
     * @param draggable
     *            true if the sub window can be dragged by the user
     */
    public boolean isDraggable() {
        return getState().draggable;
    }

    /**
     * Enables or disables that a sub window can be dragged (moved) by the user.
     * By default a sub window is draggable.
     * <p/>
     * Draggable only applies to sub windows, not to browser level windows.
     * 
     * @param draggable
     *            true if the sub window can be dragged by the user
     */
    public void setDraggable(boolean draggable) {
        getState().draggable = draggable;
    }

    /*
     * Actions
     */
    protected CloseShortcut closeShortcut;

    /**
     * Makes is possible to close the window by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * Note that this shortcut only reacts while the window has focus, closing
     * itself - if you want to close a subwindow from a parent window, use
     * {@link #addAction(com.vaadin.event.Action)} of the parent window instead.
     * 
     * @param keyCode
     *            the keycode for invoking the shortcut
     * @param modifiers
     *            the (optional) modifiers for invoking the shortcut, null for
     *            none
     */
    public void setCloseShortcut(int keyCode, int... modifiers) {
        if (closeShortcut != null) {
            removeAction(closeShortcut);
        }
        closeShortcut = new CloseShortcut(this, keyCode, modifiers);
        addAction(closeShortcut);
    }

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setCloseShortcut(int, int...)}.
     */
    public void removeCloseShortcut() {
        if (closeShortcut != null) {
            removeAction(closeShortcut);
            closeShortcut = null;
        }
    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that closes the window.
     * 
     * <pre>
     * <code>
     *  // within the window using helper
     *  subWindow.setCloseShortcut(KeyCode.ESCAPE, null);
     * 
     *  // or globally
     *  getWindow().addAction(new Window.CloseShortcut(subWindow, KeyCode.ESCAPE));
     * </code>
     * </pre>
     * 
     */
    public static class CloseShortcut extends ShortcutListener {
        protected Window window;

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        public CloseShortcut(Window window, String shorthandCaption) {
            super(shorthandCaption);
            this.window = window;
        }

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         * @param modifiers
         *            optional modifiers for shortcut
         */
        public CloseShortcut(Window window, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.window = window;
        }

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * given {@link KeyCode}.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         */
        public CloseShortcut(Window window, int keyCode) {
            this(window, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            window.close();
        }
    }

    /**
     * Note, that focus/blur listeners in Window class are only supported by sub
     * windows. Also note that Window is not considered focused if its contained
     * component currently has focus.
     * 
     * @see com.vaadin.event.FieldEvents.FocusNotifier#addListener(com.vaadin.event.FieldEvents.FocusListener)
     */

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void addListener(FocusListener listener) {
        addFocusListener(listener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(FocusListener listener) {
        removeFocusListener(listener);
    }

    /**
     * Note, that focus/blur listeners in Window class are only supported by sub
     * windows. Also note that Window is not considered focused if its contained
     * component currently has focus.
     * 
     * @see com.vaadin.event.FieldEvents.BlurNotifier#addListener(com.vaadin.event.FieldEvents.BlurListener)
     */

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * @deprecated Since 7.0, replaced by {@link #addBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void addListener(BlurListener listener) {
        addBlurListener(listener);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(BlurListener listener) {
        removeBlurListener(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * If the window is a sub-window focusing will cause the sub-window to be
     * brought on top of other sub-windows on gain keyboard focus.
     */

    @Override
    public void focus() {
        /*
         * When focusing a sub-window it basically means it should be brought to
         * the front. Instead of just moving the keyboard focus we focus the
         * window and bring it top-most.
         */
        super.focus();
        bringToFront();
    }

    @Override
    protected WindowState getState() {
        return (WindowState) super.getState();
    }
}
