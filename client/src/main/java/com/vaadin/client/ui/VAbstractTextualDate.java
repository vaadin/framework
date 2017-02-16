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

package com.vaadin.client.ui;

import java.util.Date;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Focusable;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.LocaleService;
import com.vaadin.client.VConsole;
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
        extends VDateField<R> implements Field, ChangeHandler, Focusable,
        SubPartAware, HandlesAriaCaption, HandlesAriaInvalid,
        HandlesAriaRequired, KeyDownHandler {

    private static final String PARSE_ERROR_CLASSNAME = "-parseerror";

    /** For internal use only. May be removed or replaced in the future. */
    public final TextBox text;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean lenient;

    private final String TEXTFIELD_ID = "field";

    /** For internal use only. May be removed or replaced in the future. */
    public String formatStr;

    public VAbstractTextualDate(R resoluton) {
        super(resoluton);
        text = new TextBox();
        text.addChangeHandler(this);
        text.addFocusHandler(
                event -> fireBlurFocusEvent(event, true, EventId.FOCUS));
        text.addBlurHandler(
                event -> fireBlurFocusEvent(event, false, EventId.BLUR));
        if (BrowserInfo.get().isIE()) {
            addDomHandler(this, KeyDownEvent.getType());
        }
        add(text);
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
    protected String getFormatString() {
        if (formatStr == null) {
            if (isYear(getCurrentResolution())) {
                formatStr = "yyyy"; // force full year
            } else {

                try {
                    String frmString = LocaleService
                            .getDateFormat(currentLocale);
                    frmString = cleanFormat(frmString);

                    formatStr = frmString;
                } catch (LocaleNotLoadedException e) {
                    // TODO should die instead? Can the component survive
                    // without format string?
                    VConsole.error(e);
                }
            }
        }
        return formatStr;
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
        if (currentDate != null) {
            dateText = getDateTimeService().formatDate(currentDate,
                    getFormatString());
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        text.setEnabled(enabled);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onChange(ChangeEvent event) {
        if (!text.getText().equals("")) {
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
                            getFormatString()), false);
                }

                // remove possibly added invalid value indication
                removeStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
            } catch (final Exception e) {
                VConsole.log(e);

                addStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
                // this is a hack that may eventually be removed
                getClient().updateVariable(getId(), "lastInvalidDateString",
                        text.getText(), false);
                setDate(null);
            }
        } else {
            setDate(null);
            // remove possibly added invalid value indication
            removeStyleName(getStylePrimaryName() + PARSE_ERROR_CLASSNAME);
        }
        // always send the date string
        getClient().updateVariable(getId(), "dateString", text.getText(),
                false);

        updateDateVariables();
    }

    /**
     * Updates variables to send a response to the server.
     * <p>
     * The method can be overridden by subclasses to provide a custom logic for
     * date variables to avoid overriding the {@link #onChange(ChangeEvent)}
     * method.
     */
    protected void updateDateVariables() {
        // Update variables
        // (only the smallest defining resolution needs to be
        // immediate)
        Date currentDate = getDate();
        getClient().updateVariable(getId(),
                getResolutionVariable(getResolutions().filter(this::isYear)
                        .findFirst().get()),
                currentDate != null ? currentDate.getYear() + 1900 : -1,
                isYear(getCurrentResolution()));
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

    private void fireBlurFocusEvent(DomEvent<?> event,
            boolean addFocusStyleName, String eventId) {
        String styleName = VTextField.CLASSNAME + "-"
                + VTextField.CLASSNAME_FOCUS;
        if (addFocusStyleName) {
            text.addStyleName(styleName);
        } else {
            text.removeStyleName(styleName);
        }
        if (getClient() != null && getClient()
                .hasEventListeners(VAbstractTextualDate.this, eventId)) {
            getClient().updateVariable(getId(), eventId, "", true);
        }

        // Needed for tooltip event handling
        fireEvent(event);
    }
}
