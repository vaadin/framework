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
package com.vaadin.sass.internal.resolver;

import java.io.File;
import java.util.Stack;

import org.w3c.css.sac.InputSource;

public class VaadinResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {

        // Remove extra "." and ".."
        identifier = normalize(identifier);

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

    /**
     * Normalizes "." and ".." from the path string where parent path segments
     * can be removed. Preserve leading "..".
     * 
     * @param path
     *            A relative or absolute file path
     * @return The normalized path
     */
    private static String normalize(String path) {

        // Ensure only "/" is used, also in Windows
        path = path.replace(File.separatorChar, '/');

        // Split into segments
        String[] segments = path.split("/");
        Stack<String> result = new Stack<String>();

        // Replace '.' and '..' segments
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equals(".")) {
                // Segments marked '.' are ignored

            } else if (segments[i].equals("..") && !result.isEmpty()
                    && !result.lastElement().equals("..")) {
                // If segment is ".." then remove the previous iff the previous
                // element is not a ".." and the result stack is not empty
                result.pop();
            } else {
                // Other segments are just added to the stack
                result.push(segments[i]);
            }
        }

        // Reconstruct path
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) {
                pathBuilder.append("/");
            }
            pathBuilder.append(result.get(i));
        }
        return pathBuilder.toString();
    }

}
