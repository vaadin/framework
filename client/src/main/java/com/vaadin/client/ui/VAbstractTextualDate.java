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

package com.vaadin.client.ui;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Focusable;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.LocaleService;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.aria.HandlesAriaCaption;
import com.vaadin.client.ui.aria.HandlesAriaInvalid;
import com.vaadin.client.ui.aria.HandlesAriaRequired;
import com.vaadin.shared.EventId;

/**
 * Abstract textual date field base implementation. Provides a text box as an
 * editor for a date. The class is parameterized by the date resolution
 * enumeration type.
 *
 * @author Vaadin Ltd
 *
 * @param <R>
 *            the resolution type which this field is based on (day, month, ...)
 * @since 8.0
 */
public abstract class VAbstractTextualDate<R extends Enum<R>>
        extends VDateField<R>
        implements ChangeHandler, Focusable, SubPartAware, HandlesAriaCaption,
        HandlesAriaInvalid, HandlesAriaRequired, KeyDownHandler {

    private static final String PARSE_ERROR_CLASSNAME = "-parseerror";
    private static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd";

    /** For internal use only. May be removed or replaced in the future. */
    public final TextBox text;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean lenient;

    private static final String TEXTFIELD_ID = "field";

    /** For internal use only. May be removed or replaced in the future. */
    private String formatStr;

    /** For internal use only. May be removed or replaced in the future. */
    private TimeZone timeZone;

    /**
     * Specifies whether the group of components has focus or not.
     */
    private boolean groupFocus;

    public VAbstractTextualDate(R resoluton) {
        super(resoluton);
        text = new TextBox();
        text.addChangeHandler(this);
        text.addFocusHandler(event -> fireBlurFocusEvent(event, true));
        text.addBlurHandler(event -> fireBlurFocusEvent(event, false));
        if (BrowserInfo.get().isIE()) {
            addDomHandler(this, KeyDownEvent.getType());
        }
        // Stop the browser from showing its own suggestion popup.
        WidgetUtil.disableBrowserAutocomplete(text);
        add(text);
        publishJSHelpers(getElement());
    }

    /**
     * Updates style names for the widget (and its children).
     */
    protected void updateStyleNames() {
        if (text != null) {
            text.setStyleName(VTextField.CLASSNAME);
            text.addStyleName(getStylePrimaryName() + "-textfield");
        }
    }

    /**
     * Gets the date format string for the current locale.
     *
     * @return the format string
     */
    public String getFormatString() {
        if (formatStr == null) {
            setFormatString(createFormatString());
        }
        return formatStr;
    }

    /**
     * Create a format string suitable for the widget in its current state.
     *
     * @return a date format string to use when formatting and parsing the text
     *         in the input field
     * @since 8.1
     */
    protected String createFormatString() {
        if (isYear(getCurrentResolution())) {
            return "yyyy"; // force full year
        }
        try {
            String frmString = LocaleService.getDateFormat(currentLocale);
            return cleanFormat(frmString);
        } catch (LocaleNotLoadedException e) {
            // TODO should die instead? Can the component survive
            // without format string?
            getLogger().log(Level.SEVERE,
                    e.getMessage() == null ? "" : e.getMessage(), e);
            return null;
        }
    }

    /**
     * Sets the date format string to use for the text field.
     *
     * @param formatString
     *            the format string to use, or {@code null} to force re-creating
     *            the format string from the locale the next time it is needed
     * @since 8.1
     */
    public void setFormatString(String formatString) {
        this.formatStr = formatString;
    }

    @Override
    public void bindAriaCaption(
            com.google.gwt.user.client.Element captionElement) {
        AriaHelper.bindCaption(text, captionElement);
    }

    @Override
    public void setAriaRequired(boolean required) {
        AriaHelper.handleInputRequired(text, required);
    }

    @Override
    public void setAriaInvalid(boolean invalid) {
        AriaHelper.handleInputInvalid(text, invalid);
    }

    /**
     * Updates the text field according to the current date (provided by
     * {@link #getDate()}). Takes care of updating text, enabling and disabling
     * the field, setting/removing readonly status and updating readonly styles.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * TODO: Split part of this into a method that only updates the text as this
     * is what usually is needed except for updateFromUIDL.
     */
    public void buildDate() {
        removeStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
        // Create the initial text for the textfield
        String dateText;
        Date currentDate = getDate();
        // Always call this to ensure the format ends up in the element
        String formatString = getFormatString();
        if (currentDate != null) {
            dateText = getDateTimeService().formatDate(currentDate,
                    formatString, timeZone);
        } else {
            dateText = "";
        }

        setText(dateText);
        text.setEnabled(enabled);
        text.setReadOnly(readonly);

        if (readonly) {
            text.addStyleName("v-readonly");
            Roles.getTextboxRole().setAriaReadonlyProperty(text.getElement(),
                    true);
        } else {
            text.removeStyleName("v-readonly");
            Roles.getTextboxRole()
                    .removeAriaReadonlyProperty(text.getElement());
        }
    }

    /**
     * Sets the time zone for the field.
     *
     * @param timeZone
     *            the new time zone to use
     * @since 8.2
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        text.setEnabled(enabled);
    }

    @Override
    public void onChange(ChangeEvent event) {
        updateBufferedValues();
        sendBufferedValues();
    }

    @Override
    public void updateBufferedValues() {
        updateDate();
        bufferedDateString = text.getText();
        updateBufferedResolutions();
    }

    private void updateDate() {
        if (!text.getText().isEmpty()) {
            try {
                String enteredDate = text.getText();

                setDate(getDateTimeService().parseDate(enteredDate,
                        getFormatString(), lenient));

                if (lenient) {
                    // If date value was leniently parsed, normalize text
                    // presentation.
                    // FIXME: Add a description/example here of when this is
                    // needed
                    text.setValue(getDateTimeService().formatDate(getDate(),
                            getFormatString(), timeZone), false);
                }

                // remove possibly added invalid value indication
                removeStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
            } catch (final Exception e) {
                getLogger().log(Level.INFO,
                        e.getMessage() == null ? "" : e.getMessage(), e);

                addStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
                setDate(null);
            }
        } else {
            setDate(null);
            // remove possibly added invalid value indication
            removeStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
        }
    }

    /**
     * Updates the {@link VDateField#bufferedResolutions bufferedResolutions},
     * then {@link #sendBufferedValues() sends} the values to the server.
     *
     * @since 8.2
     * @deprecated Use {@link #updateBufferedResolutions()} and
     *             {@link #sendBufferedValues()} instead.
     */
    @Deprecated
    protected final void updateAndSendBufferedValues() {
        updateBufferedResolutions();
        sendBufferedValues();
    }

    /**
     * Updates {@link VDateField#bufferedResolutions bufferedResolutions} before
     * sending a response to the server.
     * <p>
     * The method can be overridden by subclasses to provide a custom logic for
     * date variables to avoid overriding the {@link #onChange(ChangeEvent)}
     * method.
     *
     * <p>
     * Note that this method should not send the buffered values. For that, use
     * {@link #sendBufferedValues()}.
     *
     * @since 8.2
     */
    protected void updateBufferedResolutions() {
        Date currentDate = getDate();
        if (currentDate != null) {
            bufferedResolutions.put(
                    getResolutions().filter(this::isYear).findFirst().get(),
                    currentDate.getYear() + 1900);
        }
    }

    /**
     * Clean date format string to make it suitable for
     * {@link #getFormatString()}.
     *
     * @see #getFormatString()
     *
     * @param format
     *            date format string
     * @return cleaned up string
     */
    protected String cleanFormat(String format) {
        // Remove unsupported patterns
        // TODO support for 'G', era designator (used at least in Japan)
        format = format.replaceAll("[GzZwWkK]", "");

        // Remove extra delimiters ('/' and '.')
        while (format.startsWith("/") || format.startsWith(".")
                || format.startsWith("-")) {
            format = format.substring(1);
        }
        while (format.endsWith("/") || format.endsWith(".")
                || format.endsWith("-")) {
            format = format.substring(0, format.length() - 1);
        }

        // Remove duplicate delimiters
        format = format.replaceAll("//", "/");
        format = format.replaceAll("\\.\\.", ".");
        format = format.replaceAll("--", "-");

        return format.trim();
    }

    @Override
    public void focus() {
        text.setFocus(true);
    }

    /**
     * Sets the placeholder for this textual date input.
     *
     * @param placeholder
     *            the placeholder to set, or {@code null} to clear
     */
    public void setPlaceholder(String placeholder) {
        if (placeholder != null) {
            text.getElement().setAttribute("placeholder", placeholder);
        } else {
            text.getElement().removeAttribute("placeholder");
        }
    }

    /**
     * Gets the set placeholder this textual date input, or an empty string if
     * none is set.
     *
     * @return the placeholder or an empty string if none set
     */
    public String getPlaceHolder() {
        return text.getElement().getAttribute("placeholder");
    }

    protected String getText() {
        return text.getText();
    }

    protected void setText(String text) {
        this.text.setText(text);
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        if (subPart.equals(TEXTFIELD_ID)) {
            return text.getElement();
        }

        return null;
    }

    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
        if (text.getElement().isOrHasChild(subElement)) {
            return TEXTFIELD_ID;
        }

        return null;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (BrowserInfo.get().isIE()
                && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            // IE does not send change events when pressing enter in a text
            // input so we handle it using a key listener instead
            onChange(null);
        }
    }

    private void fireBlurFocusEvent(DomEvent<?> event, boolean focus) {
        String styleName = VTextField.CLASSNAME + "-"
                + VTextField.CLASSNAME_FOCUS;
        if (focus) {
            text.addStyleName(styleName);
        } else {
            text.removeStyleName(styleName);
        }

        Scheduler.get().scheduleDeferred(() -> checkGroupFocus(focus));

        // Needed for tooltip event handling
        fireEvent(event);
    }

    /**
     * Checks if the group focus has changed, and sends to the server if needed.
     *
     * @param textFocus
     *            the focus of the {@link #text}
     * @since 8.3
     */
    protected void checkGroupFocus(boolean textFocus) {
        boolean newGroupFocus = textFocus | hasChildFocus();
        if (getClient() != null
                && connector.hasEventListener(
                        textFocus ? EventId.FOCUS : EventId.BLUR)
                && groupFocus != newGroupFocus) {

            if (newGroupFocus) {
                rpc.focus();
            } else {
                rpc.blur();
            }
            sendBufferedValues();
            groupFocus = newGroupFocus;
        }
    }

    /**
     * Returns whether any of the child components has focus.
     *
     * @return {@code true} if any of the child component has focus,
     *         {@code false} otherwise
     * @since 8.3
     */
    protected boolean hasChildFocus() {
        return false;
    }

    /**
     * Publish methods/properties on the element to be used from JavaScript.
     *
     * @since 8.1
     */
    private native void publishJSHelpers(Element root)
    /*-{
        var self = this;
        root.setISOValue = $entry(function (value) {
           self.@VAbstractTextualDate::setISODate(*)(value);
        });
        root.getISOValue = $entry(function () {
           return self.@VAbstractTextualDate::getISODate()();
        });
    }-*/;

    /**
     * Sets the value of the date field as a locale independent ISO date
     * (yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd depending on whether this is a date
     * field or a date and time field).
     *
     * @param isoDate
     *            the date to set in ISO8601 format, or null to clear the date
     *            value
     * @since 8.1
     */
    public void setISODate(String isoDate) {
        Date date = null;
        if (isoDate != null) {
            date = getIsoFormatter().parse(isoDate);
        }
        setDate(date);
        updateBufferedResolutions();
        sendBufferedValues();
    }

    /**
     * Gets the value of the date field as a locale independent ISO date
     * (yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd depending on whether this is a date
     * field or a date and time field).
     *
     * @return the current date in ISO8601 format, or null if no date is set
     *
     * @since 8.1
     */
    public String getISODate() {
        Date date = getDate();
        if (date == null) {
            return null;
        }
        return getIsoFormatter().format(date);
    }

    private DateTimeFormat getIsoFormatter() {
        if (supportsTime()) {
            return DateTimeFormat.getFormat(ISO_DATE_TIME_PATTERN);
        }
        return DateTimeFormat.getFormat(ISO_DATE_PATTERN);
    }

    private static Logger getLogger() {
        return Logger.getLogger(VAbstractTextualDate.class.getName());
    }

}
