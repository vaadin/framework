/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Iterator;

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

    public void updateFromUIDL(UIDL uidl) {
        clear();
        if (uidl.getChildCount() == 0) {
            add(new HTML(" "));
        } else {
            for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
                final Object child = it.next();
                if (child instanceof String) {
                    final String errorMessage = (String) child;
                    add(new HTML(errorMessage));
                } else if (child instanceof UIDL.XML) {
                    final UIDL.XML xml = (UIDL.XML) child;
                    add(new HTML(xml.getXMLAsString()));
                } else {
                    final VErrorMessage childError = new VErrorMessage();
                    add(childError);
                    childError.updateFromUIDL((UIDL) child);
                }
            }
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
        errorContainer.setPopupPosition(DOM.getAbsoluteLeft(indicatorElement)
                + 2
                * DOM.getElementPropertyInt(indicatorElement, "offsetHeight"),
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
