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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VAbstractCalendarPanel;
import com.vaadin.client.ui.VAbstractPopupCalendar;
import com.vaadin.client.ui.VDateField;
import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState.AccessibleElement;

/**
 * Base class for various DateField connectors.
 *
 * @author Vaadin Ltd
 *
 * @param <R>
 *            the resolution type which this field is based on (day, month, ...)
 */
public abstract class AbstractDateFieldConnector<R extends Enum<R>>
        extends AbstractFieldConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().rpc = getRpcProxy(AbstractDateFieldServerRpc.class);
    }

    private void updateResolution() {
        VDateField<R> widget = getWidget();
        Map<String, Integer> stateResolutions = getState().resolutions;
        Optional<R> newResolution = widget.getResolutions()
                .filter(res -> stateResolutions.containsKey(res.name()))
                .findFirst();

        widget.setCurrentResolution(newResolution.orElse(null));
    }

    private Map<R, Integer> getTimeValues() {
        VDateField<R> widget = getWidget();
        Map<String, Integer> stateResolutions = getState().resolutions;
        Stream<R> resolutions = widget.getResolutions();
        R resolution = widget.getCurrentResolution();
        return resolutions.collect(Collectors.toMap(Function.identity(),
                res -> resolution.compareTo(res) <= 0
                        ? stateResolutions.get(res.name())
                        : null));
    }

    /**
     * Returns the default date (when no date is selected) components as a map
     * from Resolution to the corresponding value.
     *
     * @return default date component map
     * @since 8.2
     */
    protected Map<R, Integer> getDefaultValues() {
        VDateField<R> widget = getWidget();
        Map<String, Integer> stateResolutions = getState().resolutions;
        Stream<R> resolutions = widget.getResolutions();
        R resolution = widget.getCurrentResolution();
        return resolutions.collect(Collectors.toMap(Function.identity(),
                res -> resolution.compareTo(res) <= 0
                        ? stateResolutions.get("default-" + res.name())
                        : null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public VDateField<R> getWidget() {
        return (VDateField<R>) super.getWidget();
    }

    @Override
    public AbstractDateFieldState getState() {
        return (AbstractDateFieldState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        VDateField<R> widget = getWidget();

        // Save details
        widget.client = getConnection();
        widget.connector = this;

        widget.setReadonly(isReadOnly());
        widget.setEnabled(isEnabled());

        final String locale = getState().locale;
        try {
            widget.dts.setLocale(locale);
            widget.setCurrentLocale(locale);
        } catch (final LocaleNotLoadedException e) {
            widget.setCurrentLocale(widget.dts.getLocale());
            getLogger().severe("Tried to use an unloaded locale \"" + locale
                    + "\". Using default locale (" + widget.getCurrentLocale()
                    + ").");
            getLogger().log(Level.SEVERE,
                    e.getMessage() == null ? "" : e.getMessage(), e);
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        widget.setShowISOWeekNumbers(getState().showISOWeekNumbers
                && widget.dts.getFirstDayOfWeek() == 1);

        // Remove old stylename that indicates current resolution
        setWidgetStyleName(widget.getStylePrimaryName() + "-"
                + widget.resolutionAsString(), false);

        updateResolution();

        // Add stylename that indicates current resolution
        setWidgetStyleName(widget.getStylePrimaryName() + "-"
                + widget.resolutionAsString(), true);

        widget.setCurrentDate(getTimeValues());
        widget.setDefaultDate(getDefaultValues());
    }

    @SuppressWarnings("rawtypes")
    @OnStateChange("assistiveLabels")
    private void updateAssistiveLabels() {
        if (getWidget() instanceof VAbstractPopupCalendar) {
            setAndUpdateAssistiveLabels(
                    ((VAbstractPopupCalendar) getWidget()).calendar);
        }
    }

    /**
     * Sets assistive labels for the calendar panel's navigation elements, and
     * updates these labels.
     *
     * @param calendar
     *            the calendar panel for which to set the assistive labels
     * @since 8.4
     */
    @SuppressWarnings("rawtypes")
    protected void setAndUpdateAssistiveLabels(
            VAbstractCalendarPanel calendar) {
        calendar.setAssistiveLabelPreviousMonth(getState().assistiveLabels
                .get(AccessibleElement.PREVIOUS_MONTH));
        calendar.setAssistiveLabelNextMonth(
                getState().assistiveLabels.get(AccessibleElement.NEXT_MONTH));
        calendar.setAssistiveLabelPreviousYear(getState().assistiveLabels
                .get(AccessibleElement.PREVIOUS_YEAR));
        calendar.setAssistiveLabelNextYear(
                getState().assistiveLabels.get(AccessibleElement.NEXT_YEAR));

        calendar.updateAssistiveLabels();
    }

    @Override
    public void flush() {
        super.flush();
        getWidget().updateBufferedValues();
        getWidget().sendBufferedValues();
    }

    private static Logger getLogger() {
        return Logger.getLogger(AbstractDateFieldConnector.class.getName());
    }
}
