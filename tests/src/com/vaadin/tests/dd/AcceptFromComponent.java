package com.vaadin.tests.dd;

import com.vaadin.event.ComponentTransferrable;
import com.vaadin.event.Transferable;
import com.vaadin.event.AbstractDropHandler.AcceptCriterion;
import com.vaadin.ui.DragDropPane;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class AcceptFromComponent extends Window {

    public AcceptFromComponent(final Tree tree1) {
        setCaption("Checks the source is tree1 on server");

        DragDropPane pane = new DragDropPane();
        setContent(pane);
        pane.getDropHandler().setAcceptCriterion(new AcceptCriterion() {
            public boolean accepts(Transferable transferable) {
                if (transferable instanceof ComponentTransferrable) {
                    ComponentTransferrable componentTransferrable = (ComponentTransferrable) transferable;
                    if (componentTransferrable.getSourceComponent() == tree1) {
                        return true;
                    }
                }
                return false;
            }
        });
        pane.setSizeFull();
        setWidth("450px");
        setHeight("150px");
    }

}
