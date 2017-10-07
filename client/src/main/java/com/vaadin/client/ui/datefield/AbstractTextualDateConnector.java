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

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.client.TimeZoneInfo;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VAbstractTextualDate;
import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.AbstractTextualDateFieldState;

/**
 * Abstract base class for date fields with textual date representation.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <R>
 *            resolution type
 */
public abstract class AbstractTextualDateConnector<R extends Enum<R>>
        extends AbstractDateFieldConnector<R> {

    @Override
    protected void init() {
        super.init();
        getWidget().rpc = getRpcProxy(
                AbstractDateFieldServerRpc.class);
    }

    @Override
    public VAbstractTextualDate<R> getWidget() {
        return (VAbstractTextualDate<R>) super.getWidget();
    }

    @Override
    public AbstractTextualDateFieldState getState() {
        return (AbstractTextualDateFieldState) super.getState();
    }

    @OnStateChange("timeZoneJSON")
    private void onTimeZoneJSONChange() {
        TimeZone timeZone;
        String timeZoneJSON = getState().timeZoneJSON;
        if (timeZoneJSON != null) {
            TimeZoneInfo timeZoneInfo = TimeZoneInfo
                    .buildTimeZoneData(timeZoneJSON);
            timeZone = TimeZone.createTimeZone(timeZoneInfo);
        } else {
            timeZone = null;
        }
        getWidget().setTimeZone(timeZone);
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        R origRes = getWidget().getCurrentResolution();
        String oldLocale = getWidget().getCurrentLocale();
        super.onStateChanged(stateChangeEvent);
        if (origRes != getWidget().getCurrentResolution()
                || oldLocale != getWidget().getCurrentLocale()) {
            // force recreating format string
            getWidget().setFormatString(null);
        }
        getWidget().setFormatString(getState().format);

        getWidget().lenient = getState().lenient;

        getWidget().buildDate();
        // not a FocusWidget -> needs own tabindex handling
        getWidget().text.setTabIndex(getState().tabIndex);

        if (getWidget().isReadonly()) {
            getWidget().text.addStyleDependentName("readonly");
        } else {
            getWidget().text.removeStyleDependentName("readonly");
        }
    }
}
