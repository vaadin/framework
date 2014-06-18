package com.vaadin.tests.tooltip;

import java.util.Iterator;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DragAndDropWrapperTooltips extends AbstractTestUI {

    private final String BASE = ".v-widget.greenblock {vertical-align: middle; float:left; width:60px;height:60px;background: green !important; padding:0; margin:2px;-webkit-transition: width 0.3s ease-in-out;color: white;}";
    private final String B2 = ".v-widget.b2 {background-color: red !important;}";
    private final String B3 = ".v-widget.b3 {background-color: yellow !important;color: black;}";
    private final String B4 = ".v-widget.b4 {background-color: blue !important;}";
    private final String HIDEDRAGSOURCE = ".v-active-drag-source { overflow:hidden; width:0px !important;}";
    private DragAndDropWrapper dragAndDropWrapper;

    @Override
    protected void setup(VaadinRequest request) {
        TestUtils.injectCSS(this, BASE + B4 + B2 + B3 + HIDEDRAGSOURCE);

        VerticalLayout l = new VerticalLayout();
        l.setWidth("400px");
        l.setHeight("100px");
        dragAndDropWrapper = new DragAndDropWrapper(cssLayout);
        dragAndDropWrapper
                .setDescription("Tooltip for the wrapper wrapping all the draggable layouts");
        dragAndDropWrapper.setSizeFull();
        l.addComponent(dragAndDropWrapper);

        addComponent(l);

        for (int i = 1; i <= 4; i++) {
            WrappedLabel wl = new WrappedLabel("Block");
            wl.setId("wrapper" + i);
            wl.addStyleName("b" + i);
            cssLayout.addComponent(wl);
        }
        getTooltipConfiguration().setOpenDelay(300);
    }

    int count;

    private CssLayout cssLayout = new CssLayout() {
    };

    class WrappedLabel extends DragAndDropWrapper {

        private static final long serialVersionUID = 1L;

        public WrappedLabel(String content) {
            super(new Label(content + " " + ++count));
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

                    dragAndDropWrapper
                            .setDescription("Drag was performed and tooltip was changed");
                }
            }
        }
    };

    @Override
    protected String getTestDescription() {
        return "A tooltip should be shown when hovering the DragAndDropWrapper containing all the draggable layouts";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7708;
    }
}
