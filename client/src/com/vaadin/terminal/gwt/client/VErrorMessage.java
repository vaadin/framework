/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ui.VOverlay;

public class VErrorMessage extends FlowPanel {
    public static final String CLASSNAME = "v-errormessage";

    public VErrorMessage() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateMessage(String htmlErrorMessage) {
        clear();
        if (htmlErrorMessage == null || htmlErrorMessage.length() == 0) {
            add(new HTML(" "));
        } else {
            // pre-formatted on the server as div per child
            add(new HTML(htmlErrorMessage));
        }
    }

    /**
     * Shows this error message next to given element.
     * 
     * @param indicatorElement
     */
    public void showAt(Element indicatorElement) {
        VOverlay errorContainer = (VOverlay) getParent();
        if (errorContainer == null) {
            errorContainer = new VOverlay();
            errorContainer.setWidget(this);
        }
        errorContainer.setPopupPosition(
                DOM.getAbsoluteLeft(indicatorElement)
                        + 2
                        * DOM.getElementPropertyInt(indicatorElement,
                                "offsetHeight"),
                DOM.getAbsoluteTop(indicatorElement)
                        + 2
                        * DOM.getElementPropertyInt(indicatorElement,
                                "offsetHeight"));
        errorContainer.show();

    }

    public void hide() {
        final VOverlay errorContainer = (VOverlay) getParent();
        if (errorContainer != null) {
            errorContainer.hide();
        }
    }
}
