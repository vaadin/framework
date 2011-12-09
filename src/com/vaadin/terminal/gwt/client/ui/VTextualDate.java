/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.LocaleNotLoadedException;
import com.vaadin.terminal.gwt.client.LocaleService;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

public class VTextualDate extends VDateField implements Paintable, Field,
        ChangeHandler, ContainerResizedListener, Focusable, SubPartAware {

    private static final String PARSE_ERROR_CLASSNAME = CLASSNAME
            + "-parseerror";

    private final TextBox text;

    private String formatStr;

    private String width;

    private boolean needLayout;

    protected int fieldExtraWidth = -1;

    private boolean lenient;

    private static final String CLASSNAME_PROMPT = "prompt";
    private static final String ATTR_INPUTPROMPT = "prompt";
    private String inputPrompt = "";
    private boolean prompting = false;

    public VTextualDate() {

        super();
        text = new TextBox();
        // use normal textfield styles as a basis
        text.setStyleName(VTextField.CLASSNAME);
        // add datefield spesific style name also
        text.addStyleName(CLASSNAME + "-textfield");
        text.addChangeHandler(this);
        text.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                text.addStyleName(VTextField.CLASSNAME + "-"
                        + VTextField.CLASSNAME_FOCUS);
                if (prompting) {
                    text.setText("");
                    setPrompting(false);
                }
                if (getClient() != null
                        && getClient().hasEventListeners(VTextualDate.this,
                                EventId.FOCUS)) {
                    getClient()
                            .updateVariable(getId(), EventId.FOCUS, "", true);
                }
            }
        });
        text.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                text.removeStyleName(VTextField.CLASSNAME + "-"
                        + VTextField.CLASSNAME_FOCUS);
                String value = getText();
                setPrompting(inputPrompt != null
                        && (value == null || "".equals(value)));
                if (prompting) {
                    text.setText(readonly ? "" : inputPrompt);
                }
                if (getClient() != null
                        && getClient().hasEventListeners(VTextualDate.this,
                                EventId.BLUR)) {
                    getClient().updateVariable(getId(), EventId.BLUR, "", true);
                }
            }
        });
        add(text);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        int origRes = currentResolution;
        String oldLocale = currentLocale;
        super.updateFromUIDL(uidl, client);
        if (origRes != currentResolution || oldLocale != currentLocale) {
            // force recreating format string
            formatStr = null;
        }
        if (uidl.hasAttribute("format")) {
            formatStr = uidl.getStringAttribute("format");
        }

        inputPrompt = uidl.getStringAttribute(ATTR_INPUTPROMPT);

        lenient = !uidl.getBooleanAttribute("strict");

        buildDate();
        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            text.setTabIndex(uidl.getIntAttribute("tabindex"));
        }

        if (readonly) {
            text.addStyleDependentName("readonly");
        } else {
            text.removeStyleDependentName("readonly");
        }

    }

    protected String getFormatString() {
        if (formatStr == null) {
            if (currentResolution == RESOLUTION_YEAR) {
                formatStr = "yyyy"; // force full year
            } else {

                try {
                    String frmString = LocaleService
                            .getDateFormat(currentLocale);
                    frmString = cleanFormat(frmString);
                    // String delim = LocaleService
                    // .getClockDelimiter(currentLocale);

                    if (currentResolution >= RESOLUTION_HOUR) {
                        if (dts.isTwelveHourClock()) {
                            frmString += " hh";
                        } else {
                            frmString += " HH";
                        }
                        if (currentResolution >= RESOLUTION_MIN) {
                            frmString += ":mm";
                            if (currentResolution >= RESOLUTION_SEC) {
                                frmString += ":ss";
                                if (currentResolution >= RESOLUTION_MSEC) {
                                    frmString += ".SSS";
                                }
                            }
                        }
                        if (dts.isTwelveHourClock()) {
                            frmString += " aaa";
                        }

                    }

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

    /**
     * Updates the text field according to the current date (provided by
     * {@link #getDate()}). Takes care of updating text, enabling and disabling
     * the field, setting/removing readonly status and updating readonly styles.
     * 
     * TODO: Split part of this into a method that only updates the text as this
     * is what usually is needed except for updateFromUIDL.
     */
    protected void buildDate() {
        removeStyleName(PARSE_ERROR_CLASSNAME);
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
        } else {
            text.removeStyleName("v-readonly");
        }

    }

    protected void setPrompting(boolean prompting) {
        this.prompting = prompting;
        if (prompting) {
            addStyleDependentName(CLASSNAME_PROMPT);
        } else {
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
    }

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
                    text.setValue(
                            getDateTimeService().formatDate(getDate(),
                                    getFormatString()), false);
                }

                // remove possibly added invalid value indication
                removeStyleName(PARSE_ERROR_CLASSNAME);
            } catch (final Exception e) {
                VConsole.log(e);

                addStyleName(PARSE_ERROR_CLASSNAME);
                // this is a hack that may eventually be removed
                getClient().updateVariable(getId(), "lastInvalidDateString",
                        text.getText(), false);
                setDate(null);
            }
        } else {
            setDate(null);
            // remove possibly added invalid value indication
            removeStyleName(PARSE_ERROR_CLASSNAME);
        }
        // always send the date string
        getClient()
                .updateVariable(getId(), "dateString", text.getText(), false);

        // Update variables
        // (only the smallest defining resolution needs to be
        // immediate)
        Date currentDate = getDate();
        getClient().updateVariable(getId(), "year",
                currentDate != null ? currentDate.getYear() + 1900 : -1,
                currentResolution == VDateField.RESOLUTION_YEAR && immediate);
        if (currentResolution >= VDateField.RESOLUTION_MONTH) {
            getClient().updateVariable(
                    getId(),
                    "month",
                    currentDate != null ? currentDate.getMonth() + 1 : -1,
                    currentResolution == VDateField.RESOLUTION_MONTH
                            && immediate);
        }
        if (currentResolution >= VDateField.RESOLUTION_DAY) {
            getClient()
                    .updateVariable(
                            getId(),
                            "day",
                            currentDate != null ? currentDate.getDate() : -1,
                            currentResolution == VDateField.RESOLUTION_DAY
                                    && immediate);
        }
        if (currentResolution >= VDateField.RESOLUTION_HOUR) {
            getClient().updateVariable(
                    getId(),
                    "hour",
                    currentDate != null ? currentDate.getHours() : -1,
                    currentResolution == VDateField.RESOLUTION_HOUR
                            && immediate);
        }
        if (currentResolution >= VDateField.RESOLUTION_MIN) {
            getClient()
                    .updateVariable(
                            getId(),
                            "min",
                            currentDate != null ? currentDate.getMinutes() : -1,
                            currentResolution == VDateField.RESOLUTION_MIN
                                    && immediate);
        }
        if (currentResolution >= VDateField.RESOLUTION_SEC) {
            getClient()
                    .updateVariable(
                            getId(),
                            "sec",
                            currentDate != null ? currentDate.getSeconds() : -1,
                            currentResolution == VDateField.RESOLUTION_SEC
                                    && immediate);
        }
        if (currentResolution == VDateField.RESOLUTION_MSEC) {
            getClient().updateVariable(getId(), "msec",
                    currentDate != null ? getMilliseconds() : -1, immediate);
        }

    }

    private String cleanFormat(String format) {
        // Remove unnecessary d & M if resolution is too low
        if (currentResolution < VDateField.RESOLUTION_DAY) {
            format = format.replaceAll("d", "");
        }
        if (currentResolution < VDateField.RESOLUTION_MONTH) {
            format = format.replaceAll("M", "");
        }

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
    public void setWidth(String newWidth) {
        if (!"".equals(newWidth) && (width == null || !newWidth.equals(width))) {
            needLayout = true;
            width = newWidth;
            super.setWidth(width);
            iLayout();
            if (newWidth.indexOf("%") < 0) {
                needLayout = false;
            }
        } else {
            if ("".equals(newWidth) && width != null && !"".equals(width)) {
                super.setWidth("");
                needLayout = true;
                iLayout();
                needLayout = false;
                width = null;
            }
        }
    }

    /**
     * Returns pixels in x-axis reserved for other than textfield content.
     * 
     * @return extra width in pixels
     */
    protected int getFieldExtraWidth() {
        if (fieldExtraWidth < 0) {
            text.setWidth("0");
            fieldExtraWidth = text.getOffsetWidth();
        }
        return fieldExtraWidth;
    }

    public void updateWidth() {
        needLayout = true;
        fieldExtraWidth = -1;
        iLayout();
    }

    public void iLayout() {
        if (needLayout) {
            int textFieldWidth = getOffsetWidth() - getFieldExtraWidth();
            if (textFieldWidth < 0) {
                // Field can never be smaller than 0 (causes exception in IE)
                textFieldWidth = 0;
            }
            text.setWidth(textFieldWidth + "px");
        }
    }

    public void focus() {
        text.setFocus(true);
    }

    protected String getText() {
        if (prompting) {
            return "";
        }
        return text.getText();
    }

    protected void setText(String text) {
        if (inputPrompt != null && (text == null || "".equals(text))) {
            text = readonly ? "" : inputPrompt;
            setPrompting(true);
        } else {
            setPrompting(false);
        }

        this.text.setText(text);
    }

    private final String TEXTFIELD_ID = "field";

    public Element getSubPartElement(String subPart) {
        if (subPart.equals(TEXTFIELD_ID)) {
            return text.getElement();
        }

        return null;
    }

    public String getSubPartName(Element subElement) {
        if (text.getElement().isOrHasChild(subElement)) {
            return TEXTFIELD_ID;
        }

        return null;
    }

}
