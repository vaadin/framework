package com.vaadin.tests.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows to get classes from the current classpath using classes FQN filter.
 * <p>
 * The methods in the class return all real (not anonymous and not private)
 * classes from the filtered classpath.
 *
 * @author Vaadin Ltd
 *
 */
public class ClasspathHelper {

    public static final String COM_VAADIN_FILE_PREFIX = "com"
            + File.separatorChar + "vaadin" + File.separatorChar;
    private final Predicate<String> skipClassesFilter;

    public ClasspathHelper(Predicate<String> skipClassesFilter) {
        this.skipClassesFilter = skipClassesFilter;
    }

    public ClasspathHelper() {
        this(fqn -> false);
    }

    public Stream<Class<?>> getVaadinClassesFromClasspath(
            Predicate<String> classpathFilter,
            Predicate<Class<?>> classFilter) {
        return getRawClasspathEntries().stream().filter(classpathFilter)
                .map(File::new).map(file -> getVaadinClassesFromFile(file))
                .flatMap(List::stream).filter(classFilter)
                .filter(cls -> !cls.isSynthetic() && !cls.isAnonymousClass()
                        && !Modifier.isPrivate(cls.getModifiers()));

    }

    public Stream<Class<?>> getVaadinClassesFromClasspath(
            Predicate<String> classpathFilter) {
        return getVaadinClassesFromClasspath(classpathFilter, cls -> true);
    }

    private List<Class<?>> getVaadinClassesFromFile(File classesRoot) {
        try {
            if (classesRoot.isDirectory()) {
                return Files.walk(classesRoot.toPath())
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toFile().getName()
                                .endsWith(".class"))
                        .filter(path -> classesRoot.toPath().relativize(path)
                                .toString().contains(COM_VAADIN_FILE_PREFIX))
                        .map(path -> getClassFromFile(path,
                                classesRoot.toPath()))
                        .filter(Objects::nonNull).collect(Collectors.toList());
            } else if (classesRoot.getName().toLowerCase(Locale.ENGLISH)
                    .endsWith(".jar")) {
                URI uri = URI.create("jar:" + classesRoot.toURI());
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = null;
                }
                if (fileSystem == null) {
                    fileSystem = FileSystems.newFileSystem(uri,
                            Collections.emptyMap());
                }
                Path root = fileSystem.getPath(File.separator);
                return Files.walk(root).filter(Files::isRegularFile)
                        .filter(path -> path.toUri().getSchemeSpecificPart()
                                .endsWith(".class"))
                        .filter(path -> root.relativize(path).toString()
                                .contains(COM_VAADIN_FILE_PREFIX))
                        .map(path -> getClassFromFile(path, root))
                        .filter(Objects::nonNull).collect(Collectors.toList());
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getClassFromFile(Path path, Path root) {
        Path relative = root.relativize(path);
        String name = relative.toString();
        name = name.substring(0, name.length() - ".class".length());
        name = name.replace(File.separatorChar, '.');
        if (skipClassesFilter.test(name)) {
            return null;
        }
        try {
            return Class.forName(name, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final static List<String> getRawClasspathEntries() {
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
        for (int i = 0; i < split.length; i++) {
            String classpathEntry = split[i];
            locations.add(classpathEntry);
        }

        return locations;
    }

}
