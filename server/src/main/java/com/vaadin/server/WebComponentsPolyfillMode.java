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
package com.vaadin.server;

/**
 * The mode for deciding whether to automatically load the webcomponentsjs
 * polyfill when needed.
 */
public enum WebComponentsPolyfillMode {
    AUTOMATIC, NO, YES;

    /**
     * Checks if the polyfill should be loaded.
     * <p>
     * If mode is {@value #AUTOMATIC}, uses the given class loader to detect if
     * resources are present which indicates that the polyfill should be loaded.
     *
     * @param classLoader
     *            class loader to use when finding resources.
     * @return
     */
    public boolean shouldLoad(ClassLoader classLoader) {
        if (this == AUTOMATIC) {
            return isBowerJsonOnClasspath(classLoader);
        } else if (this == YES) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a bower.json file is available in the default package.
     *
     * @param classLoader
     *            the class loader to use for testing
     * @return <code>true</code> if a bower.json file exists, <code>false</code>
     *         otherwise
     */
    private static boolean isBowerJsonOnClasspath(ClassLoader classLoader) {
        return classLoader.getResource("/bower.json") != null;
    }
}
