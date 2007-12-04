/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class CaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "i-captionwrapper";
    Caption caption;
    Paintable widget;

    public CaptionWrapper(Paintable toBeWrapped, ApplicationConnection client) {
        caption = new Caption(toBeWrapped, client);
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
