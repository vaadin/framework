/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.ui.components.calendar;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Calendar;

/**
 * All Calendar events extends this class.
 *
 * @since 7.1
 * @author Vaadin Ltd.
 *
 * @deprecated As of 8.0, no replacement available.
 */
@SuppressWarnings("serial")
@Deprecated
public class CalendarComponentEvent extends Component.Event {

    /**
     * Set the source of the event.
     *
     * @param source
     *            The source calendar
     *
     */
    public CalendarComponentEvent(Calendar source) {
        super(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component.Event#getComponent()
     */
    @Override
    public Calendar getComponent() {
        return (Calendar) super.getComponent();
    }
}
