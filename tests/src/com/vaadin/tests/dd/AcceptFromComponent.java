package com.vaadin.tests.dd;

import com.vaadin.event.TransferableImpl;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptCriteria.ServerSideCriterion;
import com.vaadin.ui.DragDropPane;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class AcceptFromComponent extends Window {

    public AcceptFromComponent(final Tree tree1) {
        setCaption("Checks the source is tree1 on server");

        DragDropPane pane = new DragDropPane();
        setContent(pane);
        pane.setSizeFull();
        setWidth("450px");
        setHeight("150px");

        final ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {

            public boolean accepts(DragAndDropEvent dragEvent) {
                Transferable transferable = dragEvent.getTransferable();
                if (transferable instanceof TransferableImpl) {
                    TransferableImpl componentTransferrable = (TransferableImpl) transferable;
                    if (componentTransferrable.getSourceComponent() == tree1) {
                        return true;
                    }
                }
                return false;
            }
        };

        pane.setDropHandler(new DragDropPane.ImportPrettyMuchAnything() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return serverSideCriterion;
            }
        });

    }

}
