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
