package com.vaadin.tests.dd;

import java.util.Iterator;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.event.dd.acceptcriteria.SourceIsTarget;
import com.vaadin.event.dd.acceptcriteria.TargetDetailIs;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * Same as with css layout but tests size change propagation on emphasis +
 * rules.
 * 
 * 
 */
public class HorizontalLayoutSortableWithWrappers extends Window {

    static int count;

    private HorizontalLayout layout = new HorizontalLayout();

    class WrappedLabel extends DragAndDropWrapper {

        private static final long serialVersionUID = 1L;

        public WrappedLabel(String content) {
            super(new Label(content + " c:" + ++count));
            getCompositionRoot().setWidth("60px");
            getCompositionRoot().setHeight("60px");
            setSizeUndefined();
            setDragStartMode(DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dh;
        }

    }

    private DropHandler dh = new DropHandler() {
        AcceptCriterion crit = new And(new TargetDetailIs("horizontalLocation",
                "LEFT"), new Not(SourceIsTarget.get()));

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return crit;
        }

        @Override
        public void drop(DragAndDropEvent dropEvent) {
            Transferable transferable = dropEvent.getTransferable();
            if (transferable instanceof TransferableImpl) {
                TransferableImpl ct = (TransferableImpl) transferable;
                Component sourceComponent = ct.getSourceComponent();
                if (sourceComponent instanceof WrappedLabel) {
                    int index = 1;
                    Iterator<Component> componentIterator = layout
                            .getComponentIterator();
                    Component next = componentIterator.next();
                    TargetDetails dropTargetData = dropEvent.getTargetDetails();
                    DropTarget target = dropTargetData.getTarget();
                    while (next != target) {
                        if (next != sourceComponent) {
                            index++;
                        }
                        next = componentIterator.next();
                    }
                    if (dropTargetData.getData("horizontalLocation").equals(
                            "LEFT")) {
                        index--;
                        if (index < 0) {
                            index = 0;
                        }
                    }

                    layout.removeComponent(sourceComponent);
                    layout.addComponent(sourceComponent, index);
                }
            }
            // TODO Auto-generated method stub

        }
    };

    public HorizontalLayoutSortableWithWrappers() {
        setCaption("Horizontally sortable layout via (ddwrappers): Try sorting blocks by dragging them");
        DragAndDropWrapper pane = new DragAndDropWrapper(layout);
        setContent(pane);
        pane.setSizeFull();
        setWidth("400px");
        setHeight("100px");

        for (int i = 0; i < 4; i++) {
            layout.addComponent(new WrappedLabel("Block"));
        }

    }
}
