package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class PanelLightExample extends VerticalLayout implements ClickListener {

    private Panel p;

    public PanelLightExample() {
        setSpacing(true);

        // First panel uses layout in which the components are added
        VerticalLayout vl = new VerticalLayout();
        Label l = new Label("Push the button to toggle style.");
        Button b = new Button("Toggle style");
        b.addListener(this);
        vl.setSpacing(true);
        vl.addComponent(l);
        vl.addComponent(b);
        p = new Panel("This is a light Panel");
        p.setStyleName("light");
        p.setLayout(vl);

        addComponent(p);
    }

    public void buttonClick(ClickEvent event) {
        if (p.getStyleName().contains("light")) {
            p.removeStyleName("light");
            p.setCaption("This is a standard Panel");
        } else {
            p.setStyleName("light");
            p.setCaption("This is a light Panel");
        }
    }
}
