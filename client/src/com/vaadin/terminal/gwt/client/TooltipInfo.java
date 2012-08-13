/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

public class TooltipInfo {

    private String title;

    private String errorMessageHtml;

    public TooltipInfo() {
    }

    public TooltipInfo(String tooltip) {
        setTitle(tooltip);
    }

    public TooltipInfo(String tooltip, String errorMessage) {
        setTitle(tooltip);
        setErrorMessage(errorMessage);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getErrorMessage() {
        return errorMessageHtml;
    }

    public void setErrorMessage(String errorMessage) {
        errorMessageHtml = errorMessage;
    }

    /**
     * Checks is a message has been defined for the tooltip.
     * 
     * @return true if title or error message is present, false if both are
     *         empty
     */
    public boolean hasMessage() {
        return (title != null && !title.isEmpty())
                || (errorMessageHtml != null && !errorMessageHtml.isEmpty());
    }

    public boolean equals(TooltipInfo other) {
        return (other != null && other.title == title && other.errorMessageHtml == errorMessageHtml);
    }
}
