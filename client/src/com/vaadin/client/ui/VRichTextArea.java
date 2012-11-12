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

package com.vaadin.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Util;
import com.vaadin.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;

/**
 * This class implements a basic client side rich text editor component.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VRichTextArea extends Composite implements Field, ChangeHandler,
        BlurHandler, KeyPressHandler, KeyDownHandler, Focusable {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-richtextarea";

    /** For internal use only. May be removed or replaced in the future. */
    public String id;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate = false;

    /** For internal use only. May be removed or replaced in the future. */
    public RichTextArea rta;

    private VRichTextToolbar formatter;

    /** For internal use only. May be removed or replaced in the future. */
    public HTML html = new HTML();

    private final FlowPanel fp = new FlowPanel();

    private boolean enabled = true;

    private int extraHorizontalPixels = -1;
    private int extraVerticalPixels = -1;

    /** For internal use only. May be removed or replaced in the future. */
    public int maxLength = -1;

    private int toolbarNaturalWidth = 500;

    /** For internal use only. May be removed or replaced in the future. */
    public HandlerRegistration keyPressHandler;

    private ShortcutActionHandlerOwner hasShortcutActionHandler;

    /** For internal use only. May be removed or replaced in the future. */
    public String currentValue = "";

    private boolean readOnly = false;

    public VRichTextArea() {
        createRTAComponents();
        fp.add(formatter);
        fp.add(rta);

        initWidget(fp);
        setStyleName(CLASSNAME);

        TouchScrollDelegate.enableTouchScrolling(html, html.getElement());
    }

    private void createRTAComponents() {
        rta = new RichTextArea();
        rta.setWidth("100%");
        rta.addBlurHandler(this);
        rta.addKeyDownHandler(this);
        formatter = new VRichTextToolbar(rta);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            // rta.setEnabled(enabled);
            swapEditableArea();
            this.enabled = enabled;
        }
    }

    /**
     * Swaps html to rta and visa versa.
     */
    private void swapEditableArea() {
        if (html.isAttached()) {
            fp.remove(html);
            if (BrowserInfo.get().isWebkit()) {
                fp.remove(formatter);
                createRTAComponents(); // recreate new RTA to bypass #5379
                fp.add(formatter);
            }
            rta.setHTML(currentValue);
            fp.add(rta);
        } else {
            html.setHTML(currentValue);
            fp.remove(rta);
            fp.add(html);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void selectAll() {
        /*
         * There is a timing issue if trying to select all immediately on first
         * render. Simple deferred command is not enough. Using Timer with
         * moderated timeout. If this appears to fail on many (most likely slow)
         * environments, consider increasing the timeout.
         * 
         * FF seems to require the most time to stabilize its RTA. On Vaadin
         * tiergarden test machines, 200ms was not enough always (about 50%
         * success rate) - 300 ms was 100% successful. This however was not
         * enough on a sluggish old non-virtualized XP test machine. A bullet
         * proof solution would be nice, GWT 2.1 might however solve these. At
         * least setFocus has a workaround for this kind of issue.
         */
        new Timer() {
            @Override
            public void run() {
                rta.getFormatter().selectAll();
            }
        }.schedule(320);
    }

    public void setReadOnly(boolean b) {
        if (isReadOnly() != b) {
            swapEditableArea();
            readOnly = b;
        }
        // reset visibility in case enabled state changed and the formatter was
        // recreated
        formatter.setVisible(!readOnly);
    }

    private boolean isReadOnly() {
        return readOnly;
    }

    // TODO is this really used, or does everything go via onBlur() only?
    @Override
    public void onChange(ChangeEvent event) {
        synchronizeContentToServer();
    }

    /**
     * Method is public to let popupview force synchronization on close.
     */
    public void synchronizeContentToServer() {
        if (client != null && id != null) {
            final String html = rta.getHTML();
            if (!html.equals(currentValue)) {
                client.updateVariable(id, "text", html, immediate);
                currentValue = html;
            }
        }
    }

    @Override
    public void onBlur(BlurEvent event) {
        synchronizeContentToServer();
        // TODO notify possible server side blur/focus listeners
    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraHorizontalPixels() {
        if (extraHorizontalPixels < 0) {
            detectExtraSizes();
        }
        return extraHorizontalPixels;
    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraVerticalPixels() {
        if (extraVerticalPixels < 0) {
            detectExtraSizes();
        }
        return extraVerticalPixels;
    }

    /**
     * Detects space used by components paddings and borders.
     */
    private void detectExtraSizes() {
        Element clone = Util.cloneNode(getElement(), false);
        DOM.setElementAttribute(clone, "id", "");
        DOM.setStyleAttribute(clone, "visibility", "hidden");
        DOM.setStyleAttribute(clone, "position", "absolute");
        // due FF3 bug set size to 10px and later subtract it from extra pixels
        DOM.setStyleAttribute(clone, "width", "10px");
        DOM.setStyleAttribute(clone, "height", "10px");
        DOM.appendChild(DOM.getParent(getElement()), clone);
        extraHorizontalPixels = DOM.getElementPropertyInt(clone, "offsetWidth") - 10;
        extraVerticalPixels = DOM.getElementPropertyInt(clone, "offsetHeight") - 10;

        DOM.removeChild(DOM.getParent(getElement()), clone);
    }

    @Override
    public void setHeight(String height) {
        if (height.endsWith("px")) {
            int h = Integer.parseInt(height.substring(0, height.length() - 2));
            h -= getExtraVerticalPixels();
            if (h < 0) {
                h = 0;
            }

            super.setHeight(h + "px");
        } else {
            super.setHeight(height);
        }

        if (height == null || height.equals("")) {
            rta.setHeight("");
        } else {
            /*
             * The formatter height will be initially calculated wrong so we
             * delay the height setting so the DOM has had time to stabilize.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    int editorHeight = getOffsetHeight()
                            - getExtraVerticalPixels()
                            - formatter.getOffsetHeight();
                    if (editorHeight < 0) {
                        editorHeight = 0;
                    }
                    rta.setHeight(editorHeight + "px");
                }
            });
        }
    }

    @Override
    public void setWidth(String width) {
        if (width.endsWith("px")) {
            int w = Integer.parseInt(width.substring(0, width.length() - 2));
            w -= getExtraHorizontalPixels();
            if (w < 0) {
                w = 0;
            }

            super.setWidth(w + "px");
        } else if (width.equals("")) {
            /*
             * IE cannot calculate the width of the 100% iframe correctly if
             * there is no width specified for the parent. In this case we would
             * use the toolbar but IE cannot calculate the width of that one
             * correctly either in all cases. So we end up using a default width
             * for a RichTextArea with no width definition in all browsers (for
             * compatibility).
             */

            super.setWidth(toolbarNaturalWidth + "px");
        } else {
            super.setWidth(width);
        }
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (maxLength >= 0) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    if (rta.getHTML().length() > maxLength) {
                        rta.setHTML(rta.getHTML().substring(0, maxLength));
                    }
                }
            });
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        // delegate to closest shortcut action handler
        // throw event from the iframe forward to the shortcuthandler
        ShortcutActionHandler shortcutHandler = getShortcutHandlerOwner()
                .getShortcutActionHandler();
        if (shortcutHandler != null) {
            shortcutHandler
                    .handleKeyboardEvent(com.google.gwt.user.client.Event
                            .as(event.getNativeEvent()),
                            ConnectorMap.get(client).getConnector(this));
        }
    }

    private ShortcutActionHandlerOwner getShortcutHandlerOwner() {
        if (hasShortcutActionHandler == null) {
            Widget parent = getParent();
            while (parent != null) {
                if (parent instanceof ShortcutActionHandlerOwner) {
                    break;
                }
                parent = parent.getParent();
            }
            hasShortcutActionHandler = (ShortcutActionHandlerOwner) parent;
        }
        return hasShortcutActionHandler;
    }

    @Override
    public int getTabIndex() {
        return rta.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        rta.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        /*
         * Similar issue as with selectAll. Focusing must happen before possible
         * selectall, so keep the timeout here lower.
         */
        new Timer() {

            @Override
            public void run() {
                rta.setFocus(true);
            }
        }.schedule(300);
    }

    @Override
    public void setTabIndex(int index) {
        rta.setTabIndex(index);
    }

}
