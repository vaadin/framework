/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VAbstractCalendarPanel;
import com.vaadin.client.ui.VAbstractCalendarPanel.FocusChangeListener;
import com.vaadin.client.ui.VAbstractPopupCalendar;
import com.vaadin.shared.ui.datefield.TextualDateFieldState;

/**
 * Abstract date/time field connector which extend
 * {@link AbstractTextualDateConnector} functionality with widget that shows
 * date/time chooser as a popup panel.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <PANEL>
 *            Subclass of VAbstractCalendarPanel specific for the implementation
 * @param <R>
 *            the resolution type which the field is based on (day, month, ...)
 */
public abstract class TextualDateConnector<PANEL extends VAbstractCalendarPanel<R>, R extends Enum<R>>
        extends AbstractTextualDateConnector<R> {

    @Override
    protected void init() {
        super.init();
        getWidget().popup.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                /*
                 * FIXME This is a hack so we do not have to rewrite half of the
                 * datefield so values are not sent while selecting a date
                 * (#1399).
                 *
                 * The datefield will now only set the date variables while the
                 * user is selecting year/month/date/time and not send them
                 * directly. Only when the user closes the popup (by clicking on
                 * a day/enter/clicking outside of popup) then the new value is
                 * communicated to the server.
                 */
                getWidget().sendBufferedValues();
            }
        });
    }

    /**
     * Updates listeners registered (or register them) for the widget based on
     * the current resolution.
     * <p>
     * Subclasses may override this method to keep the common logic inside the
     * {@link #updateFromUIDL(UIDL, ApplicationConnection)} method as is and
     * customizing only listeners logic.
     */
    @SuppressWarnings("deprecation")
    protected void updateListeners() {
        FocusChangeListener listener;
        if (isResolutionMonthOrHigher()) {
            listener = date -> {
                if (isResolutionMonthOrHigher()) {
                    getWidget().updateValue(date);
                    getWidget().buildDate();
                    Date date2 = getWidget().calendar.getDate();
                    date2.setYear(date.getYear());
                    date2.setMonth(date.getMonth());
                }
            };
        } else {
            listener = null;
        }
        getWidget().calendar.setFocusChangeListener(listener);
    }

    /**
     * Returns {@code true} is the current resolution of the widget is month or
     * less specific (e.g. month, year, quarter, etc).
     *
     * @return {@code true} if the current resolution is above month
     */
    protected abstract boolean isResolutionMonthOrHigher();

    @SuppressWarnings("unchecked")
    @Override
    public VAbstractPopupCalendar<PANEL, R> getWidget() {
        return (VAbstractPopupCalendar<PANEL, R>) super.getWidget();
    }

    @Override
    public TextualDateFieldState getState() {
        return (TextualDateFieldState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        String oldLocale = getWidget().getCurrentLocale();
        boolean isReadOnly = getWidget().isReadonly();
        getWidget().parsable = getState().parsable;

        super.onStateChanged(stateChangeEvent);

        getWidget().setTextFieldEnabled(getState().textFieldEnabled);
        getWidget().setRangeStart(getState().rangeStart);
        getWidget().setRangeEnd(getState().rangeEnd);

        getWidget().calendar.setDateStyles(getState().dateStyles);
        getWidget().calendar
                .setDateTimeService(getWidget().getDateTimeService());
        getWidget().calendar
                .setShowISOWeekNumbers(getWidget().isShowISOWeekNumbers());
        if (getWidget().calendar.getResolution() != getWidget()
                .getCurrentResolution()) {
            boolean hasSelectedDate = false;
            getWidget().calendar
                    .setResolution(getWidget().getCurrentResolution());
            if (getWidget().calendar.getDate() != null
                    && getWidget().getCurrentDate() != null) {
                hasSelectedDate = true;
                getWidget().calendar
                        .setDate((Date) getWidget().getCurrentDate().clone());
            }
            // force re-render when changing resolution only
            getWidget().calendar.renderCalendar(hasSelectedDate);
        }

        // Force re-render of calendar if locale has changed (#12153)
        if (!getWidget().getCurrentLocale().equals(oldLocale)) {
            getWidget().calendar.renderCalendar();
        }

        updateListeners();

        if (getWidget().isReadonly()) {
            getWidget().calendarToggle.addStyleName(
                    VAbstractPopupCalendar.CLASSNAME + "-button-readonly");
        } else {
            getWidget().calendarToggle.removeStyleName(
                    VAbstractPopupCalendar.CLASSNAME + "-button-readonly");
            if (getState().readOnly != isReadOnly
                    && getWidget().calendar.isInitialRenderDone()) {
                getWidget().calendar.renderCalendar();
            }
        }

        getWidget().setDescriptionForAssistiveDevices(
                getState().descriptionForAssistiveDevices);

        getWidget().setTextFieldTabIndex();
    }

    @Override
    protected void setWidgetStyleName(String styleName, boolean add) {
        super.setWidgetStyleName(styleName, add);

        // update the style change to popup calendar widget
        getWidget().popup.setStyleName(styleName, add);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This will be removed once styles are no longer added with
     *             prefixes.
     */
    @Override
    @Deprecated
    protected void setWidgetStyleNameWithPrefix(String prefix, String styleName,
            boolean add) {
        super.setWidgetStyleNameWithPrefix(prefix, styleName, add);

        // update the style change to popup calendar widget with the correct
        // prefix
        if (!styleName.startsWith("-")) {
            styleName = "-" + styleName;
        }
        getWidget().popup.setStyleName(
                getWidget().getStylePrimaryName() + "-popup" + styleName, add);
    }

    @OnStateChange("dateStyles")
    void dateStylesUpdated() {
        VAbstractPopupCalendar<PANEL, R> widget = getWidget();
        widget.calendar.setDateStyles(getState().dateStyles);
        // Update text field if locale already set
        if (widget.getCurrentLocale() != null) {
            widget.buildDate();
        }
    }

}
