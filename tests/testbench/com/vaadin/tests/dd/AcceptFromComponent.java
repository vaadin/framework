package com.vaadin.tests.dd;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class AcceptFromComponent extends Window {

    private AbsoluteLayout layout;

    public AcceptFromComponent(final Tree tree1) {
        setCaption("Checks the source is tree1 on server");

        layout = new AbsoluteLayout();
        DragAndDropWrapper wrapper = new DragAndDropWrapper(layout);

        setContent(wrapper);

        wrapper.setSizeFull();
        layout.setSizeFull();
        setWidth("450px");
        setHeight("150px");

        final ServerSideCriterion serverSideCriterion = new ServerSideCriterion() {

            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
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

        wrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return serverSideCriterion;
            }

            @Override
            public void drop(DragAndDropEvent event) {

                WrapperTargetDetails ed = (WrapperTargetDetails) event
                        .getTargetDetails();
                Transferable ctr = event.getTransferable();
                if (ctr.getSourceComponent() != null) {
                    // use "component" (from DragDropPane) if available, else
                    // take
                    // the source component
                    Component component = (Component) ctr.getData("component");
                    if (component == null) {
                        component = ctr.getSourceComponent();
                    }

                    if (component.getParent() != layout) {
                        if (ctr instanceof DataBoundTransferable) {
                            // Item has been dragged, construct a Label from
                            // Item id
                            Label l = new Label();
                            l.setSizeUndefined();
                            l.setValue("ItemId : "
                                    + ((DataBoundTransferable) ctr).getItemId());
                            layout.addComponent(l);
                            component = l;

                        } else {
                            // we have a component that is been dragged, add
                            // it
                            // to
                            // this
                            layout.addComponent(component);
                        }

                        Integer left = ed.getAbsoluteLeft();
                        Integer top = ed.getAbsoluteTop();

                        MouseEventDetails eventDetails = ed.getMouseEvent();

                        int clientX = eventDetails.getClientX();
                        int clientY = eventDetails.getClientY();

                        try {
                            layout.getPosition(component).setTopValue(
                                    Float.valueOf(clientY - top));
                            layout.getPosition(component).setLeftValue(
                                    Float.valueOf(clientX - left));
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    } else {
                        // drag started and ended inside the this Pane

                        DragAndDropWrapper.WrapperTransferable tr = (WrapperTransferable) event
                                .getTransferable();

                        MouseEventDetails start = tr.getMouseDownEvent();

                        MouseEventDetails eventDetails = ed.getMouseEvent();

                        int deltaX = eventDetails.getClientX()
                                - start.getClientX();
                        int deltaY = eventDetails.getClientY()
                                - start.getClientY();

                        ComponentPosition p = layout.getPosition(component);
                        p.setTopValue(p.getTopValue() + deltaY);
                        p.setLeftValue(p.getLeftValue() + deltaX);

                    }

                } else {
                    // drag coming outside of Vaadin
                    String object = (String) ctr.getData("text/plain");

                    String content = (String) ctr.getData("fileContents");

                    Label l = new Label();
                    l.setCaption("Generated from HTML5 drag:");
                    if (object != null) {
                        l.setValue(object);
                    } else {
                        l.setValue("HTML5 dd");
                    }

                    l.setDescription(content);
                    l.setSizeUndefined();

                    layout.addComponent(l);

                }
                return;
            }
        });

    }
}
