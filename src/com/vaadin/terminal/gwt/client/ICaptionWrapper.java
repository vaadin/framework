/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ICaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "i-captionwrapper";
    ICaption caption;
    Paintable widget;

    public ICaptionWrapper(Paintable toBeWrapped, ApplicationConnection client) {
        caption = new ICaption(toBeWrapped, client);
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
