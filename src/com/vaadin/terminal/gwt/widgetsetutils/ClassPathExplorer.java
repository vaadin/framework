/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientCriterion;
import com.vaadin.terminal.Paintable;
import com.vaadin.ui.ClientWidget;

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

    private static Logger logger = Logger
            .getLogger("com.vaadin.terminal.gwt.widgetsetutils");

    /**
     * File filter that only accepts directories.
     */
    private final static FileFilter DIRECTORIES_ONLY = new FileFilter() {
        public boolean accept(File f) {
            if (f.exists() && f.isDirectory()) {
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * Raw class path entries as given in the java class path string. Only
     * entries that could include widgets/widgetsets are listed (primarily
     * directories, Vaadin JARs and add-on JARs).
     */
    private static List<String> rawClasspathEntries = getRawClasspathEntries();

    /**
     * Map from identifiers (either a package name preceded by the path and a
     * slash, or a URL for a JAR file) to the corresponding URLs. This is
     * constructed from the class path.
     */
    private static Map<String, URL> classpathLocations = getClasspathLocations(rawClasspathEntries);

    /**
     * No instantiation from outside, callable methods are static.
     */
    private ClassPathExplorer() {
    }

    /**
     * Finds server side widgets with {@link ClientWidget} annotation on the
     * class path (entries that can contain widgets/widgetsets - see {@link #getRawClasspathEntries()}).
     * 
     * As a side effect, also accept criteria are searched under the same class
     * path entries and added into the acceptCriterion collection.
     * 
     * @return a collection of {@link Paintable} classes
     */
    public static Collection<Class<? extends Paintable>> getPaintablesHavingWidgetAnnotation() {

        Collection<Class<? extends Paintable>> paintables = new HashSet<Class<? extends Paintable>>();
        Set<String> keySet = classpathLocations.keySet();
        for (String url : keySet) {
            searchForPaintables(classpathLocations.get(url), url, paintables);
        }
        return paintables;

    }

    /**
     * Finds all accept criteria having client side counterparts (classes with
     * the {@link ClientCriterion} annotation).
     * 
     * @return Collection of AcceptCriterion classes
     */
    public static Collection<Class<? extends AcceptCriterion>> getCriterion() {
        if (acceptCriterion.isEmpty()) {
            // accept criterion are searched as a side effect, normally after
            // paintable detection
            getPaintablesHavingWidgetAnnotation();
        }
        return acceptCriterion;
    }

    /**
     * Finds the names and locations of widgetsets available on the class path.
     * 
     * @return map from widgetset classname to widgetset location URL
     */
    public static Map<String, URL> getAvailableWidgetSets() {
        Map<String, URL> widgetsets = new HashMap<String, URL>();
        Set<String> keySet = classpathLocations.keySet();
        for (String location : keySet) {
            searchForWidgetSets(location, widgetsets);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Widgetsets found from classpath:\n");
        for (String ws : widgetsets.keySet()) {
            sb.append("\t");
            sb.append(ws);
            sb.append(" in ");
            sb.append(widgetsets.get(ws));
            sb.append("\n");
        }
        logger.info(sb.toString());
        return widgetsets;
    }

    /**
     * Finds all GWT modules / Vaadin widgetsets in a valid location.
     * 
     * If the location is a directory, all GWT modules (files with the
     * ".gwt.xml" extension) are added to widgetsets.
     * 
     * If the location is a JAR file, the comma-separated values of the
     * "Vaadin-Widgetsets" attribute in its manifest are added to widgetsets.
     * 
     * @param locationString
     *            an entry in {@link #classpathLocations}
     * @param widgetsets
     *            a map from widgetset name (including package, with dots as
     *            separators) to a URL (see {@link #classpathLocations}) - new
     *            entries are added to this map
     */
    private static void searchForWidgetSets(String locationString,
            Map<String, URL> widgetsets) {

        URL location = classpathLocations.get(locationString);
        File directory = new File(location.getFile());

        if (directory.exists() && !directory.isHidden()) {
            // Get the list of the files contained in the directory
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .gwt.xml files
                if (files[i].endsWith(".gwt.xml")) {
                    // remove the extension
                    String classname = files[i].substring(0,
                            files[i].length() - 8);
                    String packageName = locationString
                            .substring(locationString.lastIndexOf("/") + 1);
                    classname = packageName + "." + classname;
                    if (!widgetsets.containsKey(classname)) {
                        String packagePath = packageName.replaceAll("\\.", "/");
                        String basePath = location.getFile().replaceAll(
                                "/" + packagePath + "$", "");
                        try {
                            URL url = new URL(location.getProtocol(), location
                                    .getHost(), location.getPort(), basePath);
                            widgetsets.put(classname, url);
                        } catch (MalformedURLException e) {
                            // should never happen as based on an existing URL,
                            // only changing end of file name/path part
                            e.printStackTrace();
                        }
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
                    String value = manifest.getMainAttributes().getValue(
                            "Vaadin-Widgetsets");
                    if (value != null) {
                        String[] widgetsetNames = value.split(",");
                        for (int i = 0; i < widgetsetNames.length; i++) {
                            String widgetsetname = widgetsetNames[i].trim()
                                    .intern();
                            if (!widgetsetname.equals("")) {
                                widgetsets.put(widgetsetname, location);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error parsing jar file", e);
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
    private final static List<String> getRawClasspathEntries() {
        // try to keep the order of the classpath
        List<String> locations = new ArrayList<String>();

        String pathSep = System.getProperty("path.separator");
        String classpath = System.getProperty("java.class.path");

        if (classpath.startsWith("\"")) {
            classpath = classpath.substring(1);
        }
        if (classpath.endsWith("\"")) {
            classpath = classpath.substring(0, classpath.length() - 1);
        }

        logger.fine("Classpath: " + classpath);

        String[] split = classpath.split(pathSep);
        for (int i = 0; i < split.length; i++) {
            String classpathEntry = split[i];
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
     * See {@link #classpathLocations} for information on output format.
     * 
     * @param rawClasspathEntries
     *            raw class path entries as split from the Java class path
     *            string
     * @return map of classpath locations, see {@link #classpathLocations}
     */
    private final static Map<String, URL> getClasspathLocations(
            List<String> rawClasspathEntries) {
        // try to keep the order of the classpath
        Map<String, URL> locations = new LinkedHashMap<String, URL>();
        for (String classpathEntry : rawClasspathEntries) {
            File file = new File(classpathEntry);
            include(null, file, locations);
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
                    logger.fine(url.toString());
                    JarFile jarFile = conn.getJarFile();
                    Manifest manifest = jarFile.getManifest();
                    if (manifest != null) {
                        Attributes mainAttributes = manifest
                                .getMainAttributes();
                        if (mainAttributes.getValue("Vaadin-Widgetsets") != null) {
                            return true;
                        }
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return false;
            }
        }
    }

    /**
     * Recursively add subdirectories and jar files to locations - see
     * {@link #classpathLocations}.
     * 
     * @param name
     * @param file
     * @param locations
     */
    private final static void include(String name, File file,
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
        File[] dirs = file.listFiles(DIRECTORIES_ONLY);
        for (int i = 0; i < dirs.length; i++) {
            try {
                // add the present directory
                if (!dirs[i].isHidden()
                        && !dirs[i].getPath().contains(File.separator + ".")) {
                    String key = dirs[i].getCanonicalPath() + "/" + name
                            + dirs[i].getName();
                    locations.put(key, new URL("file://"
                            + dirs[i].getCanonicalPath()));
                }
            } catch (Exception ioe) {
                return;
            }
            include(name + dirs[i].getName(), dirs[i], locations);
        }
    }

    /**
     * Add a jar file to locations - see {@link #classpathLocations}.
     * 
     * @param name
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
     * Searches for all paintable classes and accept criteria under a location
     * based on {@link ClientWidget} and {@link ClientCriterion} annotations.
     * 
     * Note that client criteria are updated directly to the
     * {@link #acceptCriterion} field, whereas paintables are added to the
     * paintables map given as a parameter.
     * 
     * @param location
     * @param locationString
     * @param paintables
     */
    private final static void searchForPaintables(URL location,
            String locationString,
            Collection<Class<? extends Paintable>> paintables) {

        // Get a File object for the package
        File directory = new File(location.getFile());

        if (directory.exists() && !directory.isHidden()) {
            // Get the list of the files contained in the directory
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // remove the .class extension
                    String classname = files[i].substring(0,
                            files[i].length() - 6);
                    String packageName = locationString
                            .substring(locationString.lastIndexOf("/") + 1);
                    classname = packageName + "." + classname;
                    tryToAdd(classname, paintables);
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

                    Enumeration<JarEntry> e = jarFile.entries();
                    while (e.hasMoreElements()) {
                        JarEntry entry = e.nextElement();
                        String entryname = entry.getName();
                        if (!entry.isDirectory()
                                && entryname.endsWith(".class")) {
                            String classname = entryname.substring(0, entryname
                                    .length() - 6);
                            if (classname.startsWith("/")) {
                                classname = classname.substring(1);
                            }
                            classname = classname.replace('/', '.');
                            tryToAdd(classname, paintables);
                        }
                    }
                }
            } catch (IOException e) {
                logger.warning(e.toString());
            }
        }

    }

    /**
     * A print stream that ignores all output.
     * 
     * This is used to hide error messages from static initializers of classes
     * being inspected.
     */
    private static PrintStream devnull = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // NOP
        }
    });

    /**
     * Collection of all {@link AcceptCriterion} classes, updated as a side
     * effect of {@link #searchForPaintables(URL, String, Collection)} based on
     * {@link ClientCriterion} annotations.
     */
    private static Set<Class<? extends AcceptCriterion>> acceptCriterion = new HashSet<Class<? extends AcceptCriterion>>();

    /**
     * Checks a class for the {@link ClientWidget} and {@link ClientCriterion}
     * annotations, and adds it to the appropriate collection if it has either.
     * 
     * @param fullclassName
     * @param paintables
     *            the collection to which to add server side classes with
     *            {@link ClientWidget} annotation
     */
    private static void tryToAdd(final String fullclassName,
            Collection<Class<? extends Paintable>> paintables) {
        try {
            PrintStream out = System.out;
            PrintStream err = System.err;
            System.setErr(devnull);
            System.setOut(devnull);

            Class<?> c = Class.forName(fullclassName);

            System.setErr(err);
            System.setOut(out);

            if (c.getAnnotation(ClientWidget.class) != null) {
                paintables.add((Class<? extends Paintable>) c);
                // System.out.println("Found paintable " + fullclassName);
            } else if (c.getAnnotation(ClientCriterion.class) != null) {
                acceptCriterion.add((Class<? extends AcceptCriterion>) c);
            }

        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        } catch (LinkageError e) {
            // NOP
        } catch (Exception e) {
            e.printStackTrace();
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
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("classpathLocations values:");
            ArrayList<String> locations = new ArrayList<String>(
                    classpathLocations.keySet());
            for (String location : locations) {
                logger.fine(String.valueOf(classpathLocations.get(location)));
            }
        }

        Iterator<String> it = rawClasspathEntries.iterator();
        while (it.hasNext()) {
            String entry = it.next();

            File directory = new File(entry);
            if (directory.exists() && !directory.isHidden()
                    && directory.isDirectory()) {
                try {
                    return new URL("file://" + directory.getCanonicalPath());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // ignore: continue to the next classpath entry
                } catch (IOException e) {
                    e.printStackTrace();
                    // ignore: continue to the next classpath entry
                }
            }
        }
        return null;
    }

    /**
     * Test method for helper tool
     */
    public static void main(String[] args) {
        Collection<Class<? extends Paintable>> paintables = ClassPathExplorer
                .getPaintablesHavingWidgetAnnotation();
        logger.info("Found annotated paintables:");
        for (Class<? extends Paintable> cls : paintables) {
            logger.info(cls.getCanonicalName());
        }

        logger.info("");
        logger.info("Searching available widgetsets...");

        Map<String, URL> availableWidgetSets = ClassPathExplorer
                .getAvailableWidgetSets();
        for (String string : availableWidgetSets.keySet()) {

            logger.info(string + " in " + availableWidgetSets.get(string));
        }
    }

}