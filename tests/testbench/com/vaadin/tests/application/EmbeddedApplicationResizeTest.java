package com.vaadin.tests.application;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;

public class EmbeddedApplicationResizeTest extends TestBase {
    @Override
    protected String getDescription() {
        return "An undefined-height Application should not get a vertical scrollbar, "
                + "if it is embedded in a div, and the main body has a horizontal scrollbar. "
                + "<br>Run this application via <a href='/statictestfiles/ticket12336.html'>"
                + "/statictestfiles/ticket12336.html</a>. "
                + "<br>This application is called <code>"
                + getClass().getName() + "</code>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12336;
    }

    @Override
    protected void setup() {
        getMainWindow().getContent().setHeight(null);
        getMainWindow().getContent().setWidth("100%");
        addComponent(new Label(
                "<div style='background-color:#eee; height:40px'>I'm 100% wide, 40px high.</div>",
                Label.CONTENT_XHTML));
    }

}
