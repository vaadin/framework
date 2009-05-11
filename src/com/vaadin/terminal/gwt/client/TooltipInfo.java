/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

public class TooltipInfo {

    private String title;

    private UIDL errorUidl;

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
