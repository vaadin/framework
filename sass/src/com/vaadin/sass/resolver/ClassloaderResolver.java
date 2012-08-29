package com.vaadin.sass.resolver;

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
