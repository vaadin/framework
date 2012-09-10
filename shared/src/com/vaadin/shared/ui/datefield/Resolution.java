package com.vaadin.shared.ui.datefield;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Resolutions for DateFields
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 */
public enum Resolution {
    SECOND(Calendar.SECOND), MINUTE(Calendar.MINUTE), HOUR(Calendar.HOUR_OF_DAY), DAY(
            Calendar.DAY_OF_MONTH), MONTH(Calendar.MONTH), YEAR(Calendar.YEAR);

    private int calendarField;

    private Resolution(int calendarField) {
        this.calendarField = calendarField;
    }

    /**
     * Returns the field in {@link Calendar} that corresponds to this
     * resolution.
     * 
     * @return one of the field numbers used by Calendar
     */
    public int getCalendarField() {
        return calendarField;
    }

    /**
     * Returns the resolutions that are higher or equal to the given resolution,
     * starting from the given resolution. In other words passing DAY to this
     * methods returns DAY,MONTH,YEAR
     * 
     * @param r
     *            The resolution to start from
     * @return An iterable for the resolutions higher or equal to r
     */
    public static Iterable<Resolution> getResolutionsHigherOrEqualTo(
            Resolution r) {
        List<Resolution> resolutions = new ArrayList<Resolution>();
        Resolution[] values = Resolution.values();
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
    public static List<Resolution> getResolutionsLowerThan(Resolution r) {
        List<Resolution> resolutions = new ArrayList<Resolution>();
        Resolution[] values = Resolution.values();
        for (int i = r.ordinal() - 1; i >= 0; i--) {
            resolutions.add(values[i]);
        }
        return resolutions;
    }
};
