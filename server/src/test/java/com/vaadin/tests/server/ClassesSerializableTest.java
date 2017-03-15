package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Component;

public class ClassesSerializableTest {

    /**
     * JARs that will be scanned for classes to test, in addition to classpath
     * directories.
     */
    private static final String JAR_PATTERN = ".*vaadin.*\\.jar";

    private static final String[] BASE_PACKAGES = { "com.vaadin" };

    private static final String[] EXCLUDED_PATTERNS = {
            "com\\.vaadin\\.demo\\..*", //
            "com\\.vaadin\\.external\\.org\\.apache\\.commons\\.fileupload\\..*", //
            "com\\.vaadin\\.launcher\\..*", //
            "com\\.vaadin\\.client\\..*", //
            "com\\.vaadin\\.server\\.widgetsetutils\\..*", //
            "com\\.vaadin\\.server\\.themeutils\\..*", //
            "com\\.vaadin\\.tests\\..*", // exclude automated tests
            "com\\.vaadin\\.tools\\..*", //
            "com\\.vaadin\\.ui\\.themes\\..*", //
            // exact class level filtering
            "com\\.vaadin\\.event\\.FieldEvents", //
            "com\\.vaadin\\.event\\.LayoutEvents", //
            "com\\.vaadin\\.event\\.MouseEvents", //
            "com\\.vaadin\\.event\\.UIEvents", //
            "com\\.vaadin\\.server\\.VaadinPortlet", //
            "com\\.vaadin\\.server\\.MockServletConfig", //
            "com\\.vaadin\\.server\\.MockServletContext", //
            "com\\.vaadin\\.server\\.Constants", //
            "com\\.vaadin\\.server\\.VaadinServiceClassLoaderUtil", //
            "com\\.vaadin\\.server\\.VaadinServiceClassLoaderUtil\\$GetClassLoaderPrivilegedAction", //
            "com\\.vaadin\\.server\\.communication\\.FileUploadHandler\\$SimpleMultiPartInputStream", //
            "com\\.vaadin\\.server\\.communication\\.PushRequestHandler.*",
            "com\\.vaadin\\.server\\.communication\\.PushHandler.*", // PushHandler
            "com\\.vaadin\\.server\\.communication\\.DateSerializer", //
            "com\\.vaadin\\.server\\.communication\\.JSONSerializer", //
            // and its inner classes do not need to be serializable
            "com\\.vaadin\\.util\\.SerializerHelper", // fully static
            // class level filtering, also affecting nested classes and
            // interfaces
            "com\\.vaadin\\.server\\.LegacyCommunicationManager.*", //
            "com\\.vaadin\\.buildhelpers.*", //
            "com\\.vaadin\\.util\\.EncodeUtil.*", //
            "com\\.vaadin\\.util\\.ReflectTools.*", //
            "com\\.vaadin\\.data\\.util\\.ReflectTools.*", //
            "com\\.vaadin\\.data\\.util\\.JsonUtil.*", //
            "com\\.vaadin\\.data\\.util.BeanItemContainerGenerator.*",
            "com\\.vaadin\\.data\\.util\\.sqlcontainer\\.connection\\.MockInitialContextFactory",
            "com\\.vaadin\\.data\\.util\\.sqlcontainer\\.DataGenerator",
            "com\\.vaadin\\.data\\.util\\.sqlcontainer\\.FreeformQueryUtil",
            // the JSR-303 constraint interpolation context
            "com\\.vaadin\\.data\\.validator\\.BeanValidator\\$1", //
            "com\\.vaadin\\.sass.*", //
            "com\\.vaadin\\.testbench.*", //
            "com\\.vaadin\\.util\\.CurrentInstance\\$1", //
            "com\\.vaadin\\.server\\.AbstractClientConnector\\$1", //
            "com\\.vaadin\\.server\\.AbstractClientConnector\\$1\\$1", //
            "com\\.vaadin\\.server\\.JsonCodec\\$1", //
            "com\\.vaadin\\.server\\.communication\\.PushConnection", //
            "com\\.vaadin\\.server\\.communication\\.AtmospherePushConnection.*", //
            "com\\.vaadin\\.util\\.ConnectorHelper", //
            "com\\.vaadin\\.server\\.VaadinSession\\$FutureAccess", //
            "com\\.vaadin\\.external\\..*", //
            "com\\.vaadin\\.util\\.WeakValueMap.*", //
            "com\\.vaadin\\.themes\\.valoutil\\.BodyStyleName", //
            "com\\.vaadin\\.server\\.communication\\.JSR356WebsocketInitializer.*", //
            "com\\.vaadin\\.screenshotbrowser\\.ScreenshotBrowser.*", //
    };

