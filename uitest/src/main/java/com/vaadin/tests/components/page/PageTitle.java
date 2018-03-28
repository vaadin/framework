package com.vaadin.tests.components.page;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

@Title("bar")
public class PageTitle extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String title = request.getParameter("title");
        if (title != null) {
            getPage().setTitle(title);
        }

    }

    @Override
    protected String getTestDescription() {
        return "Sets the title according to a given ?title parameter. By default the ApplicationServletRunner will set the title to the fully qualified class name";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13430;
    }

}
