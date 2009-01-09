package com.itmill.toolkit.demo.sampler.features.accordions;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class AccordionDisabledExample extends VerticalLayout implements
        Accordion.SelectedTabChangeListener, Button.ClickListener {

    private Accordion a;
    private Button b1;
    private Button b2;
    private Label l1;
    private Label l2;
    private Label l3;

    public AccordionDisabledExample() {
        setSpacing(true);

        l1 = new Label("There are no previously saved actions.");
        l2 = new Label("There are no saved notes.");
        l3 = new Label("There are currently no issues.");

        ThemeResource i1 = new ThemeResource("icons/action_save.gif");
        ThemeResource i2 = new ThemeResource("icons/comment_yellow.gif");
        ThemeResource i3 = new ThemeResource("icons/icon_info.gif");

        a = new Accordion();
        a.setHeight(400, UNITS_PIXELS);
        a.setWidth(400, UNITS_PIXELS);
        a.addTab(l1, "Saved actions", i1);
        a.addTab(l2, "Notes", i2);
        a.addTab(l3, "Issues", i3);
        a.addListener(this);

        b1 = new Button("Disable 'Notes' tab");
        b2 = new Button("Hide 'Issues' tab");
        b1.addListener(this);
        b2.addListener(this);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(b1);
        hl.addComponent(b2);

        addComponent(a);
        addComponent(hl);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        String c = a.getTabCaption(event.getTabSheet().getSelectedTab());
        getWindow().showNotification("Selected tab: " + c);
    }

    public void buttonClick(ClickEvent event) {
        if (b1.equals(event.getButton())) { // b1 clicked
            if (l2.isEnabled()) {
                l2.setEnabled(false);
                b1.setCaption("Enable 'Notes' tab");
            } else {
                l2.setEnabled(true);
                b1.setCaption("Disable 'Notes' tab");
            }
        } else { // b2 clicked
            if (l3.isVisible()) {
                l3.setVisible(false);
                b2.setCaption("Show 'Issues' tab");
            } else {
                l3.setVisible(true);
                b2.setCaption("Hide 'Issues' tab");
            }
        }
        a.requestRepaint();
    }
}
