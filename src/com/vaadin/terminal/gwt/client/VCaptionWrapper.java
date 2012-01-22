/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;

public class VCaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "v-captionwrapper";
    VCaption caption;
    VPaintableWidget paintable;

    public VCaptionWrapper(VPaintableWidget toBeWrapped,
            ApplicationConnection client) {
        caption = new VCaption(toBeWrapped, client);
        add(caption);
        paintable = toBeWrapped;
        add(paintable.getWidgetForPaintable());
        setStyleName(CLASSNAME);
    }

    public void updateCaption(UIDL uidl) {
        caption.updateCaption(uidl);
        setVisible(!uidl.getBooleanAttribute("invisible"));
    }

    public VPaintableWidget getPaintable() {
        return paintable;
    }
}
