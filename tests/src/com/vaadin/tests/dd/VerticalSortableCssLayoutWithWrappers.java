package com.vaadin.tests.dd;

import java.util.Iterator;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptCriteria.AcceptAll;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class VerticalSortableCssLayoutWithWrappers extends Window {

    static int count;

    private CssLayout cssLayout = new CssLayout() {
        @Override
        protected String getCss(Component c) {
            return "float:left; width:60px;height:60px;background: yellow;padding:2px;";
        };
    };

    class WrappedLabel extends DragAndDropWrapper {

        private static final long serialVersionUID = 1L;

        public WrappedLabel(String content) {
            super(new Label(content + " c:" + ++count));
            setSizeUndefined(); // via css
            setHeight("60px"); // FIXME custom component seems to be broken:
                               // can't set height with css only
            setDragStartMode(DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dh;
        }

    }

    private DropHandler dh = new DropHandler() {

        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

        public void drop(DropEvent dropEvent) {
            Transferable transferable = dropEvent.getTransferable();
            if (transferable instanceof ComponentTransferable) {
                ComponentTransferable ct = (ComponentTransferable) transferable;
                Component sourceComponent = ct.getSourceComponent();
                if (sourceComponent instanceof WrappedLabel) {
                    int index = 1;
                    Iterator<Component> componentIterator = cssLayout
                            .getComponentIterator();
                    Component next = componentIterator.next();
                    TargetDetails dropTargetData = dropEvent
                            .getDropTargetData();
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

                    cssLayout.removeComponent(sourceComponent);
                    cssLayout.addComponent(sourceComponent, index);
                }
            }
            // TODO Auto-generated method stub

        }
    };

    public VerticalSortableCssLayoutWithWrappers() {
        setCaption("Horizontally sortable csslayout via (ddwrappers):Try sorting block by draggin them");
        DragAndDropWrapper pane = new DragAndDropWrapper(cssLayout);
        setContent(pane);
        pane.setSizeFull();
        setWidth("400px");
        setHeight("100px");

        for (int i = 0; i < 4; i++) {
            cssLayout.addComponent(new WrappedLabel("Block"));
        }

    }
}
