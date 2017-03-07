/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.data.util.sqlcontainer.connection;

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
    public Context getInitialContext(java.util.Hashtable<?, ?> environment)
            throws NamingException {
        if (mockCtx == null) {
            throw new IllegalStateException("mock context was not set.");
        }
        return mockCtx;
    }
}
