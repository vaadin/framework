package com.vaadin.tests.dd;

import java.util.Iterator;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class DDTest5 extends TestBase {

    java.util.Random r = new java.util.Random(1);

    HorizontalLayout hl = new HorizontalLayout();

    private DragAndDropWrapper dragAndDropWrapper2;

    private DropHandler dh;

    private static int count;

    class WrappedLabel extends DragAndDropWrapper {

        private static final long serialVersionUID = 1L;

        public WrappedLabel(String content) {
            super(new Label(content + " c:" + ++count));
            setDragStartMode(DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dh;
        }

    }

    @Override
    protected void setup() {
        UI w = getLayout().getUI();

        HorizontalSortableCssLayoutWithWrappers verticalSortableCssLayoutWithWrappers = new HorizontalSortableCssLayoutWithWrappers();
        w.addWindow(verticalSortableCssLayoutWithWrappers);
        verticalSortableCssLayoutWithWrappers.setPositionX(200);
        verticalSortableCssLayoutWithWrappers.setPositionY(40); /*
                                                                 * FIXME:
                                                                 * subwindow
                                                                 * horizontal
                                                                 * position does
                                                                 * not work if
                                                                 * only x set
                                                                 */

        Label l;

        l = new Label("Drag me");
        DragAndDropWrapper dragAndDropWrapper = new DragAndDropWrapper(l);
        dragAndDropWrapper.setDragStartMode(DragStartMode.COMPONENT);
        dragAndDropWrapper.setWidth("100px");
        dragAndDropWrapper.setHeight("100px");
        getLayout().addComponent(dragAndDropWrapper);

        l = new Label("Drag me too");
        dragAndDropWrapper = new DragAndDropWrapper(l);
        dragAndDropWrapper.setDragStartMode(DragStartMode.WRAPPER);
        dragAndDropWrapper.setWidth("100px");
        dragAndDropWrapper.setHeight("100px");
        getLayout().addComponent(dragAndDropWrapper);

        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("300px");

        dragAndDropWrapper2 = new DragAndDropWrapper(cssLayout);
        dragAndDropWrapper2
                .setCaption("Drop here or sort with dd (wrapper(csslayout(n*wrapper(label))))");

        dh = new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent dropEvent) {

                /*
                 * TODO wrap componentns in wrappers (so we can build reordering
                 * here)
                 */

                if (dropEvent.getTransferable() instanceof WrapperTransferable) {
                    WrapperTransferable transferable = (WrapperTransferable) dropEvent
                            .getTransferable();
                    Component sourceComponent = transferable
                            .getSourceComponent();

                    Component draggedComponent = transferable
                            .getDraggedComponent();

                    DropTarget target = dropEvent.getTargetDetails()
                            .getTarget();

                    WrappedLabel wrappedLabel = new WrappedLabel(
                            draggedComponent.toString());
                    if (target instanceof WrappedLabel) {
                        int i = 1; // add next to reference by default
                        Iterator<Component> componentIterator = cssLayout
                                .getComponentIterator();
                        Component next = componentIterator.next();
                        while (next != target && componentIterator.hasNext()) {
                            if (next != sourceComponent) {
                                // don't count on index if component is being
                                // moved
                                i++;
                            }
                            next = componentIterator.next();
                        }

                        if (sourceComponent instanceof WrappedLabel) {
                            cssLayout.removeComponent(sourceComponent);
                            wrappedLabel = (WrappedLabel) sourceComponent;
                        }
                        if (dropEvent.getTargetDetails()
                                .getData("verticalLocation").equals("TOP")) {
                            // before reference if dropped on topmost part
                            i--;
                            if (i < 0) {
                                i = 0;
                            }
                        }
                        cssLayout.addComponent(wrappedLabel, i);

                    } else {
                        cssLayout.addComponent(wrappedLabel);
                    }

                } else {
                    // no component, add label with "Text"

                    String data = (String) dropEvent.getTransferable().getData(
                            "text/plain");
                    if (data == null || "".equals(data)) {
                        data = "-- no Text --";
                    }
                    cssLayout.addComponent(new WrappedLabel(data));

                }

            }
        };

        dragAndDropWrapper2.setDropHandler(dh);

        getLayout().addComponent(dragAndDropWrapper2);

    }

    @Override
    protected String getDescription() {
        return "dd: DragAndDropWrapper to build various use cases completely on server side";
    }

    @Override
    protected Integer getTicketNumber() {
        return 119;
    }

}
