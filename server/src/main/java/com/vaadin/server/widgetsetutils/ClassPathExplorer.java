/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.server.widgetsetutils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Utility class to collect widgetset related information from classpath.
 * Utility will seek all directories from classpaths, and jar files having
 * "Vaadin-Widgetsets" key in their manifest file.
 * <p>
 * Used by WidgetMapGenerator and ide tools to implement some monkey coding for
 * you.
 * <p>
 * Developer notice: If you end up reading this comment, I guess you have faced
 * a sluggish performance of widget compilation or unreliable detection of
 * components in your classpaths. The thing you might be able to do is to use
 * annotation processing tool like apt to generate the needed information. Then
 * either use that information in {@link WidgetMapGenerator} or create the
 * appropriate monkey code for gwt directly in annotation processor and get rid
 * of {@link WidgetMapGenerator}. Using annotation processor might be a good
 * idea when dropping Java 1.5 support (integrated to javac in 6).
 *
 */
public class ClassPathExplorer {

    private static final String VAADIN_ADDON_VERSION_ATTRIBUTE = "Vaadin-Package-Version";

    /**
     * File filter that only accepts directories.
     */
    private static final FileFilter DIRECTORIES_ONLY = (File f) -> f.exists()
            && f.isDirectory();

    /**
     * Contains information about widgetsets and themes found on the classpath.
     *
     * @since 7.1
     */
    public static class LocationInfo {

        private final Map<String, URL> widgetsets;

        private final Map<String, URL> addonStyles;

        public LocationInfo(Map<String, URL> widgetsets,
                Map<String, URL> themes) {
            this.widgetsets = widgetsets;
            addonStyles = themes;
        }

        public Map<String, URL> getWidgetsets() {
            return widgetsets;
        }

        public Map<String, URL> getAddonStyles() {
            return addonStyles;
        }

    }

    /**
     * Raw class path entries as given in the java class path string. Only
     * entries that could include widgets/widgetsets are listed (primarily
     * directories, Vaadin JARs and add-on JARs).
     */
    private static final List<String> RAW_CLASSPATH_ENTRIES = getRawClasspathEntries();

    /**
     * Map from identifiers (either a package name preceded by the path and a
     * slash, or a URL for a JAR file) to the corresponding URLs. This is
     * constructed from the class path.
     */
    private static final Map<String, URL> CLASSPATH_LOCATIONS = getClasspathLocations(
            RAW_CLASSPATH_ENTRIES);

    private static boolean debug = false;

    static {
        String debugProperty = System.getProperty("debug");
        if (debugProperty != null && !debugProperty.isEmpty()) {
            debug = true;
        }
    }

    /**
     * No instantiation from outside, callable methods are static.
     */
    private ClassPathExplorer() {
    }

    /**
     * Finds the names and locations of widgetsets available on the class path.
     *
     * @return map from widgetset classname to widgetset location URL
     * @deprecated Use {@link #getAvailableWidgetSetsAndStylesheets()} instead
     */
    @Deprecated
    public static Map<String, URL> getAvailableWidgetSets() {
        return getAvailableWidgetSetsAndStylesheets().getWidgetsets();
    }

    /**
     * Finds the names and locations of widgetsets and themes available on the
     * class path.
     *
     * @return
     */
    public static LocationInfo getAvailableWidgetSetsAndStylesheets() {
        long start = System.currentTimeMillis();
        Map<String, URL> widgetsets = new HashMap<>();
        Map<String, URL> themes = new HashMap<>();
        Set<String> keySet = CLASSPATH_LOCATIONS.keySet();
        for (String location : keySet) {
            searchForWidgetSetsAndAddonStyles(location, widgetsets, themes);
        }
        long end = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("Widgetsets found from classpath:\n");
        for (String ws : widgetsets.keySet()) {
            sb.append("\t");
            sb.append(ws);
            sb.append(" in ");
            sb.append(widgetsets.get(ws));
            sb.append("\n");
        }

        sb.append("Addon styles found from classpath:\n");
        for (String theme : themes.keySet()) {
            sb.append("\t");
            sb.append(theme);
            sb.append(" in ");
            sb.append(themes.get(theme));
            sb.append("\n");
        }

        log(sb.toString());
        log("Search took " + (end - start) + "ms");
        return new LocationInfo(widgetsets, themes);
    }

