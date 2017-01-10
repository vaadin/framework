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

import com.vaadin.client.ui.VDateCalendarPanel;
import com.vaadin.client.ui.VPopupCalendar;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.shared.ui.datefield.LocalDateFieldState;
import com.vaadin.ui.AbstractLocalDateField;

/**
 * @author Vaadin Ltd
 *
 */
@Connect(AbstractLocalDateField.class)
public class DateFieldConnector
        extends TextualDateConnector<VDateCalendarPanel, DateResolution> {

    @Override
    protected boolean isResolutionMonthOrHigher() {
        return getWidget().getCurrentResolution()
                .compareTo(DateResolution.MONTH) >= 0;
    }

    @Override
    public VPopupCalendar getWidget() {
        return (VPopupCalendar) super.getWidget();
    }

    @Override
    public LocalDateFieldState getState() {
        return (LocalDateFieldState) super.getState();
    }
}
