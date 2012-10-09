package com.vaadin.sass.resolver;

import java.io.File;

import org.w3c.css.sac.InputSource;

public class VaadinResolver implements ScssStylesheetResolver {

    @Override
    public InputSource resolve(String identifier) {
        if (identifier.endsWith(".css")) {
            ScssStylesheetResolver resolver = new FilesystemResolver();
            return resolver.resolve(identifier);
        }

        if (identifier.endsWith(".scss")) {
            identifier = identifier.substring(0,
                    identifier.length() - ".scss".length());
        }
        String fileName = identifier + ".scss";

        String name = new File(identifier).getName();
        File parent = new File(identifier).getParentFile();
        if (parent != null) {
            parent = parent.getParentFile();
        }

        String themeFile = (parent == null ? "" : parent + "/") + name + "/"
                + name + ".scss";

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
            source = resolver.resolve("VAADIN/themes/" + themeFile);
        }

        return source;
    }
}
