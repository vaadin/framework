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
package com.vaadin.client.ui.calendar.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Internally used by the calendar
 * 
 * @since 7.1
 */
public class DateCellGroup {
    private WeekGridMinuteTimeRange range;
    private final List<Integer> items;

    public DateCellGroup(Integer index) {
        items = new ArrayList<Integer>();
        items.add(index);
    }

    public WeekGridMinuteTimeRange getDateRange() {
        return range;
    }

    public Date getStart() {
        return range.getStart();
    }

    public Date getEnd() {
        return range.getEnd();
    }

    public void setDateRange(WeekGridMinuteTimeRange range) {
        this.range = range;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void add(Integer index) {
        items.add(index);
    }
}
