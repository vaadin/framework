/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class IEmbedded extends HTML implements Paintable {
    private static String CLASSNAME = "i-embedded";

    private String height;
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
                Element el = null;
                boolean created = false;
                NodeList nodes = getElement().getChildNodes();
                if (nodes != null && nodes.getLength() == 1) {
                    Node n = nodes.getItem(0);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) n;
                        if (e.getTagName().equals("IMG")) {
                            el = e;
                        }
                    }
                }
                if (el == null) {
                    setHTML("");
                    el = DOM.createImg();
                    created = true;
                    client.addPngFix(el);
                    DOM.sinkEvents(el, Event.ONLOAD);
                }

                // Set attributes
                Style style = el.getStyle();
                String w = uidl.getStringAttribute("width");
                if (w != null) {
                    style.setProperty("width", w);
                } else {
                    style.setProperty("width", "");
                }
                String h = uidl.getStringAttribute("height");
                if (h != null) {
                    style.setProperty("height", h);
                } else {
                    style.setProperty("height", "");
                }
                DOM.setElementProperty(el, "src", getSrc(uidl, client));

                if (created) {
                    // insert in dom late
                    getElement().appendChild(el);
                }

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
                setHTML("<object width=\"" + width + "\" height=\"" + height
                        + "\"><param name=\"movie\" value=\""
                        + getSrc(uidl, client) + "\"><embed src=\""
                        + getSrc(uidl, client) + "\" width=\"" + width
                        + "\" height=\"" + height + "\"></embed></object>");
            } else if (mime.equals("image/svg+xml")) {
                String data;
                if (getParameter("data", uidl) == null) {
                    data = getSrc(uidl, client);
                } else {
                    data = "data:image/svg+xml," + getParameter("data", uidl);
                }
                setHTML("");
                ObjectElement obj = Document.get().createObjectElement();
                obj.setType(mime);
                obj.setData(data);
                if (width != null) {
                    obj.getStyle().setProperty("width", "100%");
                }
                if (height != null) {
                    obj.getStyle().setProperty("height", "100%");
                }
                getElement().appendChild(obj);

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

    private static String getParameter(String paramName, UIDL uidl) {
        Iterator childIterator = uidl.getChildIterator();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (child instanceof UIDL) {
                UIDL childUIDL = (UIDL) child;
                if (childUIDL.getTag().equals("embeddedparam")
                        && childUIDL.getStringAttribute("name").equals(
                                paramName)) {
                    return childUIDL.getStringAttribute("value");
                }

            }
        }
        return null;
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

    @Override
    public void setWidth(String width) {
        this.width = width;
        if (isDynamicHeight()) {
            int oldHeight = getOffsetHeight();
            super.setWidth(width);
            int newHeight = getOffsetHeight();
            /*
             * Must notify parent if the height changes as a result of a width
             * change
             */
            if (oldHeight != newHeight) {
                Util.notifyParentOfSizeChange(this, false);
            }
        } else {
            super.setWidth(width);
        }

    }

    private boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    protected void onDetach() {
        // Force browser to fire unload event when component is detached from
        // the view (IE doesn't do this automatically)
        if (browserElement != null) {
            DOM.setElementAttribute(browserElement, "src", "javascript:false");
        }
        super.onDetach();
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
    }
}
