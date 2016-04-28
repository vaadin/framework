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
package com.vaadin.shared.ui.calendar;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @since 7.1
 * @author Vaadin Ltd.
 */
public interface CalendarServerRpc extends ServerRpc {
    void eventMove(int eventIndex, String newDate);

    void rangeSelect(String range);

    void forward();

    void backward();

    void dateClick(String date);

    void weekClick(String event);

    void eventClick(int eventIndex);

    void eventResize(int eventIndex, String newStartDate, String newEndDate);

    void actionOnEmptyCell(String actionKey, String startDate, String endDate);

    void actionOnEvent(String actionKey, String startDate, String endDate,
            int eventIndex);

    @Delayed(lastOnly = true)
    void scroll(int scrollPosition);
}
