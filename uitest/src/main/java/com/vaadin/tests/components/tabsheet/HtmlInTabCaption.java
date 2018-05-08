package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class HtmlInTabCaption extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);
        TabSheet ts = new TabSheet();
        ts.setCaption("TabSheet - no <u>html</u> in tab captions");
        ts.setCaptionAsHtml(true);
        ts.addTab(new Label(), "<font color='red'>red</font>");
        ts.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(ts);

        ts = new TabSheet();
        ts.setCaption("TabSheet - <b>html</b> in tab captions");
        ts.setCaptionAsHtml(false);
        ts.setTabCaptionsAsHtml(true);
        ts.addTab(new Label(), "<font color='red'>red</font>");
        ts.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(ts);

        Accordion acc = new Accordion();
        acc.setCaption("Accordion - no <u>html</u> in tab captions");
        acc.setCaptionAsHtml(true);
        acc.addTab(new Label(), "<font color='red'>red</font>");
        acc.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(acc);

        acc = new Accordion();
        acc.setCaption("Accordion - <b>html</b> in tab captions");
        acc.setCaptionAsHtml(false);
        acc.setTabCaptionsAsHtml(true);
        acc.addTab(new Label(), "<font color='red'>red</font>");
        acc.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(acc);

    }

    @Override
    protected Integer getTicketNumber() {
        return 14609;
    }

}
