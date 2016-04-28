package com.vaadin.tests.urifragments;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SettingNullFragment extends AbstractTestUI {

    protected static final String BUTTON_FRAG_1_ID = "buttonFrag1";
    protected static final String BUTTON_NULL_FRAGMENT_ID = "buttonNullFragment";

    protected static final String FRAG_1_URI = "FRAG1";
    protected static final String NULL_FRAGMENT_URI = "";

    @Override
    protected void setup(VaadinRequest request) {
        Button button1 = new Button("Set Fragment");
        button1.setId(BUTTON_FRAG_1_ID);

        Button button2 = new Button("Set Null Fragment");
        button2.setId(BUTTON_NULL_FRAGMENT_ID);

        button1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Page.getCurrent().setUriFragment(FRAG_1_URI);
            }
        });

        button2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Page.getCurrent().setUriFragment(null);
            }
        });

        getLayout().addComponent(button1);
        getLayout().addComponent(button2);
    }

    @Override
    protected String getTestDescription() {
        return "Setting null as URI fragment should remove (clear) old fragment in the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11312;
    }
}
