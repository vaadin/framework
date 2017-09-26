package com.vaadin.v7.data.util.sqlcontainer.connection;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Provides a JNDI initial context factory for the MockContext.
 */
public class MockInitialContextFactory implements InitialContextFactory {
    private static Context mockCtx = null;

    public static void setMockContext(Context ctx) {
        mockCtx = ctx;
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment)
            throws NamingException {
        if (mockCtx == null) {
            throw new IllegalStateException("mock context was not set.");
        }
        return mockCtx;
    }
}
