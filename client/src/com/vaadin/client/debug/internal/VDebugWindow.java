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
package com.vaadin.client.debug.internal;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;
import com.vaadin.client.ui.VOverlay;

/**
 * Debug window implementation.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public final class VDebugWindow extends VOverlay {

    // CSS classes
    static final String STYLENAME = "v-debugwindow";
    static final String STYLENAME_BUTTON = STYLENAME + "-button";
    static final String STYLENAME_ACTIVE = "active";

    protected static final String STYLENAME_HEAD = STYLENAME + "-head";
    protected static final String STYLENAME_TABS = STYLENAME + "-tabs";
    protected static final String STYLENAME_TAB = STYLENAME + "-tab";
    protected static final String STYLENAME_CONTROLS = STYLENAME + "-controls";
    protected static final String STYLENAME_SECTION_HEAD = STYLENAME
            + "-section-head";
    protected static final String STYLENAME_CONTENT = STYLENAME + "-content";
    protected static final String STYLENAME_SELECTED = "selected";

    // drag this far before actually moving window
    protected static final int MOVE_TRESHOLD = 5;

    // window minimum height, minimum width comes from tab+controls
    protected static final int MIN_HEIGHT = 40;

    // size of area to grab for resize; bottom corners size in CSS
    protected static final int HANDLE_SIZE = 7;

    // identifiers for localStorage
    private static final String STORAGE_PREFIX = "v-debug-";
    private static final String STORAGE_FULL_X = "x";
    private static final String STORAGE_FULL_Y = "y";
    private static final String STORAGE_FULL_W = "w";
    private static final String STORAGE_FULL_H = "h";
    private static final String STORAGE_MIN_X = "mx";
    private static final String STORAGE_MIN_Y = "my";
    private static final String STORAGE_ACTIVE_SECTION = "t";
    private static final String STORAGE_IS_MINIMIZED = "m";
    private static final String STORAGE_FONT_SIZE = "s";

    // state, these are persisted
    protected Section activeSection;
    protected boolean minimized = false;
    protected int fullX = -10;
    protected int fullY = -10;
    protected int fullW = 300;
    protected int fullH = 150;
    protected int minX = -10;
    protected int minY = 10;
    protected int fontSize = 1; // 0-2

    // Timers since application start, and last timer reset
    private static final Duration start = new Duration();
    private static Duration lastReset = start;

    // outer panel
    protected FlowPanel window = new FlowPanel();
    // top (tabs + controls)
    protected FlowPanel head = new FlowPanel();
    protected FlowPanel tabs = new FlowPanel();
    protected FlowPanel controls = new FlowPanel();
    protected Button minimize = new DebugButton(Icon.MINIMIZE, "Minimize");
    protected Button menu = new DebugButton(Icon.MENU, "Menu");
    protected Button close = new DebugButton(Icon.CLOSE, "Close");

    // menu
    protected Menu menuPopup = new Menu();

    // section specific area
    protected FlowPanel sectionHead = new FlowPanel();
    // content wrapper
    protected SimplePanel content = new SimplePanel();

    // sections
    protected ArrayList<Section> sections = new ArrayList<Section>();

    // handles resize/move
    protected HandlerRegistration mouseDownHandler = null;
    protected HandlerRegistration mouseMoveHandler = null;

    // TODO this class should really be a singleton.
    static VDebugWindow instance;

    /**
     * This class should only be instantiated by the framework, use
     * {@link #get()} instead to get the singleton instance.
     * <p>
     * {@link VDebugWindow} provides windowing functionality and shows
     * {@link Section}s added with {@link #addSection(Section)} as tabs.
     * </p>
     * <p>
     * {@link Section#getTabButton()} is called to obtain a unique id for the
     * Sections; the id should actually be an identifier for an icon in the
     * icon-font in use.
     * </p>
     * <p>
     * {@link Section#getControls()} and {@link Section#getContent()} is called
     * when the Section is activated (displayed). Additionally
     * {@link Section#show()} is called to allow the Section to initialize
     * itself as needed when shown. Conversely {@link Section#hide()} is called
     * when the Section is deactivated.
     * </p>
     * <p>
     * Sections should take care to prefix CSS classnames used with
     * {@link VDebugWindow}.{@link #STYLENAME} to avoid that application theme
     * interferes with the debug window content.
     * </p>
     * <p>
     * Some of the window state, such as position and size, is persisted to
     * localStorage. Sections can use
     * {@link #writeState(Storage, String, Object)} and
     * {@link #readState(Storage, String, String)} (and relatives) to write and
     * read own persisted settings, keys will automatically be prefixed with
     * {@value #STORAGE_PREFIX}.
     * </p>
     */
    public VDebugWindow() {
        super(false, false);
        instance = this;
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        setStylePrimaryName(STYLENAME);

        setWidget(window);
        window.add(head);
        head.add(tabs);
        head.add(controls);
        head.add(sectionHead);
        window.add(content);

        addHandles();

        head.setStylePrimaryName(STYLENAME_HEAD);
        tabs.setStylePrimaryName(STYLENAME_TABS);
        controls.setStylePrimaryName(STYLENAME_CONTROLS);
        sectionHead.setStylePrimaryName(STYLENAME_SECTION_HEAD);
        content.setStylePrimaryName(STYLENAME_CONTENT);

        // add controls TODO move these
        controls.add(menu);
        menu.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                menuPopup.showRelativeTo(menu);
            }
        });

        controls.add(minimize);
        minimize.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleMinimized();
                writeStoredState();
            }
        });
        controls.add(close);
        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });

        Style s = content.getElement().getStyle();
        s.setOverflow(Overflow.AUTO);

        // move/resize
        final MouseHandler mouseHandler = new MouseHandler();
        mouseDownHandler = this.addDomHandler(mouseHandler,
                MouseDownEvent.getType());
        mouseMoveHandler = this.addDomHandler(mouseHandler,
                MouseMoveEvent.getType());

    }

    /**
     * Adds dummy handle elements to the bottom corners that might have
     * scrollbars that interfere with resizing on some platforms.
     * 
     * @since 7.1
     */
    private void addHandles() {
        Element el = DOM.createDiv();
        el.setClassName(VDebugWindow.STYLENAME + "-handle "
                + VDebugWindow.STYLENAME + "-handle-sw");
        content.getElement().appendChild(el);

        el = DOM.createDiv();
        el.setClassName(VDebugWindow.STYLENAME + "-handle "
                + VDebugWindow.STYLENAME + "-handle-se");
        content.getElement().appendChild(el);
    }

    /**
     * Gets the {@link #VDebugWindow()} singleton instance.
     * 
     * @return
     */
    public static VDebugWindow get() {
        if (instance == null) {
            instance = new VDebugWindow();
        }
        return instance;
    }

    /**
     * Closes the window and stops visual logging.
     */
    public void close() {
        // TODO disable even more
        if (mouseDownHandler != null) {
            mouseDownHandler.removeHandler();
            mouseMoveHandler.removeHandler();
            mouseDownHandler = null;
            mouseMoveHandler = null;
        }
        Highlight.hideAll();
        hide();

    }

    boolean isClosed() {
        return !isShowing();
    }

    /**
     * Reads the stored state from localStorage.
     */
    private void readStoredState() {
        Storage storage = Storage.getLocalStorageIfSupported();
        if (storage == null) {
            return;
        }

        fullX = readState(storage, STORAGE_FULL_X, -510);
        fullY = readState(storage, STORAGE_FULL_Y, -230);
        fullW = readState(storage, STORAGE_FULL_W, 500);
        fullH = readState(storage, STORAGE_FULL_H, 150);
        minX = readState(storage, STORAGE_MIN_X, -40);
        minY = readState(storage, STORAGE_MIN_Y, -70);
        setFontSize(readState(storage, STORAGE_FONT_SIZE, 1));

        activateSection(readState(storage, STORAGE_ACTIVE_SECTION, 0));

        setMinimized(readState(storage, STORAGE_IS_MINIMIZED, false));

        applyPositionAndSize();
    }

    /**
     * Writes the persistent state to localStorage.
     */
    private void writeStoredState() {
        if (isClosed()) {
            return;
        }
        Storage storage = Storage.getLocalStorageIfSupported();
        if (storage == null) {
            return;
        }

        writeState(storage, STORAGE_FULL_X, fullX);
        writeState(storage, STORAGE_FULL_Y, fullY);
        writeState(storage, STORAGE_FULL_W, fullW);
        writeState(storage, STORAGE_FULL_H, fullH);
        writeState(storage, STORAGE_MIN_X, minX);
        writeState(storage, STORAGE_MIN_Y, minY);
        writeState(storage, STORAGE_FONT_SIZE, fontSize);

        int activeIdx = getActiveSection();
        if (activeIdx >= 0) {
            writeState(storage, STORAGE_ACTIVE_SECTION, activeIdx);
        }

        writeState(storage, STORAGE_IS_MINIMIZED, minimized);
    }

    /**
     * Writes the given value to the given {@link Storage} using the given key
     * (automatically prefixed with {@value #STORAGE_PREFIX}).
     * 
     * @param storage
     * @param key
     * @param value
     */
    static void writeState(Storage storage, String key, Object value) {
        storage.setItem(STORAGE_PREFIX + key, String.valueOf(value));
    }

    /**
     * Returns the item with the given key (automatically prefixed with
     * {@value #STORAGE_PREFIX}) as an int from the given {@link Storage},
     * returning the given default value instead if not successful (e.g missing
     * item).
     * 
     * @param storage
     * @param key
     * @param def
     * @return stored or default value
     */
    static int readState(Storage storage, String key, int def) {
        try {
            return Integer.parseInt(storage.getItem(STORAGE_PREFIX + key));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns the item with the given key (automatically prefixed with
     * {@value #STORAGE_PREFIX}) as a boolean from the given {@link Storage},
     * returning the given default value instead if not successful (e.g missing
     * item).
     * 
     * @param storage
     * @param key
     * @param def
     * @return stored or default value
     */
    static boolean readState(Storage storage, String key, boolean def) {
        try {
            return Boolean.parseBoolean(storage.getItem(STORAGE_PREFIX + key));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns the item with the given key (automatically prefixed with
     * {@value #STORAGE_PREFIX}) as a String from the given {@link Storage},
     * returning the given default value instead if not successful (e.g missing
     * item).
     * 
     * @param storage
     * @param key
     * @param def
     * @return stored or default value
     */
    static String readState(Storage storage, String key, String def) {
        String val = storage.getItem(STORAGE_PREFIX + key);
        return val != null ? val : def;
    }

    /**
     * Resets (clears) the stored state from localStorage.
     */
    private void resetStoredState() {
        Storage storage = Storage.getLocalStorageIfSupported();
        if (storage == null) {
            return;
        }
        // note: length is live
        for (int i = 0; i < storage.getLength();) {
            String key = storage.key(i);
            if (key.startsWith(STORAGE_PREFIX)) {
                removeState(storage, key.substring(STORAGE_PREFIX.length()));
            } else {
                i++;
            }
        }
    }

    /**
     * Removes the item with the given key (automatically prefixed with
     * {@value #STORAGE_PREFIX}) from the given {@link Storage}.
     * 
     * @param storage
     * @param key
     */
    private void removeState(Storage storage, String key) {
        storage.removeItem(STORAGE_PREFIX + key);
    }

    /**
     * Applies the appropriate instance variables for width, height, x, y
     * depending on if the window is minimized or not.
     * 
     * If the value is negative, the window is positioned that amount of pixels
     * from the right/bottom instead of left/top.
     * 
     * Finally, the position is bounds-checked so that the window is not moved
     * off-screen (the adjusted values are not saved).
     */
    private void applyPositionAndSize() {
        int x = 0;
        int y = 0;
        if (minimized) {
            x = minX;
            if (minX < 0) {
                x = Window.getClientWidth() + minX;
            }
            y = minY;
            if (minY < 0) {
                y = Window.getClientHeight() + minY;
            }

        } else {
            x = fullX;
            if (fullX < 0) {
                x = Window.getClientWidth() + fullX;
            }
            y = fullY;
            if (y < 0) {
                y = Window.getClientHeight() + fullY;
            }
            content.setWidth(fullW + "px");
            content.setHeight(fullH + "px");
        }

        applyBounds(x, y);
    }

    private void applyBounds() {
        int x = getPopupLeft();
        int y = getPopupTop();
        applyBounds(x, y);
    }

    private void applyBounds(int x, int y) {
        // bounds check
        if (x < 0) {
            x = 0;
        }
        if (x > Window.getClientWidth() - getOffsetWidth()) {
            // not allowed off-screen to the right
            x = Window.getClientWidth() - getOffsetWidth();
        }
        if (y > Window.getClientHeight() - getOffsetHeight()) {
            y = Window.getClientHeight() - getOffsetHeight();
        }
        if (y < 0) {
            y = 0;
        }

        setPopupPosition(x, y);

    }

    /**
     * Reads position and size from the DOM to local variables (which in turn
     * can be stored to localStorage)
     */
    private void readPositionAndSize() {
        int x = getPopupLeft();
        int fromRight = Window.getClientWidth() - x - getOffsetWidth();
        if (fromRight < x) {
            x -= Window.getClientWidth();
        }

        int y = getPopupTop();
        int fromBottom = Window.getClientHeight() - y - getOffsetHeight();
        if (fromBottom < y) {
            y -= Window.getClientHeight();
        }

        if (minimized) {
            minY = y;
            minX = x;
        } else {
            fullY = y;
            fullX = x;
            fullW = content.getOffsetWidth();
            fullH = content.getOffsetHeight();
        }

    }

    /**
     * Adds the given {@link Section} as a tab in the {@link VDebugWindow} UI.
     * {@link Section#getTabButton()} is called to obtain a button which is used
     * tab.
     * 
     * @param section
     */
    public void addSection(final Section section) {
        Button b = section.getTabButton();
        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activateSection(section);
                writeStoredState();
            }
        });
        b.setStylePrimaryName(STYLENAME_TAB);
        tabs.add(b);
        sections.add(section);

        if (activeSection == null) {
            activateSection(section);
        }
    }

    /**
     * Activates the given {@link Section}
     * 
     * @param section
     */
    void activateSection(Section section) {
        if (section != null && section != activeSection) {
            Highlight.hideAll();
            // remove old stuff
            if (activeSection != null) {
                activeSection.hide();
                content.remove(activeSection.getContent());
                sectionHead.remove(activeSection.getControls());
            }
            // update tab styles
            for (int i = 0; i < tabs.getWidgetCount(); i++) {
                Widget tab = tabs.getWidget(i);
                tab.setStyleDependentName(STYLENAME_SELECTED,
                        tab == section.getTabButton());
            }
            // add new stuff
            content.add(section.getContent());
            sectionHead.add(section.getControls());
            activeSection = section;
            activeSection.show();
        }
    }

    void activateSection(int n) {
        if (n < sections.size()) {
            activateSection(sections.get(n));
        }
    }

    int getActiveSection() {
        return sections.indexOf(activeSection);
    }

    /**
     * Toggles the window between minimized and full states.
     */
    private void toggleMinimized() {
        setMinimized(!minimized);
        writeStoredState();
    }

    /**
     * Sets whether or not the window is minimized.
     * 
     * @param minimized
     */
    private void setMinimized(boolean minimized) {
        this.minimized = minimized;

        tabs.setVisible(!minimized);
        content.setVisible(!minimized);
        sectionHead.setVisible(!minimized);
        menu.setVisible(!minimized);

        applyPositionAndSize();
    }

    /**
     * Sets the font size in use.
     * 
     * @param size
     */
    private void setFontSize(int size) {
        removeStyleDependentName("size" + fontSize);
        fontSize = size;
        addStyleDependentName("size" + size);
    }

    /**
     * Gets the font size currently in use.
     * 
     * @return
     */
    private int getFontSize() {
        return fontSize;
    }

    /**
     * Gets the milliseconds since application start.
     * 
     * @return
     */
    static int getMillisSinceStart() {
        return start.elapsedMillis();
    }

    /**
     * Gets the milliseconds since last {@link #resetTimer()} call.
     * 
     * @return
     */
    static int getMillisSinceReset() {
        return lastReset.elapsedMillis();
    }

    /**
     * Resets the timer.
     * 
     * @return Milliseconds elapsed since the timer was last reset.
     */
    static int resetTimer() {
        int sinceLast = lastReset.elapsedMillis();
        lastReset = new Duration();
        return sinceLast;
    }

    /**
     * Gets a nicely formatted string with timing information suitable for
     * display in tooltips.
     * 
     * @param sinceStart
     * @param sinceReset
     * @return
     */
    static String getTimingTooltip(int sinceStart, int sinceReset) {
        String title = formatDuration(sinceStart) + " since start";
        title += ", &#10; " + formatDuration(sinceReset) + " since timer reset";
        title += " &#10; @ "
                + DateTimeFormat.getFormat("HH:mm:ss.SSS").format(new Date());
        return title;
    }

    /**
     * Formats the given milliseconds as hours, minutes, seconds and
     * milliseconds.
     * 
     * @param ms
     * @return
     */
    static String formatDuration(int ms) {
        NumberFormat fmt = NumberFormat.getFormat("00");
        String seconds = fmt.format((ms / 1000) % 60);
        String minutes = fmt.format((ms / (1000 * 60)) % 60);
        String hours = fmt.format((ms / (1000 * 60 * 60)) % 24);

        String millis = NumberFormat.getFormat("000").format(ms % 1000);

        return hours + "h " + minutes + "m " + seconds + "s " + millis + "ms";
    }

    /**
     * Called when the window is initialized.
     */
    public void init() {

        show();
        readStoredState();

        Window.addResizeHandler(new com.google.gwt.event.logical.shared.ResizeHandler() {

            Timer t = new Timer() {
                @Override
                public void run() {
                    applyPositionAndSize();
                }
            };

            @Override
            public void onResize(ResizeEvent event) {
                t.cancel();
                // TODO less
                t.schedule(1000);
            }
        });
    }

    /**
     * Called when the result from analyzeLayouts is received.
     * 
     * @param ac
     * @param meta
     */
    public void meta(ApplicationConnection ac, ValueMap meta) {
        if (isClosed()) {
            return;
        }
        for (Section s : sections) {
            s.meta(ac, meta);
        }
    }

    /**
     * Called when a response is received
     * 
     * @param ac
     * @param uidl
     */
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        if (isClosed()) {
            return;
        }
        for (Section s : sections) {
            s.uidl(ac, uidl);
        }
    }

    /**
     * Gets the container element for this window. The debug window is always
     * global to the document and not related to any
     * {@link ApplicationConnection} in particular.
     * 
     * @return The global overlay container element.
     */
    @Override
    public com.google.gwt.user.client.Element getOverlayContainer() {
        return RootPanel.get().getElement();
    }

    /*
     * Inner classes
     */

    /**
     * Popup menu for {@link VDebugWindow}.
     * 
     * @since 7.1
     * @author Vaadin Ltd
     */
    protected class Menu extends VOverlay {
        FlowPanel content = new FlowPanel();

        DebugButton[] sizes = new DebugButton[] {
                new DebugButton(null, "Small", "A"),
                new DebugButton(null, "Medium", "A"),
                new DebugButton(null, "Large", "A") };

        DebugButton[] modes = new DebugButton[] {
                new DebugButton(Icon.DEVMODE_OFF,
                        "Debug only (causes page reload)"),
                new DebugButton(Icon.DEVMODE_ON, "DevMode (causes page reload)"),
                new DebugButton(Icon.DEVMODE_SUPER,
                        "SuperDevMode (causes page reload)") };

        Menu() {
            super(true, true);
            setWidget(content);

            setStylePrimaryName(STYLENAME + "-menu");
            content.setStylePrimaryName(STYLENAME + "-menu-content");

            FlowPanel size = new FlowPanel();
            content.add(size);

            final ClickHandler sizeHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    for (int i = 0; i < sizes.length; i++) {
                        Button b = sizes[i];
                        if (b == event.getSource()) {
                            setSize(i);
                        }
                    }
                    hide();
                }
            };
            for (int i = 0; i < sizes.length; i++) {
                Button b = sizes[i];
                b.setStyleDependentName("size" + i, true);
                b.addClickHandler(sizeHandler);
                size.add(b);
            }

            FlowPanel mode = new FlowPanel();
            content.add(mode);
            final ClickHandler modeHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    for (int i = 0; i < modes.length; i++) {
                        Button b = modes[i];
                        if (b == event.getSource()) {
                            setDevMode(i);
                        }
                    }
                    hide();
                }
            };
            modes[getDevMode()].setActive(true);
            for (int i = 0; i < modes.length; i++) {
                Button b = modes[i];
                b.addClickHandler(modeHandler);
                mode.add(b);
            }

            Button reset = new DebugButton(Icon.RESET, "Restore defaults.",
                    " Reset");
            reset.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    resetStoredState();
                    readStoredState();
                    hide();
                }
            });
            content.add(reset);
        }

        private void setSize(int size) {
            for (int i = 0; i < sizes.length; i++) {
                Button b = sizes[i];
                b.setStyleDependentName(STYLENAME_ACTIVE, i == size);
            }
            setFontSize(size);
            writeStoredState();
        }

        @Override
        public void show() {
            super.show();
            setSize(getFontSize());
        }

        private int getDevMode() {
            if (Location.getParameter("superdevmode") != null) {
                return 2;
            } else if (Location.getParameter("gwt.codesvr") != null) {
                return 1;
            } else {
                return 0;
            }
        }

        private void setDevMode(int mode) {
            UrlBuilder u = Location.createUrlBuilder();
            switch (mode) {
            case 2:
                u.setParameter("superdevmode", "");
                u.removeParameter("gwt.codesvr");
                break;
            case 1:
                u.setParameter("gwt.codesvr", "localhost:9997");
                u.removeParameter("superdevmode");
                break;
            default:
                u.removeParameter("gwt.codesvr");
                u.removeParameter("superdevmode");
            }
            Location.assign(u.buildString());
        }

    }

    /**
     * Handler for resizing and moving window, also updates cursor on mousemove.
     * 
     * @since 7.1
     * @author Vaadin Ltd
     */
    protected class MouseHandler implements MouseMoveHandler, MouseDownHandler,
            NativePreviewHandler {

        boolean resizeLeft;
        boolean resizeRight;
        boolean resizeUp;
        boolean resizeDown;
        boolean move;
        boolean sizing;

        // dragging stopped, remove handler on next event
        boolean stop;

        HandlerRegistration dragHandler;

        int startX;
        int startY;
        int startW;
        int startH;
        int startTop;
        int startLeft;

        @Override
        public void onMouseMove(MouseMoveEvent event) {
            if (null == dragHandler) {
                updateResizeFlags(event);
                updateCursor();
            }
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (event.getNativeButton() != NativeEvent.BUTTON_LEFT
                    || dragHandler != null) {
                return;
            }
            updateResizeFlags(event);
            if (sizing || move) {
                // some os/browsers don't pass events trough scrollbars; hide
                // while dragging (esp. important for resize from right/bottom)
                content.getElement().getStyle().setOverflow(Overflow.HIDDEN);

                startX = event.getClientX();
                startY = event.getClientY();

                startW = content.getOffsetWidth();
                startH = content.getOffsetHeight();

                startTop = getPopupTop();
                startLeft = getPopupLeft();

                dragHandler = Event.addNativePreviewHandler(this);

                event.preventDefault();

                stop = false;
            }

        }

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONMOUSEMOVE && !stop
                    && hasMoved(event.getNativeEvent())) {

                int dx = event.getNativeEvent().getClientX() - startX;
                int dy = event.getNativeEvent().getClientY() - startY;

                if (sizing) {
                    int minWidth = tabs.getOffsetWidth()
                            + controls.getOffsetWidth();

                    if (resizeLeft) {
                        int w = startW - dx;
                        if (w < minWidth) {
                            w = minWidth;
                            dx = startW - minWidth;
                        }
                        content.setWidth(w + "px");
                        setPopupPosition(startLeft + dx, getPopupTop());

                    } else if (resizeRight) {
                        int w = startW + dx;
                        if (w < minWidth) {
                            w = minWidth;
                        }
                        content.setWidth(w + "px");
                    }
                    if (resizeUp) {
                        int h = startH - dy;
                        if (h < MIN_HEIGHT) {
                            h = MIN_HEIGHT;
                            dy = startH - MIN_HEIGHT;
                        }
                        content.setHeight(h + "px");
                        setPopupPosition(getPopupLeft(), startTop + dy);

                    } else if (resizeDown) {
                        int h = startH + dy;
                        if (h < MIN_HEIGHT) {
                            h = MIN_HEIGHT;
                        }
                        content.setHeight(h + "px");

                    }

                } else if (move) {
                    setPopupPosition(startLeft + dx, startTop + dy);
                }
                event.cancel();

            } else if (event.getTypeInt() == Event.ONMOUSEUP) {
                stop = true;
                if (hasMoved(event.getNativeEvent())) {
                    event.cancel();
                }

            } else if (event.getTypeInt() == Event.ONCLICK) {
                stop = true;
                if (hasMoved(event.getNativeEvent())) {
                    event.cancel();
                }

            } else if (stop) {
                stop = false;
                dragHandler.removeHandler();
                dragHandler = null;
                sizing = false;
                move = false;

                // restore scrollbars
                content.getElement().getStyle().setOverflow(Overflow.AUTO);

                updateCursor();

                applyBounds();
                readPositionAndSize();
                writeStoredState();

                event.cancel();
            }

        }

        private boolean hasMoved(NativeEvent event) {
            return Math.abs(startX - event.getClientX()) > MOVE_TRESHOLD
                    || Math.abs(startY - event.getClientY()) > MOVE_TRESHOLD;
        }

        private void updateCursor() {
            Element c = getElement();
            if (resizeLeft) {
                if (resizeUp) {
                    c.getStyle().setCursor(Cursor.NW_RESIZE);
                } else if (resizeDown) {
                    c.getStyle().setCursor(Cursor.SW_RESIZE);
                } else {
                    c.getStyle().setCursor(Cursor.W_RESIZE);
                }
            } else if (resizeRight) {
                if (resizeUp) {
                    c.getStyle().setCursor(Cursor.NE_RESIZE);
                } else if (resizeDown) {
                    c.getStyle().setCursor(Cursor.SE_RESIZE);
                } else {
                    c.getStyle().setCursor(Cursor.E_RESIZE);
                }
            } else if (resizeUp) {
                c.getStyle().setCursor(Cursor.N_RESIZE);
            } else if (resizeDown) {
                c.getStyle().setCursor(Cursor.S_RESIZE);
            } else if (move) {
                c.getStyle().setCursor(Cursor.MOVE);
            } else {
                c.getStyle().setCursor(Cursor.AUTO);
            }
        }

        protected void updateResizeFlags(MouseEvent event) {
            if (event.isShiftKeyDown()) {
                // resize from lower right
                resizeUp = false;
                resizeLeft = false;
                resizeRight = true;
                resizeDown = true;
                move = false;
                sizing = true;
                return;

            } else if (event.isAltKeyDown()) {
                // move it
                move = true;
                resizeUp = false;
                resizeLeft = false;
                resizeRight = false;
                resizeDown = false;
                sizing = false;
                return;
            }

            Element c = getElement();
            int w = c.getOffsetWidth();
            int h = c.getOffsetHeight();
            int x = event.getRelativeX(c);
            int y = event.getRelativeY(c);

            resizeLeft = x < HANDLE_SIZE && y > tabs.getOffsetHeight();
            resizeRight = (x > (w - HANDLE_SIZE) && y > tabs.getOffsetHeight())
                    || (x > (w - 2 * HANDLE_SIZE) && y > (h - 2 * HANDLE_SIZE));
            resizeUp = y > tabs.getOffsetHeight()
                    && y < tabs.getOffsetHeight() + HANDLE_SIZE;
            resizeDown = y > (h - HANDLE_SIZE)
                    || (x > (w - 2 * HANDLE_SIZE) && y > (h - 2 * HANDLE_SIZE));

            move = !resizeDown && !resizeLeft && !resizeRight && !resizeUp
                    && y < head.getOffsetHeight();

            sizing = resizeLeft || resizeRight || resizeUp || resizeDown;

        }

    }

}
