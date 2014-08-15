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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.RelevantValue;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Focusable;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Util;
import com.vaadin.client.debug.internal.VDebugWindow;
import com.vaadin.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.window.WindowMoveEvent;
import com.vaadin.client.ui.window.WindowMoveHandler;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.shared.ui.window.WindowRole;

/**
 * "Sub window" component.
 *
 * @author Vaadin Ltd
 */
public class VWindow extends VOverlay implements ShortcutActionHandlerOwner,
        ScrollHandler, KeyDownHandler, KeyUpHandler, FocusHandler, BlurHandler,
        Focusable {

    private static ArrayList<VWindow> windowOrder = new ArrayList<VWindow>();

    private static boolean orderingDefered;

    public static final String CLASSNAME = "v-window";

    private static final String MODAL_WINDOW_OPEN_CLASSNAME = "v-modal-window-open";

    private static final int STACKING_OFFSET_PIXELS = 15;

    public static final int Z_INDEX = 10000;

    /** For internal use only. May be removed or replaced in the future. */
    public Element contents;

    /** For internal use only. May be removed or replaced in the future. */
    public Element header;

    /** For internal use only. May be removed or replaced in the future. */
    public Element footer;

    private Element resizeBox;

    /** For internal use only. May be removed or replaced in the future. */
    public final FocusableScrollPanel contentPanel = new FocusableScrollPanel();

    private boolean dragging;

    private int startX;

    private int startY;

    private int origX;

    private int origY;

    private boolean resizing;

    private int origW;

    private int origH;

    /** For internal use only. May be removed or replaced in the future. */
    public Element closeBox;

    /** For internal use only. May be removed or replaced in the future. */
    public Element maximizeRestoreBox;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String id;

    /** For internal use only. May be removed or replaced in the future. */
    public ShortcutActionHandler shortcutHandler;

    /** Last known positionx read from UIDL or updated to application connection */
    private int uidlPositionX = -1;

    /** Last known positiony read from UIDL or updated to application connection */
    private int uidlPositionY = -1;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean vaadinModality = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean resizable = true;

    private boolean draggable = true;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean resizeLazy = false;

    private Element modalityCurtain;
    private Element draggingCurtain;
    private Element resizingCurtain;

    private Element headerText;

    private boolean closable = true;

    private Connector[] assistiveConnectors = new Connector[0];
    private String assistivePrefix;
    private String assistivePostfix;

    private Element topTabStop;
    private Element bottomTabStop;

    private NativePreviewHandler topEventBlocker;
    private NativePreviewHandler bottomEventBlocker;

    private HandlerRegistration topBlockerRegistration;
    private HandlerRegistration bottomBlockerRegistration;

    // Prevents leaving the window with the Tab key when true
    private boolean doTabStop;

    private boolean hasFocus;

    /**
     * If centered (via UIDL), the window should stay in the centered -mode
     * until a position is received from the server, or the user moves or
     * resizes the window.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean centered = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    private Element wrapper;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean visibilityChangesDisabled;

    /** For internal use only. May be removed or replaced in the future. */
    public int bringToFrontSequence = -1;

    private VLazyExecutor delayedContentsSizeUpdater = new VLazyExecutor(200,
            new ScheduledCommand() {

                @Override
                public void execute() {
                    updateContentsSize();
                }
            });

    public VWindow() {
        super(false, false, true); // no autohide, not modal, shadow
        // Different style of shadow for windows
        setShadowStyle("window");

        Roles.getDialogRole().set(getElement());
        Roles.getDialogRole().setAriaRelevantProperty(getElement(),
                RelevantValue.ADDITIONS);

        constructDOM();
        contentPanel.addScrollHandler(this);
        contentPanel.addKeyDownHandler(this);
        contentPanel.addKeyUpHandler(this);
        contentPanel.addFocusHandler(this);
        contentPanel.addBlurHandler(this);
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        /*
         * Stores the element that has focus in the application UI when the
         * window is opened, so it can be restored when the window closes.
         *
         * This is currently implemented for the case when one non-modal window
         * can be open at the same time, and the focus is not changed while the
         * window is open.
         */
        getApplicationConnection().getUIConnector().getWidget().storeFocus();

        /*
         * When this window gets reattached, set the tabstop to the previous
         * state.
         */
        setTabStopEnabled(doTabStop);

        // Fix for #14413. Any pseudo elements inside these elements are not
        // visible on initial render unless we shake the DOM.
        if (BrowserInfo.get().isIE8()) {
            closeBox.getStyle().setDisplay(Display.NONE);
            maximizeRestoreBox.getStyle().setDisplay(Display.NONE);
            Scheduler.get().scheduleFinally(new Command() {
                @Override
                public void execute() {
                    closeBox.getStyle().clearDisplay();
                    maximizeRestoreBox.getStyle().clearDisplay();
                }
            });
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        /*
         * Restores the previously stored focused element.
         *
         * When the focus was changed outside the window while the window was
         * open, the originally stored element is restored.
         */
        getApplicationConnection().getUIConnector().getWidget()
                .focusStoredElement();

        removeTabBlockHandlers();
    }

    private void addTabBlockHandlers() {
        if (topBlockerRegistration == null) {
            topBlockerRegistration = Event
                    .addNativePreviewHandler(topEventBlocker);
            bottomBlockerRegistration = Event
                    .addNativePreviewHandler(bottomEventBlocker);
        }
    }

    private void removeTabBlockHandlers() {
        if (topBlockerRegistration != null) {
            topBlockerRegistration.removeHandler();
            topBlockerRegistration = null;

            bottomBlockerRegistration.removeHandler();
            bottomBlockerRegistration = null;
        }
    }

    public void bringToFront() {
        int curIndex = windowOrder.indexOf(this);
        if (curIndex + 1 < windowOrder.size()) {
            windowOrder.remove(this);
            windowOrder.add(this);
            for (; curIndex < windowOrder.size(); curIndex++) {
                windowOrder.get(curIndex).setWindowOrder(curIndex);
            }
        }
    }

    /**
     * Returns true if this window is the topmost VWindow
     *
     * @return
     */
    private boolean isActive() {
        return equals(getTopmostWindow());
    }

    private static VWindow getTopmostWindow() {
        if (windowOrder.size() > 0) {
            return windowOrder.get(windowOrder.size() - 1);
        }
        return null;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setWindowOrderAndPosition() {
        // This cannot be done in the constructor as the widgets are created in
        // a different order than on they should appear on screen
        if (windowOrder.contains(this)) {
            // Already set
            return;
        }
        final int order = windowOrder.size();
        setWindowOrder(order);
        windowOrder.add(this);
        setPopupPosition(order * STACKING_OFFSET_PIXELS, order
                * STACKING_OFFSET_PIXELS);

    }

    private void setWindowOrder(int order) {
        setZIndex(order + Z_INDEX);
    }

    @Override
    protected void setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        if (vaadinModality) {
            getModalityCurtain().getStyle().setZIndex(zIndex);
        }
    }

    protected com.google.gwt.user.client.Element getModalityCurtain() {
        if (modalityCurtain == null) {
            modalityCurtain = DOM.createDiv();
            modalityCurtain.setClassName(CLASSNAME + "-modalitycurtain");
        }
        return DOM.asOld(modalityCurtain);
    }

    protected void constructDOM() {
        setStyleName(CLASSNAME);

        topTabStop = DOM.createDiv();
        DOM.setElementAttribute(topTabStop, "tabindex", "0");

        header = DOM.createDiv();
        DOM.setElementProperty(header, "className", CLASSNAME + "-outerheader");
        headerText = DOM.createDiv();
        DOM.setElementProperty(headerText, "className", CLASSNAME + "-header");
        contents = DOM.createDiv();
        DOM.setElementProperty(contents, "className", CLASSNAME + "-contents");
        footer = DOM.createDiv();
        DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
        resizeBox = DOM.createDiv();
        DOM.setElementProperty(resizeBox, "className", CLASSNAME + "-resizebox");
        closeBox = DOM.createDiv();
        maximizeRestoreBox = DOM.createDiv();
        DOM.setElementProperty(maximizeRestoreBox, "className", CLASSNAME
                + "-maximizebox");
        DOM.setElementAttribute(maximizeRestoreBox, "tabindex", "0");
        DOM.setElementProperty(closeBox, "className", CLASSNAME + "-closebox");
        DOM.setElementAttribute(closeBox, "tabindex", "0");
        DOM.appendChild(footer, resizeBox);

        bottomTabStop = DOM.createDiv();
        DOM.setElementAttribute(bottomTabStop, "tabindex", "0");

        wrapper = DOM.createDiv();
        DOM.setElementProperty(wrapper, "className", CLASSNAME + "-wrap");

        DOM.appendChild(wrapper, topTabStop);
        DOM.appendChild(wrapper, header);
        DOM.appendChild(wrapper, maximizeRestoreBox);
        DOM.appendChild(wrapper, closeBox);
        DOM.appendChild(header, headerText);
        DOM.appendChild(wrapper, contents);
        DOM.appendChild(wrapper, footer);
        DOM.appendChild(wrapper, bottomTabStop);
        DOM.appendChild(super.getContainerElement(), wrapper);

        sinkEvents(Event.ONDBLCLICK | Event.MOUSEEVENTS | Event.TOUCHEVENTS
                | Event.ONCLICK | Event.ONLOSECAPTURE);

        setWidget(contentPanel);

        // Make the closebox accessible for assistive devices
        Roles.getButtonRole().set(closeBox);
        Roles.getButtonRole().setAriaLabelProperty(closeBox, "close button");

        // Make the maximizebox accessible for assistive devices
        Roles.getButtonRole().set(maximizeRestoreBox);
        Roles.getButtonRole().setAriaLabelProperty(maximizeRestoreBox,
                "maximize button");

        // Provide the title to assistive devices
        AriaHelper.ensureHasId(headerText);
        Roles.getDialogRole().setAriaLabelledbyProperty(getElement(),
                Id.of(headerText));

        // Handlers to Prevent tab to leave the window
        topEventBlocker = new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                if (nativeEvent.getEventTarget().cast() == topTabStop
                        && nativeEvent.getKeyCode() == KeyCodes.KEY_TAB
                        && nativeEvent.getShiftKey()) {
                    nativeEvent.preventDefault();
                }
            }
        };

        bottomEventBlocker = new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                if (nativeEvent.getEventTarget().cast() == bottomTabStop
                        && nativeEvent.getKeyCode() == KeyCodes.KEY_TAB
                        && !nativeEvent.getShiftKey()) {
                    nativeEvent.preventDefault();
                }
            }
        };
    }

    /**
     * Sets the message that is provided to users of assistive devices when the
     * user reaches the top of the window when leaving a window with the tab key
     * is prevented.
     * <p>
     * This message is not visible on the screen.
     *
     * @param topMessage
     *            String provided when the user navigates with Shift-Tab keys to
     *            the top of the window
     */
    public void setTabStopTopAssistiveText(String topMessage) {
        Roles.getNoteRole().setAriaLabelProperty(topTabStop, topMessage);
    }

    /**
     * Sets the message that is provided to users of assistive devices when the
     * user reaches the bottom of the window when leaving a window with the tab
     * key is prevented.
     * <p>
     * This message is not visible on the screen.
     *
     * @param bottomMessage
     *            String provided when the user navigates with the Tab key to
     *            the bottom of the window
     */
    public void setTabStopBottomAssistiveText(String bottomMessage) {
        Roles.getNoteRole().setAriaLabelProperty(bottomTabStop, bottomMessage);
    }

    /**
     * Gets the message that is provided to users of assistive devices when the
     * user reaches the top of the window when leaving a window with the tab key
     * is prevented.
     *
     * @return the top message
     */
    public String getTabStopTopAssistiveText() {
        return Roles.getNoteRole().getAriaLabelProperty(topTabStop);
    }

    /**
     * Gets the message that is provided to users of assistive devices when the
     * user reaches the bottom of the window when leaving a window with the tab
     * key is prevented.
     *
     * @return the bottom message
     */
    public String getTabStopBottomAssistiveText() {
        return Roles.getNoteRole().getAriaLabelProperty(bottomTabStop);
    }

    /**
     * Calling this method will defer ordering algorithm, to order windows based
     * on servers bringToFront and modality instructions. Non changed windows
     * will be left intact.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public static void deferOrdering() {
        if (!orderingDefered) {
            orderingDefered = true;
            Scheduler.get().scheduleFinally(new Command() {

                @Override
                public void execute() {
                    doServerSideOrdering();
                    VNotification.bringNotificationsToFront();
                }
            });
        }
    }

    private static void doServerSideOrdering() {
        orderingDefered = false;
        VWindow[] array = windowOrder.toArray(new VWindow[windowOrder.size()]);
        Arrays.sort(array, new Comparator<VWindow>() {

            @Override
            public int compare(VWindow o1, VWindow o2) {
                /*
                 * Order by modality, then by bringtofront sequence.
                 */

                if (o1.vaadinModality && !o2.vaadinModality) {
                    return 1;
                } else if (!o1.vaadinModality && o2.vaadinModality) {
                    return -1;
                } else if (o1.bringToFrontSequence > o2.bringToFrontSequence) {
                    return 1;
                } else if (o1.bringToFrontSequence < o2.bringToFrontSequence) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < array.length; i++) {
            VWindow w = array[i];
            if (w.bringToFrontSequence != -1 || w.vaadinModality) {
                w.bringToFront();
                w.bringToFrontSequence = -1;
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        /*
         * Visibility with VWindow works differently than with other Paintables
         * in Vaadin. Invisible VWindows are not attached to DOM at all. Flag is
         * used to avoid visibility call from
         * ApplicationConnection.updateComponent();
         */
        if (!visibilityChangesDisabled) {
            super.setVisible(visible);
        }

        if (visible
                && BrowserInfo.get().requiresPositionAbsoluteOverflowAutoFix()) {

            /*
             * Shake up the DOM a bit to make the window shed unnecessary
             * scrollbars and resize correctly afterwards. The version fixing
             * ticket #11994 which was changing the size to 110% was replaced
             * with this due to ticket #12943
             */
            Util.runWebkitOverflowAutoFix(contents.getFirstChildElement());
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setDraggable(boolean draggable) {
        if (this.draggable == draggable) {
            return;
        }

        this.draggable = draggable;

        setCursorProperties();
    }

    private void setCursorProperties() {
        if (!draggable) {
            header.getStyle().setProperty("cursor", "default");
            footer.getStyle().setProperty("cursor", "default");
        } else {
            header.getStyle().setProperty("cursor", "");
            footer.getStyle().setProperty("cursor", "");
        }
    }

    /**
     * Sets the closable state of the window. Additionally hides/shows the close
     * button according to the new state.
     *
     * @param closable
     *            true if the window can be closed by the user
     */
    public void setClosable(boolean closable) {
        if (this.closable == closable) {
            return;
        }

        this.closable = closable;
        if (closable) {
            closeBox.getStyle().clearDisplay();
        } else {
            closeBox.getStyle().setDisplay(Display.NONE);
        }

    }

    /**
     * Returns the closable state of the sub window. If the sub window is
     * closable a decoration (typically an X) is shown to the user. By clicking
     * on the X the user can close the window.
     *
     * @return true if the sub window is closable
     */
    protected boolean isClosable() {
        return closable;
    }

    @Override
    public void show() {
        if (!windowOrder.contains(this)) {
            // This is needed if the window is hidden and then shown again.
            // Otherwise this VWindow is added to windowOrder in the
            // constructor.
            windowOrder.add(this);
        }

        if (vaadinModality) {
            showModalityCurtain();
        }
        super.show();
    }

    @Override
    public void hide() {

        /*
         * If the window has a RichTextArea and the RTA is focused at the time
         * of hiding in IE8 only the window will have some problems returning
         * the focus to the correct place. Curiously the focus will be returned
         * correctly if clicking on the "close" button in the window header but
         * closing the window from a button for example in the window will fail.
         * Symptom described in #10776
         *
         * The problematic part is that for the focus to be returned correctly
         * an input element needs to be focused in the root panel. Focusing some
         * other element apparently won't work.
         */
        if (BrowserInfo.get().isIE8()) {
            fixIE8FocusCaptureIssue();
        }

        if (vaadinModality) {
            hideModalityCurtain();
        }
        super.hide();

        int curIndex = windowOrder.indexOf(this);
        // Remove window from windowOrder to avoid references being left
        // hanging.
        windowOrder.remove(curIndex);
        // Update the z-indices of any remaining windows
        while (curIndex < windowOrder.size()) {
            windowOrder.get(curIndex).setWindowOrder(curIndex++);
        }
    }

    private void fixIE8FocusCaptureIssue() {
        Element e = DOM.createInputText();
        Style elemStyle = e.getStyle();
        elemStyle.setPosition(Position.ABSOLUTE);
        elemStyle.setTop(-10, Unit.PX);
        elemStyle.setWidth(0, Unit.PX);
        elemStyle.setHeight(0, Unit.PX);

        contentPanel.getElement().appendChild(e);
        e.focus();
        contentPanel.getElement().removeChild(e);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setVaadinModality(boolean modality) {
        vaadinModality = modality;
        if (vaadinModality) {
            if (isAttached()) {
                showModalityCurtain();
            }
            addTabBlockHandlers();
            deferOrdering();
        } else {
            if (modalityCurtain != null) {
                if (isAttached()) {
                    hideModalityCurtain();
                }
                modalityCurtain = null;
            }
            if (!doTabStop) {
                removeTabBlockHandlers();
            }
        }
    }

    private void showModalityCurtain() {
        getModalityCurtain().getStyle().setZIndex(
                windowOrder.indexOf(this) + Z_INDEX);

        if (isShowing()) {
            getOverlayContainer().insertBefore(getModalityCurtain(),
                    getElement());
        } else {
            getOverlayContainer().appendChild(getModalityCurtain());
        }

        Document.get().getBody().addClassName(MODAL_WINDOW_OPEN_CLASSNAME);
    }

    private void hideModalityCurtain() {
        Document.get().getBody().removeClassName(MODAL_WINDOW_OPEN_CLASSNAME);

        modalityCurtain.removeFromParent();

        if (BrowserInfo.get().isIE()) {
            // IE leaks memory in certain cases unless we release the reference
            // (#9197)
            modalityCurtain = null;
        }
    }

    /*
     * Shows an empty div on top of all other content; used when moving, so that
     * iframes (etc) do not steal event.
     */
    private void showDraggingCurtain() {
        getElement().getParentElement().insertBefore(getDraggingCurtain(),
                getElement());
    }

    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            draggingCurtain.removeFromParent();
        }
    }

    /*
     * Shows an empty div on top of all other content; used when resizing, so
     * that iframes (etc) do not steal event.
     */
    private void showResizingCurtain() {
        getElement().getParentElement().insertBefore(getResizingCurtain(),
                getElement());
    }

    private void hideResizingCurtain() {
        if (resizingCurtain != null) {
            resizingCurtain.removeFromParent();
        }
    }

    private Element getDraggingCurtain() {
        if (draggingCurtain == null) {
            draggingCurtain = createCurtain();
            draggingCurtain.setClassName(CLASSNAME + "-draggingCurtain");
        }

        return draggingCurtain;
    }

    private Element getResizingCurtain() {
        if (resizingCurtain == null) {
            resizingCurtain = createCurtain();
            resizingCurtain.setClassName(CLASSNAME + "-resizingCurtain");
        }

        return resizingCurtain;
    }

    private Element createCurtain() {
        Element curtain = DOM.createDiv();

        curtain.getStyle().setPosition(Position.ABSOLUTE);
        curtain.getStyle().setTop(0, Unit.PX);
        curtain.getStyle().setLeft(0, Unit.PX);
        curtain.getStyle().setWidth(100, Unit.PCT);
        curtain.getStyle().setHeight(100, Unit.PCT);
        curtain.getStyle().setZIndex(VOverlay.Z_INDEX);

        return curtain;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setResizable(boolean resizability) {
        resizable = resizability;
        if (resizability) {
            DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
            DOM.setElementProperty(resizeBox, "className", CLASSNAME
                    + "-resizebox");
        } else {
            DOM.setElementProperty(footer, "className", CLASSNAME + "-footer "
                    + CLASSNAME + "-footer-noresize");
            DOM.setElementProperty(resizeBox, "className", CLASSNAME
                    + "-resizebox " + CLASSNAME + "-resizebox-disabled");
        }
    }

    public void updateMaximizeRestoreClassName(boolean visible,
            WindowMode windowMode) {
        String className;
        if (windowMode == WindowMode.MAXIMIZED) {
            className = CLASSNAME + "-restorebox";
        } else {
            className = CLASSNAME + "-maximizebox";
        }
        if (!visible) {
            className = className + " " + className + "-disabled";
        }
        maximizeRestoreBox.setClassName(className);
    }

    // TODO this will eventually be removed, currently used to avoid updating to
    // server side.
    public void setPopupPositionNoUpdate(int left, int top) {
        if (top < 0) {
            // ensure window is not moved out of browser window from top of the
            // screen
            top = 0;
        }
        super.setPopupPosition(left, top);
    }

    @Override
    public void setPopupPosition(int left, int top) {
        if (top < 0) {
            // ensure window is not moved out of browser window from top of the
            // screen
            top = 0;
        }
        super.setPopupPosition(left, top);
        if (left != uidlPositionX && client != null) {
            client.updateVariable(id, "positionx", left, false);
            uidlPositionX = left;
        }
        if (top != uidlPositionY && client != null) {
            client.updateVariable(id, "positiony", top, false);
            uidlPositionY = top;
        }
    }

    public void setCaption(String c) {
        setCaption(c, null);
    }

    public void setCaption(String c, String icon) {
        String html = Util.escapeHTML(c);
        if (icon != null) {
            icon = client.translateVaadinUri(icon);
            html = "<img src=\"" + Util.escapeAttribute(icon)
                    + "\" class=\"v-icon\" alt=\"\" />" + html;
        }

        // Provide information to assistive device users that a sub window was
        // opened
        String prefix = "<span class='"
                + AriaHelper.ASSISTIVE_DEVICE_ONLY_STYLE + "'>"
                + assistivePrefix + "</span>";
        String postfix = "<span class='"
                + AriaHelper.ASSISTIVE_DEVICE_ONLY_STYLE + "'>"
                + assistivePostfix + "</span>";

        html = prefix + html + postfix;
        DOM.setInnerHTML(headerText, html);
    }

    /**
     * Setter for the text for assistive devices the window caption is prefixed
     * with.
     *
     * @param assistivePrefix
     *            the assistivePrefix to set
     */
    public void setAssistivePrefix(String assistivePrefix) {
        this.assistivePrefix = assistivePrefix;
    }

    /**
     * Getter for the text for assistive devices the window caption is prefixed
     * with.
     *
     * @return the assistivePrefix
     */
    public String getAssistivePrefix() {
        return assistivePrefix;
    }

    /**
     * Setter for the text for assistive devices the window caption is postfixed
     * with.
     *
     * @param assistivePostfix
     *            the assistivePostfix to set
     */
    public void setAssistivePostfix(String assistivePostfix) {
        this.assistivePostfix = assistivePostfix;
    }

    /**
     * Getter for the text for assistive devices the window caption is postfixed
     * with.
     *
     * @return the assistivePostfix
     */
    public String getAssistivePostfix() {
        return assistivePostfix;
    }

    @Override
    protected com.google.gwt.user.client.Element getContainerElement() {
        // in GWT 1.5 this method is used in PopupPanel constructor
        if (contents == null) {
            return super.getContainerElement();
        }
        return DOM.asOld(contents);
    }

    private Event headerDragPending;

    @Override
    public void onBrowserEvent(final Event event) {
        boolean bubble = true;

        final int type = event.getTypeInt();

        final Element target = DOM.eventGetTarget(event);

        if (resizing || resizeBox == target) {
            onResizeEvent(event);
            bubble = false;
        } else if (isClosable() && target == closeBox) {
            if (type == Event.ONCLICK) {
                onCloseClick();
            }
            bubble = false;
        } else if (target == maximizeRestoreBox) {
            // handled in connector
            if (type != Event.ONCLICK) {
                bubble = false;
            }
        } else if (header.isOrHasChild(target) && !dragging) {
            // dblclick handled in connector
            if (type != Event.ONDBLCLICK && draggable) {
                if (type == Event.ONMOUSEDOWN) {
                    /**
                     * Prevents accidental selection of window caption or
                     * content. (#12726)
                     */
                    event.preventDefault();

                    headerDragPending = event;
                } else if (type == Event.ONMOUSEMOVE
                        && headerDragPending != null) {
                    // ie won't work unless this is set here
                    dragging = true;
                    onDragEvent(headerDragPending);
                    onDragEvent(event);
                    headerDragPending = null;
                } else {
                    headerDragPending = null;
                }
                bubble = false;
            }
            if (type == Event.ONCLICK) {
                activateOnClick();
            }
        } else if (dragging || !contents.isOrHasChild(target)) {
            onDragEvent(event);
            bubble = false;
        } else if (type == Event.ONCLICK) {
            activateOnClick();
        }

        /*
         * If clicking on other than the content, move focus to the window.
         * After that this windows e.g. gets all keyboard shortcuts.
         */
        if (type == Event.ONMOUSEDOWN
                && !contentPanel.getElement().isOrHasChild(target)
                && target != closeBox && target != maximizeRestoreBox) {
            contentPanel.focus();
        }

        if (!bubble) {
            event.stopPropagation();
        } else {
            // Super.onBrowserEvent takes care of Handlers added by the
            // ClickEventHandler
            super.onBrowserEvent(event);
        }
    }

    private void activateOnClick() {
        // clicked inside window or inside header, ensure to be on top
        if (!isActive()) {
            bringToFront();
        }
    }

    private void onCloseClick() {
        // Send the close event to the server
        client.updateVariable(id, "close", true, true);
    }

    private void onResizeEvent(Event event) {
        if (resizable && Util.isTouchEventOrLeftMouseButton(event)) {
            switch (event.getTypeInt()) {
            case Event.ONMOUSEDOWN:
            case Event.ONTOUCHSTART:
                if (!isActive()) {
                    bringToFront();
                }
                showResizingCurtain();
                if (BrowserInfo.get().isIE()) {
                    resizeBox.getStyle().setVisibility(Visibility.HIDDEN);
                }
                resizing = true;
                startX = Util.getTouchOrMouseClientX(event);
                startY = Util.getTouchOrMouseClientY(event);
                origW = getElement().getOffsetWidth();
                origH = getElement().getOffsetHeight();
                DOM.setCapture(getElement());
                event.preventDefault();
                break;
            case Event.ONMOUSEUP:
            case Event.ONTOUCHEND:
                setSize(event, true);
            case Event.ONTOUCHCANCEL:
                DOM.releaseCapture(getElement());
            case Event.ONLOSECAPTURE:
                hideResizingCurtain();
                if (BrowserInfo.get().isIE()) {
                    resizeBox.getStyle().clearVisibility();
                }
                resizing = false;
                break;
            case Event.ONMOUSEMOVE:
            case Event.ONTOUCHMOVE:
                if (resizing) {
                    centered = false;
                    setSize(event, false);
                    event.preventDefault();
                }
                break;
            default:
                event.preventDefault();
                break;
            }
        }
    }

    /**
     * TODO check if we need to support this with touch based devices.
     *
     * Checks if the cursor was inside the browser content area when the event
     * happened.
     *
     * @param event
     *            The event to be checked
     * @return true, if the cursor is inside the browser content area
     *
     *         false, otherwise
     */
    private boolean cursorInsideBrowserContentArea(Event event) {
        if (event.getClientX() < 0 || event.getClientY() < 0) {
            // Outside to the left or above
            return false;
        }

        if (event.getClientX() > Window.getClientWidth()
                || event.getClientY() > Window.getClientHeight()) {
            // Outside to the right or below
            return false;
        }

        return true;
    }

    private void setSize(Event event, boolean updateVariables) {
        if (!cursorInsideBrowserContentArea(event)) {
            // Only drag while cursor is inside the browser client area
            return;
        }

        int w = Util.getTouchOrMouseClientX(event) - startX + origW;
        int h = Util.getTouchOrMouseClientY(event) - startY + origH;

        w = Math.max(w, getMinWidth());
        h = Math.max(h, getMinHeight());

        setWidth(w + "px");
        setHeight(h + "px");

        if (updateVariables) {
            // sending width back always as pixels, no need for unit
            client.updateVariable(id, "width", w, false);
            client.updateVariable(id, "height", h, immediate);
        }

        if (updateVariables || !resizeLazy) {
            // Resize has finished or is not lazy
            updateContentsSize();
        } else {
            // Lazy resize - wait for a while before re-rendering contents
            delayedContentsSizeUpdater.trigger();
        }
    }

    private int getMinHeight() {
        return getPixelValue(getElement().getStyle().getProperty("minHeight"));
    }

    private int getMinWidth() {
        return getPixelValue(getElement().getStyle().getProperty("minWidth"));
    }

    private static int getPixelValue(String size) {
        if (size == null || !size.endsWith("px")) {
            return -1;
        } else {
            return Integer.parseInt(size.substring(0, size.length() - 2));
        }
    }

    public void updateContentsSize() {
        LayoutManager layoutManager = getLayoutManager();
        layoutManager.setNeedsMeasure(ConnectorMap.get(client).getConnector(
                this));
        layoutManager.layoutNow();
    }

    @Override
    public void setWidth(String width) {
        // Override PopupPanel which sets the width to the contents
        getElement().getStyle().setProperty("width", width);
        // Update v-has-width in case undefined window is resized
        setStyleName("v-has-width", width != null && width.length() > 0);
    }

    @Override
    public void setHeight(String height) {
        // Override PopupPanel which sets the height to the contents
        getElement().getStyle().setProperty("height", height);
        // Update v-has-height in case undefined window is resized
        setStyleName("v-has-height", height != null && height.length() > 0);
    }

    private void onDragEvent(Event event) {
        if (!Util.isTouchEventOrLeftMouseButton(event)) {
            return;
        }

        switch (DOM.eventGetType(event)) {
        case Event.ONTOUCHSTART:
            if (event.getTouches().length() > 1) {
                return;
            }
        case Event.ONMOUSEDOWN:
            if (!isActive()) {
                bringToFront();
            }
            beginMovingWindow(event);
            break;
        case Event.ONMOUSEUP:
        case Event.ONTOUCHEND:
        case Event.ONTOUCHCANCEL:
        case Event.ONLOSECAPTURE:
            stopMovingWindow();
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            moveWindow(event);
            break;
        default:
            break;
        }
    }

    private void moveWindow(Event event) {
        if (dragging) {
            centered = false;
            if (cursorInsideBrowserContentArea(event)) {
                // Only drag while cursor is inside the browser client area
                final int x = Util.getTouchOrMouseClientX(event) - startX
                        + origX;
                final int y = Util.getTouchOrMouseClientY(event) - startY
                        + origY;
                setPopupPosition(x, y);
            }
            DOM.eventPreventDefault(event);
        }
    }

    private void beginMovingWindow(Event event) {
        if (draggable) {
            showDraggingCurtain();
            dragging = true;
            startX = Util.getTouchOrMouseClientX(event);
            startY = Util.getTouchOrMouseClientY(event);
            origX = DOM.getAbsoluteLeft(getElement());
            origY = DOM.getAbsoluteTop(getElement());
            DOM.setCapture(getElement());
            DOM.eventPreventDefault(event);
        }
    }

    private void stopMovingWindow() {
        dragging = false;
        hideDraggingCurtain();
        DOM.releaseCapture(getElement());

        // fire move event
        fireEvent(new WindowMoveEvent(uidlPositionX, uidlPositionY));
    }

    @Override
    public boolean onEventPreview(Event event) {
        if (dragging) {
            onDragEvent(event);
            return false;
        } else if (resizing) {
            onResizeEvent(event);
            return false;
        }

        // TODO This is probably completely unnecessary as the modality curtain
        // prevents events from reaching other windows and any security check
        // must be done on the server side and not here.
        // The code here is also run many times as each VWindow has an event
        // preview but we cannot check only the current VWindow here (e.g.
        // if(isTopMost) {...}) because PopupPanel will cause all events that
        // are not cancelled here and target this window to be consume():d
        // meaning the event won't be sent to the rest of the preview handlers.

        if (getTopmostWindow() != null && getTopmostWindow().vaadinModality) {
            // Topmost window is modal. Cancel the event if it targets something
            // outside that window (except debug console...)
            if (DOM.getCaptureElement() != null) {
                // Allow events when capture is set
                return true;
            }

            final Element target = event.getEventTarget().cast();
            if (!DOM.isOrHasChild(getTopmostWindow().getElement(), target)) {
                // not within the modal window, but let's see if it's in the
                // debug window
                Widget w = Util.findWidget(target, null);
                while (w != null) {
                    if (w instanceof VDebugWindow) {
                        return true; // allow debug-window clicks
                    } else if (ConnectorMap.get(client).isConnector(w)) {
                        return false;
                    }
                    w = w.getParent();
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void addStyleDependentName(String styleSuffix) {
        // VWindow's getStyleElement() does not return the same element as
        // getElement(), so we need to override this.
        setStyleName(getElement(), getStylePrimaryName() + "-" + styleSuffix,
                true);
    }

    @Override
    public ShortcutActionHandler getShortcutActionHandler() {
        return shortcutHandler;
    }

    @Override
    public void onScroll(ScrollEvent event) {
        client.updateVariable(id, "scrollTop",
                contentPanel.getScrollPosition(), false);
        client.updateVariable(id, "scrollLeft",
                contentPanel.getHorizontalScrollPosition(), false);

    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (hasFocus && event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
            event.preventDefault();
        }

        if (shortcutHandler != null) {
            shortcutHandler
                    .handleKeyboardEvent(Event.as(event.getNativeEvent()));
            return;
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (isClosable() && event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
            onCloseClick();
        }
    }

    @Override
    public void onBlur(BlurEvent event) {
        hasFocus = false;

        if (client.hasEventListeners(this, EventId.BLUR)) {
            client.updateVariable(id, EventId.BLUR, "", true);
        }
    }

    @Override
    public void onFocus(FocusEvent event) {
        hasFocus = true;

        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(id, EventId.FOCUS, "", true);
        }
    }

    @Override
    public void focus() {
        contentPanel.focus();
    }

    private int getDecorationHeight() {
        LayoutManager lm = getLayoutManager();
        int headerHeight = lm.getOuterHeight(header);
        int footerHeight = lm.getOuterHeight(footer);
        return headerHeight + footerHeight;
    }

    private LayoutManager getLayoutManager() {
        return LayoutManager.get(client);
    }

    private int getDecorationWidth() {
        LayoutManager layoutManager = getLayoutManager();
        return layoutManager.getOuterWidth(getElement())
                - contentPanel.getElement().getOffsetWidth();
    }

    /**
     * Allows to specify which connectors contain the description for the
     * window. Text contained in the widgets of the connectors will be read by
     * assistive devices when it is opened.
     * <p>
     * When the provided array is empty, an existing description is removed.
     *
     * @param connectors
     *            with the connectors of the widgets to use as description
     */
    public void setAssistiveDescription(Connector[] connectors) {
        if (connectors != null) {
            assistiveConnectors = connectors;

            if (connectors.length == 0) {
                Roles.getDialogRole().removeAriaDescribedbyProperty(
                        getElement());
            } else {
                Id[] ids = new Id[connectors.length];
                for (int index = 0; index < connectors.length; index++) {
                    if (connectors[index] == null) {
                        throw new IllegalArgumentException(
                                "All values in parameter description need to be non-null");
                    }

                    Element element = ((ComponentConnector) connectors[index])
                            .getWidget().getElement();
                    AriaHelper.ensureHasId(element);
                    ids[index] = Id.of(element);
                }

                Roles.getDialogRole().setAriaDescribedbyProperty(getElement(),
                        ids);
            }
        } else {
            throw new IllegalArgumentException(
                    "Parameter description must be non-null");
        }
    }

    /**
     * Gets the connectors that are used as assistive description. Text
     * contained in these connectors will be read by assistive devices when the
     * window is opened.
     *
     * @return list of previously set connectors
     */
    public List<Connector> getAssistiveDescription() {
        return Collections.unmodifiableList(Arrays.asList(assistiveConnectors));
    }

    /**
     * Sets the WAI-ARIA role the window.
     *
     * This role defines how an assistive device handles a window. Available
     * roles are alertdialog and dialog (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a>).
     *
     * The default role is dialog.
     *
     * @param role
     *            WAI-ARIA role to set for the window
     */
    public void setWaiAriaRole(WindowRole role) {
        if (role == WindowRole.ALERTDIALOG) {
            Roles.getAlertdialogRole().set(getElement());
        } else {
            Roles.getDialogRole().set(getElement());
        }
    }

    /**
     * Registers the handlers that prevent to leave the window using the
     * Tab-key.
     * <p>
     * The value of the parameter doTabStop is stored and used for non-modal
     * windows. For modal windows, the handlers are always registered, while
     * preserving the stored value.
     *
     * @param doTabStop
     *            true to prevent leaving the window, false to allow leaving the
     *            window for non modal windows
     */
    public void setTabStopEnabled(boolean doTabStop) {
        this.doTabStop = doTabStop;

        if (doTabStop || vaadinModality) {
            addTabBlockHandlers();
        } else {
            removeTabBlockHandlers();
        }
    }

    /**
     * Adds a Handler for when user moves the window.
     *
     * @since 7.1.9
     *
     * @return {@link HandlerRegistration} used to remove the handler
     */
    public HandlerRegistration addMoveHandler(WindowMoveHandler handler) {
        return addHandler(handler, WindowMoveEvent.getType());
    }

}
