package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IPanel extends SimplePanel implements Paintable,
        ContainerResizedListener {

    public static final String CLASSNAME = "i-panel";

    ApplicationConnection client;

    String id;

    private Element captionNode = DOM.createDiv();

    private Element bottomDecoration = DOM.createDiv();

    private Element contentNode = DOM.createDiv();

    private String height;

    private Widget layout;

    public IPanel() {
        super();
        DOM.appendChild(getElement(), captionNode);
        DOM.appendChild(getElement(), contentNode);
        DOM.appendChild(getElement(), bottomDecoration);
        setStyleName(CLASSNAME);
        DOM
                .setElementProperty(captionNode, "className", CLASSNAME
                        + "-caption");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(bottomDecoration, "className", CLASSNAME
                + "-deco");
    }

    protected Element getContainerElement() {
        return contentNode;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        this.client = client;
        id = uidl.getId();

        // Panel size. Height needs to be saved for later use
        String w = uidl.hasVariable("width") ? uidl.getStringVariable("width")
                : null;
        height = uidl.hasVariable("height") ? uidl.getStringVariable("height")
                : null;
        setWidth(w != null ? w : "");

        // Handle caption displaying
        boolean hasCaption = false;
        if (uidl.hasAttribute("caption")
                && !uidl.getStringAttribute("caption").equals("")) {
            DOM.setInnerText(captionNode, uidl.getStringAttribute("caption"));
            hasCaption = true;
        } else {
            DOM.setInnerText(captionNode, "");
            DOM.setElementProperty(captionNode, "className", CLASSNAME
                    + "-nocaption");
        }

        // Add proper stylenames for all elements
        if (uidl.hasAttribute("style")) {
            String[] styles = uidl.getStringAttribute("style").split(" ");
            String captionBaseClass = CLASSNAME
                    + (hasCaption ? "-caption" : "-nocaption");
            String contentBaseClass = CLASSNAME + "-content";
            String decoBaseClass = CLASSNAME + "-deco";
            String captionClass = captionBaseClass;
            String contentClass = contentBaseClass;
            String decoClass = decoBaseClass;
            for (int i = 0; i < styles.length; i++) {
                captionClass += " " + captionBaseClass + "-" + styles[i];
                contentClass += " " + contentBaseClass + "-" + styles[i];
                decoClass += " " + decoBaseClass + "-" + styles[i];
            }
            DOM.setElementProperty(captionNode, "className", captionClass);
            DOM.setElementProperty(contentNode, "className", contentClass);
            DOM.setElementProperty(bottomDecoration, "className", decoClass);
        }

        // Height adjustment
        iLayout();

        // Render content
        UIDL layoutUidl = uidl.getChildUIDL(0);
        Widget newLayout = client.getWidget(layoutUidl);
        if (newLayout != layout) {
            if (layout != null) {
                client.unregisterPaintable((Paintable) layout);
            }
            setWidget(newLayout);
            layout = newLayout;
        }
        ((Paintable) layout).updateFromUIDL(layoutUidl, client);

    }

    public void iLayout() {
        if (height != null && height != "") {
            // need to fix containers height properly
            DOM.setStyleAttribute(getElement(), "height", height);
            DOM.setStyleAttribute(contentNode, "overflow", "hidden");
            boolean hasChildren = getWidget() != null;
            Element contentEl = null;
            String origPositioning = null;
            if (hasChildren) {
                // remove children temporary form normal flow to detect proper
                // size
                contentEl = getWidget().getElement();
                origPositioning = DOM.getStyleAttribute(contentEl, "position");
                DOM.setStyleAttribute(contentEl, "position", "absolute");
            }
            DOM.setStyleAttribute(contentNode, "height", "");
            int availableH = DOM.getElementPropertyInt(getElement(),
                    "clientHeight");

            int usedH = DOM
                    .getElementPropertyInt(bottomDecoration, "offsetTop")
                    - DOM.getElementPropertyInt(getElement(), "offsetTop")
                    + DOM.getElementPropertyInt(bottomDecoration,
                            "offsetHeight");

            int contentH = availableH - usedH;
            if (contentH < 0) {
                contentH = 0;
            }
            DOM.setStyleAttribute(contentNode, "height", contentH + "px");
            if (hasChildren) {
                ApplicationConnection.getConsole().log(
                        "positioning:" + origPositioning);
                DOM.setStyleAttribute(contentEl, "position", origPositioning);
            }
            DOM.setStyleAttribute(contentNode, "overflow", "auto");
        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
        }
        Util.runDescendentsLayout(this);
    }

}
