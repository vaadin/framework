package com.vaadin.tests.tickets;

import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket1923 extends com.vaadin.server.LegacyApplication {

    private static final int ROWS = 50;

    private Panel p;

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("TestPanel 250x300", pl);
        // p.getLayout().setWidth("100%");
        // p.setContent(new GridLayout(1, 100));
        for (int i = 0; i < ROWS; i++) {
            pl.addComponent(new Label(
                    "Label"
                            + i
                            + " 5067w09adsfasdjfahlsdfjhalfjhaldjfhalsjdfhlajdhflajhdfljahdslfjahldsjfhaljdfhaljfdhlajsdhflajshdflkajhsdlfkjahsldfkjahsldfhalskjfdhlksjfdh857idifhaljsdfhlajsdhflajhdflajhdfljahldfjhaljdfhalsjdfhalkjdhflkajhdfljahsdlfjahlsdjfhaldjfhaljfdhlajdhflajshdfljahsdlfjhalsjdfhalskjhfdlhusfglksuhdflgjshflgjhslfghslfjghsljfglsjhfglsjhfgljshfgljshflgjhslfghsljfgsljdfglsdjhfglsjhflgkjshfldjgh"));
        }
        // main.getLayout().setSizeFull();

        p.setHeight("300px");
        p.setWidth("250px");
        // p.setWidth("50%");

        p.setScrollTop(100);
        p.setScrollLeft(100);

        main.addComponent(p);

        VerticalLayout ol = new VerticalLayout();
        pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("a", pl);
        pl.addComponent(new Label("Longer than caption"));
        ol.addComponent(p);

        main.addComponent(ol);

        ol = new VerticalLayout();
        pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("captionasdfjahsdjfh this should be clipped god damn it",
                pl);
        // p.getLayout().setSizeFull();
        p.setWidth("50px");
        p.setHeight("100px");
        pl.addComponent(new Label(
                "aasdfaasdfja dslkfj lakfdj lakjdf lkaj dflkaj ldfkj alsdfj laksdj flkajs dflkj sdfsadfasdfasd"));
        ol.addComponent(p);

        main.addComponent(ol);

        ol = new VerticalLayout();
        pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("300x-1", pl);
        // p.getLayout().setSizeFull();
        p.setWidth("300px");
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        pl.addComponent(new Label("Short"));
        ol.addComponent(p);

        main.addComponent(ol);
    }
}
