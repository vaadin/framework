/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IEmbedded extends HTML implements Paintable {

    private String heigth;
    private String width;
    private Element browserElement;

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        boolean clearBrowserElement = true;

        if (uidl.hasAttribute("type")) {
            final String type = uidl.getStringAttribute("type");
            if (type.equals("image")) {
                setHTML("<img src=\"" + getSrc(uidl, client) + "\"/>");
            } else if (type.equals("browser")) {
                if (browserElement == null) {
                    setHTML("<iframe width=\"100%\" height=\"100%\" frameborder=\"0\" src=\""
                            + getSrc(uidl, client) + "\"></iframe>");
                    browserElement = DOM.getFirstChild(getElement());
                } else {
                    DOM.setElementAttribute(browserElement, "src", getSrc(uidl,
                            client));
                }
                clearBrowserElement = false;
            } else {
                ApplicationConnection.getConsole().log(
                        "Unknown Embedded type '" + type + "'");
            }
        } else if (uidl.hasAttribute("mimetype")) {
            final String mime = uidl.getStringAttribute("mimetype");
            if (mime.equals("application/x-shockwave-flash")) {
                setHTML("<object width=\"" + width + "\" height=\"" + heigth
                        + "\"><param name=\"movie\" value=\""
                        + getSrc(uidl, client) + "\"><embed src=\""
                        + getSrc(uidl, client) + "\" width=\"" + width
                        + "\" height=\"" + heigth + "\"></embed></object>");
            } else {
                ApplicationConnection.getConsole().log(
                        "Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            ApplicationConnection.getConsole().log(
                    "Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            browserElement = null;
        }

    }

    /**
     * Helper to return translated src-attribute from embedded's UIDL
     * 
     * @param uidl
     * @param client
     * @return
     */
    private String getSrc(UIDL uidl, ApplicationConnection client) {
        return client.translateToolkitUri(uidl.getStringAttribute("src"));
    }

    public void setWidth(String width) {
        if (width == null || width.equals("")) {
            width = "100%";
        }
        this.width = width;
        super.setHeight(width);
    }

    public void setHeight(String height) {
        if (height == null || height.equals("")) {
            height = "100%";
        }
        heigth = height;
        super.setHeight(height);
    }
}
