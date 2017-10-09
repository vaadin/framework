/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VDateField;
import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState;

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
                .filter(res -> stateResolutions
                        .containsKey(widget.getResolutionVariable(res)))
                .findFirst();

        widget.setCurrentResolution(newResolution.orElse(null));
    }

    private Map<R, Integer> getTimeValues() {
        VDateField<R> widget = getWidget();
        Map<String, Integer> stateResolutions = getState().resolutions;
        Stream<R> resolutions = widget.getResolutions();
        R resolution = widget.getCurrentResolution();
        return resolutions
                .collect(Collectors.toMap(Function.identity(),
                        res -> (resolution.compareTo(res) <= 0)
                                ? stateResolutions
                                        .get(widget.getResolutionVariable(res))
                                : -1));
    }

    /**
     * Returns the default date (when no date is selected) components as a map
     * from Resolution to the corresponding value.
     * 
     * @return default date component map
     * @since
     */
    protected Map<R, Integer> getDefaultValues() {
        Map<String, Integer> stateResolutions = getState().resolutions;
        Stream<R> resolutions = getWidget().getResolutions();
        R resolution = getWidget().getCurrentResolution();
        return resolutions.collect(Collectors.toMap(Function.identity(),
                res -> (resolution.compareTo(res) <= 0)
                        ? stateResolutions.get("default-"
                                + getWidget().getResolutionVariable(res))
                        : -1));
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
        widget.paintableId = getConnectorId();

        widget.setReadonly(isReadOnly());
        widget.setEnabled(isEnabled());

        final String locale = getState().locale;
        if (locale != null) {
            try {
                widget.dts.setLocale(locale);
                widget.setCurrentLocale(locale);
            } catch (final LocaleNotLoadedException e) {
                widget.setCurrentLocale(widget.dts.getLocale());
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale ("
                        + widget.getCurrentLocale() + ").");
                VConsole.error(e);
            }
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
}
