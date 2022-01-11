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
package com.vaadin.shared.ui.datefield;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Shared state for the AbstractDateField component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class AbstractDateFieldState extends AbstractFieldState {

    /**
     * Navigation elements that have assistive label.
     *
     * @since 8.4
     */
    public enum AccessibleElement {
        PREVIOUS_YEAR, NEXT_YEAR, PREVIOUS_MONTH, NEXT_MONTH
    }

    {
        primaryStyleName = "v-datefield";
    }

    /**
     * Start range that has been cleared, depending on the resolution of the
     * date field. The format is "2018-05-27" or "2018-05-27 14:38:39"
     *
     * @see com.vaadin.ui.AbstractDateField#RANGE_FORMATTER
     */
    @NoLayout
    public String rangeStart;

    /**
     * End range that has been cleared, depending on the resolution of the date
     * field. The format is "2018-05-27" or "2018-05-27 14:38:39"
     *
     * @see com.vaadin.ui.AbstractDateField#RANGE_FORMATTER
     */
    @NoLayout
    public String rangeEnd;

    /**
     * The JSON used to construct a TimeZone on the client side, can be
     * {@code null}.
     *
     * @since 8.2
     */
    public String timeZoneJSON;

    /**
     * The used Locale, can be {@code null}.
     *
     * @since 8.2
     */
    public String locale;

    /**
     * Overridden date format string, can be {@code null} if default formatting
     * of the components locale is used.
     *
     * @since 8.2
     */
    public String format;

    /**
     * Whether the date/time interpretation is lenient.
     *
     * @since 8.2
     */
    public boolean lenient;

    /**
     * The map of {@code Resolution}s which are currently used by the component.
     *
     * The key is the resolution name e.g. "HOUR", "MINUTE", with possibly
     * prefixed by "default-".
     *
     * The value can be {@code null}
     *
     * @since 8.2
     */
    public Map<String, Integer> resolutions = new HashMap<>();

    /**
     * Determines if week numbers are shown in the date selector.
     *
     * @since 8.2
     */
    public boolean showISOWeekNumbers;

    /**
     * Was the last entered string parsable? If this flag is false, datefields
     * internal validator does not pass.
     *
     * @since 8.2
     */
    public boolean parsable = true;

    /**
     * Map of custom style names that correspond with given dates. Each date
     * must be set to midnight for the handling logic to work correctly.
     *
     * @since 8.3
     */
    public Map<String, String> dateStyles = new HashMap<String, String>();

    /**
     * Map of elements and their corresponding assistive labels.
     *
     * @since 8.4
     */
    public Map<AccessibleElement, String> assistiveLabels = new HashMap<>();

    // Set default accessive labels
    {
        assistiveLabels.put(AccessibleElement.PREVIOUS_YEAR, "Previous year");
        assistiveLabels.put(AccessibleElement.NEXT_YEAR, "Next year");
        assistiveLabels.put(AccessibleElement.PREVIOUS_MONTH, "Previous month");
        assistiveLabels.put(AccessibleElement.NEXT_MONTH, "Next month");
    }

}
