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
package com.vaadin.client.ui;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * @author Vaadin Ltd
 *
 */
public class VPopupCalendar extends VAbstractPopupCalendar<DateResolution> {

    public VPopupCalendar() {
        super(GWT.create(VDateCalendarPanel.class), DateResolution.YEAR);
    }

    @Override
    protected DateResolution[] doGetResolutions() {
        return DateResolution.values();
    }

    @Override
    public String resolutionAsString() {
        return getResolutionVariable(getCurrentResolution());
    }

    @Override
    public void setCurrentResolution(DateResolution resolution) {
        super.setCurrentResolution(
                resolution == null ? DateResolution.YEAR : resolution);
    }

    @Override
    public boolean isYear(DateResolution resolution) {
        return DateResolution.YEAR.equals(resolution);
    }

    @Override
    protected Date getDate(Map<DateResolution, Integer> dateVaules) {
        return VTextualDate.makeDate(dateVaules);
    }

}
