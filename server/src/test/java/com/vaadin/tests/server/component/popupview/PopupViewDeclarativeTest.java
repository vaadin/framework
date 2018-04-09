package com.vaadin.tests.server.component.popupview;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;

public class PopupViewDeclarativeTest extends DeclarativeTestBase<PopupView> {

    @Test
    public void testEmptyPopupView() {
        PopupView component = new PopupView();
        Component popup = component.getContent().getPopupComponent();
        String design = "<vaadin-popup-view><popup-content>"
                + new DesignContext().createElement(popup)
                + "</popup-content></vaadin-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }

    @Test
    public void testVisiblePopupDesign() {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("300px");
        verticalLayout.setHeight("400px");

        PopupView component = new PopupView("Click <u>here</u> to open",
                verticalLayout);
        component.setHideOnMouseOut(true);
        component.setPopupVisible(true);
        // hide-on-mouse-out is true by default. not seen in design
        String design = "<vaadin-popup-view popup-visible>" //
                + "Click <u>here</u> to open" + "<popup-content>"
                + new DesignContext().createElement(verticalLayout)
                + "</popup-content>" //
                + "</vaadin-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }

    @Test
    public void testHideOnMouseOutDisabled() {
        final Label label = new Label("Foo");
        PopupView component = new PopupView("Click Me!", label);
        component.setHideOnMouseOut(false);
        String design = "<vaadin-popup-view hide-on-mouse-out='false'>" //
                + "Click Me!" + "<popup-content>"
                + new DesignContext().createElement(label) + "</popup-content>" //
                + "</vaadin-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }
}
