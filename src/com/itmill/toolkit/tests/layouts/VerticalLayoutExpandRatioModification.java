package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class VerticalLayoutExpandRatioModification extends TestBase implements
        ClickListener {

    private boolean isVisible = false;
    private VerticalLayout mainLayout;
    private VerticalLayout vl1;
    private VerticalLayout vl2;
    private Button button;

    public void setup() {
        Window main = new Window("The Main Window");
        mainLayout = new VerticalLayout();
        main.setLayout(mainLayout);
        setMainWindow(main);

        // The upper layout
        vl1 = new VerticalLayout();
        Label label1 = new Label("The upper layout");
        vl1.addComponent(label1);

        // Button that hides or shows the bottom part
        button = new Button("show / hide", this);

        // The bottom layout
        vl2 = new VerticalLayout();
        Label label2 = new Label("The bottom layout");
        vl2.addComponent(label2);

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
