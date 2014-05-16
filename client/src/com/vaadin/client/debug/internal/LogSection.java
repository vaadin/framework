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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.HtmlLogFormatter;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;

/**
 * Displays the log messages.
 * <p>
 * Scroll lock state is persisted.
 * </p>
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class LogSection implements Section {

    private final class LogSectionHandler extends Handler {
        private LogSectionHandler() {
            setLevel(Level.ALL);
            setFormatter(new HtmlLogFormatter(true) {
                @Override
                protected String getHtmlPrefix(LogRecord event) {
                    return "";
                }

                @Override
                protected String getHtmlSuffix(LogRecord event) {
                    return "";
                }

                @Override
                protected String getRecordInfo(LogRecord event, String newline) {
                    return "";
                }
            });
        }

        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }

            // If no message is provided, record.getMessage will be null and so
            // the formatter.format will fail with NullPointerException (#12588)
            if (record.getMessage() == null) {
                record.setMessage("");
            }

            Formatter formatter = getFormatter();
            String msg = formatter.format(record);

            addRow(record.getLevel(), msg);
        }

        @Override
        public void close() {
            // Nothing to do
        }

        @Override
        public void flush() {
            // Nothing todo
        }
    }

    // If scroll is not locked, content will be scrolled after delay
    private static final int SCROLL_DELAY = 100;
    private Timer scrollTimer = null;

    // TODO should be persisted
    // log content limit
    private int limit = 500;

    private final DebugButton tabButton = new DebugButton(Icon.LOG,
            "Debug message log");

    private final HTML content = new HTML();
    private final Element contentElement;
    private final FlowPanel controls = new FlowPanel();

    private final Button clear = new DebugButton(Icon.CLEAR, "Clear log");
    private final Button reset = new DebugButton(Icon.RESET_TIMER,
            "Reset timer");
    private final Button scroll = new DebugButton(Icon.SCROLL_LOCK,
            "Scroll lock");

    public LogSection() {
        contentElement = content.getElement();
        content.setStylePrimaryName(VDebugWindow.STYLENAME + "-log");

        // clear log button
        controls.add(clear);
        clear.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        clear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clear();
            }
        });

        // reset timer button
        controls.add(reset);
        reset.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        reset.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                resetTimer();
            }
        });

        // scroll lock toggle
        controls.add(scroll);
        scroll.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        scroll.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleScrollLock();
            }
        });

        // select message if row is clicked
        content.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Element el = Element
                        .as(event.getNativeEvent().getEventTarget());
                while (!el.getClassName().contains(
                        VDebugWindow.STYLENAME + "-message")) {
                    if (el == contentElement) {
                        // clicked something else
                        return;
                    }
                    el = el.getParentElement();
                }
                selectText(el);
            }
        });

        // Add handler to the root logger
        Logger.getLogger("").addHandler(new LogSectionHandler());
    }

    /**
     * Toggles scroll lock, writes state to persistent storage.
     */
    void toggleScrollLock() {
        setScrollLock(scrollTimer != null);

        Storage storage = Storage.getLocalStorageIfSupported();
        if (storage == null) {
            return;
        }
        VDebugWindow.writeState(storage, "log-scrollLock", scrollTimer == null);
    }

    /**
     * Activates or deactivates scroll lock
     * 
     * @param locked
     */
    void setScrollLock(boolean locked) {
        if (locked && scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;

        } else if (!locked && scrollTimer == null) {
            scrollTimer = new Timer() {
                @Override
                public void run() {
                    Element el = (Element) contentElement.getLastChild();
                    if (el != null) {
                        el = el.getFirstChildElement();
                        if (el != null) {
                            el.scrollIntoView();
                        }
                    }
                }
            };

        }
        scroll.setStyleDependentName(VDebugWindow.STYLENAME_ACTIVE, locked);

    }

    private native void selectText(Element el)
    /*-{
        if ($doc.selection && $doc.selection.createRange) {
            var r = $doc.selection.createRange();
            r.moveToElementText(el);
            r.select();
        } else if ($doc.createRange && $wnd.getSelection) {
            var r = $doc.createRange();
            r.selectNode(el);
            var selection = $wnd.getSelection();
            selection.removeAllRanges();
            selection.addRange(r);
        }
    }-*/;

    private void clear() {
        contentElement.setInnerText("");
    }

    private void applyLimit() {
        while (contentElement.getChildCount() > limit) {
            contentElement.removeChild(contentElement.getFirstChild());
        }
    }

    /**
     * Sets the log row limit.
     * 
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
        applyLimit();

        // TODO shoud be persisted
    }

    /**
     * Gets the current log row limit.
     * 
     * @return
     */
    public int getLimit() {
        // TODO should be read from persistent storage
        return limit;
    }

    @Override
    public DebugButton getTabButton() {
        return tabButton;
    }

    @Override
    public Widget getControls() {
        return controls;
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    public void show() {
        Storage storage = Storage.getLocalStorageIfSupported();
        if (storage == null) {
            return;
        }
        setScrollLock(VDebugWindow.readState(storage, "log-scrollLock", false));
    }

    @Override
    public void hide() {
        // remove timer
        setScrollLock(true);
    }

    /**
     * Schedules a scoll if scroll lock is not active.
     */
    private void maybeScroll() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer.schedule(SCROLL_DELAY);
        }
    }

    /**
     * Resets the timer and inserts a log row indicating this.
     */
    private void resetTimer() {
        int sinceStart = VDebugWindow.getMillisSinceStart();
        int sinceReset = VDebugWindow.resetTimer();
        Element row = DOM.createDiv();
        row.addClassName(VDebugWindow.STYLENAME + "-reset");
        row.setInnerHTML(Icon.RESET_TIMER + " Timer reset");
        row.setTitle(VDebugWindow.getTimingTooltip(sinceStart, sinceReset));
        contentElement.appendChild(row);
        maybeScroll();
    }

    /**
     * Adds a row to the log, applies the log row limit by removing old rows if
     * needed, and scrolls new row into view if scroll lock is not active.
     * 
     * @param level
     * @param msg
     * @return
     */
    private Element addRow(Level level, String msg) {
        int sinceReset = VDebugWindow.getMillisSinceReset();
        int sinceStart = VDebugWindow.getMillisSinceStart();

        Element row = DOM.createDiv();
        row.addClassName(VDebugWindow.STYLENAME + "-row");
        row.addClassName(level.getName());

        String inner = "<span class='" + VDebugWindow.STYLENAME + "-"
                + "'></span><span class='" + VDebugWindow.STYLENAME
                + "-time' title='"
                + VDebugWindow.getTimingTooltip(sinceStart, sinceReset) + "'>"
                + sinceReset + "ms</span><span class='"
                + VDebugWindow.STYLENAME + "-message'>" + msg + "</span>";
        row.setInnerHTML(inner);

        contentElement.appendChild(row);
        applyLimit();

        maybeScroll();

        return row;
    }

    @Override
    public void meta(ApplicationConnection ac, ValueMap meta) {
        addRow(Level.FINE, "Meta: " + meta.toSource());
    }

    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        addRow(Level.FINE, "UIDL: " + uidl.toSource());
    }

}
