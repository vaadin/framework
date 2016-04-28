package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class GridLayoutSpanExpansion extends TestBase {

    @Override
    protected void setup() {
        GridLayout heightSpan = new GridLayout(2, 2);
        heightSpan.setHeight("200px");
        heightSpan.setWidth("200px");
        heightSpan.addComponent(new Label("1"), 0, 0);
        heightSpan.addComponent(new Label("2"), 0, 1);
        heightSpan.addComponent(new Label(
                "This is a somewhat long text that spans over a few lines."),
                1, 0, 1, 1);
        heightSpan.setRowExpandRatio(1, 1);
        addComponent(heightSpan);

        GridLayout widthSpan = new GridLayout(2, 2);
        widthSpan.setHeight("100px");
        widthSpan.setWidth("200px");
        widthSpan.addComponent(new Label(
                "This is a somewhat long text that spans over both columns."),
                0, 0, 1, 0);
        Label label1 = new Label("1");
        label1.setSizeUndefined();
        widthSpan.addComponent(label1, 0, 1);
        widthSpan.addComponent(new Label("2"), 1, 1);
        widthSpan.setColumnExpandRatio(1, 1);
        addComponent(widthSpan);

        GridLayout multipleSpans = new GridLayout(3, 3);
        multipleSpans.setWidth("400px");
        multipleSpans.addComponent(new Button("Button 0,0"), 0, 0);
        multipleSpans.addComponent(new Button("Button 1,0"), 1, 0);
        multipleSpans.addComponent(
                makeWideButton("A wide spanning button at 0,1"), 0, 1, 1, 1);
        multipleSpans.addComponent(
                makeWideButton("Another wide spanning button at 1,2"), 1, 2, 2,
                2);
        multipleSpans.setColumnExpandRatio(0, 1);
        multipleSpans.setColumnExpandRatio(1, 3);
        multipleSpans.setColumnExpandRatio(2, 2);
        addComponent(multipleSpans);
    }

    private static Button makeWideButton(String caption) {
        Button wideButton = new Button(caption);
        wideButton.setWidth("100%");
        return wideButton;
    }

    @Override
    protected String getDescription() {
        return "In the two first examples, the 1 and the 2 should be close to each other because of the expansion ratios. In the final example, there should be little extra space in the left column, much extra space in the middle and some extra space to the right";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(5868);
    }

}
