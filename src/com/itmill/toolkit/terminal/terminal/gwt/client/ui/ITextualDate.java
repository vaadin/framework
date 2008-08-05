/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Focusable;
import com.itmill.toolkit.terminal.gwt.client.LocaleNotLoadedException;
import com.itmill.toolkit.terminal.gwt.client.LocaleService;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ITextualDate extends IDateField implements Paintable, Field,
        ChangeListener, ContainerResizedListener, Focusable {

    private static final String PARSE_ERROR_CLASSNAME = CLASSNAME
            + "-parseerror";

    private final ITextField text;

    private String formatStr;

    private String width;

    private boolean needLayout;

    protected int fieldExtraWidth = -1;

    public ITextualDate() {
        super();
        text = new ITextField();
        text.addChangeListener(this);
        add(text);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        int origRes = currentResolution;
        super.updateFromUIDL(uidl, client);
        if (origRes != currentResolution) {
            // force recreating format string
            formatStr = null;
        }
        buildDate();
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
                    String delim = LocaleService
                            .getClockDelimiter(currentLocale);

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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return formatStr;
    }

    /**
     * 
     */
    protected void buildDate() {
        removeStyleName(PARSE_ERROR_CLASSNAME);
        // Create the initial text for the textfield
        String dateText;
        if (date != null) {
            dateText = DateTimeFormat.getFormat(getFormatString()).format(date);
        } else {
            dateText = "";
        }

        text.setText(dateText);
        text.setEnabled(enabled && !readonly);

        if (readonly) {
            text.addStyleName("i-readonly");
        } else {
            text.removeStyleName("i-readonly");
        }

    }

    public void onChange(Widget sender) {
        if (sender == text) {
            if (!text.getText().equals("")) {
                try {
                    date = DateTimeFormat.getFormat(getFormatString()).parse(
                            text.getText());
                    // remove possibly added invalid value indication
                    removeStyleName(PARSE_ERROR_CLASSNAME);
                } catch (final Exception e) {
                    ApplicationConnection.getConsole().log(e.getMessage());
                    addStyleName(PARSE_ERROR_CLASSNAME);
                    client.updateVariable(id, "lastInvalidDateString", text
                            .getText(), false);
                    date = null;
                }
            } else {
                date = null;
                // remove possibly added invalid value indication
                removeStyleName(PARSE_ERROR_CLASSNAME);
            }

            if (date != null) {
                showingDate = new Date(date.getTime());
            }

            // Update variables
            // (only the smallest defining resolution needs to be
            // immediate)
            client.updateVariable(id, "year",
                    date != null ? date.getYear() + 1900 : -1,
                    currentResolution == IDateField.RESOLUTION_YEAR
                            && immediate);
            if (currentResolution >= IDateField.RESOLUTION_MONTH) {
                client.updateVariable(id, "month", date != null ? date
                        .getMonth() + 1 : -1,
                        currentResolution == IDateField.RESOLUTION_MONTH
                                && immediate);
            }
            if (currentResolution >= IDateField.RESOLUTION_DAY) {
                client.updateVariable(id, "day", date != null ? date.getDate()
                        : -1, currentResolution == IDateField.RESOLUTION_DAY
                        && immediate);
            }
            if (currentResolution >= IDateField.RESOLUTION_HOUR) {
                client.updateVariable(id, "hour", date != null ? date
                        .getHours() : -1,
                        currentResolution == IDateField.RESOLUTION_HOUR
                                && immediate);
            }
            if (currentResolution >= IDateField.RESOLUTION_MIN) {
                client.updateVariable(id, "min", date != null ? date
                        .getMinutes() : -1,
                        currentResolution == IDateField.RESOLUTION_MIN
                                && immediate);
            }
            if (currentResolution >= IDateField.RESOLUTION_SEC) {
                client.updateVariable(id, "sec", date != null ? date
                        .getSeconds() : -1,
                        currentResolution == IDateField.RESOLUTION_SEC
                                && immediate);
            }
            if (currentResolution == IDateField.RESOLUTION_MSEC) {
                client.updateVariable(id, "msec",
                        date != null ? getMilliseconds() : -1, immediate);
            }

        }
    }

    private String cleanFormat(String format) {
        // Remove unnecessary d & M if resolution is too low
        if (currentResolution < IDateField.RESOLUTION_DAY) {
            format = format.replaceAll("d", "");
        }
        if (currentResolution < IDateField.RESOLUTION_MONTH) {
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

    public void setWidth(String newWidth) {
        if (!"".equals(newWidth) && (width == null || !newWidth.equals(width))) {
            if (Util.isIE6()) {
                text.setColumns(1); // in IE6 cols ~ min-width
            }
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
            text.setWidth("0px");
            fieldExtraWidth = text.getOffsetWidth();
        }
        return fieldExtraWidth;
    }

    public void iLayout() {
        if (needLayout) {
            text.setWidth((getOffsetWidth() - getFieldExtraWidth()) + "px");
        }
    }

    public void focus() {
        text.setFocus(true);
    }
}
