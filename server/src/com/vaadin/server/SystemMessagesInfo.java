package com.vaadin.server;

import java.io.Serializable;
import java.util.Locale;

public class SystemMessagesInfo implements Serializable {

    private Locale locale;
    private VaadinRequest request;
    private VaadinService service;

    /**
     * The locale of the UI related to the {@link SystemMessages} request.
     * 
     * @return The Locale or null if the locale is not known
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the request currently in progress.
     * 
     * @return The request currently in progress or null if no request is in
     *         progress.
     */
    public VaadinRequest getRequest() {
        return request;
    }

    public void setRequest(VaadinRequest request) {
        this.request = request;
    }

    /**
     * Returns the service this SystemMessages request comes from.
     * 
     * @return The service which triggered this request or null of not triggered
     *         from a service.
     */
    public VaadinService getService() {
        return service;
    }

    public void setService(VaadinService service) {
        this.service = service;
    }

}
