package com.vaadin.tests.resources;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

/**
 * This class tests the caching behavior of PublishedFileHandler.
 *
 * Previously PublishedFileHandler did not include cache headers in it's
 * responses. Unfortunately there isn't a good way to automate this test, so
 * manual testing is required at this time. To test the caching behavior run
 * this file as a java application on the development server debug
 * configuration, and access it through the url
 * http://localhost:8888/run/com.vaadin
 * .tests.resources.PublishedFileHandlerCaching?restartApplication
 *
 * On loading the page you'll need to examine the network traffic (e.g. with
 * FireBug), keeping an eye on the GET requests for cachingtest.js and it's
 * cache headers.
 *
 * @author Vaadin Ltd
 */
public class PublishedFileHandlerCaching extends AbstractReindeerTestUI {

    /**
     * generated serialVersionUID
     */
    private static final long serialVersionUID = 2275457343547688505L;

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new CachingJavaScriptComponent());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test that PublishedFileHandler includes appropriate cache headers.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return new Integer(13574);
    }

}
