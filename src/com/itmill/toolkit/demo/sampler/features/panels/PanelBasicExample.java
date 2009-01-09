package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class PanelBasicExample extends VerticalLayout implements ClickListener {

    private Panel p1;
    private Panel p2;

    public PanelBasicExample() {
        setSpacing(true);

        // First panel uses layout in which the components are added
        VerticalLayout vl = new VerticalLayout();
        Label l = new Label("Push the button to toggle caption.");
        Button b = new Button("Toggle caption");
        b.addListener(this);
        vl.setSpacing(true);
        vl.addComponent(l);
        vl.addComponent(b);
        p1 = new Panel("This is a standard Panel");
        p1.setLayout(vl);

        // We add the Label component directly to the second panel
        p2 = new Panel();
        p2.addComponent(new Label("This is a standard panel without caption."));

        addComponent(p1);
        addComponent(p2);
    }

    public void buttonClick(ClickEvent event) {
        if (p1.getCaption().equals("")) {
            p1.setCaption("This is a standard Panel");
        } else {
            p1.setCaption("");
        }
    }
}