    /**
     * Tests that all the relevant classes and interfaces under
     * {@link #BASE_PACKAGES} implement Serializable.
     *
     * @throws Exception
     */
    @Test
    public void testClassesSerializable() throws Exception {
        List<String> rawClasspathEntries = getRawClasspathEntries();

        List<String> classes = new ArrayList<>();
        for (String location : rawClasspathEntries) {
            classes.addAll(findServerClasses(location));
        }

        ArrayList<Field> nonSerializableFunctionFields = new ArrayList<>();

        ArrayList<Class<?>> nonSerializableClasses = new ArrayList<>();
        for (String className : classes) {
            Class<?> cls = Class.forName(className);
            // Don't add classes that have a @Ignore annotation on the class
            if (isTestClass(cls)) {
                continue;
            }

            // report fields that use lambda types that won't be serializable
            // (also in syntehtic classes)
            Stream.of(cls.getDeclaredFields())
                    .filter(field -> isFunctionalType(field.getGenericType()))
                    .forEach(nonSerializableFunctionFields::add);

            // skip annotations and synthetic classes
            if (cls.isAnnotation() || cls.isSynthetic()) {
                continue;
            }

            if (Component.class.isAssignableFrom(cls) && !cls.isInterface()
                    && !Modifier.isAbstract(cls.getModifiers())) {
                serializeAndDeserialize(cls);
            }

            // report non-serializable classes and interfaces
            if (!Serializable.class.isAssignableFrom(cls)) {
                if (cls.getSuperclass() == Object.class
                        && cls.getInterfaces().length == 1) {
                    // Single interface implementors
                    Class<?> iface = cls.getInterfaces()[0];

                    if (iface == Runnable.class) {
                        // Ignore Runnables used with access()
                        continue;
                    } else if (iface == Comparator.class) {
                        // Ignore inline comparators
                        continue;
                    }
                }
                nonSerializableClasses.add(cls);
                // TODO easier to read when testing
                // System.err.println(cls);
            }
        }

        // useful failure message including all non-serializable classes and
        // interfaces
        if (!nonSerializableClasses.isEmpty()) {
            failSerializableClasses(nonSerializableClasses);
        }

        if (!nonSerializableFunctionFields.isEmpty()) {
            failSerializableFields(nonSerializableFunctionFields);
        }
    }

