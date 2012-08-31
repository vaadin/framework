package com.vaadin.tests.dd;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

public class StartHtml5Drag extends TestBase {

    @Override
    protected void setup() {
        DragAndDropWrapper dragStart = new DragAndDropWrapper(new Label(
                "Drag me"));
        dragStart.setDragStartMode(DragStartMode.HTML5);
        dragStart.setHTML5DataFlavor("Text", "HTML5!");
        addComponent(dragStart);

        DragAndDropWrapper dropTarget = new DragAndDropWrapper(new Label(
                "over here"));
        dropTarget.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                getWindows()
                        .iterator()
                        .next()
                        .showNotification(
                                ((WrapperTransferable) event.getTransferable())
                                        .getText());
            }
        });
        addComponent(dropTarget);

        Embedded iframe = new Embedded("", new ClassResource("html5drop.htm"));
        iframe.setType(Embedded.TYPE_BROWSER);
        iframe.setWidth(400, Sizeable.UNITS_PIXELS);
        iframe.setHeight(400, Sizeable.UNITS_PIXELS);
        addComponent(iframe);

    }

    @Override
    protected String getDescription() {
        return "Should work. Try to e.g. drag the 'Hello Vaadin user' "
                + "label to native text editor application. In text "
                + "editor app 'HTML5!' text should appear.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7833;
    }
}
