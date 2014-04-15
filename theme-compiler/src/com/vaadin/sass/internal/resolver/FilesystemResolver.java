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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.w3c.css.sac.InputSource;

public class FilesystemResolver implements ScssStylesheetResolver {

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

        try {
            InputStream is = new FileInputStream(fileName);
            InputSource source = new InputSource();
            source.setByteStream(is);
            source.setURI(fileName);
            return source;

        } catch (FileNotFoundException e) {
            // not found, try something else
            return null;
        }

    }

}
