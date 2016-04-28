package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class Ticket2021 extends LegacyApplication {

    private TextArea tf1, tf2, tf3;

    private String contents = "This TextField SHOULD FILL the panel and NOT CAUSE any scrollbars to appear in the Panel. Scrollbars SHOULD appear in the TextField AND the whole scrollbars (includinc arrow down) SHOULD be visible.\n\n"
            + ""
            + "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent quis justo. Vivamus nec mi eu libero convallis auctor. Mauris et arcu. Nunc luctus justo. Aenean convallis, odio in vehicula scelerisque, est magna condimentum pede, a aliquam elit eros vitae diam. Phasellus porttitor convallis tellus. Nullam elementum, ligula nec viverra malesuada, risus tortor bibendum dui, eget hendrerit sem enim at massa. Nam eu pede sed nulla congue fermentum. Vestibulum malesuada libero non nunc. Proin rutrum. Fusce erat pede, volutpat vitae, aliquam ut, sagittis vel, augue. Fusce dui pede, convallis nec, accumsan tincidunt, consectetuer ac, purus. Nulla facilisi. Ut nisi. Sed orci risus, lacinia eu, sodales molestie, gravida quis, neque. Vestibulum pharetra ornare elit. Nulla porttitor molestie mauris. Morbi fringilla tellus sed risus. Curabitur varius massa."
            + "Nulla nisi. Sed blandit, ante vitae sagittis volutpat, arcu mauris vehicula risus, vitae posuere felis lectus sit amet purus. Donec nec magna et leo eleifend scelerisque. Suspendisse condimentum pharetra ligula. Curabitur lorem. Pellentesque a augue sit amet enim fermentum placerat. Phasellus ante risus, molestie at, iaculis at, pellentesque non, tellus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Phasellus non urna eget risus tempus imperdiet. Integer est diam, sagittis sit amet, posuere sit amet, bibendum sed, lacus. Aenean adipiscing cursus ipsum. Quisque at elit. Vestibulum vitae nunc. Praesent placerat metus viverra lorem. Cras nec elit congue nisi faucibus feugiat. Nam eget mi. Vestibulum condimentum. Nunc nisl ante, cursus in, dictum ac, lobortis rutrum, mi. Nulla eu nisi. In ultricies vehicula magna."
            + "Nunc eros dui, elementum at, ullamcorper eget, varius at, velit. Ut dictum. Cras ullamcorper ante vel tortor. Quisque viverra mauris vulputate quam. Nulla dui. Suspendisse non eros at ipsum faucibus hendrerit. Morbi dignissim pharetra tortor. Etiam malesuada. Mauris lacinia elementum erat. Duis mollis placerat metus. Nunc risus felis, cursus ac, cursus vel, convallis vel, metus. Ut vehicula nibh et nulla. Vivamus id pede. Quisque egestas arcu a ligula. Maecenas vehicula. Quisque sed ligula quis tellus tempus rutrum. Curabitur vel augue sed orci egestas pharetra. Duis pharetra.";

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        w.setContent(new GridLayout(2, 2));
        setMainWindow(w);

        VerticalLayout layout = new VerticalLayout();
        Panel p = new Panel(layout);
        p.setCaption("ExpandLayout");
        p.setWidth("500px");
        p.setHeight("500px");
        layout.setSizeFull();

        w.addComponent(p);

        tf1 = new TextArea();
        tf1.setRows(5);
        tf1.setSizeFull();
        tf1.setValue(contents);
        tf1.setCaption("TextField caption");
        layout.addComponent(tf1);

        /*
         * 
         * OrderedLayout
         */

        VerticalLayout layout2 = new VerticalLayout();
        Panel p2 = new Panel(layout2);
        p2.setCaption("OrderedLayout");
        p2.setWidth("500px");
        p2.setHeight("500px");
        layout2.setSizeFull();

        w.addComponent(p2);

        tf2 = new TextArea();
        tf2.setRows(5);
        tf2.setSizeFull();
        tf2.setValue(contents);
        tf2.setCaption("TextField caption");
        layout2.addComponent(tf2);

        /*
         * 
         * GridLayout
         */

        VerticalLayout p3l = new VerticalLayout();
        p3l.setMargin(true);
        Panel p3 = new Panel(p3l);
        p3.setCaption("GridLayout");
        p3.setWidth("500px");
        p3.setHeight("500px");
        // p3.setContent(new GridLayout());
        p3l.setSizeFull();
        p3l.setMargin(false);

        GridLayout gl = new GridLayout();
        gl.setSizeFull();
        gl.setMargin(false);
        p3l.addComponent(gl);
        w.addComponent(p3);

        tf3 = new TextArea();
        tf3.setRows(5);
        tf3.setSizeFull();
        tf3.setValue(contents);
        tf3.setCaption("TextField caption");
        // p3.getContent().addComponent(tf3);
        gl.addComponent(tf3);

        // Panel pp = new Panel();
        // pp.setCaption("OrderedLayout");
        // pp.setWidth("500px");
        // pp.setHeight("500px");
        // pp.getContent().setSizeFull();
        // orderedLayout = new VerticalLayout();
        // pp.getContent().addComponent(orderedLayout);
        // w.getContent().addComponent(pp);
        // createUI(orderedLayout);
    }

    @SuppressWarnings("unused")
    private void createUI(Layout layout) {
        Label l = new Label("Label");
        Button b = new Button("Enable/disable caption and watch button move",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        System.out.println("Enable/disable caption");
                        for (AbstractComponent l : new AbstractComponent[] {
                                tf1, tf2, tf3 }) {
                            // AbstractComponent l = tf2;
                            // Layout l = (Layout) event.getButton().getData();
                            if (l.getCaption() == null) {
                                l.setCaption("Expand layout caption");
                            } else {
                                l.setCaption(null);
                            }
                        }
                    }

                });
        b.setData(layout);
        Label l2 = new Label("This should always be visible");

        layout.addComponent(l);
        layout.addComponent(b);
        layout.addComponent(l2);

        if (layout instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) layout).setExpandRatio(l, 1);

        }
    }
}
