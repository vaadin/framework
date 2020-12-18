package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

public class PopupViewContentWithExpandRatio extends AbstractTestUI {
    private PopupView popup;

    @Override
    protected void setup(VaadinRequest request) {
        popup = new PopupView("Open popup", createPopupContent());
        popup.setHideOnMouseOut(false);
        popup.setPopupVisible(false);
        addComponent(popup);
    }

    private VerticalLayout createPopupContent() {
        Label label = new Label(
                "Placeholder content that should take up most of the available space");
        label.setValue(LoremIpsum.get(56));
        label.setSizeFull();
        label.setId("label");

        Button refreshBtn = new Button("Force layout", e -> {
            JavaScript.eval("vaadin.forceLayout()");
        });
        refreshBtn.setId("refresh");

        Button submitBtn = new Button("Close popup");
        submitBtn.addClickListener(clickEvent -> {
            popup.setPopupVisible(false);
        });
        submitBtn.setId("close");

        VerticalLayout content = new VerticalLayout();
        content.setHeight("300px");
        content.setSpacing(true);
        content.setMargin(true);

        content.addComponent(label);
        content.addComponent(refreshBtn);
        content.addComponent(submitBtn);
        content.setExpandRatio(label, 2.0f);
        return content;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11187;
    }

    @Override
    protected String getTestDescription() {
        return "Expand ratio shouldn't cause contents to overflow "
                + "from popup view. The popup should be opened at least "
                + "20 times without SuperDevMode or TestBench or other "
                + "configurations that might slow down the processing.";
    }
}
