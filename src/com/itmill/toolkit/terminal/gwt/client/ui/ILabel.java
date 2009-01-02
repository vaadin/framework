/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ITooltip;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ILabel extends HTML implements Paintable {

    public static final String CLASSNAME = "i-label";
    private ApplicationConnection client;
    private int verticalPaddingBorder = 0;
    private int horizontalPaddingBorder = 0;

    public ILabel() {
        super();
        setStyleName(CLASSNAME);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
    }

    public ILabel(String text) {
        super(text);
        setStyleName(CLASSNAME);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
            event.cancelBubble(true);
            return;
        }
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        this.client = client;

        boolean sinkOnloads = false;

        final String mode = uidl.getStringAttribute("mode");
        if (mode == null || "text".equals(mode)) {
            setText(uidl.getChildString(0));
        } else if ("pre".equals(mode)) {
            setHTML(uidl.getChildrenAsXML());
        } else if ("uidl".equals(mode)) {
            setHTML(uidl.getChildrenAsXML());
        } else if ("xhtml".equals(mode)) {
            setHTML(uidl.getChildUIDL(0).getChildUIDL(0).getChildString(0));
            sinkOnloads = true;
        } else if ("xml".equals(mode)) {
            setHTML(uidl.getChildUIDL(0).getChildString(0));
        } else if ("raw".equals(mode)) {
            setHTML(uidl.getChildUIDL(0).getChildString(0));
            sinkOnloads = true;
        } else {
            setText("");
        }
        if (sinkOnloads) {
            sinkOnloadsForContainedImgs();
        }
    }

    private void sinkOnloadsForContainedImgs() {
        NodeList<Element> images = getElement().getElementsByTagName("img");
        for (int i = 0; i < images.getLength(); i++) {
            Element img = images.getItem(i);
            DOM.sinkEvents((com.google.gwt.user.client.Element) img,
                    Event.ONLOAD);
        }

    }

    @Override
    public void setHeight(String height) {
        verticalPaddingBorder = Util.setHeightExcludingPaddingAndBorder(this,
                height, verticalPaddingBorder);
    }

    @Override
    public void setWidth(String width) {
        horizontalPaddingBorder = Util.setWidthExcludingPaddingAndBorder(this,
                width, horizontalPaddingBorder);
    }
}
