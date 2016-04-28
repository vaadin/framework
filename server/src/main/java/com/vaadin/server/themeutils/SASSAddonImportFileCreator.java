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
package com.vaadin.server.themeutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.widgetsetutils.ClassPathExplorer;
import com.vaadin.server.widgetsetutils.ClassPathExplorer.LocationInfo;

/**
 * Helper class for managing the addon imports and creating an a SCSS file for
 * importing all your addon themes. The helper method searches the classpath for
 * Vaadin addons and uses the 'Vaadin-Themes' metadata to create the imports.
 * 
 * <p>
 * The addons.scss is always overwritten when this tool is invoked.
 * </p>
 * 
 * @since 7.1
 */
public class SASSAddonImportFileCreator {

    private static final String ADDON_IMPORTS_FILE = "addons.scss";

    private static final String ADDON_IMPORTS_FILE_TEXT = "This file is automatically managed and "
            + "will be overwritten from time to time.";

    /**
     * 
     * @param args
     *            Theme directory where the addons.scss file should be created
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
        } else {
            String themeDirectory = args[0];
            updateTheme(themeDirectory);
        }
    }

    /**
     * Updates a themes addons.scss with the addon themes found on the classpath
     * 
     * @param themeDirectory
     *            The target theme directory
     */
    public static void updateTheme(String themeDirectory) throws IOException {

        File addonImports = new File(themeDirectory, ADDON_IMPORTS_FILE);

        if (!addonImports.exists()) {

            // Ensure directory exists
            addonImports.getParentFile().mkdirs();

            // Ensure file exists
            addonImports.createNewFile();
        }

        LocationInfo info = ClassPathExplorer
                .getAvailableWidgetSetsAndStylesheets();

        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(
                    addonImports));

            printStream.println("/* " + ADDON_IMPORTS_FILE_TEXT + " */");

            printStream.println("/* Do not manually edit this file. */");

            printStream.println();

            Map<String, URL> addonThemes = info.getAddonStyles();

            // Sort addon styles so that CSS imports are first and SCSS import
            // last
            List<String> paths = new ArrayList<String>(addonThemes.keySet());
            Collections.sort(paths, new Comparator<String>() {

                @Override
                public int compare(String path1, String path2) {
                    if (path1.toLowerCase().endsWith(".css")
                            && path2.toLowerCase().endsWith(".scss")) {
                        return -1;
                    }
                    if (path1.toLowerCase().endsWith(".scss")
                            && path2.toLowerCase().endsWith(".css")) {
                        return 1;
                    }
                    return 0;
                }
            });

            List<String> mixins = new ArrayList<String>();
            for (String path : paths) {
                mixins.addAll(addImport(printStream, path,
                        addonThemes.get(path)));
                printStream.println();
            }

            createAddonsMixin(printStream, mixins);

        } catch (FileNotFoundException e) {
            // Should not happen since file is checked before this
            getLogger().log(Level.WARNING, "Error updating addons.scss", e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(SASSAddonImportFileCreator.class.getName());
    }

    private static List<String> addImport(PrintStream stream, String file,
            URL location) {

        // Add import comment
        printImportComment(stream, location);

        List<String> foundMixins = new ArrayList<String>();

        if (file.endsWith(".css")) {
            stream.print("@import url(\"../../../" + file + "\");\n");
        } else {
            // Assume SASS
            stream.print("@import \"../../../" + file + "\";\n");

            // Convention is to name the mixin after the stylesheet. Strip
            // .scss from filename
            String mixin = file.substring(file.lastIndexOf("/") + 1,
                    file.length() - ".scss".length());

            foundMixins.add(mixin);
        }

        stream.println();

        return foundMixins;
    }

    private static void printImportComment(PrintStream stream, URL location) {

        // file:/absolute/path/to/addon.jar!/
        String path = location.getPath();

        try {
            // Try to parse path for better readability
            path = path.substring(path.lastIndexOf(":") + 1,
                    path.lastIndexOf("!"));

            // Extract jar archive filename
            path = path.substring(path.lastIndexOf("/") + 1);

        } catch (Exception e) {
            // Parsing failed but no worries, we then use whatever
            // location.getPath() returns
        }

        stream.println("/* Provided by " + path + " */");
    }

    private static void createAddonsMixin(PrintStream stream,
            List<String> mixins) {

        stream.println("/* Import and include this mixin into your project theme to include the addon themes */");
        stream.println("@mixin addons {");
        for (String addon : mixins) {
            stream.println("\t@include " + addon + ";");
        }
        stream.println("}");
        stream.println();
    }

    private static void printUsage() {
        String className = SASSAddonImportFileCreator.class.getSimpleName();
        PrintStream o = System.out;
        o.println(className + " usage:");
        o.println();
        o.println("./" + className + " [Path to target theme folder]");
    }
}
