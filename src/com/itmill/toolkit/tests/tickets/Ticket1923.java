package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1923 extends com.itmill.toolkit.Application {

    private static final int ROWS = 1;

    private Panel p;

    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        p = new Panel("TestPanel");
        // p.getLayout().setWidth("100%");
        p.setScrollable(true);
        // p.setLayout(new GridLayout(1, 100));
        for (int i = 0; i < ROWS; i++) {
            p
                    .addComponent(new Label(
                            "Label"
                                    + i
                                    + " 5067w09adsfasdjfahlsdfjhalfjhaldjfhalsjdfhlajdhflajhdfljahdslfjahldsjfhaljdfhaljfdhlajsdhflajshdflkajhsdlfkjahsldfkjahsldfhalskjfdhlksjfdh857idifhaljsdfhlajsdhflajhdflajhdfljahldfjhaljdfhalsjdfhalkjdhflkajhdfljahsdlfjahlsdjfhaldjfhaljfdhlajdhflajshdfljahsdlfjhalsjdfhalskjhfdlhusfglksuhdflgjshflgjhslfghslfjghsljfglsjhfglsjhfgljshfgljshflgjhslfghsljfgsljdfglsdjhfglsjhflgkjshfldjgh"));
        }
        //main.getLayout().setSizeFull();

        p.setHeight("300px");
        p.setWidth("250px");
        //p.setWidth("50%");

        p.setScrollTop(100);
        p.setScrollLeft(100);

        main.addComponent(p);

    }
}