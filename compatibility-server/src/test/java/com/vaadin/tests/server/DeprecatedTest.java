/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 *
 */
public class DeprecatedTest {

    private static String CLASS_SUFFIX = ".class";

    @Test
    public void allTypesAreDeprecated() throws URISyntaxException {
        URL url = DeprecatedTest.class.getResource("/");
        File file = new File(url.toURI());
        List<File> classpath = getRawClasspathEntries().stream()
                .filter(entry -> entry.contains("compatibility-server"))
                .map(File::new).filter(fileEntry -> !fileEntry.equals(file))
                .collect(Collectors.toList());
        Assert.assertFalse(classpath.isEmpty());
        classpath.forEach(this::checkDeprecatedClasses);
    }

    private void checkDeprecatedClasses(File classesRoot) {
        try {
            if (classesRoot.isDirectory()) {
                Files.walk(classesRoot.toPath()).filter(Files::isRegularFile)
                        .filter(path -> path.toFile().getName()
                                .endsWith(CLASS_SUFFIX))
                        .forEach(path -> checkDeprecatedClass(path,
                                classesRoot.toPath()));
            } else if (classesRoot.getName().toLowerCase(Locale.ENGLISH)
                    .endsWith(".jar")) {
                URI uri = URI.create("jar:file:" + classesRoot.getPath());
                Path root = FileSystems
                        .newFileSystem(uri, Collections.emptyMap())
                        .getPath("/");
                Files.walk(root).filter(Files::isRegularFile)
                        .filter(path -> path.toUri().getSchemeSpecificPart()
                                .endsWith(CLASS_SUFFIX))
                        .forEach(path -> checkDeprecatedClass(path, root));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkDeprecatedClass(Path path, Path root) {
        Path relative = root.relativize(path);
        String name = relative.toString();
        name = name.substring(0, name.length() - CLASS_SUFFIX.length());
        name = name.replace('/', '.');
        try {
            Class<?> clazz = Class.forName(name);
            if (clazz.isSynthetic() || clazz.isAnonymousClass()) {
                return;
            }
            if (Modifier.isPrivate(clazz.getModifiers())) {
                return;
            }
            Assert.assertNotNull(
                    "Class " + clazz
                            + " is in compatability package and it's not deprecated",
                    clazz.getAnnotation(Deprecated.class));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
        for (int i = 0; i < split.length; i++) {
            String classpathEntry = split[i];
            locations.add(classpathEntry);
        }

        return locations;
    }
}
