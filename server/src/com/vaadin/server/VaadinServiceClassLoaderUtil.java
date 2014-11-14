/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Utility class used by {@link VaadinService#setDefaultClassLoader()}.
 * 
 * @since
 * @author Vaadin Ltd
 */
class VaadinServiceClassLoaderUtil {

    private static class GetClassLoaderPrivilegedAction implements
            PrivilegedAction<ClassLoader> {
        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * Called by {@link VaadinService#setDefaultClassLoader()} to acquire
     * appropriate class loader to load application's classes (e.g. UI). Calls
     * should be guarded by try/catch block to catch SecurityException and log
     * appropriate message. The code for this method is modeled after
     * recommendations laid out by JEE 5 specification sections EE.6.2.4.7 and
     * EE.8.2.5
     * 
     * @return Instance of {@link ClassLoader} that should be used by this
     *         instance of {@link VaadinService}
     * @throws SecurityException
     *             if current security policy doesn't allow acquiring current
     *             thread's context class loader
     */
    static protected ClassLoader findDefaultClassLoader()
            throws SecurityException {
        return AccessController
                .doPrivileged(new VaadinServiceClassLoaderUtil.GetClassLoaderPrivilegedAction());
    }

}
