package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaSize extends TestBase {

    @Override
    protected String getDescription() {
        return "Test the size of a rich text area. The first area is 100px*100px wide, the second 100%*100% (of 500x500px), the third one has undefined width and height.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2573;
    }

    @Override
    protected void setup() {
        getMainWindow().getLayout().setHeight(null);

        RichTextArea first = new RichTextArea();
        RichTextArea second = new RichTextArea();
        RichTextArea third = new RichTextArea();

        first.setWidth("100px");
        first.setHeight("100px");
        second.setSizeFull();
        third.setSizeUndefined();

        VerticalLayout secondLayout = new VerticalLayout();
        secondLayout.setWidth("500px");
        secondLayout.setHeight("500px");
        secondLayout.addComponent(second);

        addComponent(first);
        addComponent(secondLayout);
        addComponent(third);
    }

}
