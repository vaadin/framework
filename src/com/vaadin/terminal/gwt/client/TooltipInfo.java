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

}
