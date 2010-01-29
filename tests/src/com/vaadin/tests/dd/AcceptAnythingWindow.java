package com.vaadin.tests.dd;

import com.vaadin.ui.DragDropPane;
import com.vaadin.ui.Window;

public class AcceptAnythingWindow extends Window {

    public AcceptAnythingWindow() {
        setCaption("Drop anything here");
        DragDropPane pane = new DragDropPane();
        setContent(pane);
        pane.setSizeFull();
        setWidth("250px");
        setHeight("100px");
    }

}
