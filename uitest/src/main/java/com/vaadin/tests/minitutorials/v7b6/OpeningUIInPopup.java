package com.vaadin.tests.minitutorials.v7b6;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

public class OpeningUIInPopup extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Button popupButton = new Button("Open popup with MyPopupUI");

        BrowserWindowOpener popupOpener = new BrowserWindowOpener(
                MyPopupUI.class);
        popupOpener.setFeatures("height=300,width=300");
        popupOpener.extend(popupButton);

        // Add a parameter
        popupOpener.setParameter("foo", "bar");

        // Set a fragment
        popupOpener.setUriFragment("myfragment");

        setContent(popupButton);
    }

}
