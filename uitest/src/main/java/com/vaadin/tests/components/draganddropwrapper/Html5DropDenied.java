package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Image;

public class Html5DropDenied extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Image sample = new Image();
        sample.setSource(new ThemeResource("../runo/icons/64/document.png"));

        Button neverButton = new Button("Never accepts drop");
        neverButton.setId("never");
        neverButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("click on " + event.getButton().getCaption());
            }
        });

        DragAndDropWrapper neverAccept = new DragAndDropWrapper(neverButton);
        neverAccept.setSizeFull();
        neverAccept.setDragStartMode(DragStartMode.NONE);
        neverAccept.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Not((ClientSideCriterion) AcceptAll.get());
            }

            @Override
            public void drop(DragAndDropEvent event) {
                log("This should never happen");
            }
        });
        Button alwaysButton = new Button("always accepts drop");
        alwaysButton.setId("always");
        alwaysButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("click on " + event.getButton().getCaption());
            }
        });

        DragAndDropWrapper alwaysAccept = new DragAndDropWrapper(alwaysButton);
        alwaysAccept.setSizeFull();
        alwaysAccept.setDragStartMode(DragStartMode.NONE);
        alwaysAccept.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                log("Drop on always accept");
            }
        });

        addComponent(sample);
        addComponent(neverAccept);
        addComponent(alwaysAccept);

    }

}
