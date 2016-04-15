package com.vaadin.tests.dd;

import java.util.Iterator;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ActiveDragSourceClassName extends TestBase {

    private static final String GREENBOXES = ".greenblock {float:left; width:60px;height:60px;background: green !important; padding:0; margin:2px;-webkit-transition: width 0.3s ease-in-out;}";
    private static final String HIDEDRAGSOURCE = ".v-active-drag-source { overflow:hidden; width:0px !important;}";

    @Override
    protected void setup() {
        TestUtils.injectCSS(getMainWindow(), GREENBOXES + HIDEDRAGSOURCE);

        VerticalLayout l = new VerticalLayout();
        l.setWidth("400px");
        l.setHeight("100px");
        DragAndDropWrapper pane = new DragAndDropWrapper(cssLayout);
        pane.setSizeFull();
        l.addComponent(pane);

        addComponent(l);

        for (int i = 0; i < 4; i++) {
            cssLayout.addComponent(new WrappedLabel("Block"));
        }

    }

    static int count;

    private CssLayout cssLayout = new CssLayout() {
    };

    class WrappedLabel extends DragAndDropWrapper {

        private static final long serialVersionUID = 1L;

        public WrappedLabel(String content) {
            super(new Label(content + " c:" + ++count));
            getCompositionRoot().setSizeUndefined();
            setHeight("60px"); // FIXME custom component seems to be broken:
            // can't set height with css only
            setWidth("60px");
            setDragStartMode(DragStartMode.WRAPPER);
            addStyleName("greenblock");
        }

        @Override
        public DropHandler getDropHandler() {
            return dh;
        }

    }

    private DropHandler dh = new DropHandler() {

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

        @Override
        public void drop(DragAndDropEvent dropEvent) {
            Transferable transferable = dropEvent.getTransferable();
            if (transferable instanceof TransferableImpl) {
                TransferableImpl ct = (TransferableImpl) transferable;
                Component sourceComponent = ct.getSourceComponent();
                if (sourceComponent instanceof WrappedLabel) {
                    int index = 1;
                    Iterator<Component> componentIterator = cssLayout
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

                    cssLayout.removeComponent(sourceComponent);
                    cssLayout.addComponent(sourceComponent, index);
                }
            }
        }
    };

    @Override
    protected String getDescription() {
        return "It should be possible to style the source component during the drag.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6813;
    }
}
