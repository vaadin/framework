package com.vaadin.demo.sampler.features.accordions;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;

public class AccordionDisabledExample extends VerticalLayout implements
        Accordion.SelectedTabChangeListener, Button.ClickListener {

    private Accordion a;
    private Button b1;
    private Button b2;
    private Label l1;
    private Label l2;
    private Label l3;
    private Tab t1;
    private Tab t2;
    private Tab t3;

    private static final ThemeResource icon1 = new ThemeResource(
            "icons/action_save.gif");
    private static final ThemeResource icon2 = new ThemeResource(
            "icons/comment_yellow.gif");
    private static final ThemeResource icon3 = new ThemeResource(
            "icons/icon_info.gif");

    public AccordionDisabledExample() {
        setSpacing(true);

        l1 = new Label("There are no previously saved actions.");
        l2 = new Label("There are no saved notes.");
        l3 = new Label("There are currently no issues.");

        a = new Accordion();
        a.setHeight("300px");
        a.setWidth("400px");
        t1 = a.addTab(l1, "Saved actions", icon1);
        t2 = a.addTab(l2, "Notes", icon2);
        t3 = a.addTab(l3, "Issues", icon3);
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
        String c = a.getTab(event.getTabSheet().getSelectedTab()).getCaption();
        getWindow().showNotification("Selected tab: " + c);
    }

    public void buttonClick(ClickEvent event) {
        if (b1.equals(event.getButton())) { // b1 clicked
            if (t2.isEnabled()) {
                t2.setEnabled(false);
                b1.setCaption("Enable 'Notes' tab");
            } else {
                t2.setEnabled(true);
                b1.setCaption("Disable 'Notes' tab");
            }
        } else { // b2 clicked
            if (t3.isVisible()) {
                t3.setVisible(false);
                b2.setCaption("Show 'Issues' tab");
            } else {
                t3.setVisible(true);
                b2.setCaption("Hide 'Issues' tab");
            }
        }
        a.requestRepaint();
    }
}
