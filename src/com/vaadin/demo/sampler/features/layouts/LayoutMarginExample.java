package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LayoutMarginExample extends GridLayout implements
        Button.ClickListener {

    VerticalLayout marginLayout;
    Button topMargin;
    Button rightMargin;
    Button bottomMargin;
    Button leftMargin;

    public LayoutMarginExample() {
        super(3, 3);

        space();
        topMargin = new Button("Top margin", this);
        topMargin.setSwitchMode(true);
        addComponent(topMargin);
        setComponentAlignment(topMargin, "center");

        space();
        leftMargin = new Button("Left margin", this);
        leftMargin.setSwitchMode(true);
        addComponent(leftMargin);
        setComponentAlignment(leftMargin, "middle");

        marginLayout = new VerticalLayout();
        marginLayout.setStyleName("marginexample");
        marginLayout.setSizeUndefined();
        addComponent(marginLayout);
        marginLayout.addComponent(new Label("Margins all around?"));

        rightMargin = new Button("Right margin", this);
        rightMargin.setSwitchMode(true);
        addComponent(rightMargin);
        setComponentAlignment(rightMargin, "middle");

        space();
        bottomMargin = new Button("Bottom margin", this);
        bottomMargin.setSwitchMode(true);
        addComponent(bottomMargin);
        setComponentAlignment(bottomMargin, "center");

    }

    public void buttonClick(ClickEvent event) {
        marginLayout.setMargin(topMargin.booleanValue(), rightMargin
                .booleanValue(), bottomMargin.booleanValue(), leftMargin
                .booleanValue());

    }
}
