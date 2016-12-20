package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaScrolling extends TestBase {

    @Override
    protected String getDescription() {
        return "A read-only RichTextArea should be (touch) scrollable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7036;
    }

    @Override
    protected void setup() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; ++i) {
            sb.append("A long string with several lines<br/>");
        }

        HorizontalLayout main = new HorizontalLayout();
        main.setSpacing(true);
        addComponent(main);

        RichTextArea first = new RichTextArea("Defined height");
        RichTextArea second = new RichTextArea("Full height");
        RichTextArea third = new RichTextArea("Undefined height");

        first.setValue(sb.toString());
        second.setValue(sb.toString());
        third.setValue(sb.toString());

        first.setReadOnly(true);
        second.setReadOnly(true);
        third.setReadOnly(true);

        first.setWidth("200px");
        first.setHeight("400px");
        second.setSizeFull();
        third.setSizeUndefined();

        VerticalLayout secondLayout = new VerticalLayout();
        secondLayout.setWidth("200px");
        secondLayout.setHeight("200px");
        secondLayout.addComponent(second);

        main.addComponent(first);
        main.addComponent(secondLayout);
        main.addComponent(third);
    }
}
