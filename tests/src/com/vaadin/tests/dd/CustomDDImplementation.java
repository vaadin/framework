package com.vaadin.tests.dd;

import com.vaadin.event.AbstractDropHandler;
import com.vaadin.event.DragDropDetails;
import com.vaadin.event.DragDropHandler;
import com.vaadin.event.DragRequest;
import com.vaadin.event.DropHandler;
import com.vaadin.event.DropTarget;
import com.vaadin.event.Transferable;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager.DragEventType;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;

/**
 * Test/Example/Draft code how to build custom DD implementation using the thing
 * framework provided by Vaadin.
 * 
 */
public class CustomDDImplementation extends CustomComponent {

    public CustomDDImplementation() {
        Layout l = new CssLayout();
        l.addComponent(new MyDropTarget());
        l.addComponent(new MyDragSource());
    }

    /**
     * Server side component that accepts drags must implement HasDropHandler
     * that have one method to get reference of DropHandler.
     * 
     * DropHandler may be implemented directly or probably most commonly using a
     * half baked implementation {@link AbstractDropHandler}.
     * 
     * Check the @ClientWidget
     * 
     */
    @ClientWidget(VMyDropTarget.class)
    class MyDropTarget extends AbstractComponent implements DropTarget {
        public DropHandler getDropHandler() {
            return new DragDropHandler() {
                public void handleDragRequest(DragRequest dragRequest,
                        Transferable transferable,
                        DragDropDetails dragDropDetails) {
                    DragEventType type = dragRequest.getType();
                    switch (type) {

                    case ENTER:
                        // eg. validate transferrable
                        if (transferable.getDataFlawors().contains("Foo")) {
                            dragRequest.getResponseData().put("valueFor",
                                    "clientSideCallBack");
                        }

                        break;
                    case OVER:

                        break;
                    case LEAVE:

                        break;
                    default:
                        break;
                    }

                }

                public boolean drop(Transferable transferable,
                        DragDropDetails dropDetails) {
                    // Do something with data
                    return true;
                }
            };
        }

    }

    /**
     * Server side implementation of source does not necessary need to contain
     * anything.
     * 
     * Check the @ClientWidget
     * 
     * However component might have different modes to support starting drag
     * operations that are controlled via server side api.
     * 
     */
    @ClientWidget(VMyDragSource.class)
    public class MyDragSource extends AbstractComponent implements Component {

    }

}
