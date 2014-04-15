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
import java.io.InputStream;

import org.w3c.css.sac.InputSource;

public class ClassloaderResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {
        // identifier should not have .scss, fileName should
        String ext = ".scss";
        if (identifier.endsWith(".css")) {
            ext = ".css";
        }
        String fileName = identifier;
        if (identifier.endsWith(ext)) {
            identifier = identifier.substring(0,
                    identifier.length() - ext.length());
        } else {
            fileName = fileName + ext;
        }

        // Ensure only "/" is used, also in Windows
        fileName = fileName.replace(File.separatorChar, '/');

        // Filename should be a relative path starting with VAADIN/...
        int vaadinIdx = fileName.lastIndexOf("VAADIN/");
        if (vaadinIdx > -1) {
            fileName = fileName.substring(vaadinIdx);
        }

        // Can the classloader find it?
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                fileName);
        if (is != null) {
            InputSource source = new InputSource();
            source.setByteStream(is);
            source.setURI(fileName);
            return source;

        } else {
            return null;
        }

    }

}
