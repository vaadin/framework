package com.vaadin.tests.dd;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class AcceptAnythingWindow extends Window {

    private AbsoluteLayout layout = new AbsoluteLayout();

    public AcceptAnythingWindow() {
        setCaption("Drop anything here");

        final DragAndDropWrapper wrapper = new DragAndDropWrapper(layout);
        wrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                WrapperTargetDetails ed = (WrapperTargetDetails) event
                        .getTargetDetails();
                Transferable transferable = event.getTransferable();
                TransferableImpl ctr = (TransferableImpl) transferable;
                Component component = ctr.getSourceComponent();
                if (component == wrapper) {
                    // if the drag source was wrapper we are interested about
                    // the dragged component
                    WrapperTransferable tr = (WrapperTransferable) transferable;
                    component = tr.getDraggedComponent();
                    // html5 drag created by wrapper set component null
                }

                if (component != null) {

                    if (component.getParent() != layout) {
                        if (transferable instanceof DataBoundTransferable) {
                            // Item has been dragged, construct a Label from
                            // Item id
                            Label l = new Label();
                            l.setSizeUndefined();
                            l.setValue("ItemId : "
                                    + ((DataBoundTransferable) transferable)
                                            .getItemId());
                            layout.addComponent(l);
                            component = l;

                        } else {
                            // we have a component that is been dragged, add
                            // it to this
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

                        WrapperTransferable tr = (WrapperTransferable) transferable;
                        // drag ended inside the this Pane

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
                    String object = (String) transferable.getData("Text");

                    String content = (String) transferable
                            .getData("fileContents");

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

        wrapper.setDragStartMode(DragStartMode.COMPONENT);
        wrapper.setSizeFull();
        setContent(wrapper);

        setWidth("250px");
        setHeight("100px");
    }
}
