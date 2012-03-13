/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

public class URLReference {

    private String URL;

    /**
     * Returns the URL that this object refers to.
     * <p>
     * Note that the URL can use special protocols like theme://
     * 
     * @return The URL for this reference or null if unknown.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Sets the URL that this object refers to
     * 
     * @param URL
     */
    public void setURL(String URL) {
        this.URL = URL;
    }
}