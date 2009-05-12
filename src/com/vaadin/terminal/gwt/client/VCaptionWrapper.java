/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class VCaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "v-captionwrapper";
    VCaption caption;
    Paintable widget;

    public VCaptionWrapper(Paintable toBeWrapped, ApplicationConnection client) {
        caption = new VCaption(toBeWrapped, client);
        add(caption);
        widget = toBeWrapped;
        add((Widget) widget);
        setStyleName(CLASSNAME);
    }

    public void updateCaption(UIDL uidl) {
        caption.updateCaption(uidl);
        setVisible(!uidl.getBooleanAttribute("invisible"));
    }

    public Paintable getPaintable() {
        return widget;
    }
}
