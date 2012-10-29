package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.impl.PopupImplMozilla;

public class VPopupImplMozilla extends PopupImplMozilla {

    @Override
    public void onShow(Element popup) {
        // Move the overlay to the appropriate overlay container
        Element e = VOverlay.current.getOverlayContainer();
        e.appendChild(popup);

        super.onShow(popup);
    }

}
