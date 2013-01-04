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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.css.sac.InputSource;

public class VaadinResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {
        if (identifier.endsWith(".css")) {
            // CSS support mainly for testing, don't load from classpath etc
            ScssStylesheetResolver resolver = new FilesystemResolver();
            return resolver.resolve(identifier);
        }

        InputSource source = null;

        Pattern pattern = Pattern
                .compile("\\.\\.\\/([^\\/]+)\\/([^\\/]+\\.scss)");
        Matcher matcher = pattern.matcher(identifier);

        if (matcher.find()) {
            // theme include
            ScssStylesheetResolver resolver = new FilesystemResolver();
            source = resolver.resolve(identifier);

            if (source == null) {
                String themeName = matcher.group(1);
                String fileName = matcher.group(2);
                resolver = new ClassloaderResolver();
                String id = "VAADIN/themes/" + themeName + "/" + fileName;
                source = resolver.resolve(id);
            }

        } else {
            ScssStylesheetResolver resolver = new FilesystemResolver();
            source = resolver.resolve(identifier);

            if (source == null) {
                resolver = new ClassloaderResolver();
                source = resolver.resolve(identifier);
            }
        }

        return source;
    }
}
