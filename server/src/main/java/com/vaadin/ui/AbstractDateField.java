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

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.data.Result;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState;
import com.vaadin.shared.ui.datefield.DateFieldConstants;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.TimeZoneUtil;

/**
 * A date editor component with {@link LocalDate} as an input value.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            type of date ({@code LocalDate} or {@code LocalDateTime}).
 * @param <R>
 *            resolution enumeration type
 *
 */
public abstract class AbstractDateField<T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>, R extends Enum<R>>
        extends AbstractField<T>
        implements LegacyComponent, FocusNotifier, BlurNotifier {

    /**
     * Value of the field.
     */
    private T value;

    /**
     * Default value of the field, displayed when nothing has been selected.
     * 
     * @since 8.1.2
     */
    private T defaultValue = null;
    /**
     * Specified smallest modifiable unit for the date field.
     */
    private R resolution;

    /**
     * Overridden format string
     */
    private String dateFormat;

    private ZoneId zoneId;

    private boolean lenient = false;

    private String dateString = "";

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

    private String dateOutOfRangeMessage = "Date is out of allowed range";

    /* Constructors */

    /**
     * Constructs an empty <code>AbstractDateField</code> with no caption and
     * specified {@code resolution}.
     *
     * @param resolution
     *            initial resolution for the field
     */
    public AbstractDateField(R resolution) {
        this.resolution = resolution;
    }

    /**
     * Constructs an empty <code>AbstractDateField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     * @param resolution
     *            initial resolution for the field
     */
    public AbstractDateField(String caption, R resolution) {
        this(resolution);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>AbstractDateField</code> with the given caption
     * and initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the date/time value.
     * @param resolution
     *            initial resolution for the field
     */
    public AbstractDateField(String caption, T value, R resolution) {
        this(caption, resolution);
        setValue(value);
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
            target.addAttribute("format", getDateFormat());
        }

        if (!isLenient()) {
            target.addAttribute("strict", true);
        }

        target.addAttribute(DateFieldConstants.ATTR_WEEK_NUMBERS,
                isShowISOWeekNumbers());
        target.addAttribute("parsable", uiHasValidDateString);
        final T currentDate = getValue();

        // Only paint variables for the resolution and up, e.g. Resolution DAY
        // paints DAY,MONTH,YEAR
        for (R res : getResolutionsHigherOrEqualTo(getResolution())) {
            int value = -1;
            if (currentDate != null) {
                value = getDatePart(currentDate, res);
            }
            String variableName = getResolutionVariable(res);
            target.addVariable(this, variableName, value);
            if (defaultValue != null) {
                int defaultValuePart = getDatePart(defaultValue, res);
                target.addVariable(this, "default-" + variableName,
                        defaultValuePart);
            } else {
                target.addVariable(this, "default-" + variableName, -1);
            }
        }
    }

    /*
     * Invoked when a variable of the component changes. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        Set<String> resolutionNames = getResolutions()
                .map(this::getResolutionVariable).collect(Collectors.toSet());
        resolutionNames.retainAll(variables.keySet());
        if (!isReadOnly() && (!resolutionNames.isEmpty()
                || variables.containsKey("dateString"))) {

            // Old and new dates
            final T oldDate = getValue();

            // this enables analyzing invalid input on the server
            // this variable is null if the date was chosen with popup calendar
            // or contains user-typed string
            final String newDateString = (String) variables.get("dateString");

            T newDate;

            boolean hasChanges = false;

            if ("".equals(newDateString)) {

                newDate = null;
                // TODO check if the following 3 lines are necessary
                hasChanges = !uiHasValidDateString;
                uiHasValidDateString = true;
                currentParseErrorMessage = null;
            } else {
                newDate = reconstructDateFromFields(variables, oldDate);
            }

            hasChanges |= !Objects.equals(dateString, newDateString)
                    || !Objects.equals(oldDate, newDate);

            if (hasChanges) {
                dateString = newDateString;
                if (newDateString == null || newDateString.isEmpty()) {
                    uiHasValidDateString = true;
                    currentParseErrorMessage = null;
                    setComponentError(null);
                    setValue(newDate, true);
                } else {
                    if (variables.get("lastInvalidDateString") != null) {
                        Result<T> parsedDate = handleUnparsableDateString(
                                dateString);
                        parsedDate.ifOk(v -> {
                            uiHasValidDateString = true;
                            currentParseErrorMessage = null;
                            setValue(v, true);
                        });
                        if (parsedDate.isError()) {
                            dateString = null;
                            uiHasValidDateString = false;
                            currentParseErrorMessage = parsedDate.getMessage()
                                    .orElse("Parsing error");
                            setComponentError(
                                    new UserError(getParseErrorMessage()));
                            setValue(null, true);
                        }
                    } else {
                        uiHasValidDateString = true;
                        currentParseErrorMessage = null;
                        setValue(newDate, true);
                    }
                }
                markAsDirty();
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
     * Construct a date object from the individual field values received from
     * the client.
     *
     * @since 8.1.1
     */
    protected T reconstructDateFromFields(Map<String, Object> variables,
            T oldDate) {
        Map<R, Integer> calendarFields = new HashMap<>();

        for (R resolution : getResolutionsHigherOrEqualTo(getResolution())) {
            // Only handle what the client is allowed to send. The same
            // resolutions that are painted
            String variableName = getResolutionVariable(resolution);

            Integer newValue = (Integer) variables.get(variableName);
            if (newValue != null && newValue >= 0) {
                calendarFields.put(resolution, newValue);
            } else {
                calendarFields.put(resolution,
                        getDatePart(oldDate, resolution));
            }
        }
        return buildDate(calendarFields);
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
    public void setRangeStart(T startDate) {
        Date date = convertToDate(startDate);
        if (date != null && getState().rangeEnd != null
                && date.after(getState().rangeEnd)) {
            throw new IllegalStateException(
                    "startDate cannot be later than endDate");
        }

        getState().rangeStart = date;
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
     * @return the date/time field resolution
     */
    public R getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution of the DateField.
     *
     * The default resolution is {@link DateResolution#DAY} since Vaadin 7.0.
     *
     * @param resolution
     *            the resolution to set, not {@code null}
     */
    public void setResolution(R resolution) {
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
    public void setRangeEnd(T endDate) {
        Date date = convertToDate(endDate);
        if (date != null && getState().rangeStart != null
                && getState().rangeStart.after(date)) {
            throw new IllegalStateException(
                    "endDate cannot be earlier than startDate");
        }

        getState().rangeEnd = date;
    }

    /**
     * Returns the precise rangeStart used.
     *
     * @return the precise rangeStart used, may be null.
     */
    public T getRangeStart() {
        return convertFromDate(getState(false).rangeStart);
    }

    /**
     * Returns the precise rangeEnd used.
     *
     * @return the precise rangeEnd used, may be null.
     */
    public T getRangeEnd() {
        return convertFromDate(getState(false).rangeEnd);
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
     * Sets the {@link ZoneId}, which is used when {@code z} is included inside
     * the {@link #setDateFormat(String)}.
     *
     * @param zoneId
     *            the zone id
     * @since 8.2
     */
    public void setZoneId(ZoneId zoneId) {
        if (zoneId != this.zoneId
                || (zoneId != null && !zoneId.equals(this.zoneId))) {
            updateTimeZoneJSON(zoneId, getLocale());
        }
        this.zoneId = zoneId;
    }

    private void updateTimeZoneJSON(ZoneId zoneId, Locale locale) {
        String timeZoneJSON;
        if (zoneId != null && locale != null) {
            timeZoneJSON = TimeZoneUtil.toJSON(zoneId, locale);
        } else {
            timeZoneJSON = null;
        }
        getState().timeZoneJSON = timeZoneJSON;
    }

    @Override
    public void setLocale(Locale locale) {
        Locale oldLocale = getLocale();
        if (locale != oldLocale
                || (locale != null && !locale.equals(oldLocale))) {
            updateTimeZoneJSON(getZoneId(), locale);
        }
        super.setLocale(locale);
    }

    /**
     * Returns the {@link ZoneId}, which is used when {@code z} is included
     * inside the {@link #setDateFormat(String)}.
     *
     * @return the zoneId
     * @since 8.2
     */
    public ZoneId getZoneId() {
        return zoneId;
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
    public T getValue() {
        return value;
    }

    /**
     * Returns the current default value.
     *
     * @see #setDefaultValue(Temporal)
     * @return the default value
     * @since 8.1.2
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value for the field. The default value is the starting
     * point for the date field when nothing has been selected yet. If no
     * default value is set, current date/time is used.
     *
     * @param defaultValue
     * @since 8.1.2
     */
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent} .
     *
     * @param value
     *            the new value, may be {@code null}
     */
    @Override
    public void setValue(T value) {
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

    @Override
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("value") && !design.attr("value").isEmpty()) {
            Type dateType = GenericTypeReflector.getTypeParameter(getClass(),
                    AbstractDateField.class.getTypeParameters()[0]);
            if (dateType instanceof Class<?>) {
                Class<?> clazz = (Class<?>) dateType;
                T date = (T) DesignAttributeHandler.getFormatter()
                        .parse(design.attr("value"), clazz);
                // formatting will return null if it cannot parse the string
                if (date == null) {
                    Logger.getLogger(AbstractDateField.class.getName())
                            .info("cannot parse " + design.attr("value")
                                    + " as date");
                }
                doSetValue(date);
            } else {
                throw new RuntimeException("Cannot detect resoluton type "
                        + Optional.ofNullable(dateType).map(Type::getTypeName)
                                .orElse(null));
            }
        }
    }

    /**
     * Formats date according to the components locale. To be reimplemented in
     * subclasses.
     *
     * @param value
     *            the date or {@code null}
     * @return textual representation of the date or empty string for
     *         {@code null}
     * @since 8.1.1
     */
    protected String formatDate(T value) {
        return Objects.toString(value, "");
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        if (getValue() != null) {
            design.attr("value",
                    DesignAttributeHandler.getFormatter().format(getValue()));
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
    protected Result<T> handleUnparsableDateString(String dateString) {
        return Result.error(getParseErrorMessage());
    }

    @Override
    protected AbstractDateFieldState getState() {
        return (AbstractDateFieldState) super.getState();
    }

    @Override
    protected AbstractDateFieldState getState(boolean markAsDirty) {
        return (AbstractDateFieldState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(T value) {

        this.value = value;
        // Also set the internal dateString
        if (value != null) {
            dateString = formatDate(value);
        } else {
            dateString = formatDate(getEmptyValue());
        }
        RangeValidator<T> validator = getRangeValidator();// TODO move range
                                                          // check to internal
                                                          // validator?
        ValidationResult result = validator.apply(value,
                new ValueContext(this, this));
        if (result.isError()) {
            currentParseErrorMessage = getDateOutOfRangeMessage();
        }
        if (currentParseErrorMessage == null) {
            setComponentError(null);
        } else {
            setComponentError(new UserError(currentParseErrorMessage));
        }
    }

    /**
     * Returns a date integer value part for the given {@code date} for the
     * given {@code resolution}.
     *
     * @param date
     *            the given date
     * @param resolution
     *            the resolution to extract a value from the date by
     * @return the integer value part of the date by the given resolution
     */
    protected abstract int getDatePart(T date, R resolution);

    /**
     * Builds date by the given {@code resolutionValues} which is a map whose
     * keys are resolution and integer values.
     * <p>
     * This is the opposite to {@link #getDatePart(Temporal, Enum)}.
     *
     * @param resolutionValues
     *            date values to construct a date
     * @return date built from the given map of date values
     */
    protected abstract T buildDate(Map<R, Integer> resolutionValues);

    /**
     * Returns a custom date range validator which is applicable for the type
     * {@code T}.
     *
     * @return the date range validator
     */
    protected abstract RangeValidator<T> getRangeValidator();

    /**
     * Converts {@link Date} to date type {@code T}.
     *
     * @param date
     *            a date to convert
     * @return object of type {@code T} representing the {@code date}
     */
    protected abstract T convertFromDate(Date date);

    /**
     * Converts the object of type {@code T} to {@link Date}.
     * <p>
     * This is the opposite to {@link #convertFromDate(Date)}.
     *
     * @param date
     *            the date of type {@code T}
     * @return converted date of type {@code Date}
     */
    protected abstract Date convertToDate(T date);

    private String getResolutionVariable(R resolution) {
        return resolution.name().toLowerCase(Locale.ENGLISH);
    }

    @SuppressWarnings("unchecked")
    private Stream<R> getResolutions() {
        Type resolutionType = GenericTypeReflector.getTypeParameter(getClass(),
                AbstractDateField.class.getTypeParameters()[1]);
        if (resolutionType instanceof Class<?>) {
            Class<?> clazz = (Class<?>) resolutionType;
            return Stream.of(clazz.getEnumConstants())
                    .map(object -> (R) object);
        } else {
            throw new RuntimeException("Cannot detect resoluton type "
                    + Optional.ofNullable(resolutionType).map(Type::getTypeName)
                            .orElse(null));
        }
    }

    private Iterable<R> getResolutionsHigherOrEqualTo(R resoution) {
        return getResolutions().skip(resolution.ordinal())
                .collect(Collectors.toList());
    }

    @Override
    public Validator<T> getDefaultValidator() {
        return new Validator<T>() {
            @Override
            public ValidationResult apply(T value, ValueContext context) {
                if (currentParseErrorMessage != null) {
                    return ValidationResult.error(currentParseErrorMessage);
                }
                // Pass to range validator.
                return getRangeValidator().apply(value, context);
            }
        };
    }
}
