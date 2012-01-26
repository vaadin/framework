/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.label;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VLabel extends HTML {

    public static final String CLASSNAME = "v-label";
    private static final String CLASSNAME_UNDEFINED_WIDTH = "v-label-undef-w";

    private int verticalPaddingBorder = 0;
    private int horizontalPaddingBorder = 0;

    public VLabel() {
        super();
        setStyleName(CLASSNAME);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    public VLabel(String text) {
        super(text);
        setStyleName(CLASSNAME);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            // FIXME: Should not be here but in paintable
            Util.notifyParentOfSizeChange(this, true);
            event.cancelBubble(true);
            return;
        }
        // FIXME: Move to paintable
        // if (client != null) {
        // client.handleTooltipEvent(event, this);
        // }
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
        if (width == null || width.equals("")) {
            setStyleName(getElement(), CLASSNAME_UNDEFINED_WIDTH, true);
        } else {
            setStyleName(getElement(), CLASSNAME_UNDEFINED_WIDTH, false);
        }
    }

    @Override
    public void setText(String text) {
        if (BrowserInfo.get().isIE8()) {
            // #3983 - IE8 incorrectly replaces \n with <br> so we do the
            // escaping manually and set as HTML
            super.setHTML(Util.escapeHTML(text));
        } else {
            super.setText(text);
        }
    }
}
