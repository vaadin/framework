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
package com.vaadin.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jsoup.nodes.Element;

import com.vaadin.data.Result;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.datefield.DateFieldConstants;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.datefield.TextualDateFieldState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A date editor component with <code>java.util.Date</code> as an input value.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 */
public abstract class AbstractDateField extends AbstractField<Date>
        implements LegacyComponent {

    /**
     * Value of the field.
     */
    private Date value;

    /**
     * Specified smallest modifiable unit for the date field.
     */
    private Resolution resolution = Resolution.DAY;

    /**
     * The internal calendar to be used in java.utl.Date conversions.
     */
    private transient Calendar calendar;

    /**
     * Overridden format string
     */
    private String dateFormat;

    private boolean lenient = false;

    private String dateString = null;

    private String currentParseErrorMessage;

    /**
     * Was the last entered string parsable? If this flag is false, datefields
     * internal validator does not pass.
     */
    private boolean uiHasValidDateString = true;

    /**
     * Determines if week numbers are shown in the date selector.
     */
    private boolean showISOWeekNumbers = false;

    private String defaultParseErrorMessage = "Date format not recognized";

    private TimeZone timeZone = null;

    private static Map<Resolution, String> variableNameForResolution = new HashMap<>();

    private String dateOutOfRangeMessage = "Date is out of allowed range";

    /**
     * Determines whether the ValueChangeEvent should be fired. Used to prevent
     * firing the event when UI has invalid string until uiHasValidDateString
     * flag is set
     */
    private boolean preventValueChangeEvent;

    static {
        variableNameForResolution.put(Resolution.SECOND, "sec");
        variableNameForResolution.put(Resolution.MINUTE, "min");
        variableNameForResolution.put(Resolution.HOUR, "hour");
        variableNameForResolution.put(Resolution.DAY, "day");
        variableNameForResolution.put(Resolution.MONTH, "month");
        variableNameForResolution.put(Resolution.YEAR, "year");
    }

    /* Constructors */

    /**
     * Constructs an empty <code>DateField</code> with no caption.
     */
    public AbstractDateField() {
    }

    /**
     * Constructs an empty <code>DateField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public AbstractDateField(String caption) {
        setCaption(caption);
    }

    /**
     * Constructs a new <code>DateField</code> with the given caption and
     * initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the Date value.
     */
    public AbstractDateField(String caption, Date value) {
        setValue(value);
        setCaption(caption);
    }

    /* Component basic features */

    /*
     * Paints this component. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // Adds the locale as attribute
        final Locale l = getLocale();
        if (l != null) {
            target.addAttribute("locale", l.toString());
        }

        if (getDateFormat() != null) {
            target.addAttribute("format", dateFormat);
        }

        if (!isLenient()) {
            target.addAttribute("strict", true);
        }

        target.addAttribute(DateFieldConstants.ATTR_WEEK_NUMBERS,
                isShowISOWeekNumbers());
        target.addAttribute("parsable", uiHasValidDateString);
        /*
         * TODO communicate back the invalid date string? E.g. returning back to
         * app or refresh.
         */

        // Gets the calendar
        final Calendar calendar = getCalendar();
        final Date currentDate = getValue();

        // Only paint variables for the resolution and up, e.g. Resolution DAY
        // paints DAY,MONTH,YEAR
        for (Resolution res : Resolution
                .getResolutionsHigherOrEqualTo(resolution)) {
            int value = -1;
            if (currentDate != null) {
                value = calendar.get(res.getCalendarField());
                if (res == Resolution.MONTH) {
                    // Calendar month is zero based
                    value++;
                }
            }
            target.addVariable(this, variableNameForResolution.get(res), value);
        }
    }

    /*
     * Invoked when a variable of the component changes. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        if (!isReadOnly() && (variables.containsKey("year")
                || variables.containsKey("month")
                || variables.containsKey("day") || variables.containsKey("hour")
                || variables.containsKey("min") || variables.containsKey("sec")
                || variables.containsKey("msec")
                || variables.containsKey("dateString"))) {

            // Old and new dates
            final Date oldDate = getValue();
            Date newDate = null;

            // this enables analyzing invalid input on the server
            final String newDateString = (String) variables.get("dateString");
            dateString = newDateString;

            // Gets the new date in parts
            boolean hasChanges = false;
            Map<Resolution, Integer> calendarFieldChanges = new HashMap<>();

            for (Resolution r : Resolution
                    .getResolutionsHigherOrEqualTo(resolution)) {
                // Only handle what the client is allowed to send. The same
                // resolutions that are painted
                String variableName = variableNameForResolution.get(r);

                if (variables.containsKey(variableName)) {
                    Integer value = (Integer) variables.get(variableName);
                    if (r == Resolution.MONTH) {
                        // Calendar MONTH is zero based
                        value--;
                    }
                    if (value >= 0) {
                        hasChanges = true;
                        calendarFieldChanges.put(r, value);
                    }
                }
            }

            // If no new variable values were received, use the previous value
            if (!hasChanges) {
                newDate = null;
            } else {
                // Clone the calendar for date operation
                final Calendar cal = getCalendar();

                // Update the value based on the received info
                // Must set in this order to avoid invalid dates (or wrong
                // dates if lenient is true) in calendar
                for (int r = Resolution.YEAR.ordinal(); r >= 0; r--) {
                    Resolution res = Resolution.values()[r];
                    if (calendarFieldChanges.containsKey(res)) {

                        // Field resolution should be included. Others are
                        // skipped so that client can not make unexpected
                        // changes (e.g. day change even though resolution is
                        // year).
                        Integer newValue = calendarFieldChanges.get(res);
                        cal.set(res.getCalendarField(), newValue);
                    }
                }
                newDate = cal.getTime();
            }

            if (newDate == null && dateString != null
                    && !dateString.isEmpty()) {
                Result<Date> parsedDate = handleUnparsableDateString(
                        dateString);
                if (parsedDate.isError()) {

                    /*
                     * Saves the localized message of parse error. This can be
                     * overridden in handleUnparsableDateString. The message
                     * will later be used to show a validation error.
                     */
                    currentParseErrorMessage = parsedDate.getMessage().get();

                    /*
                     * The value of the DateField should be null if an invalid
                     * value has been given. Not using setValue() since we do
                     * not want to cause the client side value to change.
                     */
                    uiHasValidDateString = false;

                    /*
                     * Datefield now contains some text that could't be parsed
                     * into date. ValueChangeEvent is fired after the value is
                     * changed and the flags are set
                     */
                    if (oldDate != null) {
                        /*
                         * Set the logic value to null without firing the
                         * ValueChangeEvent
                         */
                        preventValueChangeEvent = true;
                        try {
                            setValue(null);
                        } finally {
                            preventValueChangeEvent = false;
                        }

                        /*
                         * Reset the dateString (overridden to null by setValue)
                         */
                        dateString = newDateString;
                    }

                    /*
                     * If value was changed fire the ValueChangeEvent
                     */
                    if (oldDate != null) {
                        fireEvent(createValueChange(true));
                    }

                    markAsDirty();
                } else {
                    parsedDate.ifOk(value -> setValue(value, true));

                    /*
                     * Ensure the value is sent to the client if the value is
                     * set to the same as the previous (#4304). Does not repaint
                     * if handleUnparsableDateString throws an exception. In
                     * this case the invalid text remains in the DateField.
                     */
                    markAsDirty();
                }

            } else if (newDate != oldDate
                    && (newDate == null || !newDate.equals(oldDate))) {
                setValue(newDate, true); // Don't require a repaint, client
                // updates itself
            } else if (!uiHasValidDateString) { // oldDate ==
                                                // newDate == null
                // Empty value set, previously contained unparsable date string,
                // clear related internal fields
                setValue(null);
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }

        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    /**
     * Sets the start range for this component. If the value is set before this
     * date (taking the resolution into account), the component will not
     * validate. If <code>startDate</code> is set to <code>null</code>, any
     * value before <code>endDate</code> will be accepted by the range
     *
     * @param startDate
     *            - the allowed range's start date
     */
    public void setRangeStart(Date startDate) {
        if (startDate != null && getState().rangeEnd != null
                && startDate.after(getState().rangeEnd)) {
            throw new IllegalStateException(
                    "startDate cannot be later than endDate");
        }

        // Create a defensive copy against issues when using java.sql.Date (and
        // also against mutable Date).
        getState().rangeStart = startDate != null
                ? new Date(startDate.getTime()) : null;
    }

    /**
     * Sets the current error message if the range validation fails.
     *
     * @param dateOutOfRangeMessage
     *            - Localizable message which is shown when value (the date) is
     *            set outside allowed range
     */
    public void setDateOutOfRangeMessage(String dateOutOfRangeMessage) {
        this.dateOutOfRangeMessage = dateOutOfRangeMessage;
    }

    /**
     * Returns current date-out-of-range error message.
     *
     * @see #setDateOutOfRangeMessage(String)
     * @return Current error message for dates out of range.
     */
    public String getDateOutOfRangeMessage() {
        return dateOutOfRangeMessage;
    }

    /**
     * Gets the resolution.
     *
     * @return int
     */
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution of the DateField.
     *
     * The default resolution is {@link Resolution#DAY} since Vaadin 7.0.
     *
     * @param resolution
     *            the resolution to set.
     */
    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
        markAsDirty();
    }

    /**
     * Sets the end range for this component. If the value is set after this
     * date (taking the resolution into account), the component will not
     * validate. If <code>endDate</code> is set to <code>null</code>, any value
     * after <code>startDate</code> will be accepted by the range.
     *
     * @param endDate
     *            - the allowed range's end date (inclusive, based on the
     *            current resolution)
     */
    public void setRangeEnd(Date endDate) {
        if (endDate != null && getState().rangeStart != null
                && getState().rangeStart.after(endDate)) {
            throw new IllegalStateException(
                    "endDate cannot be earlier than startDate");
        }

        // Create a defensive copy against issues when using java.sql.Date (and
        // also against mutable Date).
        getState().rangeEnd = endDate != null ? new Date(endDate.getTime())
                : null;
    }

    /**
     * Returns the precise rangeStart used.
     *
     * @return the precise rangeStart used
     */
    public Date getRangeStart() {
        return getState(false).rangeStart;
    }

    /**
     * Returns the precise rangeEnd used.
     *
     * @return the precise rangeEnd used
     */
    public Date getRangeEnd() {
        return getState(false).rangeEnd;
    }

    /**
     * Sets formatting used by some component implementations. See
     * {@link SimpleDateFormat} for format details.
     *
     * By default it is encouraged to used default formatting defined by Locale,
     * but due some JVM bugs it is sometimes necessary to use this method to
     * override formatting. See Vaadin issue #2200.
     *
     * @param dateFormat
     *            the dateFormat to set
     *
     * @see com.vaadin.ui.AbstractComponent#setLocale(Locale))
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        markAsDirty();
    }

    /**
     * Returns a format string used to format date value on client side or null
     * if default formatting from {@link Component#getLocale()} is used.
     *
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Specifies whether or not date/time interpretation in component is to be
     * lenient.
     *
     * @see Calendar#setLenient(boolean)
     * @see #isLenient()
     *
     * @param lenient
     *            true if the lenient mode is to be turned on; false if it is to
     *            be turned off.
     */
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
        markAsDirty();
    }

    /**
     * Returns whether date/time interpretation is to be lenient.
     *
     * @see #setLenient(boolean)
     *
     * @return true if the interpretation mode of this calendar is lenient;
     *         false otherwise.
     */
    public boolean isLenient() {
        return lenient;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public void setValue(Date value) {
        /*
         * First handle special case when the client side component have a date
         * string but value is null (e.g. unparsable date string typed in by the
         * user). No value changes should happen, but we need to do some
         * internal housekeeping.
         */
        if (value == null && !uiHasValidDateString) {
            /*
             * Side-effects of doSetValue clears possible previous strings and
             * flags about invalid input.
             */
            doSetValue(null);

            markAsDirty();
            return;
        }
        super.setValue(value);
    }

    /**
     * Checks whether ISO 8601 week numbers are shown in the date selector.
     *
     * @return true if week numbers are shown, false otherwise.
     */
    public boolean isShowISOWeekNumbers() {
        return showISOWeekNumbers;
    }

    /**
     * Sets the visibility of ISO 8601 week numbers in the date selector. ISO
     * 8601 defines that a week always starts with a Monday so the week numbers
     * are only shown if this is the case.
     *
     * @param showWeekNumbers
     *            true if week numbers should be shown, false otherwise.
     */
    public void setShowISOWeekNumbers(boolean showWeekNumbers) {
        showISOWeekNumbers = showWeekNumbers;
        markAsDirty();
    }

    /**
     * Returns new instance calendar used in Date conversions.
     *
     * Returns new clone of the calendar object initialized using the the
     * current date (if available)
     *
     * If this is no calendar is assigned the <code>Calendar.getInstance</code>
     * is used.
     *
     * @return the Calendar.
     * @see #setCalendar(Calendar)
     */
    private Calendar getCalendar() {

        // Makes sure we have an calendar instance
        if (calendar == null) {
            calendar = Calendar.getInstance();
            // Start by a zeroed calendar to avoid having values for lower
            // resolution variables e.g. time when resolution is day
            int min, field;
            for (Resolution r : Resolution
                    .getResolutionsLowerThan(resolution)) {
                field = r.getCalendarField();
                min = calendar.getActualMinimum(field);
                calendar.set(field, min);
            }
            calendar.set(Calendar.MILLISECOND, 0);
        }

        // Clone the instance
        final Calendar newCal = (Calendar) calendar.clone();

        final TimeZone currentTimeZone = getTimeZone();
        if (currentTimeZone != null) {
            newCal.setTimeZone(currentTimeZone);
        }

        final Date currentDate = getValue();
        if (currentDate != null) {
            newCal.setTime(currentDate);
        }
        return newCal;
    }

    /**
     * Gets the time zone used by this field. The time zone is used to convert
     * the absolute time in a Date object to a logical time displayed in the
     * selector and to convert the select time back to a Date object.
     *
     * If {@code null} is returned, the current default time zone returned by
     * {@code TimeZone.getDefault()} is used.
     *
     * @return the current time zone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Return the error message that is shown if the user inputted value can't
     * be parsed into a Date object. If
     * {@link #handleUnparsableDateString(String)} is overridden and it throws a
     * custom exception, the message returned by
     * {@link Exception#getLocalizedMessage()} will be used instead of the value
     * returned by this method.
     *
     * @see #setParseErrorMessage(String)
     *
     * @return the error message that the DateField uses when it can't parse the
     *         textual input from user to a Date object
     */
    public String getParseErrorMessage() {
        return defaultParseErrorMessage;
    }

    /**
     * Sets the default error message used if the DateField cannot parse the
     * text input by user to a Date field. Note that if the
     * {@link #handleUnparsableDateString(String)} method is overridden, the
     * localized message from its exception is used.
     *
     * @see #getParseErrorMessage()
     * @see #handleUnparsableDateString(String)
     * @param parsingErrorMessage
     */
    public void setParseErrorMessage(String parsingErrorMessage) {
        defaultParseErrorMessage = parsingErrorMessage;
    }

    /**
     * Sets the time zone used by this date field. The time zone is used to
     * convert the absolute time in a Date object to a logical time displayed in
     * the selector and to convert the select time back to a Date object.
     *
     * If no time zone has been set, the current default time zone returned by
     * {@code TimeZone.getDefault()} is used.
     *
     * @see #getTimeZone()
     * @param timeZone
     *            the time zone to use for time calculations.
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        markAsDirty();
    }

    /**
     * Adds a <code>FocusListener</code> to the Component which gets fired when
     * a <code>LegacyField</code> receives keyboard focus.
     *
     * @param listener
     * @see FocusListener
     */
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * Removes a <code>FocusListener</code> from the Component.
     *
     * @param listener
     * @see FocusListener
     */
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * Adds a <code>BlurListener</code> to the Component which gets fired when a
     * <code>LegacyField</code> loses keyboard focus.
     *
     * @param listener
     * @see BlurListener
     */
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * Removes a <code>BlurListener</code> from the Component.
     *
     * @param listener
     * @see BlurListener
     */
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("value") && !design.attr("value").isEmpty()) {
            Date date = DesignAttributeHandler.getFormatter()
                    .parse(design.attr("value"), Date.class);
            // formatting will return null if it cannot parse the string
            if (date == null) {
                Logger.getLogger(AbstractDateField.class.getName()).info(
                        "cannot parse " + design.attr("value") + " as date");
            }
            setValue(date);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        if (getValue() != null) {
            design.attr("value",
                    DesignAttributeHandler.getFormatter().format(getValue()));
        }
    }

    @Override
    protected void fireEvent(EventObject event) {
        if (event instanceof ValueChange) {
            if (!preventValueChangeEvent) {
                super.fireEvent(event);
            }
        } else {
            super.fireEvent(event);
        }
    }

    /**
     * This method is called to handle a non-empty date string from the client
     * if the client could not parse it as a Date.
     *
     * By default, an error result is returned whose error message is
     * {@link #getParseErrorMessage()}.
     *
     * This can be overridden to handle conversions, to return a result with
     * {@code null} value (equivalent to empty input) or to return a custom
     * error.
     *
     * @param dateString
     *            date string to handle
     * @return result that contains parsed Date as a value or an error
     */
    protected Result<Date> handleUnparsableDateString(String dateString) {
        return Result.error(getParseErrorMessage());
    }

    @Override
    protected TextualDateFieldState getState() {
        return (TextualDateFieldState) super.getState();
    }

    @Override
    protected TextualDateFieldState getState(boolean markAsDirty) {
        return (TextualDateFieldState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(Date value) {
        // Also set the internal dateString
        if (value != null) {
            dateString = value.toString();
        } else {
            dateString = null;
        }

        this.value = value;
        setComponentError(null);
        if (!uiHasValidDateString) {
            // clear component error and parsing flag
            uiHasValidDateString = true;
            setComponentError(new UserError(currentParseErrorMessage));
        } else {
            DateRangeValidator validator = new DateRangeValidator(
                    getDateOutOfRangeMessage(), getRangeStart(getResolution()),
                    getRangeEnd(getResolution()), getResolution());
            Result<Date> result = validator.apply(value);
            if (result.isError()) {
                setComponentError(new UserError(getDateOutOfRangeMessage()));
            }
        }
    }

    /**
     * Gets the start range for a certain resolution. The range is inclusive, so
     * if <code>rangeStart</code> is set to one millisecond before year n and
     * resolution is set to YEAR, any date in year n - 1 will be accepted.
     * Lowest supported resolution is DAY.
     *
     * @param forResolution
     *            - the range conforms to the resolution
     * @return
     */
    private Date getRangeStart(Resolution forResolution) {
        if (getState(false).rangeStart == null) {
            return null;
        }
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(getState(false).rangeStart);

        if (forResolution == Resolution.YEAR) {
            startCal.set(startCal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        } else if (forResolution == Resolution.MONTH) {
            startCal.set(startCal.get(Calendar.YEAR),
                    startCal.get(Calendar.MONTH), 1, 0, 0, 0);
        } else {
            startCal.set(startCal.get(Calendar.YEAR),
                    startCal.get(Calendar.MONTH), startCal.get(Calendar.DATE),
                    0, 0, 0);
        }

        startCal.set(Calendar.MILLISECOND, 0);
        return startCal.getTime();
    }

    /**
     * Gets the end range for a certain resolution. The range is inclusive, so
     * if rangeEnd is set to zero milliseconds past year n and resolution is set
     * to YEAR, any date in year n will be accepted. Resolutions lower than DAY
     * will be interpreted on a DAY level. That is, everything below DATE is
     * cleared
     *
     * @param forResolution
     *            - the range conforms to the resolution
     * @return
     */
    private Date getRangeEnd(Resolution forResolution) {
        // We need to set the correct resolution for the dates,
        // otherwise the range validator will complain

        Date rangeEnd = getState(false).rangeEnd;
        if (rangeEnd == null) {
            return null;
        }

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(rangeEnd);

        if (forResolution == Resolution.YEAR) {
            // Adding one year (minresolution) and clearing the rest.
            endCal.set(endCal.get(Calendar.YEAR) + 1, 0, 1, 0, 0, 0);
        } else if (forResolution == Resolution.MONTH) {
            // Adding one month (minresolution) and clearing the rest.
            endCal.set(endCal.get(Calendar.YEAR),
                    endCal.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        } else {
            endCal.set(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH),
                    endCal.get(Calendar.DATE) + 1, 0, 0, 0);
        }
        // removing one millisecond will now get the endDate to return to
        // current resolution's set time span (year or month)
        endCal.set(Calendar.MILLISECOND, -1);
        return endCal.getTime();
    }

}
