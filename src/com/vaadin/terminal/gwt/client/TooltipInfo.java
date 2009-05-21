/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

public class TooltipInfo {

    private String title;

    private UIDL errorUidl;

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

    public UIDL getErrorUidl() {
        return errorUidl;
    }

    public void setErrorUidl(UIDL errorUidl) {
        this.errorUidl = errorUidl;
    }

}
