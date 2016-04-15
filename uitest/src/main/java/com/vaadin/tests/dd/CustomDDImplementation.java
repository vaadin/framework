package com.vaadin.tests.dd;

import java.util.Map;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractComponent;
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
    class MyDropTarget extends AbstractComponent implements DropTarget {
        @Override
        public DropHandler getDropHandler() {
            return new DropHandler() {

                @Override
                public void drop(DragAndDropEvent event) {
                    // Do something with data
                    return;
                }

                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return AcceptAll.get();
                }

            };
        }

        @Override
        public TargetDetails translateDropTargetDetails(
                Map<String, Object> clientVariables) {
            // If component has some special drop details that it needs to
            // translate for server side use, developer must return a
            // DragDropDetails here. If details does not exist or raw client
            // side data is ok, it is safe to return null here.
            return null;
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
    public class MyDragSource extends AbstractComponent implements Component {

    }

}
