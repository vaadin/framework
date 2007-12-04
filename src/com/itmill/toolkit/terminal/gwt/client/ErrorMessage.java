/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ui.ToolkitOverlay;

public class ErrorMessage extends FlowPanel {
    public static final String CLASSNAME = "i-error";

    public ErrorMessage() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl) {
        clear();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final Object child = it.next();
            if (child instanceof String) {
                final String errorMessage = (String) child;
                add(new HTML(errorMessage));
            } else if (child instanceof UIDL.XML) {
                final UIDL.XML xml = (UIDL.XML) child;
                add(new HTML(xml.getXMLAsString()));
            } else {
                final ErrorMessage childError = new ErrorMessage();
                add(childError);
                childError.updateFromUIDL((UIDL) child);
            }
        }
    }

    /**
     * Shows this error message next to given element.
     * 
     * @param indicatorElement
     */
    public void showAt(Element indicatorElement) {
        ToolkitOverlay errorContainer = (ToolkitOverlay) getParent();
        if (errorContainer == null) {
            errorContainer = new ToolkitOverlay();
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
        final ToolkitOverlay errorContainer = (ToolkitOverlay) getParent();
        if (errorContainer != null) {
            errorContainer.hide();
        }
    }
}
