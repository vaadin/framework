package com.vaadin.sass.resolver;

import org.w3c.css.sac.InputSource;

public class VaadinResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {
        String ext = ".scss";
        if (identifier.endsWith(".css")) {
            ext = ".css";
        }

        // 'normalize' identifier to use in themeFile
        String fileName = identifier;
        if (identifier.endsWith(ext)) {
            identifier = identifier.substring(0,
                    identifier.length() - ext.length());
        }
        // also look here
        String themeFile = "VAADIN/themes/" + identifier + "/" + fileName;

        // first plain file
        ScssStylesheetResolver resolver = new FilesystemResolver();
        InputSource source = resolver.resolve(fileName);

        if (source == null) {
            // then file in theme
            source = resolver.resolve(themeFile);
        }

        if (source == null) {
            // then plain via classloader
            resolver = new ClassloaderResolver();
            source = resolver.resolve(identifier);

        }
        if (source == null) {
            // then try theme via classloader
            source = resolver.resolve(themeFile);
        }

        return source;
    }

}
