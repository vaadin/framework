/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.sass.internal.resolver;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.css.sac.InputSource;

public class VaadinResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {

        /*
         * Normalize classpath so ../../ segments are resolved
         */
        try {
            identifier = new URI(identifier).normalize().getPath();
        } catch (URISyntaxException e) {
            // No worries, continuing with the unnormalized path and hope for
            // the best
        }

        InputSource source = null;
        
        // Can we find the scss from the file system?
        ScssStylesheetResolver resolver = new FilesystemResolver();
        source = resolver.resolve(identifier);

        if (source == null) {
            // How about the classpath?
            resolver = new ClassloaderResolver();
            source = resolver.resolve(identifier);
        }

        return source;
    }
}
