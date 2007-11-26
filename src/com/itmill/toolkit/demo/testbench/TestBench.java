package com.itmill.toolkit.demo.testbench;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;

/**
 * TestBench finds out testable classes within given java packages and adds them
 * to menu from where they can be executed. Class is considered testable if it
 * is of class CustomComponent.
 * 
 * Note: edit TestBench.testablePackages array
 * 
 * @author IT Mill Ltd.
 * 
 */
public class TestBench extends com.itmill.toolkit.Application implements
        Property.ValueChangeListener {

    // Add here packages which are used for finding testable classes
    String[] testablePackages = { "com.itmill.toolkit.demo.tests" };

    HierarchicalContainer testables = new HierarchicalContainer();

    Window mainWindow = new Window("TestBench window");

    // Main layout consists of tree menu and body layout
    SplitPanel mainLayout = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

    Tree menu;

    Panel bodyLayout = new Panel();

    HashMap itemCaptions = new HashMap();

    public void init() {

        // Add testable classes to hierarchical container
        for (int p = 0; p < testablePackages.length; p++) {
            testables.addItem(testablePackages[p]);
            try {
                List testableClasses = getTestableClassesForPackage(testablePackages[p]);
                for (Iterator it = testableClasses.iterator(); it.hasNext();) {
                    Class t = (Class) it.next();
                    // ignore TestBench itself
                    if (t.equals(TestBench.class)) {
                        continue;
                    }
                    try {
                        testables.addItem(t);
                        itemCaptions.put(t, t.getName());
                        testables.setParent(t, testablePackages[p]);
                        continue;
                    } catch (Exception e) {
                        try {
                            testables.addItem(t);
                            itemCaptions.put(t, t.getName());
                            testables.setParent(t, testablePackages[p]);
                            continue;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        menu = new Tree("Testables", testables);
        // simplify captions
        for (Iterator i = itemCaptions.keySet().iterator(); i.hasNext();) {
            Class testable = (Class) i.next();
            menu.setItemCaption(testable, testable.getName());
        }
        menu.addListener(this);
        menu.setImmediate(true);

        mainLayout.addComponent(menu);

        bodyLayout.addStyleName("light");
        bodyLayout.setHeight(100);
        bodyLayout.setHeightUnits(Sizeable.UNITS_PERCENTAGE);
        bodyLayout.setLayout(new ExpandLayout());

        mainLayout.addComponent(bodyLayout);

        mainLayout.setSplitPosition(30);

        mainWindow.setLayout(mainLayout);

        setMainWindow(mainWindow);
    }

    private Component createTestable(Class c) {
        try {
            Application app = (Application) c.newInstance();
            app.init();
            return app.getMainWindow().getLayout();
        } catch (Exception e) {
            try {
                CustomComponent cc = (CustomComponent) c.newInstance();
                return cc;
            } catch (Exception e1) {
                e1.printStackTrace();
                return new Label("Cannot create custom component: "
                        + e1.toString());
            }
        }
    }

    // Handle menu selection and update body
    public void valueChange(Property.ValueChangeEvent event) {
        bodyLayout.removeAllComponents();
        bodyLayout.setCaption(null);

        String title = ((Class) menu.getValue()).getName();
        bodyLayout.setCaption(title);
        bodyLayout.addComponent(createTestable((Class) menu.getValue()));
    }

    /**
     * Return all testable classes within given package. Class is considered
     * testable if it's superclass is CustomComponent.
     * 
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    public static List getTestableClassesForPackage(String packageName)
            throws Exception {
        ArrayList directories = new ArrayList();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = packageName.replace('.', '/');
            // Ask for all resources for the path
            Enumeration resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = (URL) resources.nextElement();
                directories.add(new File(url.getFile()));
            }
        } catch (Exception x) {
            throw new Exception(packageName
                    + " does not appear to be a valid package.");
        }

        ArrayList classes = new ArrayList();
        // For every directory identified capture all the .class files
        for (Iterator it = directories.iterator(); it.hasNext();) {
            File directory = (File) it.next();
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (int j = 0; j < files.length; j++) {
                    // we are only interested in .class files
                    if (files[j].endsWith(".class")) {
                        // removes the .class extension
                        String p = packageName + '.'
                                + files[j].substring(0, files[j].length() - 6);
                        Class c = Class.forName(p);
                        if (c.getSuperclass() != null) {
                            // if ((c.getSuperclass()
                            // .equals(com.itmill.toolkit.Application.class))) {
                            // classes.add(c);
                            // } else
                            if ((c.getSuperclass()
                                    .equals(com.itmill.toolkit.ui.CustomComponent.class))) {
                                classes.add(c);
                            }
                        }
                    }
                }
            } else {
                throw new ClassNotFoundException(packageName + " ("
                        + directory.getPath()
                        + ") does not appear to be a valid package");
            }
        }

        return classes;
    }

}
