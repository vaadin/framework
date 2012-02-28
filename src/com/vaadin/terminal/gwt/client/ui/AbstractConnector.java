package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Connector;

public abstract class AbstractConnector implements Connector {

    private ApplicationConnection connection;
    private String id;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintable#getConnection()
     */
    public final ApplicationConnection getConnection() {
        return connection;
    }

    private final void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Connector#getId()
     */
    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * Called once by the framework to initialize the connector.
     * 
     * Custom widgets should not override this method, override init instead;
     * 
     * Note that the shared state is not yet available at this point.
     */
    public final void doInit(String connectorId,
            ApplicationConnection connection) {
        setConnection(connection);
        setId(connectorId);

        init();
    }

    /**
     * Called when the connector has been initialized. Override this method to
     * perform initialization of the connector.
     */
    // FIXME: It might make sense to make this abstract to force users to use
    // init instead of constructor, where connection and id has not yet been
    // set.
    protected void init() {

    }

}