    private void serializeAndDeserialize(Class<?> clazz)
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Optional<Constructor<?>> defaultCtor = Stream
                .of(clazz.getDeclaredConstructors())
                .filter(ctor -> ctor.getParameterCount() == 0).findFirst();
        if (!defaultCtor.isPresent()) {
            return;
        }
        defaultCtor.get().setAccessible(true);
        Object instance = defaultCtor.get().newInstance();
        serializeAndDeserialize(instance);
    }

    public static <T> T serializeAndDeserialize(T instance)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(instance);
        byte[] data = bs.toByteArray();
        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data));

        @SuppressWarnings("unchecked")
        T readObject = (T) in.readObject();

        return readObject;
    }

    private void failSerializableFields(
            ArrayList<Field> nonSerializableFunctionFields) {
        String nonSerializableString = nonSerializableFunctionFields.stream()
                .map(field -> String.format("%s.%s",
                        field.getDeclaringClass().getName(), field.getName()))
                .collect(Collectors.joining(", "));

        Assert.fail("Fields with functional types that are not serializable: "
                + nonSerializableString);
    }

    private void failSerializableClasses(
            ArrayList<Class<?>> nonSerializableClasses) {
        String nonSerializableString = "";
        Iterator<Class<?>> it = nonSerializableClasses.iterator();
        while (it.hasNext()) {
            Class<?> c = it.next();
            nonSerializableString += ", " + c.getName();
            if (c.isAnonymousClass()) {
                nonSerializableString += "(super: ";
                nonSerializableString += c.getSuperclass().getName();
                nonSerializableString += ", interfaces: ";
                for (Class<?> i : c.getInterfaces()) {
                    nonSerializableString += i.getName();
                    nonSerializableString += ",";
                }
                nonSerializableString += ")";
            }
        }
        Assert.fail(
                "Serializable not implemented by the following classes and interfaces: "
                        + nonSerializableString);

    }

    private static boolean isFunctionalType(Type type) {
        return type.getTypeName().contains("java.util.function");
    }

    private boolean isTestClass(Class<?> cls) {
        if (cls.getEnclosingClass() != null
                && isTestClass(cls.getEnclosingClass())) {
            return true;
        }

        // Test classes with a @Test annotation on some method
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Lists all class path entries by splitting the class path string.
     *
     * Adapted from ClassPathExplorer.getRawClasspathEntries(), but without
     * filtering.
     *
     * @return List of class path segment strings
     */
    private final static List<String> getRawClasspathEntries() {
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

        String[] split = classpath.split(pathSep);
        locations.addAll(Arrays.asList(split));

        return locations;
    }

    /**
     * Finds the server side classes/interfaces under a class path entry -
     * either a directory or a JAR that matches {@link #JAR_PATTERN}.
     *
     * Only classes under {@link #BASE_PACKAGES} are considered, and those
     * matching {@link #EXCLUDED_PATTERNS} are filtered out.
     *
     * @param classpathEntry
     * @return
     * @throws IOException
     */
    private List<String> findServerClasses(String classpathEntry)
            throws IOException {
        Collection<String> classes = new ArrayList<>();

        File file = new File(classpathEntry);
        if (file.isDirectory()) {
            classes = findClassesInDirectory(null, file);
        } else if (file.getName().matches(JAR_PATTERN)) {
            classes = findClassesInJar(file);
        } else {
            System.out.println("Ignoring " + classpathEntry);
            return Collections.emptyList();
        }

        List<String> filteredClasses = new ArrayList<>();
        for (String className : classes) {
            boolean ok = false;
            for (String basePackage : BASE_PACKAGES) {
                if (className.startsWith(basePackage + ".")) {
                    ok = true;
                    break;
                }
            }
            for (String excludedPrefix : EXCLUDED_PATTERNS) {
                if (className.matches(excludedPrefix)) {
                    ok = false;
                    break;
                }
            }

            // Don't add test classes
            if (className.contains("Test")) {
                ok = false;
            }

            if (ok) {
                filteredClasses.add(className);
            }
        }

        return filteredClasses;
    }

    /**
     * Lists class names (based on .class files) in a JAR file.
     *
     * @param file
     *            a valid JAR file
     * @return collection of fully qualified class names in the JAR
     * @throws IOException
     */
    private Collection<String> findClassesInJar(File file) throws IOException {
        Collection<String> classes = new ArrayList<>();

        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String nameWithoutExtension = entry.getName()
                            .replaceAll("\\.class", "");
                    String className = nameWithoutExtension.replace('/', '.');
                    classes.add(className);
                }
            }
        }
        return classes;
    }

    /**
     * Lists class names (based on .class files) in a directory (a package path
     * root).
     *
     * @param parentPackage
     *            parent package name or null at root of hierarchy, used by
     *            recursion
     * @param parent
     *            File representing the directory to scan
     * @return collection of fully qualified class names in the directory
     */
    private final static Collection<String> findClassesInDirectory(
            String parentPackage, File parent) {
        if (parent.isHidden()
                || parent.getPath().contains(File.separator + ".")) {
            return Collections.emptyList();
        }

        if (parentPackage == null) {
            parentPackage = "";
        } else {
            parentPackage += ".";
        }

        Collection<String> classNames = new ArrayList<>();

        // add all directories recursively
        File[] files = parent.listFiles();
        for (File child : files) {
            if (child.isDirectory()) {
                classNames.addAll(findClassesInDirectory(
                        parentPackage + child.getName(), child));
            } else if (child.getName().endsWith(".class")) {
                classNames.add(parentPackage.replace(File.separatorChar, '.')
                        + child.getName().replaceAll("\\.class", ""));
            }
        }

        return classNames;
    }

}
