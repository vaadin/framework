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

package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.Field;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.datefield.VCalendarPanel.FocusOutListener;
import com.vaadin.client.ui.datefield.VCalendarPanel.SubmitListener;
import com.vaadin.shared.ui.datefield.Resolution;

/**
 * Represents a date selection component with a text field and a popup date
 * selector.
 * 
 * <b>Note:</b> To change the keyboard assignments used in the popup dialog you
 * should extend <code>com.vaadin.client.ui.VCalendarPanel</code> and then pass
 * set it by calling the <code>setCalendarPanel(VCalendarPanel panel)</code>
 * method.
 * 
 */
public class VPopupCalendar extends VTextualDate implements Field,
        ClickHandler, CloseHandler<PopupPanel>, SubPartAware {

    protected final Button calendarToggle = new Button();

    protected VCalendarPanel calendar;

    protected final VOverlay popup;
    private boolean open = false;
    protected boolean parsable = true;

    public VPopupCalendar() {
        super();

        calendarToggle.setText("");
        calendarToggle.addClickHandler(this);
        // -2 instead of -1 to avoid FocusWidget.onAttach to reset it
        calendarToggle.getElement().setTabIndex(-2);
        add(calendarToggle);

        calendar = GWT.create(VCalendarPanel.class);
        calendar.setParentField(this);
        calendar.setFocusOutListener(new FocusOutListener() {
            @Override
            public boolean onFocusOut(DomEvent<?> event) {
                event.preventDefault();
                closeCalendarPanel();
                return true;
            }
        });

        calendar.setSubmitListener(new SubmitListener() {
            @Override
            public void onSubmit() {
                // Update internal value and send valuechange event if immediate
                updateValue(calendar.getDate());

                // Update text field (a must when not immediate).
                buildDate(true);

                closeCalendarPanel();
            }

            @Override
            public void onCancel() {
                closeCalendarPanel();
            }
        });

        popup = new VOverlay(true, true, true);
        popup.setOwner(this);

        popup.setWidget(calendar);
        popup.addCloseHandler(this);

        DOM.setElementProperty(calendar.getElement(), "id",
                "PID_VAADIN_POPUPCAL");

        sinkEvents(Event.ONKEYDOWN);

        updateStyleNames();
    }

    @SuppressWarnings("deprecation")
    protected void updateValue(Date newDate) {
        Date currentDate = getCurrentDate();
        if (currentDate == null || newDate.getTime() != currentDate.getTime()) {
            setCurrentDate((Date) newDate.clone());
            getClient().updateVariable(getId(), "year",
                    newDate.getYear() + 1900, false);
            if (getCurrentResolution().getCalendarField() > Resolution.YEAR
                    .getCalendarField()) {
                getClient().updateVariable(getId(), "month",
                        newDate.getMonth() + 1, false);
                if (getCurrentResolution().getCalendarField() > Resolution.MONTH
                        .getCalendarField()) {
                    getClient().updateVariable(getId(), "day",
                            newDate.getDate(), false);
                    if (getCurrentResolution().getCalendarField() > Resolution.DAY
                            .getCalendarField()) {
                        getClient().updateVariable(getId(), "hour",
                                newDate.getHours(), false);
                        if (getCurrentResolution().getCalendarField() > Resolution.HOUR
                                .getCalendarField()) {
                            getClient().updateVariable(getId(), "min",
                                    newDate.getMinutes(), false);
                            if (getCurrentResolution().getCalendarField() > Resolution.MINUTE
                                    .getCalendarField()) {
                                getClient().updateVariable(getId(), "sec",
                                        newDate.getSeconds(), false);
                            }
                        }
                    }
                }
            }
            if (isImmediate()) {
                getClient().sendPendingVariableChanges();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.UIObject#setStyleName(java.lang.String)
     */
    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames();
    }

    @Override
    public void setStylePrimaryName(String style) {
        removeStyleName(getStylePrimaryName() + "-popupcalendar");
        super.setStylePrimaryName(style);
        updateStyleNames();
    }

    @Override
    protected void updateStyleNames() {
        super.updateStyleNames();
        if (getStylePrimaryName() != null && calendarToggle != null) {
            addStyleName(getStylePrimaryName() + "-popupcalendar");
            calendarToggle.setStyleName(getStylePrimaryName() + "-button");
            popup.setStyleName(getStylePrimaryName() + "-popup");
            calendar.setStyleName(getStylePrimaryName() + "-calendarpanel");
        }
    }

    /**
     * Opens the calendar panel popup
     */
    public void openCalendarPanel() {

        if (!open && !readonly) {
            open = true;

            if (getCurrentDate() != null) {
                calendar.setDate((Date) getCurrentDate().clone());
            } else {
                calendar.setDate(new Date());
            }

            // clear previous values
            popup.setWidth("");
            popup.setHeight("");
            popup.setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    final int w = offsetWidth;
                    final int h = offsetHeight;
                    final int browserWindowWidth = Window.getClientWidth()
                            + Window.getScrollLeft();
                    final int browserWindowHeight = Window.getClientHeight()
                            + Window.getScrollTop();
                    int t = calendarToggle.getAbsoluteTop();
                    int l = calendarToggle.getAbsoluteLeft();

                    // Add a little extra space to the right to avoid
                    // problems with IE7 scrollbars and to make it look
                    // nicer.
                    int extraSpace = 30;

                    boolean overflowRight = false;
                    if (l + +w + extraSpace > browserWindowWidth) {
                        overflowRight = true;
                        // Part of the popup is outside the browser window
                        // (to the right)
                        l = browserWindowWidth - w - extraSpace;
                    }

                    if (t + h + calendarToggle.getOffsetHeight() + 30 > browserWindowHeight) {
                        // Part of the popup is outside the browser window
                        // (below)
                        t = browserWindowHeight - h
                                - calendarToggle.getOffsetHeight() - 30;
                        if (!overflowRight) {
                            // Show to the right of the popup button unless we
                            // are in the lower right corner of the screen
                            l += calendarToggle.getOffsetWidth();
                        }
                    }

                    // fix size
                    popup.setWidth(w + "px");
                    popup.setHeight(h + "px");

                    popup.setPopupPosition(l,
                            t + calendarToggle.getOffsetHeight() + 2);

                    /*
                     * We have to wait a while before focusing since the popup
                     * needs to be opened before we can focus
                     */
                    Timer focusTimer = new Timer() {
                        @Override
                        public void run() {
                            setFocus(true);
                        }
                    };

                    focusTimer.schedule(100);
                }
            });
        } else {
            VConsole.error("Cannot reopen popup, it is already open!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == calendarToggle && isEnabled()) {
            openCalendarPanel();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google.gwt
     * .event.logical.shared.CloseEvent)
     */
    @Override
    public void onClose(CloseEvent<PopupPanel> event) {
        if (event.getSource() == popup) {
            buildDate();
            if (!BrowserInfo.get().isTouchDevice()) {
                /*
                 * Move focus to textbox, unless on touch device (avoids opening
                 * virtual keyboard).
                 */
                focus();
            }

            // TODO resolve what the "Sigh." is all about and document it here
            // Sigh.
            Timer t = new Timer() {
                @Override
                public void run() {
                    open = false;
                }
            };
            t.schedule(100);
        }
    }

    /**
     * Sets focus to Calendar panel.
     * 
     * @param focus
     */
    public void setFocus(boolean focus) {
        calendar.setFocus(focus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.VTextualDate#buildDate()
     */
    @Override
    protected void buildDate() {
        // Save previous value
        String previousValue = getText();
        super.buildDate();

        // Restore previous value if the input could not be parsed
        if (!parsable) {
            setText(previousValue);
        }
    }

    /**
     * Update the text field contents from the date. See {@link #buildDate()}.
     * 
     * @param forceValid
     *            true to force the text field to be updated, false to only
     *            update if the parsable flag is true.
     */
    protected void buildDate(boolean forceValid) {
        if (forceValid) {
            parsable = true;
        }
        buildDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.VDateField#onBrowserEvent(com.google
     * .gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(com.google.gwt.user.client.Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONKEYDOWN
                && event.getKeyCode() == getOpenCalenderPanelKey()) {
            openCalendarPanel();
            event.preventDefault();
        }
    }

    /**
     * Get the key code that opens the calendar panel. By default it is the down
     * key but you can override this to be whatever you like
     * 
     * @return
     */
    protected int getOpenCalenderPanelKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Closes the open popup panel
     */
    public void closeCalendarPanel() {
        if (open) {
            popup.hide(true);
        }
    }

    private final String CALENDAR_TOGGLE_ID = "popupButton";

    @Override
    public Element getSubPartElement(String subPart) {
        if (subPart.equals(CALENDAR_TOGGLE_ID)) {
            return calendarToggle.getElement();
        }

        return super.getSubPartElement(subPart);
    }

    @Override
    public String getSubPartName(Element subElement) {
        if (calendarToggle.getElement().isOrHasChild(subElement)) {
            return CALENDAR_TOGGLE_ID;
        }

        return super.getSubPartName(subElement);
    }

}
