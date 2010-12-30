package com.vaadin.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

public class VaadinClasses {

    public static void main(String[] args) {
        System.out.println("ComponentContainers");
        System.out.println("===================");
        for (Class<? extends ComponentContainer> c : getComponentContainers()) {
            System.out.println(c.getName());
        }
        System.out.println();
        System.out.println("Components");
        System.out.println("==========");
        for (Class<? extends Component> c : getComponents()) {
            System.out.println(c.getName());
        }
    }

    private static List<Class<? extends Component>> getComponents() {
        try {
            return findClasses(Component.class, "com.vaadin.ui");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Class<? extends ComponentContainer>> getComponentContainers() {
        try {
            return findClasses(ComponentContainer.class, "com.vaadin.ui");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage) throws IOException {
        List<Class<? extends T>> componentContainers = new ArrayList<Class<? extends T>>();

        String basePackageDirName = "/" + basePackage.replace('.', '/');
        URL location = Application.class.getResource(basePackageDirName);
        if (location.getProtocol().equals("file")) {
            try {
                File f = new File(location.toURI());
                if (!f.exists()) {
                    throw new IOException("Directory " + f.toString()
                            + " does not exist");
                }
                findPackages(f, basePackage, baseClass, componentContainers);
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        } else if (location.getProtocol().equals("jar")) {
            JarURLConnection juc = (JarURLConnection) location.openConnection();
            findPackages(juc, basePackage, baseClass, componentContainers);
        }

        Collections.sort(componentContainers,
                new Comparator<Class<? extends T>>() {

                    public int compare(Class<? extends T> o1,
                            Class<? extends T> o2) {
                        return o1.getName().compareTo(o2.getName());
                    }

                });
        return componentContainers;

    }

    private static <T> void findPackages(JarURLConnection juc,
            String javaPackage, Class<T> baseClass,
            Collection<Class<? extends T>> result) throws IOException {
        String prefix = "com/vaadin/ui";
        Enumeration<JarEntry> ent = juc.getJarFile().entries();
        while (ent.hasMoreElements()) {
            JarEntry e = ent.nextElement();
            if (e.getName().endsWith(".class")
                    && e.getName().startsWith(prefix)) {
                String fullyQualifiedClassName = e.getName().replace('/', '.')
                        .replace(".class", "");
                addClassIfMatches(result, fullyQualifiedClassName, baseClass);
            }
        }
    }

    private static <T> void findPackages(File parent, String javaPackage,
            Class<T> baseClass, Collection<Class<? extends T>> result) {
        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                findPackages(file, javaPackage + "." + file.getName(),
                        baseClass, result);
            } else if (file.getName().endsWith(".class")) {
                String fullyQualifiedClassName = javaPackage + "."
                        + file.getName().replace(".class", "");
                addClassIfMatches(result, fullyQualifiedClassName, baseClass);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> void addClassIfMatches(
            Collection<Class<? extends T>> result,
            String fullyQualifiedClassName, Class<T> baseClass) {
        try {
            // Try to load the class

            Class<?> c = Class.forName(fullyQualifiedClassName);
            if (baseClass.isAssignableFrom(c)
                    && !Modifier.isAbstract(c.getModifiers())) {
                result.add((Class<? extends T>) c);
            }
        } catch (Exception e) {
            // Could ignore that class cannot be loaded
            e.printStackTrace();
        }

    }
}
