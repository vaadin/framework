package com.vaadin.tests.components.browserframe;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.TabSheet;

public class BrowserFrameDoubleScrollbars extends AbstractTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 11780;
    }

    @Override
    protected void setup(VaadinRequest request) {

        getLayout().setHeight("100%");
        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        getLayout().addComponent(tabs);

        BrowserFrame help = new BrowserFrame();
        help.setSizeFull();
        help.setSource(new ExternalResource("/statictestfiles/long-html.htm"));

        tabs.addComponent(help);

    }

    @Override
    protected String getTestDescription() {
        return "Embedded browser causes second scrollbar";
    }

}
