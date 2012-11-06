package com.vaadin.tests.tickets;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2405 extends LegacyApplication {

    private Label label;
    private HorizontalSplitPanel split;

    @Override
    public void init() {

        final LegacyWindow root = new LegacyWindow("VaadinTunes");
        root.setWidth("90%");
        root.setHeight("90%");

        // We'll attach the window to the browser view already here, so we won't
        // forget it later.
        // browser.addWindow(root);
        setMainWindow(root);

        root.getContent().setSizeFull();
        ((MarginHandler) root.getContent()).setMargin(false);

        // Top area, containing playback and volume controls, play status, view
        // modes and search
        HorizontalLayout top = new HorizontalLayout();
        // GridLayout top = new GridLayout(1, 1);
        top.setWidth("100%");
        top.setMargin(false);
        top.setSpacing(false);

        // Let's attach that one straight away too
        root.addComponent(top);

        label = new Label(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent porttitor porta lacus. Nulla tellus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin mollis turpis in mauris faucibus posuere. Nullam rutrum, nisi a fermentum tempus, lacus metus rutrum massa, a condimentum mauris justo a tortor. Mauris aliquet, ante quis ultricies posuere, sapien libero laoreet sem, a accumsan diam metus sed elit. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur vehicula metus nec turpis dignissim cursus. Suspendisse potenti. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam feugiat orci eget risus. Vestibulum at sem. ");
        label.setWidth("100%");
        top.addComponent(label);
        split = new HorizontalSplitPanel();
        split.setHeight("100%");
        Embedded image = new Embedded("An image", new ExternalResource(
                "http://dev.itmill.com/chrome/site/toolkit-logo.png"));
        image.setWidth("100%");
        root.addComponent(split);
        ((VerticalLayout) root.getContent()).setExpandRatio(split, 1.0f);
        VerticalLayout vl = new VerticalLayout();
        split.addComponent(vl);

        vl.addComponent(new TextField("abc"));
        vl.addComponent(image);
        vl.setExpandRatio(image, 1.0f);
        vl.setComponentAlignment(image, Alignment.BOTTOM_CENTER);
        vl.setHeight("100%");
        // We'll need one splitpanel to separate the sidebar and track listing
        Button bottomButton = new Button("Filler");
        bottomButton.setSizeFull();
        // root.addComponent(bottomButton);

        // The splitpanel is by default 100% x 100%, but we'll need to adjust
        // our main window layout to accomodate the height
        root.getContent().setHeight("100%");
        // ((VerticalLayout) root.getLayout()).setExpandRatio(bottomButton,
        // 1.0F);

    }

}
