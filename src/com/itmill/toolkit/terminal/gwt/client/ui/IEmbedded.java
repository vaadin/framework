/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IEmbedded extends HTML implements Paintable {
    private static String CLASSNAME = "i-embedded";

    private String heigth;
    private String width;
    private Element browserElement;

    private ApplicationConnection client;

    public IEmbedded() {
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        this.client = client;

        boolean clearBrowserElement = true;

        if (uidl.hasAttribute("type")) {
            final String type = uidl.getStringAttribute("type");
            if (type.equals("image")) {
                String w = uidl.getStringAttribute("width");
                if (w != null) {
                    w = " width=\"" + w + "\" ";
                } else {
                    w = "";
                }
                String h = uidl.getStringAttribute("height");
                if (h != null) {
                    h = " height=\"" + h + "\" ";
                } else {
                    h = "";
                }

                setHTML("<img src=\"" + getSrc(uidl, client) + "\"" + w + h
                        + "/>");

                Element el = DOM.getFirstChild(getElement());
                DOM.sinkEvents(el, Event.ONLOAD);
                client.addPngFix(el);

            } else if (type.equals("browser")) {
                if (browserElement == null) {
                    setHTML("<iframe width=\"100%\" height=\"100%\" frameborder=\"0\" src=\""
                            + getSrc(uidl, client)
                            + "\" name=\""
                            + uidl.getId() + "\"></iframe>");
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
        String url = client.translateToolkitUri(uidl.getStringAttribute("src"));
        if (url == null) {
            return "";
        }
        return url;
    }

    public void setWidth(String width) {
        this.width = width;
        super.setWidth(width);
    }

    public void setHeight(String height) {
        heigth = height;
        super.setHeight(height);
    }

    protected void onDetach() {
        // Force browser to fire unload event when component is detached from
        // the view (IE doesn't do this automatically)
        if (browserElement != null) {
            DOM.setElementAttribute(browserElement, "src", "javascript:false");
        }
        super.onDetach();
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            Set<Widget> w = new HashSet<Widget>();
            w.add(this);
            Util.componentSizeUpdated(w);
        }
    }
}
