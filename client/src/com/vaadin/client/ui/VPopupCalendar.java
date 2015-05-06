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

import java.util.Date;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.LiveValue;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.VCalendarPanel.FocusOutListener;
import com.vaadin.client.ui.VCalendarPanel.SubmitListener;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.ui.datefield.PopupDateFieldState;
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

    /** For internal use only. May be removed or replaced in the future. */
    public final Button calendarToggle = new Button();

    /** For internal use only. May be removed or replaced in the future. */
    public VCalendarPanel calendar;

    /** For internal use only. May be removed or replaced in the future. */
    public final VOverlay popup;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean parsable = true;

    private boolean open = false;

    /*
     * #14857: If calendarToggle button is clicked when calendar popup is
     * already open we should prevent calling openCalendarPanel() in onClick,
     * since we don't want to reopen it again right after it closes.
     */
    private boolean preventOpenPopupCalendar = false;
    private boolean cursorOverCalendarToggleButton = false;
    private boolean toggleButtonClosesWithGuarantee = false;

    private boolean textFieldEnabled = true;

    private String captionId;

    private Label selectedDate;

    private Element descriptionForAssisitveDevicesElement;

    public VPopupCalendar() {
        super();

        calendarToggle.setText("");
        calendarToggle.addClickHandler(this);

        calendarToggle.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                cursorOverCalendarToggleButton = true;
            }
        }, MouseOverEvent.getType());

        calendarToggle.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                cursorOverCalendarToggleButton = false;
            }
        }, MouseOutEvent.getType());

        // -2 instead of -1 to avoid FocusWidget.onAttach to reset it
        calendarToggle.getElement().setTabIndex(-2);

        Roles.getButtonRole().set(calendarToggle.getElement());
        Roles.getButtonRole().setAriaHiddenState(calendarToggle.getElement(),
                true);

        add(calendarToggle);

        // Description of the usage of the widget for assisitve device users
        descriptionForAssisitveDevicesElement = DOM.createDiv();
        descriptionForAssisitveDevicesElement
                .setInnerText(PopupDateFieldState.DESCRIPTION_FOR_ASSISTIVE_DEVICES);
        AriaHelper.ensureHasId(descriptionForAssisitveDevicesElement);
        Roles.getTextboxRole().setAriaDescribedbyProperty(text.getElement(),
                Id.of(descriptionForAssisitveDevicesElement));
        AriaHelper.setVisibleForAssistiveDevicesOnly(
                descriptionForAssisitveDevicesElement, true);

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

        // FIXME: Problem is, that the element with the provided id does not
        // exist yet in html. This is the same problem as with the context menu.
        // Apply here the same fix (#11795)
        Roles.getTextboxRole().setAriaControlsProperty(text.getElement(),
                Id.of(calendar.getElement()));
        Roles.getButtonRole().setAriaControlsProperty(
                calendarToggle.getElement(), Id.of(calendar.getElement()));

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

        popup = new VOverlay(true, false, true);
        popup.setOwner(this);

        FlowPanel wrapper = new FlowPanel();
        selectedDate = new Label();
        selectedDate.setStyleName(getStylePrimaryName() + "-selecteddate");
        AriaHelper.setVisibleForAssistiveDevicesOnly(selectedDate.getElement(),
                true);

        Roles.getTextboxRole().setAriaLiveProperty(selectedDate.getElement(),
                LiveValue.ASSERTIVE);
        Roles.getTextboxRole().setAriaAtomicProperty(selectedDate.getElement(),
                true);
        wrapper.add(selectedDate);
        wrapper.add(calendar);

        popup.setWidget(wrapper);
        popup.addCloseHandler(this);

        DOM.setElementProperty(calendar.getElement(), "id",
                "PID_VAADIN_POPUPCAL");

        sinkEvents(Event.ONKEYDOWN);

        updateStyleNames();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.appendChild(RootPanel.get().getElement(),
                descriptionForAssisitveDevicesElement);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        descriptionForAssisitveDevicesElement.removeFromParent();
    }

    @SuppressWarnings("deprecation")
    public void updateValue(Date newDate) {
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
        }
    }

    /**
     * Checks whether the text field is enabled.
     * 
     * @see VPopupCalendar#setTextFieldEnabled(boolean)
     * @return The current state of the text field.
     */
    public boolean isTextFieldEnabled() {
        return textFieldEnabled;
    }

    /**
     * Sets the state of the text field of this component. By default the text
     * field is enabled. Disabling it causes only the button for date selection
     * to be active, thus preventing the user from entering invalid dates. See
     * {@link http://dev.vaadin.com/ticket/6790}.
     * 
     * @param state
     */
    public void setTextFieldEnabled(boolean textFieldEnabled) {
        this.textFieldEnabled = textFieldEnabled;
        updateTextFieldEnabled();
    }

    protected void updateTextFieldEnabled() {
        boolean reallyEnabled = isEnabled() && isTextFieldEnabled();
        // IE has a non input disabled themeing that can not be overridden so we
        // must fake the functionality using readonly and unselectable
        if (BrowserInfo.get().isIE()) {
            if (!reallyEnabled) {
                text.getElement().setAttribute("unselectable", "on");
                text.getElement().setAttribute("readonly", "");
                text.setTabIndex(-2);
            } else if (reallyEnabled
                    && text.getElement().hasAttribute("unselectable")) {
                text.getElement().removeAttribute("unselectable");
                text.getElement().removeAttribute("readonly");
                text.setTabIndex(0);
            }
        } else {
            text.setEnabled(reallyEnabled);
        }

        if (reallyEnabled) {
            calendarToggle.setTabIndex(-1);
            Roles.getButtonRole().setAriaHiddenState(
                    calendarToggle.getElement(), true);
        } else {
            calendarToggle.setTabIndex(0);
            Roles.getButtonRole().setAriaHiddenState(
                    calendarToggle.getElement(), false);
        }

        handleAriaAttributes();
    }

    /**
     * Set correct tab index for disabled text field in IE as the value set in
     * setTextFieldEnabled(...) gets overridden in
     * TextualDateConnection.updateFromUIDL(...)
     * 
     * @since 7.3.1
     */
    public void setTextFieldTabIndex() {
        if (BrowserInfo.get().isIE() && !textFieldEnabled) {
            // index needs to be -2 because FocusWidget updates -1 to 0 onAttach
            text.setTabIndex(-2);
        }
    }

    @Override
    public void bindAriaCaption(
            com.google.gwt.user.client.Element captionElement) {
        if (captionElement == null) {
            captionId = null;
        } else {
            captionId = captionElement.getId();
        }

        if (isTextFieldEnabled()) {
            super.bindAriaCaption(captionElement);
        } else {
            AriaHelper.bindCaption(calendarToggle, captionElement);
        }

        handleAriaAttributes();
    }

    private void handleAriaAttributes() {
        Widget removeFromWidget;
        Widget setForWidget;

        if (isTextFieldEnabled()) {
            setForWidget = text;
            removeFromWidget = calendarToggle;
        } else {
            setForWidget = calendarToggle;
            removeFromWidget = text;
        }

        Roles.getFormRole().removeAriaLabelledbyProperty(
                removeFromWidget.getElement());
        if (captionId == null) {
            Roles.getFormRole().removeAriaLabelledbyProperty(
                    setForWidget.getElement());
        } else {
            Roles.getFormRole().setAriaLabelledbyProperty(
                    setForWidget.getElement(), Id.of(captionId));
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

        if (!open && !readonly && isEnabled()) {
            open = true;

            if (getCurrentDate() != null) {
                calendar.setDate((Date) getCurrentDate().clone());
            } else {
                calendar.setDate(new Date());
            }

            // clear previous values
            popup.setWidth("");
            popup.setHeight("");
            popup.setPopupPositionAndShow(new PopupPositionCallback());
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
            if (!preventOpenPopupCalendar) {
                openCalendarPanel();
            }
            preventOpenPopupCalendar = false;
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
            if (!BrowserInfo.get().isTouchDevice() && textFieldEnabled) {
                /*
                 * Move focus to textbox, unless on touch device (avoids opening
                 * virtual keyboard) or if textField is disabled.
                 */
                focus();
            }

            open = false;

            if (cursorOverCalendarToggleButton
                    && !toggleButtonClosesWithGuarantee) {
                preventOpenPopupCalendar = true;
            }

            toggleButtonClosesWithGuarantee = false;
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateTextFieldEnabled();
        calendarToggle.setEnabled(enabled);
        Roles.getButtonRole().setAriaDisabledState(calendarToggle.getElement(),
                !enabled);
    }

    /**
     * Sets the content of a special field for assistive devices, so that they
     * can recognize the change and inform the user (reading out in case of
     * screen reader)
     * 
     * @param selectedDate
     *            Date that is currently selected
     */
    public void setFocusedDate(Date selectedDate) {
        this.selectedDate.setText(DateTimeFormat.getFormat("dd, MMMM, yyyy")
                .format(selectedDate));
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * @see com.vaadin.client.ui.VTextualDate#buildDate()
     */
    @Override
    public void buildDate() {
        // Save previous value
        String previousValue = getText();
        super.buildDate();

        // Restore previous value if the input could not be parsed
        if (!parsable) {
            setText(previousValue);
        }
        updateTextFieldEnabled();
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
            toggleButtonClosesWithGuarantee = true;
            popup.hide(true);
        }
    }

    private final String CALENDAR_TOGGLE_ID = "popupButton";

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if (subPart.equals(CALENDAR_TOGGLE_ID)) {
            return calendarToggle.getElement();
        }

        return super.getSubPartElement(subPart);
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (calendarToggle.getElement().isOrHasChild(subElement)) {
            return CALENDAR_TOGGLE_ID;
        }

        return super.getSubPartName(subElement);
    }

    /**
     * Set a description that explains the usage of the Widget for users of
     * assistive devices.
     * 
     * @param descriptionForAssistiveDevices
     *            String with the description
     */
    public void setDescriptionForAssistiveDevices(
            String descriptionForAssistiveDevices) {
        descriptionForAssisitveDevicesElement
                .setInnerText(descriptionForAssistiveDevices);
    }

    /**
     * Get the description that explains the usage of the Widget for users of
     * assistive devices.
     * 
     * @return String with the description
     */
    public String getDescriptionForAssistiveDevices() {
        return descriptionForAssisitveDevicesElement.getInnerText();
    }

    /**
     * Sets the start range for this component. The start range is inclusive,
     * and it depends on the current resolution, what is considered inside the
     * range.
     * 
     * @param startDate
     *            - the allowed range's start date
     */
    public void setRangeStart(Date rangeStart) {
        calendar.setRangeStart(rangeStart);
    }

    /**
     * Sets the end range for this component. The end range is inclusive, and it
     * depends on the current resolution, what is considered inside the range.
     * 
     * @param endDate
     *            - the allowed range's end date
     */
    public void setRangeEnd(Date rangeEnd) {
        calendar.setRangeEnd(rangeEnd);
    }

    private class PopupPositionCallback implements PositionCallback {

        @Override
        public void setPosition(int offsetWidth, int offsetHeight) {
            final int width = offsetWidth;
            final int height = offsetHeight;
            final int browserWindowWidth = Window.getClientWidth()
                    + Window.getScrollLeft();
            final int windowHeight = Window.getClientHeight()
                    + Window.getScrollTop();
            int left = calendarToggle.getAbsoluteLeft();

            // Add a little extra space to the right to avoid
            // problems with IE7 scrollbars and to make it look
            // nicer.
            int extraSpace = 30;

            boolean overflow = left + width + extraSpace > browserWindowWidth;
            if (overflow) {
                // Part of the popup is outside the browser window
                // (to the right)
                left = browserWindowWidth - width - extraSpace;
            }

            int top = calendarToggle.getAbsoluteTop();
            int extraHeight = 2;
            boolean verticallyRepositioned = false;
            ComputedStyle style = new ComputedStyle(popup.getElement());
            int[] margins = style.getMargin();
            int desiredPopupBottom = top + height
                    + calendarToggle.getOffsetHeight() + margins[0]
                    + margins[2];

            if (desiredPopupBottom > windowHeight) {
                int updatedLeft = left;
                left = getLeftPosition(left, width, style, overflow);

                // if position has not been changed then it means there is no
                // space to make popup fully visible
                if (updatedLeft == left) {
                    // let's try to show popup on the top of the field
                    int updatedTop = top - extraHeight - height - margins[0]
                            - margins[2];
                    verticallyRepositioned = updatedTop >= 0;
                    if (verticallyRepositioned) {
                        top = updatedTop;
                    }
                }
                // Part of the popup is outside the browser window
                // (below)
                if (!verticallyRepositioned) {
                    verticallyRepositioned = true;
                    top = windowHeight - height - extraSpace + extraHeight;
                }
            }
            if (verticallyRepositioned) {
                popup.setPopupPosition(left, top);
            } else {
                popup.setPopupPosition(left,
                        top + calendarToggle.getOffsetHeight() + extraHeight);
            }
            doSetFocus();
        }

        private int getLeftPosition(int left, int width, ComputedStyle style,
                boolean overflow) {
            if (positionRightSide()) {
                // Show to the right of the popup button unless we
                // are in the lower right corner of the screen
                if (overflow) {
                    return left;
                } else {
                    return left + calendarToggle.getOffsetWidth();
                }
            } else {
                int[] margins = style.getMargin();
                int desiredLeftPosition = calendarToggle.getAbsoluteLeft()
                        - width - margins[1] - margins[3];
                if (desiredLeftPosition >= 0) {
                    return desiredLeftPosition;
                } else {
                    return left;
                }
            }
        }

        private boolean positionRightSide() {
            int buttonRightSide = calendarToggle.getAbsoluteLeft()
                    + calendarToggle.getOffsetWidth();
            int textRightSide = text.getAbsoluteLeft() + text.getOffsetWidth();
            return buttonRightSide >= textRightSide;
        }

        private void doSetFocus() {
            /*
             * We have to wait a while before focusing since the popup needs to
             * be opened before we can focus
             */
            Timer focusTimer = new Timer() {
                @Override
                public void run() {
                    setFocus(true);
                }
            };

            focusTimer.schedule(100);
        }
    }

}
