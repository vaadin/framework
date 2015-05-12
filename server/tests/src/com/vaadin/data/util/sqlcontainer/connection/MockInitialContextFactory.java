package com.vaadin.data.util.sqlcontainer.connection;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.junit.Test;

/**
 * Provides a JNDI initial context factory for the MockContext.
 */
public class MockInitialContextFactory implements InitialContextFactory {
    private static Context mockCtx = null;

    public static void setMockContext(Context ctx) {
        mockCtx = ctx;
    }

    @Override
    public Context getInitialContext(java.util.Hashtable<?, ?> environment)
            throws NamingException {
        if (mockCtx == null) {
            throw new IllegalStateException("mock context was not set.");
        }
        return mockCtx;
    }
}
