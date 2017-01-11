package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;

public class GridInPopupView extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid(PersonContainer.createWithTestData(100));
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                String sel = "";
                for (Object o : event.getSelected()) {
                    sel += ((Person) o).getFirstName();
                }
                log("Selection: " + sel);
            }
        });
        PopupView pv = new PopupView(new Content() {
            @Override
            public Component getPopupComponent() {
                return new VerticalLayout(grid);
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "foo";
            }
        });
        pv.setHideOnMouseOut(false);
        addComponent(pv);
    }

}
