package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.impl.PopupImpl;

public class VPopupImpl extends PopupImpl {

    @Override
    public void onShow(Element popup) {
        // Move the overlay to the appropriate overlay container
        final VOverlay overlay = VOverlay.current;
        if (overlay != null) {
            final Element e = overlay.getOverlayContainer();
            e.appendChild(popup);
        }

        super.onShow(popup);
    }

}
