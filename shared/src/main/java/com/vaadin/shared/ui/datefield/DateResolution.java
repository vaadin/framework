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
package com.vaadin.shared.ui.datefield;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolutions for DateFields.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public enum DateResolution {
    DAY, MONTH, YEAR;

    /**
     * Returns the resolutions that are higher or equal to the given resolution,
     * starting from the given resolution. In other words passing DAY to this
     * methods returns DAY,MONTH,YEAR
     *
     * @param r
     *            The resolution to start from
     * @return An iterable for the resolutions higher or equal to r
     */
    public static Iterable<DateResolution> getResolutionsHigherOrEqualTo(
            DateResolution r) {
        List<DateResolution> resolutions = new ArrayList<>();
        DateResolution[] values = DateResolution.values();
        for (int i = r.ordinal(); i < values.length; i++) {
            resolutions.add(values[i]);
        }
        return resolutions;
    }

    /**
     * Returns the resolutions that are lower than the given resolution,
     * starting from the given resolution. In other words passing DAY to this
     * methods returns HOUR,MINUTE,SECOND.
     *
     * @param r
     *            The resolution to start from
     * @return An iterable for the resolutions lower than r
     */
    public static List<DateResolution> getResolutionsLowerThan(
            DateResolution r) {
        List<DateResolution> resolutions = new ArrayList<>();
        DateResolution[] values = DateResolution.values();
        for (int i = r.ordinal() - 1; i >= 0; i--) {
            resolutions.add(values[i]);
        }
        return resolutions;
    }

}
