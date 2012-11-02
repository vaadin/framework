package com.vaadin.tests.tickets;

import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket1923 extends com.vaadin.LegacyApplication {

    private static final int ROWS = 50;

    private Panel p;

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        p = new Panel("TestPanel 250x300");
        // p.getLayout().setWidth("100%");
        // p.setContent(new GridLayout(1, 100));
        for (int i = 0; i < ROWS; i++) {
            p.addComponent(new Label(
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
        p = new Panel("a");
        p.addComponent(new Label("Longer than caption"));
        ol.addComponent(p);

        main.addComponent(ol);

        ol = new VerticalLayout();
        p = new Panel("captionasdfjahsdjfh this should be clipped god damn it");
        // p.getLayout().setSizeFull();
        p.setWidth("50px");
        p.setHeight("100px");
        p.addComponent(new Label(
                "aasdfaasdfja dslkfj lakfdj lakjdf lkaj dflkaj ldfkj alsdfj laksdj flkajs dflkj sdfsadfasdfasd"));
        ol.addComponent(p);

        main.addComponent(ol);

        ol = new VerticalLayout();
        p = new Panel("300x-1");
        // p.getLayout().setSizeFull();
        p.setWidth("300px");
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        p.addComponent(new Label("Short"));
        ol.addComponent(p);

        main.addComponent(ol);
    }
}