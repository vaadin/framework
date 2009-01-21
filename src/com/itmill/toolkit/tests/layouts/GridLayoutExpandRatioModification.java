package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class GridLayoutExpandRatioModification extends TestBase implements
        ClickListener {

    private boolean isVisible = false;
    private GridLayout mainLayout;
    private VerticalLayout vl1;
    private VerticalLayout vl2;
    private Button button;

    public void setup() {
        Window main = new Window("The Main Window");
        mainLayout = new GridLayout(3, 3);
        main.setLayout(mainLayout);
        setMainWindow(main);

        // The upper layout
        vl1 = new VerticalLayout();
        Label label1 = new Label("The upper/left layout");
        vl1.addComponent(label1);

        // Button that hides or shows the bottom part
        button = new Button("show / hide", this);

        // The bottom layout
        vl2 = new VerticalLayout();
        TextField tf = new TextField("The bottom/right field");
        tf.setHeight("100%");
        tf.setWidth("100%");
        vl2.addComponent(tf);

        // Add everything to the view
        mainLayout.addComponent(vl1, 0, 0);
        mainLayout.addComponent(button, 1, 1);
        mainLayout.addComponent(vl2, 2, 2);

        // Set expand ratios, hide lower
        mainLayout.setRowExpandRatio(0, 1);
        mainLayout.setColumnExpandRatio(0, 1);
        mainLayout.setRowExpandRatio(2, 0);
        mainLayout.setColumnExpandRatio(2, 0);

        // Maximize everything
        main.setSizeFull();
        mainLayout.setSizeFull();
        vl1.setSizeFull();
        vl2.setSizeFull();
    }

    public void buttonClick(ClickEvent event) {
        if (isVisible) {
            mainLayout.setRowExpandRatio(2, 0);
            mainLayout.setColumnExpandRatio(2, 0);
            isVisible = false;
        } else {
            mainLayout.setRowExpandRatio(2, 1);
            mainLayout.setColumnExpandRatio(2, 1);
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
