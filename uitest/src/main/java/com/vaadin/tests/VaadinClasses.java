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

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

@SuppressWarnings("deprecation")
public class VaadinClasses {

    public static List<Class<? extends Component>> getComponents() {
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

    public static List<Class<? extends ComponentContainer>> getComponentContainersSupportingAddRemoveComponent() {
        List<Class<? extends ComponentContainer>> classes = getComponentContainers();
        classes.remove(PopupView.class);
        classes.remove(CustomComponent.class);
        classes.remove(DragAndDropWrapper.class);
        classes.remove(CustomComponent.class);
        classes.remove(LoginForm.class);
        classes.remove(UI.class);

        return classes;
    }

    public static List<Class<? extends ComponentContainer>> getComponentContainersSupportingUnlimitedNumberOfComponents() {
        List<Class<? extends ComponentContainer>> classes = getComponentContainersSupportingAddRemoveComponent();
        classes.remove(VerticalSplitPanel.class);
        classes.remove(HorizontalSplitPanel.class);
        classes.remove(Window.class);

        return classes;
    }

    private static <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage) throws IOException {
        return findClasses(baseClass, basePackage, new String[] {});
    }

    private static <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage, String[] ignoredPackages) throws IOException {
        List<Class<? extends T>> classes = new ArrayList<>();
        String basePackageDirName = "/" + basePackage.replace('.', '/');
        URL location = VaadinSession.class.getResource(basePackageDirName);
        if (location.getProtocol().equals("file")) {
            try {
                File f = new File(location.toURI());
                if (!f.exists()) {
                    throw new IOException(
                            "Directory " + f.toString() + " does not exist");
                }
                findPackages(f, basePackage, baseClass, classes,
                        ignoredPackages);
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        } else if (location.getProtocol().equals("jar")) {
            JarURLConnection juc = (JarURLConnection) location.openConnection();
            findPackages(juc, basePackage, baseClass, classes);
        }

        Collections.sort(classes, new Comparator<Class<? extends T>>() {

            @Override
            public int compare(Class<? extends T> o1, Class<? extends T> o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        return classes;
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
            Class<T> baseClass, Collection<Class<? extends T>> result,
            String[] ignoredPackages) {
        for (String ignoredPackage : ignoredPackages) {
            if (javaPackage.equals(ignoredPackage)) {
                return;
            }
        }

        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                findPackages(file, javaPackage + "." + file.getName(),
                        baseClass, result, ignoredPackages);
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
                    && !Modifier.isAbstract(c.getModifiers())
                    && !c.isAnonymousClass() && !c.isMemberClass()
                    && !c.isLocalClass()) {
                result.add((Class<? extends T>) c);
            }
        } catch (Exception e) {
            // Could ignore that class cannot be loaded
            e.printStackTrace();
        } catch (LinkageError e) {
            // Ignore. Client side classes will at least throw LinkageErrors
        }

    }
}
