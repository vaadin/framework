package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket2048 extends LegacyApplication {

    private Embedded embedded;
    private Panel p;
    private VerticalLayout orderedLayout;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        // splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        // getMainWindow().setContent(splitPanel);

        // GridLayout layout = new GridLayout(10, 10);
        // w.setContent(layout);
        // gridLayout = new GridLayout(1, 1);
        orderedLayout = new VerticalLayout();

        getMainWindow().setContent(orderedLayout);
        // getMainWindow().setContent(new GridLayout(1, 1));
        getMainWindow().setSizeFull();
        getMainWindow().getContent().setSizeFull();

        createUI(orderedLayout);
        // createUI(gridLayout);

    }

    private void createUI(Layout layout) {
        // Button sw = new Button("Switch", new ClickListener() {
        //
        // public void buttonClick(ClickEvent event) {
        // Layout l = getMainWindow().getLayout();
        // if (l == orderedLayout) {
        // getMainWindow().setContent(gridLayout);
        // } else {
        // getMainWindow().setContent(orderedLayout);
        // }
        //
        // }
        // });
        // layout.addComponent(sw);

        Layout ol = new GridLayout(1, 2);
        p = new Panel("Panel", ol);
        p.setSizeFull();
        Label l = new Label("Spacer");
        l.setHeight("400px");
        ol.addComponent(l);

        embedded = new Embedded(null, new ThemeResource(
                "icons/64/folder-add.png"));
        layout.addComponent(embedded);
        Button b = new Button(
                "Replace image with new embedded component (flashes)",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Embedded newEmbedded = new Embedded(null,
                                new ThemeResource("icons/64/folder-add.png"));
                        getMainWindow().replaceComponent(embedded, newEmbedded);
                        embedded = newEmbedded;

                    }

                });
        ol.addComponent(b);

        b = new Button("Change image source (is fine)", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String img = "folder-add";
                if (((ThemeResource) embedded.getSource()).getResourceId()
                        .contains("folder-add")) {
                    img = "folder-delete";
                }
                embedded.setSource(new ThemeResource("icons/64/" + img + ".png"));

            }

        });

        ol.addComponent(b);
        layout.addComponent(p);
    }
}
