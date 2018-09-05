package com.vaadin.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

/**
 * TestBench finds out testable classes within given java packages and adds them
 * to menu from where they can be executed. Class is considered testable if it
 * is of class Application or CustomComponent.
 *
 * Note: edit TestBench.testablePackages array
 *
 * @author Vaadin Ltd.
 *
 */
public class TestBench extends com.vaadin.server.LegacyApplication
        implements Property.ValueChangeListener {

    // Add here packages which are used for finding testable classes
    String[] testablePackages = { "com.vaadin.tests",
            "com.vaadin.tests.tickets" };

    HierarchicalContainer testables = new HierarchicalContainer();

    LegacyWindow mainWindow = new LegacyWindow("TestBench window");

    // Main layout consists of tree menu and body layout
    HorizontalSplitPanel mainLayout = new HorizontalSplitPanel();

    Tree menu;

    VerticalLayout bodyLayout = new VerticalLayout();

    Set<Class<?>> itemCaptions = new HashSet<>();

    @Override
    public void init() {

        // Add testable classes to hierarchical container
        for (int p = 0; p < testablePackages.length; p++) {
            testables.addItem(testablePackages[p]);
            try {
                final List<Class<?>> testableClasses = getTestableClassesForPackage(
                        testablePackages[p]);
                for (final Class<?> t : testableClasses) {
                    // ignore TestBench itself
                    if (t.equals(TestBench.class)) {
                        continue;
                    }
                    try {
                        testables.addItem(t);
                        itemCaptions.add(t);
                        testables.setParent(t, testablePackages[p]);
                        testables.setChildrenAllowed(t, false);
                        continue;
                    } catch (final Exception e) {
                        try {
                            testables.addItem(t);
                            itemCaptions.add(t);
                            testables.setParent(t, testablePackages[p]);
                            testables.setChildrenAllowed(t, false);
                            continue;
                        } catch (final Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        menu = new Tree("Testables", testables);

        for (final Class<?> testable : itemCaptions) {
            // simplify captions
            final String name = testable.getName()
                    .substring(testable.getName().lastIndexOf('.') + 1);
            menu.setItemCaption(testable, name);
        }
        // expand all root items
        for (final Object id : menu.rootItemIds()) {
            menu.expandItemsRecursively(id);
        }

        menu.addListener(this);
        menu.setImmediate(true);
        menu.setNullSelectionAllowed(false);
        VerticalLayout lo = new VerticalLayout();
        lo.addComponent(menu);

        mainWindow.getPage().addUriFragmentChangedListener(event -> {
            String fragment = event.getUriFragment();
            if (fragment != null && !fragment.isEmpty()) {
                // try to find a proper test class

                // exact match
                for (Object next : menu.getItemIds()) {
                    if (next instanceof Class) {
                        Class<?> c = (Class<?>) next;
                        String string = c.getName();
                        if (string.equals(fragment)) {
                            menu.setValue(c);
                            mainLayout.setSplitPosition(0);
                            return;
                        }
                    }
                }

                // simple name match
                for (Object next : menu.getItemIds()) {
                    if (next instanceof Class) {
                        Class<?> c = (Class<?>) next;
                        String string = c.getSimpleName();
                        if (string.equals(fragment)) {
                            menu.setValue(c);
                            mainLayout.setSplitPosition(0);
                            return;
                        }
                    }
                }
                // ticket match
                for (Object next : menu.getItemIds()) {
                    if (next instanceof Class) {
                        Class<?> c = (Class<?>) next;
                        String string = c.getSimpleName();
                        if (string.startsWith("Ticket" + fragment)) {
                            menu.setValue(c);
                            mainLayout.setSplitPosition(0);
                            return;
                        }
                    }
                }

                // just partly match lowercase
                for (Object next : menu.getItemIds()) {
                    if (next instanceof Class) {
                        Class<?> c = (Class<?>) next;
                        String string = c.getSimpleName();
                        if (string.toLowerCase(Locale.ROOT)
                                .contains(fragment.toLowerCase(Locale.ROOT))) {
                            menu.setValue(c);
                            mainLayout.setSplitPosition(0);
                            return;
                        }
                    }
                }

                getMainWindow()
                        .showNotification("No potential matc for #" + fragment);
            }
        });

        mainLayout.addComponent(lo);

        Panel bodyPanel = new Panel(bodyLayout);
        bodyPanel.addStyleName("light");
        bodyPanel.setSizeFull();

        mainLayout.addComponent(bodyPanel);

        mainLayout.setSplitPosition(30);

        mainWindow.setContent(mainLayout);

        setMainWindow(mainWindow);
    }

    private Component createTestable(Class<?> c) {
        try {
            final LegacyApplication app = (LegacyApplication) c.newInstance();
            app.doInit(null);
            Layout lo = (Layout) app.getMainWindow().getContent();
            lo.setParent(null);
            return lo;
        } catch (final Exception e) {
            try {
                final CustomComponent cc = (CustomComponent) c.newInstance();
                cc.setSizeFull();
                return cc;
            } catch (final Exception e1) {
                e1.printStackTrace();
                VerticalLayout lo = new VerticalLayout();
                lo.addComponent(new Label(
                        "Cannot create application / custom component: " + e1));

                Link l = new Link("Try opening via app runner",
                        new ExternalResource("../run/" + c.getName()));
                lo.addComponent(l);

                return lo;
            }
        }
    }

    // Handle menu selection and update body
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        bodyLayout.removeAllComponents();
        bodyLayout.setCaption(null);

        final Object o = menu.getValue();
        if (o != null && o instanceof Class) {
            final Class<?> c = (Class<?>) o;
            final String title = c.getName();
            bodyLayout.setCaption(title);
            bodyLayout.addComponent(createTestable(c));
        } else {
            // NOP node selected or deselected tree item
        }
    }

    /**
     * Return all testable classes within given package. Class is considered
     * testable if it's superclass is Application or CustomComponent.
     *
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getTestableClassesForPackage(
            String packageName) throws Exception {
        final List<File> directories = new ArrayList<>();
        try {
            final ClassLoader cld = Thread.currentThread()
                    .getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            final String path = packageName.replace('.', '/');
            // Ask for all resources for the path
            final Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                directories.add(new File(url.getFile()));
            }
        } catch (final Exception x) {
            throw new Exception(
                    packageName + " does not appear to be a valid package.");
        }

        final List<Class<?>> classes = new ArrayList<>();
        // For every directory identified capture all the .class files
        for (final File directory : directories) {
            if (directory.exists()) {
                // Get the list of the files contained in the package
                final String[] files = directory.list();
                for (int j = 0; j < files.length; j++) {
                    // we are only interested in .class files
                    if (files[j].endsWith(".class")) {
                        // removes the .class extension
                        final String p = packageName + '.'
                                + files[j].substring(0, files[j].length() - 6);
                        final Class<?> c = Class.forName(p);
                        if (c.getSuperclass() != null) {
                            if ((c.getSuperclass().equals(
                                    com.vaadin.server.VaadinSession.class))) {
                                classes.add(c);
                            } else if ((c.getSuperclass().equals(
                                    com.vaadin.ui.CustomComponent.class))) {
                                classes.add(c);
                            }
                        }

                        // for (Class cc : c.getInterfaces()) {
                        // if (cc.equals(Testable.class)) {
                        // // Class is testable
                        // classes.add(c);
                        // }
                        // }
                    }
                }
            } else {
                throw new ClassNotFoundException(
                        packageName + " (" + directory.getPath()
                                + ") does not appear to be a valid package");
            }
        }

        return classes;
    }

}
