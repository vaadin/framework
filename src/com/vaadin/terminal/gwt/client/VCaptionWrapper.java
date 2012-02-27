/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;

public class VCaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "v-captionwrapper";
    VCaption caption;
    VPaintableWidget paintable;

    /**
     * Creates a new caption wrapper panel.
     * 
     * @param toBeWrapped
     *            paintable that the caption is associated with, not null
     * @param client
     *            ApplicationConnection
     */
    public VCaptionWrapper(VPaintableWidget toBeWrapped,
            ApplicationConnection client) {
        caption = new VCaption(toBeWrapped, client);
        add(caption);
        paintable = toBeWrapped;
        add(paintable.getWidget());
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
