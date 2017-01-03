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

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VDateField;
import com.vaadin.shared.ui.datefield.DateFieldConstants;

public abstract class AbstractDateFieldConnector<R extends Enum<R>>
        extends AbstractFieldConnector implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        getWidget().setReadonly(isReadOnly());
        getWidget().setEnabled(isEnabled());

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                getWidget().dts.setLocale(locale);
                getWidget().setCurrentLocale(locale);
            } catch (final LocaleNotLoadedException e) {
                getWidget().setCurrentLocale(getWidget().dts.getLocale());
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale ("
                        + getWidget().getCurrentLocale() + ").");
                VConsole.error(e);
            }
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        getWidget().setShowISOWeekNumbers(
                uidl.getBooleanAttribute(DateFieldConstants.ATTR_WEEK_NUMBERS)
                        && getWidget().dts.getFirstDayOfWeek() == 1);

        // Remove old stylename that indicates current resolution
        setWidgetStyleName(getWidget().getStylePrimaryName() + "-"
                + getWidget().resolutionAsString(), false);

        updateResolution(uidl);

        // Add stylename that indicates current resolution
        setWidgetStyleName(getWidget().getStylePrimaryName() + "-"
                + getWidget().resolutionAsString(), true);

        getWidget().setCurrentDate(getTimeValues(uidl));
    }

    private void updateResolution(UIDL uidl) {
        Optional<R> newResolution = getWidget().getResolutions().filter(
                res -> uidl.hasVariable(getWidget().getResolutionVariable(res)))
                .findFirst();

        getWidget().setCurrentResolution(newResolution.orElse(null));
    }

    protected Map<R, Integer> getTimeValues(UIDL uidl) {
        Stream<R> resolutions = getWidget().getResolutions();
        R resolution = getWidget().getCurrentResolution();
        return resolutions
                .collect(Collectors.toMap(Function.identity(),
                        res -> (resolution.compareTo(res) <= 0)
                                ? uidl.getIntVariable(
                                        getWidget().getResolutionVariable(res))
                                : -1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public VDateField<R> getWidget() {
        return (VDateField<R>) super.getWidget();
    }

}
