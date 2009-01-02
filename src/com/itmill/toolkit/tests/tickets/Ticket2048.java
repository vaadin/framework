package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2048 extends Application {

    private Embedded embedded;
    private Panel p;
    private SplitPanel splitPanel;
    private GridLayout gridLayout;
    private OrderedLayout orderedLayout;

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        // splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        // getMainWindow().setLayout(splitPanel);

        // GridLayout layout = new GridLayout(10, 10);
        // w.setLayout(layout);
        // gridLayout = new GridLayout(1, 1);
        orderedLayout = new OrderedLayout();

        getMainWindow().setLayout(orderedLayout);
        // getMainWindow().setLayout(new GridLayout(1, 1));
        getMainWindow().setSizeFull();
        getMainWindow().getLayout().setSizeFull();

        createUI(orderedLayout);
        // createUI(gridLayout);

    }

    private void createUI(Layout layout) {
        // Button sw = new Button("Switch", new ClickListener() {
        //
        // public void buttonClick(ClickEvent event) {
        // Layout l = getMainWindow().getLayout();
        // if (l == orderedLayout) {
        // getMainWindow().setLayout(gridLayout);
        // } else {
        // getMainWindow().setLayout(orderedLayout);
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
        p.addComponent(l);

        embedded = new Embedded(null, new ThemeResource(
                "icons/64/folder-add.png"));
        layout.addComponent(embedded);
        Button b = new Button(
                "Replace image with new embedded component (flashes)",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        Embedded newEmbedded = new Embedded(null,
                                new ThemeResource("icons/64/folder-add.png"));
                        getMainWindow().getLayout().replaceComponent(embedded,
                                newEmbedded);
                        embedded = newEmbedded;

                    }

                });
        p.addComponent(b);

        b = new Button("Change image source (is fine)", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                String img = "folder-add";
                if (((ThemeResource) embedded.getSource()).getResourceId()
                        .contains("folder-add")) {
                    img = "folder-delete";
                }
                embedded
                        .setSource(new ThemeResource("icons/64/" + img + ".png"));

            }

        });

        p.addComponent(b);
        layout.addComponent(p);
    }
}
