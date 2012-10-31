package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutExpandRatioModification extends TestBase implements
        ClickListener {

    private boolean isVisible = false;
    private VerticalLayout mainLayout;
    private VerticalLayout vl1;
    private VerticalLayout vl2;
    private Button button;

    @Override
    public void setup() {
        LegacyWindow main = getMainWindow();

        mainLayout = new VerticalLayout();
        main.setContent(mainLayout);

        // The upper layout
        vl1 = new VerticalLayout();
        Label label1 = new Label("The upper layout");
        vl1.addComponent(label1);

        // Button that hides or shows the bottom part
        button = new Button("show / hide", this);

        // The bottom layout
        vl2 = new VerticalLayout();
        TextField tf = new TextField("The bottom field");
        tf.setHeight("100%");
        vl2.addComponent(tf);

        // Add everything to the view
        mainLayout.addComponent(vl1);
        mainLayout.addComponent(button);
        mainLayout.addComponent(vl2);

        // Set expand ratios, hide lower
        mainLayout.setExpandRatio(vl1, 1);
        mainLayout.setExpandRatio(vl2, 0);

        // Maximize everything
        main.setSizeFull();
        mainLayout.setSizeFull();
        vl1.setSizeFull();
        vl2.setSizeFull();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (isVisible) {
            mainLayout.setExpandRatio(vl2, 0);
            isVisible = false;
        } else {
            mainLayout.setExpandRatio(vl2, 1);
            isVisible = true;
        }
    }

    @Override
    protected String getDescription() {
        return "Changing the expand ratio should repaint the layout correctly. Changing from 0 to something else should render the previously invisible component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2454;
    }
}