    /**
     * Finds all GWT modules / Vaadin widgetsets and Addon styles in a valid
     * location.
     *
     * If the location is a directory, all GWT modules (files with the
     * ".gwt.xml" extension) are added to widgetsets.
     *
     * If the location is a JAR file, the comma-separated values of the
     * "Vaadin-Widgetsets" attribute in its manifest are added to widgetsets.
     *
     * @param locationString
     *            an entry in {@link #CLASSPATH_LOCATIONS}
     * @param widgetsets
     *            a map from widgetset name (including package, with dots as
     *            separators) to a URL (see {@link #CLASSPATH_LOCATIONS}) - new
     *            entries are added to this map
     */
    private static void searchForWidgetSetsAndAddonStyles(String locationString,
            Map<String, URL> widgetsets, Map<String, URL> addonStyles) {

        URL location = CLASSPATH_LOCATIONS.get(locationString);
        File directory = new File(location.getFile());

        if (directory.exists() && !directory.isHidden()) {
            // Get the list of the files contained in the directory
            for (String file : directory.list()) {
                // we are only interested in .gwt.xml files
                if (!file.endsWith(".gwt.xml")) {
                    continue;
                }

                // remove the .gwt.xml extension
                String classname = file.substring(0, file.length() - 8);
                String packageName = locationString
                        .substring(locationString.lastIndexOf('/') + 1);
                classname = packageName + "." + classname;

                if (!WidgetSetBuilder.isWidgetset(classname)) {
                    // Only return widgetsets and not GWT modules to avoid
                    // comparing modules and widgetsets
                    continue;
                }

                if (!widgetsets.containsKey(classname)) {
                    String packagePath = packageName.replaceAll("\\.", "/");

                    String basePath = location.getFile();
                    if (basePath.endsWith("/" + packagePath)) {
                        basePath = basePath.replaceAll("/" + packagePath + "$",
                                "");
                    } else if (basePath.endsWith("/" + packagePath + "/")) {
                        basePath = basePath.replaceAll("/" + packagePath + "/$",
                                "");
                    } else {
                        throw new IllegalStateException(
                                "Error trying to find base path, location ("
                                        + location.getFile()
                                        + ") does not end in expected '/"
                                        + packagePath + "'");
                    }
                    try {
                        URL url = new URL(location.getProtocol(),
                                location.getHost(), location.getPort(),
                                basePath);
                        widgetsets.put(classname, url);
                    } catch (MalformedURLException e) {
                        // should never happen as based on an existing URL,
                        // only changing end of file name/path part
                        error("Error locating the widgetset " + classname, e);
                    }
                }
            }
        } else {

            try {
                // check files in jar file, entries will list all directories
                // and files in jar

                URLConnection openConnection = location.openConnection();
                if (openConnection instanceof JarURLConnection) {
                    JarURLConnection conn = (JarURLConnection) openConnection;

                    JarFile jarFile = conn.getJarFile();

                    Manifest manifest = jarFile.getManifest();
                    if (manifest == null) {
                        // No manifest so this is not a Vaadin Add-on
                        return;
                    }

                    // Check for widgetset attribute
                    String value = manifest.getMainAttributes()
                            .getValue("Vaadin-Widgetsets");
                    if (value != null) {
                        String[] widgetsetNames = value.split(",");
                        for (String widgetsetName : widgetsetNames) {
                            String widgetsetname = widgetsetName.trim();
                            if (!widgetsetname.isEmpty()) {
                                widgetsets.put(widgetsetname, location);
                            }
                        }
                    }

                    // Check for theme attribute
                    value = manifest.getMainAttributes()
                            .getValue("Vaadin-Stylesheets");
                    if (value != null) {
                        String[] stylesheets = value.split(",");
                        for (String stylesheet1 : stylesheets) {
                            String stylesheet = stylesheet1.trim();
                            if (!stylesheet.isEmpty()) {
                                addonStyles.put(stylesheet, location);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                error("Error parsing jar file", e);
            }

        }
    }

    /**
     * Splits the current class path into entries, and filters them accepting
     * directories, Vaadin add-on JARs with widgetsets and Vaadin JARs.
     *
     * Some other non-JAR entries may also be included in the result.
     *
     * @return filtered list of class path entries
     */
    private static final List<String> getRawClasspathEntries() {
        // try to keep the order of the classpath
        List<String> locations = new ArrayList<>();

        String pathSep = System.getProperty("path.separator");
        String classpath = System.getProperty("java.class.path");

        if (classpath.startsWith("\"")) {
            classpath = classpath.substring(1);
        }
        if (classpath.endsWith("\"")) {
            classpath = classpath.substring(0, classpath.length() - 1);
        }

        debug("Classpath: " + classpath);

        String[] split = classpath.split(pathSep);
        for (String classpathEntry : split) {
            if (acceptClassPathEntry(classpathEntry)) {
                locations.add(classpathEntry);
            }
        }

        return locations;
    }

    /**
     * Determine every URL location defined by the current classpath, and it's
     * associated package name.
     *
     * See {@link #CLASSPATH_LOCATIONS} for information on output format.
     *
     * @param rawClasspathEntries
     *            raw class path entries as split from the Java class path
     *            string
     * @return map of classpath locations, see {@link #CLASSPATH_LOCATIONS}
     */
    private static final Map<String, URL> getClasspathLocations(
            List<String> rawClasspathEntries) {
        long start = System.currentTimeMillis();
        // try to keep the order of the classpath
        Map<String, URL> locations = new LinkedHashMap<>();
        for (String classpathEntry : rawClasspathEntries) {
            File file = new File(classpathEntry);
            include(null, file, locations);
        }
        long end = System.currentTimeMillis();
        if (debug) {
            debug("getClassPathLocations took " + (end - start) + "ms");
        }
        return locations;
    }

    /**
     * Checks a class path entry to see whether it can contain widgets and
     * widgetsets.
     *
     * All directories are automatically accepted. JARs are accepted if they
     * have the "Vaadin-Widgetsets" attribute in their manifest or the JAR file
     * name contains "vaadin-" or ".vaadin.".
     *
     * Also other non-JAR entries may be accepted, the caller should be prepared
     * to handle them.
     *
     * @param classpathEntry
     *            class path entry string as given in the Java class path
     * @return true if the entry should be considered when looking for widgets
     *         or widgetsets
     */
    private static boolean acceptClassPathEntry(String classpathEntry) {
        if (!classpathEntry.endsWith(".jar")) {
            // accept all non jars (practically directories)
            return true;
        } else {
            // accepts jars that comply with vaadin-component packaging
            // convention (.vaadin. or vaadin- as distribution packages),
            if (classpathEntry.contains("vaadin-")
                    || classpathEntry.contains(".vaadin.")) {
                return true;
            } else {
                URL url;
                try {
                    url = new URL("file:"
                            + new File(classpathEntry).getCanonicalPath());
                    url = new URL("jar:" + url.toExternalForm() + "!/");
                    JarURLConnection conn = (JarURLConnection) url
                            .openConnection();
                    debug(url.toString());

                    JarFile jarFile = conn.getJarFile();
                    Manifest manifest = jarFile.getManifest();
                    if (manifest != null) {
                        Attributes mainAttributes = manifest
                                .getMainAttributes();
                        if (mainAttributes
                                .getValue("Vaadin-Widgetsets") != null) {
                            return true;
                        }
                        if (mainAttributes
                                .getValue("Vaadin-Stylesheets") != null) {
                            return true;
                        }
                    }
                } catch (MalformedURLException e) {
                    if (debug) {
                        error("Failed to inspect JAR file", e);
                    }
                } catch (IOException e) {
                    if (debug) {
                        error("Failed to inspect JAR file", e);
                    }
                }

                return false;
            }
        }
    }

    /**
     * Recursively add subdirectories and jar files to locations - see
     * {@link #CLASSPATH_LOCATIONS}.
     *
     * @param name
     * @param file
     * @param locations
     */
    private static final void include(String name, File file,
            Map<String, URL> locations) {
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            // could be a JAR file
            includeJar(file, locations);
            return;
        }

        if (file.isHidden() || file.getPath().contains(File.separator + ".")) {
            return;
        }

        if (name == null) {
            name = "";
        } else {
            name += ".";
        }

        // add all directories recursively
        for (File dir : file.listFiles(DIRECTORIES_ONLY)) {
            try {
                // add the present directory
                if (!dir.isHidden()
                        && !dir.getPath().contains(File.separator + ".")) {
                    String key = dir.getCanonicalPath() + "/" + name
                            + dir.getName();
                    URL url = dir.getCanonicalFile().toURI().toURL();
                    locations.put(key, url);
                }
            } catch (Exception ioe) {
                return;
            }
            include(name + dir.getName(), dir, locations);
        }
    }

    /**
     * Add a jar file to locations - see {@link #CLASSPATH_LOCATIONS}.
     *
     * @param file
     * @param locations
     */
    private static void includeJar(File file, Map<String, URL> locations) {
        try {
            URL url = new URL("file:" + file.getCanonicalPath());
            url = new URL("jar:" + url.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            JarFile jarFile = conn.getJarFile();
            if (jarFile != null) {
                // the key does not matter here as long as it is unique
                locations.put(url.toString(), url);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return;
        }

    }

    /**
     * Find and return the default source directory where to create new
     * widgetsets.
     *
     * Return the first directory (not a JAR file etc.) on the classpath by
     * default.
     *
     * TODO this could be done better...
     *
     * @return URL
     */
    public static URL getDefaultSourceDirectory() {
        return getWidgetsetSourceDirectory(null);
    }

    /**
     * Find and return the source directory which contains the given widgetset
     * file.
     *
     * If not applicable or widgetsetFileName is null, return the first
     * directory (not a JAR file etc.) on the classpath.
     *
     * TODO this could be done better...
     *
     * @since 7.6.5
     *
     * @param widgetsetFileName
     *            relative path for the widgetset
     *
     * @return URL
     */
    public static URL getWidgetsetSourceDirectory(String widgetsetFileName) {
        if (debug) {
            debug("classpathLocations values:");
            List<String> locations = new ArrayList<>(
                    CLASSPATH_LOCATIONS.keySet());
            for (String location : locations) {
                debug(String.valueOf(CLASSPATH_LOCATIONS.get(location)));
            }
        }

        URL firstDirectory = null;
        for (String entry : RAW_CLASSPATH_ENTRIES) {

            File directory = new File(entry);
            if (directory.exists() && !directory.isHidden()
                    && directory.isDirectory()) {
                try {
                    URL directoryUrl = directory.toURI().toURL();

                    // Store the first directory encountered.
                    if (firstDirectory == null) {
                        firstDirectory = directoryUrl;
                    }

                    if (widgetsetFileName == null
                            || new File(directory, widgetsetFileName)
                                    .exists()) {
                        return directoryUrl;
                    }
                } catch (MalformedURLException e) {
                    // ignore: continue to the next classpath entry
                    if (debug) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return firstDirectory;
    }

    /**
     * Test method for helper tool.
     */
    public static void main(String[] args) {
        log("Searching for available widgetsets and stylesheets...");

        ClassPathExplorer.getAvailableWidgetSetsAndStylesheets();
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static void error(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }

    private static void debug(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

}
