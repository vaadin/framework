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
package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.gentyref.GenericTypeReflector;
import org.jsoup.nodes.Element;

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
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState.AccessibleElement;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.TimeZoneUtil;

import elemental.json.Json;

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
        extends AbstractField<T> implements FocusNotifier, BlurNotifier {

    private static final DateTimeFormatter RANGE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd[ HH:mm:ss]", Locale.ENGLISH);
    private AbstractDateFieldServerRpc rpc = new AbstractDateFieldServerRpc() {

        @Override
        public void update(String newDateString,
                Map<String, Integer> resolutions) {
            valueUpdate(newDateString, resolutions);
        }

        @Override
        public void updateValueWithDelay(String newDateString,
                Map<String, Integer> resolutions) {
            valueUpdate(newDateString, resolutions);
        }

        private void valueUpdate(String newDateString,
                Map<String, Integer> resolutions) {
            Set<String> resolutionNames = getResolutions().map(Enum::name)
                    .collect(Collectors.toSet());
            resolutionNames.retainAll(resolutions.keySet());
            if (!isReadOnly()
                    && (!resolutionNames.isEmpty() || newDateString != null)) {

                // Old and new dates
                final T oldDate = getValue();

                T newDate;

                boolean hasChanges = false;

                if ("".equals(newDateString)) {

                    newDate = null;
                } else {
                    newDate = reconstructDateFromFields(resolutions, oldDate);
                }

                boolean parseErrorWasSet = currentErrorMessage != null;
                hasChanges |= !Objects.equals(dateString, newDateString)
                        || !Objects.equals(oldDate, newDate)
                        || parseErrorWasSet;

                if (hasChanges) {
                    dateString = newDateString;
                    currentErrorMessage = null;
                    if (newDateString == null || newDateString.isEmpty()) {
                        boolean valueChanged = setValue(newDate, true);
                        if (!valueChanged && parseErrorWasSet) {
                            doSetValue(newDate);
                        }
                    } else {
                        // invalid date string
                        if (resolutions.isEmpty()) {
                            Result<T> parsedDate = handleUnparsableDateString(
                                    dateString);
                            // If handleUnparsableDateString returns the same
                            // date as current, force update state to display
                            // correct representation
                            parsedDate.ifOk(v -> {
                                if (!setValue(v, true)
                                        && !isDifferentValue(v)) {
                                    updateDiffstate("resolutions",
                                            Json.createObject());
                                    doSetValue(v);
                                }
                            });
                            if (parsedDate.isError()) {
                                dateString = null;
                                currentErrorMessage = parsedDate.getMessage()
                                        .orElse("Parsing error");

                                if (!isDifferentValue(null)) {
                                    doSetValue(null);
                                } else {
                                    setValue(null, true);
                                }
                            }
                        } else {
                            setValue(newDate, true);
                        }
                    }
                }
            }
        }

        @Override
        public void focus() {
            fireEvent(new FocusEvent(AbstractDateField.this));
        }

        @Override
        public void blur() {
            fireEvent(new BlurEvent(AbstractDateField.this));
        }
    };

    /**
     * Value of the field.
     */
    private T value;

    /**
     * Default value of the field, displayed when nothing has been selected.
     *
     * @since 8.1.2
     */
    private T defaultValue;

    /**
     * Specified smallest modifiable unit for the date field.
     */
    private R resolution;

    private ZoneId zoneId;

    private String dateString = "";

    private String currentErrorMessage;

    private String defaultParseErrorMessage = "Date format not recognized";

    private String dateOutOfRangeMessage = "Date is out of allowed range";

    /* Constructors */

    /**
     * Constructs an empty {@code AbstractDateField} with no caption and
     * specified {@code resolution}.
     *
     * @param resolution
     *            initial resolution for the field, not {@code null}
     */
    public AbstractDateField(R resolution) {
        registerRpc(rpc);
        setResolution(resolution);
    }

    /**
     * Constructs an empty {@code AbstractDateField} with caption.
     *
     * @param caption
     *            the caption of the datefield
     * @param resolution
     *            initial resolution for the field, not {@code null}
     */
    public AbstractDateField(String caption, R resolution) {
        this(resolution);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code AbstractDateField} with the given caption and
     * initial text contents.
     *
     * @param caption
     *            the caption {@code String} for the editor.
     * @param value
     *            the date/time value.
     * @param resolution
     *            initial resolution for the field, not {@code null}
     */
    public AbstractDateField(String caption, T value, R resolution) {
        this(caption, resolution);
        setValue(value);
    }

    /* Component basic features */

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        Locale locale = getLocale();
        getState().locale = locale == null ? null : locale.toString();
    }

    /**
     * Construct a date object from the individual field values received from
     * the client.
     *
     * @param resolutions
     *            map of time unit (resolution) name and value, the key is the
     *            resolution name e.g. "HOUR", "MINUTE", the value can be
     *            {@code null}
     * @param oldDate
     *            used as a fallback to get needed values if they are not
     *            defined in the specified {@code resolutions}
     *
     * @return the date object built from the specified resolutions
     * @since 8.2
     */
    protected T reconstructDateFromFields(Map<String, Integer> resolutions,
            T oldDate) {
        Map<R, Integer> calendarFields = new HashMap<>();

        for (R resolution : getResolutionsHigherOrEqualTo(getResolution())) {
            // Only handle what the client is allowed to send. The same
            // resolutions that are painted
            String resolutionName = resolution.name();

            Integer newValue = resolutions.get(resolutionName);
            if (newValue == null) {
                newValue = getDatePart(oldDate, resolution);
            }
            calendarFields.put(resolution, newValue);
        }
        return buildDate(calendarFields);
    }

    /**
     * Sets the start range for this component. If the value is set before this
     * date (taking the resolution into account), the component will not
     * validate. If {@code startDate} is set to {@code null}, any value before
     * {@code endDate} will be accepted by the range
     * <p>
     * Note: Negative, i.e. BC dates are not supported
     *
     * @param startDate
     *            - the allowed range's start date
     */
    public void setRangeStart(T startDate) {
        if (afterDate(startDate, convertFromDateString(getState().rangeEnd))) {
            throw new IllegalStateException(
                    "startDate cannot be later than endDate");
        }

        getState().rangeStart = convertToDateString(startDate);
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
        updateResolutions();
    }

    /**
     * Sets the end range for this component. If the value is set after this
     * date (taking the resolution into account), the component will not
     * validate. If {@code endDate} is set to {@code null}, any value after
     * {@code startDate} will be accepted by the range.
     *
     * @param endDate
     *            the allowed range's end date (inclusive, based on the current
     *            resolution)
     */
    public void setRangeEnd(T endDate) {
        String date = convertToDateString(endDate);
        if (afterDate(convertFromDateString(getState().rangeStart), endDate)) {
            throw new IllegalStateException(
                    "endDate cannot be earlier than startDate");
        }

        getState().rangeEnd = date;
    }

    /**
     * Returns the precise rangeStart used.
     *
     * @return the precise rangeStart used, may be {@code null}.
     */
    public T getRangeStart() {
        return convertFromDateString(getState(false).rangeStart);
    }

    /**
     * Parses string representaion of date range limit into date type
     *
     * @param temporalStr
     *            the string representation
     * @return parsed value
     * @see AbstractDateFieldState#rangeStart
     * @see AbstractDateFieldState#rangeEnd
     * @since 8.4
     */
    protected T convertFromDateString(String temporalStr) {
        if (temporalStr == null) {
            return null;
        }
        return toType(RANGE_FORMATTER.parse(temporalStr));
    }

    /**
     * Converts a temporal value into field-specific data type.
     *
     * @param temporalAccessor
     *            - source value
     * @return conversion result.
     * @since 8.4
     */
    protected abstract T toType(TemporalAccessor temporalAccessor);

    /**
     * Converts date range limit into string representation.
     *
     * @param temporal
     *            the value
     * @return textual representation
     * @see AbstractDateFieldState#rangeStart
     * @see AbstractDateFieldState#rangeEnd
     * @since 8.4
     */
    protected String convertToDateString(T temporal) {
        if (temporal == null) {
            return null;
        }
        return RANGE_FORMATTER.format(temporal);
    }

    /**
     * Checks if {@code value} is after {@code base} or not.
     *
     * @param value
     *            temporal value
     * @param base
     *            temporal value to compare to
     * @return {@code true} if {@code value} is after {@code base},
     *         {@code false} otherwise
     */
    protected boolean afterDate(T value, T base) {
        if (value == null || base == null) {
            return false;
        }
        return value.compareTo(base) > 0;
    }

    /**
     * Returns the precise rangeEnd used.
     *
     * @return the precise rangeEnd used, may be {@code null}.
     */
    public T getRangeEnd() {
        return convertFromDateString(getState(false).rangeEnd);
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
     *            the dateFormat to set, can be {@code null}
     *
     * @see com.vaadin.ui.AbstractComponent#setLocale(Locale))
     */
    public void setDateFormat(String dateFormat) {
        getState().format = dateFormat;
    }

    /**
     * Returns a format string used to format date value on client side or
     * {@code null} if default formatting from {@link Component#getLocale()} is
     * used.
     *
     * @return the dateFormat
     */
    public String getDateFormat() {
        return getState(false).format;
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

    private void updateResolutions() {
        final T currentDate = getValue();

        Map<String, Integer> resolutions = getState().resolutions;
        resolutions.clear();

        // Only paint variables for the resolution and up, e.g. Resolution DAY
        // paints DAY,MONTH,YEAR
        for (R resolution : getResolutionsHigherOrEqualTo(getResolution())) {
            String resolutionName = resolution.name();

            Integer value = getValuePart(currentDate, resolution);
            resolutions.put(resolutionName, value);

            Integer defaultValuePart = getValuePart(defaultValue, resolution);
            resolutions.put("default-" + resolutionName, defaultValuePart);
        }
        updateDiffstate("resolutions", Json.createObject());
    }

    private Integer getValuePart(T date, R resolution) {
        if (date == null) {
            return null;
        }
        return getDatePart(date, resolution);
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
        getState().lenient = lenient;
    }

    /**
     * Returns whether date/time interpretation is lenient.
     *
     * @see #setLenient(boolean)
     *
     * @return {@code true} if the interpretation mode of this calendar is
     *         lenient; {@code false} otherwise.
     */
    public boolean isLenient() {
        return getState(false).lenient;
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
     *            the default value, may be {@code null}
     * @since 8.1.2
     */
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        updateResolutions();
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
        currentErrorMessage = null;
        /*
         * First handle special case when the client side component have a date
         * string but value is null (e.g. unparsable date string typed in by the
         * user). No value changes should happen, but we need to do some
         * internal housekeeping.
         */
        if (value == null && !getState(false).parsable) {
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
        return getState(false).showISOWeekNumbers;
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
        getState().showISOWeekNumbers = showWeekNumbers;
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
     * @param parsingErrorMessage
     *            the default parsing error message
     *
     * @see #getParseErrorMessage()
     * @see #handleUnparsableDateString(String)
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
     * Formats date according to the components locale.
     *
     * @param value
     *            the date or {@code null}
     * @return textual representation of the date or empty string for
     *         {@code null}
     * @since 8.1.1
     */
    protected abstract String formatDate(T value);

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

        // Also set the internal dateString
        this.value = value;
        if (value == null) {
            value = getEmptyValue();
        }
        dateString = formatDate(value);
        // TODO move range check to internal validator?
        RangeValidator<T> validator = getRangeValidator();
        ValidationResult result = validator.apply(value,
                new ValueContext(this, this));

        if (result.isError()) {
            currentErrorMessage = getDateOutOfRangeMessage();
        }

        getState().parsable = currentErrorMessage == null;

        ErrorMessage errorMessage;
        if (currentErrorMessage == null) {
            errorMessage = null;
        } else {
            errorMessage = new UserError(currentErrorMessage);
        }
        setComponentError(errorMessage);

        updateResolutions();
    }

    /**
     * Returns a date integer value part for the given {@code date} for the
     * given {@code resolution}.
     *
     * @param date
     *            the given date, can be {@code null}
     * @param resolution
     *            the resolution to extract a value from the date by, not
     *            {@code null}
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

    @SuppressWarnings("unchecked")
    private Stream<R> getResolutions() {
        Type resolutionType = GenericTypeReflector.getTypeParameter(getClass(),
                AbstractDateField.class.getTypeParameters()[1]);
        if (resolutionType instanceof Class<?>) {
            Class<?> clazz = (Class<?>) resolutionType;
            return Stream.of(clazz.getEnumConstants())
                    .map(object -> (R) object);
        }
        throw new RuntimeException("Cannot detect resoluton type "
                + Optional.ofNullable(resolutionType).map(Type::getTypeName)
                        .orElse(null));
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

                // currentErrorMessage contains two type of messages, one is
                // DateOutOfRangeMessage and the other one is the ParseError
                if (currentErrorMessage != null) {
                    if (currentErrorMessage
                            .equals(getDateOutOfRangeMessage())) {
                        // if the currentErrorMessage is DateOutOfRangeMessage,
                        // then need to double check whether the error message
                        // has been updated, that is because of #11276.
                        ValidationResult validationResult = getRangeValidator()
                                .apply(value, context);
                        if (validationResult.isError()) {
                            return ValidationResult.error(currentErrorMessage);
                        }
                    } else {
                        // if the current Error is parsing error, pass it to the
                        // ValidationResult
                        return ValidationResult.error(currentErrorMessage);
                    }
                }

                // Pass to range validator.
                return getRangeValidator().apply(value, context);
            }
        };
    }

    /**
     * <p>
     * Sets a custom style name for the given date's calendar cell. Setting the
     * style name will override any previous style names that have been set for
     * that date, but can contain several actual style names separated by space.
     * Setting the custom style name {@code null} will only remove the previous
     * custom style name.
     * </p>
     * <p>
     * This logic is entirely separate from {@link #setStyleName(String)}
     * </p>
     * <p>
     * Usage examples: <br>
     * {@code setDateStyle(LocalDate.now(), "teststyle");} <br>
     * {@code setDateStyle(LocalDate.now(), "teststyle1 teststyle2");}
     * </p>
     *
     * @param date
     *            which date cell to modify, not {@code null}
     * @param styleName
     *            the custom style name(s) for given date, {@code null} to clear
     *            custom style name(s)
     *
     * @since 8.3
     */
    public void setDateStyle(LocalDate date, String styleName) {
        Objects.requireNonNull(date, "Date cannot be null");
        if (styleName != null) {
            getState().dateStyles.put(date.toString(), styleName);
        } else {
            getState().dateStyles.remove(date.toString());
        }
    }

    /**
     * Returns the custom style name that corresponds with the given date's
     * calendar cell.
     *
     * @param date
     *            which date cell's custom style name(s) to return, not
     *            {@code null}
     * @return the corresponding style name(s), if any, {@code null} otherwise
     *
     * @see #setDateStyle(LocalDate, String)
     * @since 8.3
     */
    public String getDateStyle(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");

        return getState(false).dateStyles.get(date.toString());
    }

    /**
     * Returns a map from dates to custom style names in each date's calendar
     * cell.
     *
     * @return unmodifiable map from dates to custom style names in each date's
     *         calendar cell
     *
     * @see #setDateStyle(LocalDate, String)
     * @since 8.3
     */
    public Map<LocalDate, String> getDateStyles() {
        HashMap<LocalDate, String> hashMap = new HashMap<>();
        for (Entry<String, String> entry : getState(false).dateStyles
                .entrySet()) {
            hashMap.put(LocalDate.parse(entry.getKey()), entry.getValue());
        }
        return Collections.unmodifiableMap(hashMap);
    }

    /**
     * Sets the assistive label for a calendar navigation element. This sets the
     * {@code aria-label} attribute for the element which is used by screen
     * reading software.
     *
     * @param element
     *            the element for which to set the label. Not {@code null}.
     * @param label
     *            the assistive label to set
     * @since 8.4
     */
    public void setAssistiveLabel(AccessibleElement element, String label) {
        Objects.requireNonNull(element, "Element cannot be null");
        getState().assistiveLabels.put(element, label);
    }

    /**
     * Gets the assistive label of a calendar navigation element.
     *
     * @param element
     *            the element of which to get the assistive label
     * @since 8.4
     */
    public void getAssistiveLabel(AccessibleElement element) {
        getState(false).assistiveLabels.get(element);
    }
}
