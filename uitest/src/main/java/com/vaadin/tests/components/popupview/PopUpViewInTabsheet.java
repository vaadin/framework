package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.*;

public class PopUpViewInTabsheet extends AbstractTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        CssLayout layout = new CssLayout();
        addComponent(layout);

        VerticalLayout popupContent = new VerticalLayout();
        popupContent.setId("content");
        PopupView popup = new PopupView("Pop it up", popupContent);
        popupContent.addComponent(new Button("Button"));

        popup.setHideOnMouseOut(false);
        popup.setId("popupId");
        popup.setHeight("40px");

        TabSheet tabsheet = new TabSheet();

        VerticalLayout tab1 = new VerticalLayout();
        tab1.addComponent(popup);
        tabsheet.addTab(tab1, "Mercury").setId("tab0");
        VerticalLayout tab2 = new VerticalLayout();
        tab2.addComponent(new TextField("Enter"));
        tab2.setCaption("Venus");
        tabsheet.addTab(tab2).setId("tab1");
        layout.addComponent(tabsheet);
    }
}
